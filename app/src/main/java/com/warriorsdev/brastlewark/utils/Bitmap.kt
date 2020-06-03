package com.warriorsdev.brastlewark.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint

/**
 * Returns a scaled Bitmap with the new dimensions.
 * @param newWidth The resized bitmap width (in pixels).
 * @param newHeight The resized bitmap height (in pixels).
 */
fun Bitmap.resize(newWidth: Int, newHeight: Int): Bitmap {
    val oldWidth = width
    val oldHeight = height

    val widthScale = newWidth / oldWidth.toFloat()
    val heightScale = newHeight / oldHeight.toFloat()

    val scaleMatrix = Matrix().apply {
        postScale(widthScale, heightScale)
    }

    return Bitmap.createBitmap(this, 0, 0, oldWidth, oldHeight, scaleMatrix, false)
}

/**
 * Creates a bitmap with a solid color as the background (chosen randomly by the system) and
 * a given text in white.
 *
 * @param text The text that will be used to the bitmap.
 */
fun createBitmap(text: String): Bitmap {
    val canvasText = text.getNameCapitalLetters()
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 30f
        color = Color.WHITE
        textAlign = Paint.Align.LEFT
    }
    val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    Canvas(bitmap).apply {
        drawColor(arrayOf(Color.RED, Color.DKGRAY, Color.BLUE, Color.GRAY).random())
        drawText(canvasText, 30f, 60f, paint)
    }
    return bitmap
}
