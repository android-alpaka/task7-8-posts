package com.example.posts

import android.app.Application
import androidx.room.Room
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory.create

class MyApp : Application() {

    companion object {
        lateinit var service: PostsService
        lateinit var postDao: PostDao
    }

    override fun onCreate() {
        super.onCreate()

        postDao = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "DatabasePosts").build().postDao()

        val retrofit = Retrofit
            .Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(create())
            .build()

        service = retrofit.create(PostsService::class.java)
    }
}