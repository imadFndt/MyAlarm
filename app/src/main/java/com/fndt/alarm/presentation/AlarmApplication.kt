package com.fndt.alarm.presentation

import android.app.Application
import com.fndt.alarm.di.AlarmComponent
import com.fndt.alarm.di.AlarmModule
import com.fndt.alarm.di.DaggerAlarmComponent


class AlarmApplication : Application() {
    val component: AlarmComponent by lazy {
        DaggerAlarmComponent.builder().alarmModule(AlarmModule(this)).build()
    }
}