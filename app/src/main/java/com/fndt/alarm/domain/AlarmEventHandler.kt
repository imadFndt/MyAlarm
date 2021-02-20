package com.fndt.alarm.domain

import com.fndt.alarm.domain.dto.AlarmIntent

interface AlarmEventHandler {
    fun handleEvent(intent: AlarmIntent)
}
