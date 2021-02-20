package com.fndt.alarm.domain

import com.fndt.alarm.model.AlarmIntent

interface AlarmEventHandler {
    fun handleEvent(intent: AlarmIntent)
}
