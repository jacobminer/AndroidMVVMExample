package com.example.mvvmexample.service

import com.example.mvvmexample.model.CounterModel
import kotlinx.coroutines.delay

/**
 * MVVMExample
 * Created by jake on 19/04/21, 3:55 PM
 */
class MockCounterService: CounterService {
    // emulate some server state
    private val counterModel = CounterModel(counter = 0)

    // emulate fetching counter from remote
    override suspend fun fetchCount(): CounterModel {
        // emulate a delay when fetching
        delay(1000)
        return counterModel
    }

    // emulate posting an edited counter to remote
    override suspend fun incrementCount() {
        // emulate a delay when posting
        delay(500)
        counterModel.counter++
    }
}