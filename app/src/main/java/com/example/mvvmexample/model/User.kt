package com.example.mvvmexample.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * MVVMExample
 * Created by jake on 30/04/21, 3:16 PM
 */
@Serializable
data class User(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String)