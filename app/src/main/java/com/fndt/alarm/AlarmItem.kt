package com.fndt.alarm

import android.icu.util.Calendar
import java.time.DayOfWeek

data class AlarmItem(
    var time: Calendar,
    var name: String,
    var repeatPeriod: Array<DayOfWeek>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AlarmItem

        if (time != other.time) return false
        if (name != other.name) return false
        if (!repeatPeriod.contentEquals(other.repeatPeriod)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = time.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + repeatPeriod.contentHashCode()
        return result
    }
}