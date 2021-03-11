package com.fndt.alarm.domain

import com.fndt.alarm.domain.dto.AlarmItem

interface ServiceHandler {
    fun startService(action: String, alarmItem: AlarmItem)
}
