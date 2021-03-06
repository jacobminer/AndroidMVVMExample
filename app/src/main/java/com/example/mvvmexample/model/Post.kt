package com.example.mvvmexample.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * MVVMExample
 * Created by jake on 30/04/21, 3:19 PM
 */
@Serializable
@Parcelize
data class Post(
    @SerialName("userId") val userId: Int,
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String,
    @SerialName("body") val body: String
): Parcelable

@Parcelize
data class PostList(val posts: List<Post>): Parcelable