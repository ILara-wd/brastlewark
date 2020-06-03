package com.warriorsdev.brastlewark.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.warriorsdev.brastlewark.Events

/**
 * Provides observation to a [LiveData] that reports a [Pair] of a given T object
 * and an Exception. A default function is passed as the exception as the value.
 *
 * @param owner The lifecycle owner observing the LiveData.
 * @param onComplete callback reporting the completion result of the observation.
 * @param onError callback reporting the error exception.
 */
fun <T> LiveData<Pair<T?, Exception?>>.observeWith(
    owner: LifecycleOwner,
    onComplete: (result: T) -> Unit,
    onError: (e: Exception?) -> Unit = { Events.reportException(it) }
) = this.observe(owner, Observer {
    it?.first?.let(onComplete) ?: run {
        onError(it.second)
    }
})