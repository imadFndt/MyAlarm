package com.fndt.alarm.model.util

import android.content.Intent
import com.fndt.alarm.model.AlarmItem
import kotlin.experimental.or

fun AlarmItem.toIntent(action: String) = Intent().apply {
    setAction(action)
    putExtra(ITEM_EXTRA, this@toIntent)
}

fun Intent.getAlarmItem() = this.getSerializableExtra(ITEM_EXTRA) as AlarmItem?

fun Byte.toDayString(): CharSequence? = when (this) {
    REPEAT_MONDAY -> "M"
    REPEAT_TUESDAY -> "T"
    REPEAT_WEDNESDAY -> "W"
    REPEAT_THURSDAY -> "T"
    REPEAT_FRIDAY -> "F"
    REPEAT_SATURDAY -> "S"
    REPEAT_SUNDAY -> "S"
    else -> ""
}

fun Long.toTimeString(): String {
    val hoursString = "${this / 60}"
    val minutes = this % 60
    val minutesString = "${if (minutes < 10) "0" else ""}$minutes"
    return "$hoursString:$minutesString"
}

fun Long.toExtendedTimeString(): String {
    val days = this / (24 * 60 * 60 * 1000)
    val remainingDay = this - days * 24 * 60 * 60 * 1000
    val hours = remainingDay / (60 * 60 * 1000)
    val remainingMinutes = remainingDay - hours * 60 * 60 * 1000
    val minutes = remainingMinutes / (60 * 1000)
    val seconds = remainingMinutes % (60 * 1000) / 1000
    val daysString = if (days == 0L) "" else "$days d."
    val hoursString = "${if (hours < 10) "0" else ""}$hours"
    val minutesString = "${if (minutes < 10) "0" else ""}$minutes"
    val secondsString = "${if (seconds < 10) "0" else ""}$seconds"
    return "$daysString $hoursString:$minutesString:$secondsString"
}

fun Byte.toRepeatString(): CharSequence? {
    var finalString = ""
    var count = 0
    if (this == (this or REPEAT_MONDAY)) {
        finalString += "M"
        count++
    }
    if (this == (this or REPEAT_TUESDAY)) {
        if (count > 0) finalString += ", "
        finalString += "T"
        count++
    }
    if (this == (this or REPEAT_WEDNESDAY)) {
        if (count > 0) finalString += ", "
        finalString += "W"
        count++
    }
    if (this == (this or REPEAT_THURSDAY)) {
        if (count > 0) finalString += ", "
        finalString += "T"
        count++
    }
    if (this == (this or REPEAT_FRIDAY)) {
        if (count > 0) finalString += ", "
        finalString += "F"
        count++
    }
    if (this == (this or REPEAT_SATURDAY)) {
        if (count > 0) finalString += ", "
        finalString += "S"
        count++
    }
    if (this == (this or REPEAT_SUNDAY)) {
        if (count > 0) finalString += ", "
        finalString += "S"
        count++
    }
    if (count == 0) finalString = "No repeat"
    return finalString
}