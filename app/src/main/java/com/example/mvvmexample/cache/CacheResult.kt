package com.example.mvvmexample.cache

import android.os.Parcelable

/**
 * MVVMExample
 * Created by jake on 19/04/21, 4:19 PM
 */
// a wrapper for a cached result. probably not necessary, but makes cache management cleaner
data class CacheResult<T: Parcelable>(val result: T, val shouldExit: Boolean)