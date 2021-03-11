package com.fndt.alarm.di

import com.fndt.alarm.presentation.AlarmReceiver
import com.fndt.alarm.presentation.AlarmService
import com.fndt.alarm.presentation.viewmodels.AlarmViewModel
import com.fndt.alarm.presentation.viewmodels.MainActivityViewModel
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Singleton
@Component(modules = [ViewModelModule::class])
interface AlarmComponent {
    fun getViewModelFactory(): MainActivityViewModel.Factory
    fun getAlarmViewModelFactory(): AlarmViewModel.Factory
    fun inject(alarmService: AlarmService)
    fun inject(alarmReceiver: AlarmReceiver)
}