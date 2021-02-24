package com.example.posts

import retrofit2.Call
import retrofit2.http.*

public interface PostsService {
    @GET("posts")
    fun get() : Call<List<Post>>

    @DELETE("posts/{id}")
    fun delete(@Path("id") id : Long): Call<Unit>

    @POST("posts")
    fun post(@Body post : CreatedPost) : Call<Post>
}