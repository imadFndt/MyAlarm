package com.fndt.alarm.model

import android.content.Intent

interface AlarmEventHandler {
    fun handleEvent(intent: Intent)
}
