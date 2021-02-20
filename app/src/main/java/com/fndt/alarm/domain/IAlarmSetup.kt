package com.fndt.alarm.domain

import com.fndt.alarm.model.NextAlarmItem

interface IAlarmSetup {
    fun setAlarm(item: NextAlarmItem)
    fun cancelAlarm()
}
