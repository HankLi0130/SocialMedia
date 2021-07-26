package app.hankdev.utils.ext

import java.text.DateFormat.DATE_FIELD
import java.text.SimpleDateFormat
import java.util.*

fun Date.toDateString() = SimpleDateFormat.getDateInstance(DATE_FIELD).format(this)

fun Date.toTimeString() = SimpleDateFormat.getTimeInstance(DATE_FIELD).format(this)

fun Date.toDateTimeString() =
    SimpleDateFormat.getDateTimeInstance(DATE_FIELD, DATE_FIELD).format(this)
