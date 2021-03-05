package com.example.posts

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_item.*

class MainActivity : AppCompatActivity() {

    private val msg = Message()
    private var binder : MyService.MyBinder? = null
    private val conn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binder = service as MyService.MyBinder
            binder?.apply {
                init(recyclerView,progressBar,msg)
                takeAllPosts()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            binder = null
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Log.e("MainActivity", "onCreate")
        if (savedInstanceState != null) {
            progressBar.visibility = ProgressBar.GONE
        }
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
        //--------------------
        bindService(
            Intent(this,
                MyService::class.java,
            ),
            conn,
            Context.BIND_AUTO_CREATE,
        )
    }

    fun onSend(view : View) {
        binder?.add(
            Post(
                binder!!.nextId(),
                2032001,
                editTextTitle.text.toString(),
                textInputEditText.text.toString()
        ))
    }

    fun onRefresh(view : View) {
        binder?.refresh()
    }

    fun onDown(view : View) {
        binder?.toEnd()
    }

    override fun onDestroy() {
        binder?.reset()
        unbindService(conn)
        super.onDestroy()
    }


    inner class Message{
        fun showMessage(text: String) {
            Toast.makeText(this@MainActivity, text, Toast.LENGTH_LONG).show()
        }
    }
}