package com.topjohnwu.magisk.util

import java.text.SimpleDateFormat
import java.util.*


val now get() = System.currentTimeMillis()

fun Long.toTime(format: SimpleDateFormat) = format.format(this).orEmpty()

private val locale get() = Locale.getDefault()
val timeFormatFull by lazy { SimpleDateFormat("YYYY/MM/DD_HH:mm:ss", locale) }
