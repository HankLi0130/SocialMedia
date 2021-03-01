package dev.hankli.iamstar.utils.ext

import java.util.*

fun Calendar.toDateString() = this.time.toDateString()

fun Calendar.toTimeString() = this.time.toTimeString()

fun Calendar.timeToZero(): Calendar {
    this.set(Calendar.HOUR_OF_DAY, 0)
    this.set(Calendar.MINUTE, 0)
    this.set(Calendar.SECOND, 0)
    return this
}