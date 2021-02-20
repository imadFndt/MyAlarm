package com.fndt.alarm.domain

import com.fndt.alarm.domain.dto.AlarmItem

interface IServiceHandler {
    fun startService(action: String, alarmItem: AlarmItem)
}
