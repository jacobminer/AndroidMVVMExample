package com.example.mvvmexample.cache

import android.os.Parcelable
import kotlinx.coroutines.delay

// an in-memory cache service
class InMemoryCacheService: CacheService {
    // some cached data
    private val cachedData = hashMapOf<String, Parcelable>()

    // read from the cache
    override suspend fun <T: Parcelable> readFromCache(key: CacheKey<T>, cacheMode: CacheMode): CacheResult<T>? {
        // add a short delay to emulate a slow disk
        delay(100)

        // check if we have cached data, if not, no cached result
        val cached = cachedData[key.key] as? T ?: return null
        // build a cacheResult based on the cache mode (or cache time)
        return when (cacheMode) {
            CacheMode.CacheAndUpdate -> {
                CacheResult(cached, shouldExit = false)
            }
            CacheMode.CacheOnly -> {
                CacheResult(cached, shouldExit = true)
            }
            CacheMode.NoCache -> {
                null
            }
        }
    }

    // update the cached data
    override fun <T: Parcelable> updateCache(key: CacheKey<T>, value: T) {
        cachedData[key.key] = value
    }

    // clear the cache entirely
    override fun clearCache() {
        cachedData.clear()
    }

    // remove a single value from the cache
    override fun <T: Parcelable> removeFromCache(key: CacheKey<T>) {
        cachedData.remove(key.key)
    }
}