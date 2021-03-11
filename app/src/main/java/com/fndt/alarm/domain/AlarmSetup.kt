package com.fndt.alarm.domain

import com.fndt.alarm.domain.dto.NextAlarmItem

interface AlarmSetup {
    fun setAlarm(item: NextAlarmItem)
    fun cancelAlarm()
}
