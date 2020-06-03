package com.warriorsdev.brastlewark.utils

import android.text.Editable

/**
 * Returns an Editable nullable object as an Integer.
 */
fun Editable?.asInt() = try {
    this?.toString()?.toInt() ?: 0
} catch (e: Exception) {
    0
}