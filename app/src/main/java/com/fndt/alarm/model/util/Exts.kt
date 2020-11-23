package com.fndt.alarm.model.util

import android.content.Context
import android.content.Intent
import com.fndt.alarm.R
import com.fndt.alarm.model.AlarmItem
import com.fndt.alarm.model.AlarmRepeat

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

fun Long.toExtendedTimeString(context: Context?): String {
    val days = this / (24 * 60 * 60 * 1000)
    val remainingDay = this - days * 24 * 60 * 60 * 1000
    val hours = remainingDay / (60 * 60 * 1000)
    val remainingMinutes = remainingDay - hours * 60 * 60 * 1000
    val minutes = remainingMinutes / (60 * 1000)
    val seconds = remainingMinutes % (60 * 1000) / 1000
    val daysString = when (days) {
        0L -> ""
        1L -> context?.resources?.getString(R.string.day_remaining, days.toString())
        else -> context?.resources?.getString(R.string.days_remaining, days.toString())
    }
    val hoursString = "${if (hours < 10) "0" else ""}$hours"
    val minutesString = "${if (minutes < 10) "0" else ""}$minutes"
    val secondsString = "${if (seconds < 10) "0" else ""}$seconds"
    return "$daysString $hoursString:$minutesString:$secondsString"
}

fun AlarmRepeat.toCalendarDayOfWeek(): Int = when (this) {
    AlarmRepeat.MONDAY -> 2
    AlarmRepeat.TUESDAY -> 3
    AlarmRepeat.WEDNESDAY -> 4
    AlarmRepeat.THURSDAY -> 5
    AlarmRepeat.FRIDAY -> 6
    AlarmRepeat.SATURDAY -> 7
    AlarmRepeat.SUNDAY -> 1
    else -> 0
}


fun List<AlarmRepeat>.toRepeatString(): CharSequence? {
    var finalString = ""
    var count = 0
    for (i in this) {
        when (i) {
            AlarmRepeat.MONDAY -> {
                finalString += "M"
                count++
            }
            AlarmRepeat.TUESDAY -> {
                if (count > 0) finalString += ", "
                finalString += "T"
                count++
            }
            AlarmRepeat.WEDNESDAY -> {
                if (count > 0) finalString += ", "
                finalString += "W"
                count++
            }
            AlarmRepeat.THURSDAY -> {
                if (count > 0) finalString += ", "
                finalString += "T"
                count++
            }
            AlarmRepeat.FRIDAY -> {
                if (count > 0) finalString += ", "
                finalString += "F"
                count++
            }
            AlarmRepeat.SATURDAY -> {
                if (count > 0) finalString += ", "
                finalString += "S"
                count++
            }
            AlarmRepeat.SUNDAY -> {
                if (count > 0) finalString += ", "
                finalString += "S"
                count++
            }
            AlarmRepeat.NONE -> {
                finalString += "No repeat"
            }
            AlarmRepeat.ONCE_DESTROY -> {
                finalString += "Snooze"
            }
        }
    }
    return finalString
//    if (this == (this or REPEAT_MONDAY)) {
//        finalString += "M"
//        count++
//    }
//    if (this == (this or REPEAT_TUESDAY)) {
//        if (count > 0) finalString += ", "
//        finalString += "T"
//        count++
//    }
//    if (this == (this or REPEAT_WEDNESDAY)) {
//        if (count > 0) finalString += ", "
//        finalString += "W"
//        count++
//    }
//    if (this == (this or REPEAT_THURSDAY)) {
//        if (count > 0) finalString += ", "
//        finalString += "T"
//        count++
//    }
//    if (this == (this or REPEAT_FRIDAY)) {
//        if (count > 0) finalString += ", "
//        finalString += "F"
//        count++
//    }
//    if (this == (this or REPEAT_SATURDAY)) {
//        if (count > 0) finalString += ", "
//        finalString += "S"
//        count++
//    }
//    if (this == (this or REPEAT_SUNDAY)) {
//        if (count > 0) finalString += ", "
//        finalString += "S"
//        count++
//    }
}