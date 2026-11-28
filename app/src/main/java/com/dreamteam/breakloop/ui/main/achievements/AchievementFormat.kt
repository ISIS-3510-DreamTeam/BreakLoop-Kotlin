package com.dreamteam.breakloop.ui.main.achievements

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")

/** Unlock date in the phone's time zone, same format as the Flutter app. */
fun formatUnlockDate(millis: Long?): String {
    if (millis == null) return ""
    return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormat)
}