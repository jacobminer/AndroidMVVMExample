package com.example.mvvmexample.ui

/**
 * MVVMExample
 * Created by jake on 30/04/21, 3:46 PM
 */
sealed class ContentLoadViewState {
    data class Error(val throwable: Throwable): ContentLoadViewState()
    object Loading: ContentLoadViewState()
    object Success: ContentLoadViewState()

    fun combined(otherState: ContentLoadViewState?): ContentLoadViewState {
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