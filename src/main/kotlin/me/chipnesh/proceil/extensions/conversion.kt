package me.chipnesh.proceil.extensions

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*


var dateFormatter = DateTimeFormatter
    .ofLocalizedDateTime(FormatStyle.SHORT)
    .withLocale(Locale.getDefault())
    .withZone(ZoneId.systemDefault())

fun Instant?.format(): String = dateFormatter.format(this)
