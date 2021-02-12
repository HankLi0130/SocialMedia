package dev.hankli.iamstar.utils

import java.util.*

fun getClearedCal(): Calendar {
    val cal = Calendar.getInstance(TimeZone.getDefault())
    cal.clear()
    return cal
}