package com.fndt.alarm.model.util

import android.app.Application

class AlarmApplication : Application() {
    val component: AlarmComponent =
        DaggerAlarmComponent.builder().alarmModule(AlarmModule(this)).build()
}