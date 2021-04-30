package com.example.mvvmexample.service

import com.example.mvvmexample.model.Post
import retrofit2.http.GET

/**
 * MVVMExample
 * Created by jake on 30/04/21, 3:15 PM
 */
interface PostsService {
    @GET("posts")
    suspend fun fetchPosts(): List<Post>
}