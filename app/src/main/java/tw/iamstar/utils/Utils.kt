package tw.iamstar.utils

import tw.iamstar.utils.ext.timeToZero
import java.util.*

fun getCal(): Calendar = Calendar.getInstance(TimeZone.getDefault())

fun getClearedCal(): Calendar {
    val cal = getCal()
    cal.clear()
    return cal
}

fun getToday(): Calendar {
    val cal = getCal()
    return cal.timeToZero()
}