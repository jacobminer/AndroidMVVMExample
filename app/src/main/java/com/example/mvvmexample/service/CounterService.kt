package com.example.mvvmexample.service

import com.example.mvvmexample.model.CounterModel
import kotlin.jvm.Throws

/**
 * MVVMExample
 * Created by jake on 19/04/21, 3:55 PM
 */
// an example service, mimicking retrofit or a similar service
interface CounterService {
    @Throws
    suspend fun fetchCount(): CounterModel

    @Throws
    suspend fun incrementCount()
}