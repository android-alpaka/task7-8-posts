package com.example.posts

import androidx.room.*

@Dao
public interface PostDao {
    @Query("SELECT * FROM post")
    fun getAll() : List<Post>

    @Insert
    fun insertAll(posts : List<Post>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(post: Post)

    @Update
    fun update(post: Post)

    @Query("DELETE FROM post")
    fun deleteAll()
}