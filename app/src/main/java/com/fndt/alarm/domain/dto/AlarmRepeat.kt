package com.fndt.alarm.domain.dto

import com.fndt.alarm.R
import com.fndt.alarm.domain.utils.*

enum class AlarmRepeat(val byteValue: Byte, val text: Int, val abbreviationText: Int) {
    MONDAY(REPEAT_MONDAY, R.string.monday, R.string.monday_short),
    TUESDAY(REPEAT_TUESDAY, R.string.tuesday, R.string.tuesday_short),
    WEDNESDAY(REPEAT_WEDNESDAY, R.string.wednesday, R.string.wednesday_short),
    THURSDAY(REPEAT_THURSDAY, R.string.thursday, R.string.thursday_short),
    FRIDAY(REPEAT_FRIDAY, R.string.friday, R.string.friday_short),
    SATURDAY(REPEAT_SATURDAY, R.string.saturday, R.string.saturday_short),
    SUNDAY(REPEAT_SUNDAY, R.string.sunday, R.string.sunday_short),
    NONE(REPEAT_NONE, R.string.non_repeatable, R.string.non_repeatable),
    ONCE_DESTROY(REPEAT_ONCE_DESTROY, R.string.snooze, R.string.snooze)
}