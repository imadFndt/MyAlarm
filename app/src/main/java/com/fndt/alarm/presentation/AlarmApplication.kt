package com.fndt.alarm.presentation

import android.app.Application
import com.fndt.alarm.di.*
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
class AlarmApplication : Application() {
    val component: AlarmComponent by lazy {
        DaggerAlarmComponent.builder()
            .appContextModule(AppContextModule(this))
            .dataModule(DataModule())
            .alarmUtilsModule(AlarmUtilsModule())
            .viewModelModule(ViewModelModule())
            .alarmControlModule(AlarmControlModule())
            .build()
    }
}