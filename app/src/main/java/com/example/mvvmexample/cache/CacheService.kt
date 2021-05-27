package com.example.mvvmexample.cache

import android.os.Parcelable

/**
 * MVVMExample
 * Created by jake on 19/04/21, 4:30 PM
 */
// a service implementation of a simple cache
interface CacheService {
    suspend fun <T: Parcelable> readFromCache(key: CacheKey<T>, cacheMode: CacheMode): CacheResult<T>?
    fun <T: Parcelable> updateCache(key: CacheKey<T>, value: T)
    fun <T: Parcelable> removeFromCache(key: CacheKey<T>)
    fun clearCache()
}

