package com.example.mvvmexample.cache

import android.os.Parcelable

/**
 * MVVMExample
 * Created by jake on 2021-05-27, 11:13 a.m.
 */
data class CacheKey<T: Parcelable>(val key: String)