package com.fndt.alarm.di

import com.fndt.alarm.domain.AlarmDataUseCase
import com.fndt.alarm.presentation.viewmodels.AlarmViewModel
import com.fndt.alarm.presentation.viewmodels.MainActivityViewModel
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Module(includes = [AlarmControlModule::class])
class ViewModelModule {
    @Provides
    fun viewModelFactory(alarmControl: AlarmDataUseCase) = MainActivityViewModel.Factory(alarmControl)

    @Provides
    fun alarmViewModelFactory(alarmControl: AlarmDataUseCase) = AlarmViewModel.Factory(alarmControl)
}