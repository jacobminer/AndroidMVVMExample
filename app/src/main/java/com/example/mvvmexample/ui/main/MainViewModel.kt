package com.example.mvvmexample.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mvvmexample.cache.CacheMode
import com.example.mvvmexample.repository.CounterRepository
import com.example.mvvmexample.ui.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * MVVMExample
 * Created by jake on 19/04/21, 3:21 PM
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    // injected via hilt
    private val counterRepository: CounterRepository
): ViewModel() {
    // this maps the counterState flow to a liveData for view consumption
    // as soon as counterState changes, mapLatest will be called, and the result will be
    // sent via the exampleText LiveData
    val exampleText = counterRepository.counterState.mapLatest {
        // as our views become more complicated, we can start subclassing viewstate
            // for example, it might only be useful to have this generic ViewState apply to a whole screen
            // and have each view inside the screen use it's own viewState
        when (it) {
            ViewState.Empty -> "No Data"
            is ViewState.Error -> "Error: ${it.throwable.message}"
            ViewState.Loading -> "Loading..."
            is ViewState.Success -> "Counter = ${it.data.counter}"
        }
    }.asLiveData()

    // calls refresh, allowing the cached value to be displayed
    fun refreshTapped() = viewModelScope.launch {
        counterRepository.getRemoteCounter(CacheMode.CacheAndUpdate)
    }

    // increments the counter
    fun incrementTapped() = viewModelScope.launch {
        counterRepository.incrementCounter()
    }
}