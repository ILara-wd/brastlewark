package com.warriorsdev.brastlewark

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * Object reporting global events for the UI.
 */
object Events {
    /**
     * MutableLiveData that provides exceptions to the UI.
     */
    private val errorMutableLiveData = MutableLiveData<Exception>()
    /**
     * Immutable providing of the [errorMutableLiveData] class.
     */
    val errorLiveData = errorMutableLiveData as LiveData<Exception>

    /**
     * Reports an exception to any observers.
     */
    fun reportException(e: Exception?) = errorMutableLiveData.postValue(e)
}