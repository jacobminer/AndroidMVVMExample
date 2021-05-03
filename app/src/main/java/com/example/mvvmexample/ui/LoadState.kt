package com.example.mvvmexample.ui

/**
 * MVVMExample
 * Created by jake on 30/04/21, 3:46 PM
 */
sealed class LoadState {
    data class Error(val throwable: Throwable): LoadState()
    object Loading: LoadState()
    object Success: LoadState()

    fun combined(otherState: LoadState?): LoadState {
        if (otherState == null) return this
        return when {
            this is Error || otherState is Error -> {
                val error = (this as? Error)?.throwable ?: (otherState as? Error)?.throwable!!
                Error(error)
            }
            this is Loading || otherState is Loading -> {
                Loading
            }
            this is Success && otherState is Success -> {
                Success
            }
            else -> {
                // should never reach here
                throw IllegalArgumentException()
            }
        }
    }
}