package com.example.mvvmexample.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapNotNull

/**
 * MVVMExample
 * Created by jake on 14/05/21, 12:52 PM
 */
sealed class ThrowableFlow<out T: Any> {
    data class Error(val throwable: Throwable): ThrowableFlow<Nothing>()
    data class Success<out T: Any>(val data: T): ThrowableFlow<T>()

    companion object {
        fun error(throwable: Throwable) = Error(throwable)
        fun <T: Any> success(data: T) = Success(data)
        suspend fun <T: Any> from(function: suspend () -> T, onError: (suspend (Throwable) -> Unit)?): ThrowableFlow<T> {
            return try {
                success(function.invoke())
            } catch(e: Exception) {
                onError?.invoke(e)
                error(e)
            }
        }
    }
}

@Suppress("FunctionName")
fun <T : Any> MutableStateThrowableFlow(value: T): MutableStateThrowableFlow<T> = MutableStateFlow(ThrowableFlow.success(value))
fun <T: Any> StateThrowableFlow<T>.catchError(error: (Throwable) -> Unit): Flow<T> {
    return mapNotNull { result ->
        when (result) {
            is ThrowableFlow.Error -> {
                error(result.throwable)
                null
            }
            is ThrowableFlow.Success -> result.data
        }
    }
}

suspend fun <T: Any> MutableStateThrowableFlow<T>.emitThrowable(throwable: Throwable) {
    emit(ThrowableFlow.error(throwable))
}
suspend fun <T: Any> MutableStateThrowableFlow<T>.emitData(data: T) {
    emit(ThrowableFlow.success(data))
}
suspend fun <T: Any> MutableStateThrowableFlow<T>.emitFrom(onError: (suspend (Throwable) -> Unit)? = null, function: suspend () -> T) {
    emit(ThrowableFlow.from(function, onError))
}

typealias StateThrowableFlow<T> = StateFlow<ThrowableFlow<T>>
typealias MutableStateThrowableFlow<T> = MutableStateFlow<ThrowableFlow<T>>