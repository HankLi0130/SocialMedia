package dev.hankli.iamstar.utils.ext

import java.text.DateFormat.DATE_FIELD
import java.text.SimpleDateFormat
import java.util.*

fun Date.display() = SimpleDateFormat.getDateInstance(DATE_FIELD).format(this)
