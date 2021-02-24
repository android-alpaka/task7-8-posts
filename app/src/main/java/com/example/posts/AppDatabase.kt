package com.example.posts

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Post::class], version = 1)
public abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao() : PostDao
}