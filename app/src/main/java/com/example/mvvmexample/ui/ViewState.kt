package com.example.mvvmexample.ui

/**
 * MVVMExample
 * Created by jake on 19/04/21, 4:08 PM
 */
// a generic high-level view state with a bunch of states that should be handled
sealed class ViewState<out T: Any> {
    object Empty: ViewState<Nothing>()
    data class Error(val throwable: Throwable): ViewState<Nothing>()
    object Loading: ViewState<Nothing>()
    data class Success<out T: Any>(val data: T): ViewState<T>()
}