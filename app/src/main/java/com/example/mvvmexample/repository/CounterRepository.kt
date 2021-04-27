package com.example.mvvmexample.repository

import android.os.Parcelable
import com.example.mvvmexample.service.CounterService
import com.example.mvvmexample.cache.CacheMode
import com.example.mvvmexample.cache.CacheService
import com.example.mvvmexample.model.CounterModel
import com.example.mvvmexample.ui.ViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

/**
 * MVVMExample
 * Created by jake on 19/04/21, 3:21 PM
 */
// an example of a repository for interacting with a remote service, and implementing a simple cache
class CounterRepository(
    private val counterService: CounterService,
    private val cacheService: CacheService
) {
    // the private mutable version of the counter state
    private val mutableCounterState = MutableStateFlow<ViewState<CounterModel>>(ViewState.Empty)

    // the read-only version of the counter state, which should be monitored by a viewModel
    val counterState = mutableCounterState.asStateFlow()

    init {
        // fetch the remote counter as soon as you're created, launching on an IO coroutine
        GlobalScope.launch(Dispatchers.IO) {
            getRemoteCounter(CacheMode.CacheAndUpdate)
        }
    }

    // simulates fetching the counter from a remote source
    suspend fun getRemoteCounter(cacheMode: CacheMode) {
        // send loading
        mutableCounterState.emit(ViewState.Loading)

        // try to read from cache
        val cacheResult = cacheService.readFromCache(CounterKey, cacheMode)
        if (cacheResult != null) {
            // send cached value
            mutableCounterState.emit(ViewState.Success(cacheResult.result as CounterModel))
            if (cacheResult.shouldExit) {
                return
            }
        }

        try {
            // get count from remote source
            val count = counterService.fetchCount()
            // emit new value
            mutableCounterState.emit(ViewState.Success(count))
            // update cache
            cacheService.updateCache(CounterKey, count as Parcelable)
        } catch (e: Exception) {
            mutableCounterState.emit(ViewState.Error(e))
        }
    }

    // simulates writing to a server, and causes a refresh internally
    suspend fun incrementCounter() {
        mutableCounterState.emit(ViewState.Loading)
        try {
            // update remote source
            counterService.incrementCount()
            // invalidate the cache
            cacheService.clearCache()
            // re-fetch the current data
            getRemoteCounter(CacheMode.CacheAndUpdate)
        } catch (e: Exception) {
            mutableCounterState.emit(ViewState.Error(e))
        }
    }

    companion object {
        private const val CounterKey = "counter"
    }
}