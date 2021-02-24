package com.example.posts

import android.annotation.SuppressLint
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
import java.lang.Error

class MyService : Service() {

    private var fail: MainActivity.Failure? = null
    private var recyclerView: RecyclerView? = null
    private var progressBar: ProgressBar?   = null

    val adapter = PostAdapter(mutableListOf()) {
        it.deleted = true

        DoWhateverAsync { MyApp.postDao.update(it) }


        MyApp.service.delete(it.id).enqueue(MyCallback { Log.e("LocalService", "Post deleted !") })
    }

    override fun onBind(intent: Intent): IBinder {
        return MyBinder()
    }

    inner class MyBinder : Binder() {
        fun add(post: Post) {
            DoWhateverAsync { MyApp.postDao.update(post) }

            adapter.items.add(post)
            adapter.notifyItemChanged(adapter.items.size -1)
            recyclerView?.scrollToPosition(adapter.items.size-1)

            MyApp.service.post(CreatedPost(post.userId,post.title,post.body))
        }

        fun nextId() : Long = (adapter.items.size +1).toLong()

        fun reset() {
            fail = null
            progressBar = null
            recyclerView = null
        }

        fun refresh() {
            MyApp.service.get().enqueue(MyCallback {
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

        fun init(recView: RecyclerView, bar: ProgressBar, f: MainActivity.Failure) {
            fail = f
            progressBar = bar
            recyclerView = recView
            recyclerView!!.adapter = adapter
        }

        fun takeAllPosts() {
            if(adapter.items.isEmpty()) TakeAllPostAsync().execute()
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class TakeAllPostAsync() : AsyncTask<Unit, Unit, List<Post>>() {

        override fun doInBackground(vararg params: Unit?): List<Post> {
            return MyApp.postDao.getAll()
        }

        override fun onPostExecute(result: List<Post>) {
            if(result.isNotEmpty()) {
                progressBar?.visibility ?: ProgressBar.INVISIBLE
                adapter.items.addAll(result)
                adapter.notifyDataSetChanged()
            } else {
                MyApp.service.get().enqueue(MyCallback<List<Post>>() {
                    Log.e("LocalService", "Get all posts !")
                    val posts = it.body()!!
                    adapter.items.addAll(posts)
                    adapter.notifyDataSetChanged()
                    progressBar?.visibility = ProgressBar.INVISIBLE
                    DoWhateverAsync {
                        MyApp.postDao.insertAll(posts)
                    }
                })

            }
            super.onPostExecute(result)
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


    inner class MyCallback<T>(
        private val handler: (response: Response<T>) -> Unit
    ) :
        Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            if (response.isSuccessful) {
                if (response.body() != null) {
                    handler(response)
                } else {
                    Log.e("LocalService", "Response.body() == null")
                }
            } else {
                fail?.showFailure(response.toString())
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            Log.e("Guide API", "Failed with", t)
        }
    }
}