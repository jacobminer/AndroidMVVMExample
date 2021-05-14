package com.example.mvvmexample.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * MVVMExample
 * Created by jake on 14/05/21, 1:19 PM
 */
fun <T> MutableLiveData<T>.asLiveData(): LiveData<T> = this