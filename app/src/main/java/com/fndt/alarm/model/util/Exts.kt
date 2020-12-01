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


fun List<AlarmRepeat>.toRepeatString(context: Context): CharSequence? {
    var finalString = ""
    for ((count, i) in this.withIndex()) {
        if (count > 0) finalString += ", "
        finalString += context.resources.getString(i.abbreviationText)
    }
    return finalString
}

fun AlarmItem.snoozed() = AlarmItem(
    time = this.time + FIVE_MINUTES,
    name = this.name,
    true,
    mutableListOf(AlarmRepeat.ONCE_DESTROY),
    melody = this.melody
)
