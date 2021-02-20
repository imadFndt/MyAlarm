package com.fndt.alarm.di

import com.fndt.alarm.presentation.AlarmReceiver
import com.fndt.alarm.presentation.AlarmService
import com.fndt.alarm.presentation.viewmodels.AlarmViewModel
import com.fndt.alarm.presentation.viewmodels.MainActivityViewModel
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