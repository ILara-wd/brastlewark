package com.warriorsdev.brastlewark.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * Returns a LiveData, reporting the value of the block() function
 * @param T the object that has to be reported by the LiveData.
 * @param block Anonymous function that will report the LiveData value.
 */
fun <T> forLiveData(block: () -> T) = MutableLiveData<T>().apply {
    runOnWorkerThread {
        postValue(block())
    }
} as LiveData<T>

/**
 * Maps the result of an anonymous function to another [MutableLiveData] that reports the same value.
 */
fun <T> MutableLiveData<T>.map(block: () -> T) = runOnWorkerThread {
    postValue(block())
}