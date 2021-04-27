package com.example.mvvmexample.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * MVVMExample
 * Created by jake on 19/04/21, 5:00 PM
 */
// a model that could be coming back from the server
// used parcelable here, but this could easily use JSON parsing instead, as JSON is already parcelable
@Parcelize
data class CounterModel(
    var counter: Int
): Parcelable