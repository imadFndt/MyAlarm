package com.fndt.alarm.model.util

import com.fndt.alarm.model.AlarmReceiver
import com.fndt.alarm.model.AlarmService
import com.fndt.alarm.view.AlarmViewModel
import com.fndt.alarm.view.main.MainActivityViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AlarmModule::class])
interface AlarmComponent {
    fun getViewModelFactory(): MainActivityViewModel.Factory
    fun getAlarmViewModelFactory(): AlarmViewModel.Factory
    fun inject(alarmService: AlarmService)
    fun inject(alarmReceiver: AlarmReceiver)
}