package com.example.posts

import android.app.Service
import android.content.Intent
import android.os.AsyncTask
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference

class MyService : Service() {

    private var msg: MainActivity.Message? = null
    private var recyclerView: RecyclerView? = null
    private var progressBar: ProgressBar? = null

    val adapter = PostAdapter(mutableListOf()) {
        it.deleted = true
        DoWhateverAsync { MyApp.postDao.update(it) }
        MyApp.service.delete(it.id)
            .enqueue(MyCallback(this) { Log.i("MyService", "Post deleted !") })
    }

    override fun onBind(intent: Intent): IBinder {
        return MyBinder()
    }

    inner class MyBinder : Binder() {
        fun add(post: Post) {
            DoWhateverAsync { MyApp.postDao.insert(post) }

            adapter.items.add(post)
            adapter.notifyItemChanged(adapter.items.size - 1)
            recyclerView?.scrollToPosition(adapter.items.size - 1)

            MyApp.service.post(CreatedPost(post.userId, post.title, post.body))
                .enqueue(MyCallback(this@MyService) {
                    Log.i("MyService", "Post ${post.id} posted")
                })
        }

        fun toEnd() {
            recyclerView?.scrollToPosition(adapter.items.size - 1)
        }

        fun nextId(): Long = (adapter.items.size + 1).toLong()

        fun reset() {
            msg = null
            progressBar = null
            recyclerView = null
        }

        fun refresh() {
            MyApp.service.get().enqueue(MyCallback(this@MyService) {
                val posts = it.body()!!
                DoWhateverAsync {
                    MyApp.postDao.deleteAll()
                    MyApp.postDao.insertAll(posts)
                }
                adapter.apply {
                    items.clear()
                    items.addAll(posts)
                    notifyDataSetChanged()
                }
            })
        }

        fun init(recView: RecyclerView, bar: ProgressBar, m: MainActivity.Message) {
            msg = m
            progressBar = bar
            recyclerView = recView
            recyclerView!!.adapter = adapter
        }

        fun takeAllPosts() {
            if (adapter.items.isEmpty()) TakeAllPostAsync(this@MyService).execute()
        }
    }

    class TakeAllPostAsync(s: MyService) : AsyncTask<Unit, Unit, List<Post>>() {
        private val serviceRef = WeakReference(s)

        override fun doInBackground(vararg params: Unit?): List<Post> {
            return MyApp.postDao.getAll()
        }

        override fun onPostExecute(result: List<Post>) {
            val service = serviceRef.get()
            if (service != null) {
                if (result.isNotEmpty()) {
                    service.progressBar?.visibility ?: ProgressBar.GONE
                    service.adapter.items.addAll(result)
                    service.adapter.notifyDataSetChanged()
                } else {
                    MyApp.service.get().enqueue(
                        MyCallback<List<Post>>(service) {
                            //Log.e("LocalService", "Get all posts !")
                            val posts = it.body()!!
                            service.adapter.items.addAll(posts)
                            service.adapter.notifyDataSetChanged()
                            service.progressBar?.visibility = ProgressBar.GONE
                            DoWhateverAsync {
                                MyApp.postDao.insertAll(posts)
                            }
                        }
                    )
                }
            }
        }
    }

    private class DoWhateverAsync(private val handler: () -> Unit) : AsyncTask<Unit, Unit, Unit>() {
        init {
            execute()
        }

        override fun doInBackground(vararg params: Unit) {
            handler()
        }
    }

    class MyCallback<T>(s: MyService, private val handler: (response: Response<T>) -> Unit) :
        Callback<T> {
        private val serviceRef = WeakReference(s)

        override fun onResponse(call: Call<T>, response: Response<T>) {
            val service = serviceRef.get()
            if (response.isSuccessful) {
                if (response.body() != null) {
                    handler(response)
                } else {
                    Log.e("LocalService", "Response.body() == null")
                }
            }
            service?.msg?.showMessage(response.toString())
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            Log.e("Guide API", "Failed with", t)
        }
    }
}