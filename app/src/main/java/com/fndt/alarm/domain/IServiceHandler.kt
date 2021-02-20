package com.fndt.alarm.domain

import com.fndt.alarm.model.AlarmItem

interface IServiceHandler {
    fun startService(action: String, alarmItem: AlarmItem)
}
