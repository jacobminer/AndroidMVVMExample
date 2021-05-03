package com.example.mvvmexample.cache

import android.os.Parcelable
import kotlinx.coroutines.delay

// an in-memory cache service
class InMemoryCacheService: CacheService {
    // some cached data
    private val cachedData = hashMapOf<String, Parcelable>()

    // read from the cache
    override suspend fun readFromCache(key: String, cacheMode: CacheMode): CacheResult<Parcelable>? {
        // add a short delay to emulate a slow disk
        delay(100)

        // check if we have cached data, if not, no cached result
        val cached = cachedData[key] ?: return null
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
    override fun updateCache(key: String, value: Parcelable) {
        cachedData[key] = value
    }

    // clear the cache entirely
    override fun clearCache() {
        cachedData.clear()
    }

    // remove a single value from the cache
    override fun removeFromCache(key: String) {
        cachedData.remove(key)
    }
}