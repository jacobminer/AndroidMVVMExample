package com.example.mvvmexample.service

import com.example.mvvmexample.model.User
import retrofit2.http.GET

/**
 * MVVMExample
 * Created by jake on 30/04/21, 3:18 PM
 */
interface UsersService {
    @GET("users")
    suspend fun fetchUsers(): List<User>
}