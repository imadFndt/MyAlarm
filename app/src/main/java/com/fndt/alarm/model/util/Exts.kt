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