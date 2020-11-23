package com.fndt.alarm.model

import com.fndt.alarm.model.util.*

enum class AlarmRepeat(val byteValue: Byte) {
    MONDAY(REPEAT_MONDAY),
    TUESDAY(REPEAT_TUESDAY),
    WEDNESDAY(REPEAT_WEDNESDAY),
    THURSDAY(REPEAT_THURSDAY),
    FRIDAY(REPEAT_FRIDAY),
    SATURDAY(REPEAT_SATURDAY),
    SUNDAY(REPEAT_SUNDAY),
    NONE(REPEAT_NONE),
    ONCE_DESTROY(REPEAT_ONCE_DESTROY)
}