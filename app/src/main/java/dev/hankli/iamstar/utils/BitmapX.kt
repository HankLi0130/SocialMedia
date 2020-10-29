package dev.hankli.iamstar.utils

import android.graphics.Bitmap
import kotlin.math.min
import kotlin.math.roundToInt

fun Bitmap.scale(maxSize: Int, filter: Boolean): Bitmap {
    val ratio = min(maxSize / this.width.toFloat(), maxSize / this.height.toFloat())
    val newWidth = (ratio * this.width).roundToInt()
    val newHeight = (ratio * this.height).roundToInt()
    return Bitmap.createScaledBitmap(this, newWidth, newHeight, filter)
}