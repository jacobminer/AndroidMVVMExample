package com.example.mvvmexample.cache

import android.os.Parcelable

/**
 * MVVMExample
 * Created by jake on 19/04/21, 4:30 PM
 */
// a service implementation of a simple cache
interface CacheService {
    suspend fun readFromCache(key: String, cacheMode: CacheMode): CacheResult<Parcelable>?
    fun updateCache(key: String, value: Parcelable)
    fun removeFromCache(key: String)
    fun clearCache()
}

