package com.example.mvvmexample.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * MVVMExample
 * Created by jake on 30/04/21, 3:16 PM
 */
@Parcelize
@Serializable
data class User(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String): Parcelable

@Parcelize
data class UserList(val users: List<User>): Parcelable