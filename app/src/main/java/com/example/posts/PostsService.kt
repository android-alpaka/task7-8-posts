package com.example.posts

import retrofit2.Call
import retrofit2.http.*

interface PostsService {
    @GET("posts")
    fun get() : Call<List<Post>>

    @DELETE("posts/{id}")
    fun delete(@Path("id") id : Long): Call<Unit>

    @POST("posts")
    @Headers("Content-type:application/json;charset=UTF-8 ")
    fun post(@Body post : CreatedPost) : Call<Post>
}