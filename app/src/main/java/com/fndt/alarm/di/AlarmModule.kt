package com.fndt.alarm.di

import android.content.Context
import android.os.PowerManager
import androidx.core.content.getSystemService
import com.fndt.alarm.data.AlarmDatabase
import com.fndt.alarm.data.AlarmRepository
import com.fndt.alarm.domain.*
import com.fndt.alarm.presentation.AlarmSetup
import com.fndt.alarm.presentation.ServiceHandler
import com.fndt.alarm.presentation.WakelockProvider
import com.fndt.alarm.presentation.viewmodels.AlarmViewModel
import com.fndt.alarm.presentation.viewmodels.MainActivityViewModel
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton
@ExperimentalCoroutinesApi
@Module
class AlarmModule(private val context: Context) {
    @Provides
    fun context() = context

    @Provides
    @Singleton
    fun repository(repository: AlarmRepository): IRepository = repository

    @Provides
    @Singleton
    fun alarmDataUseCase(control: AlarmControl): AlarmDataUseCase = control

    @Provides
    @Singleton
    fun wakeLockUseCase(alarmControl: AlarmControl): WakeLockUseCase = alarmControl

    @Provides
    fun serviceHandler(serviceHandler: ServiceHandler): IServiceHandler = serviceHandler

    @Provides
    fun wakeLockProvider(wakelockProvider: WakelockProvider): IWakelockProvider = wakelockProvider

    @Provides
    fun dao() = AlarmDatabase.buildDb(context).issueDao()

    @Provides
    fun alarmSetup(alarmSetup: AlarmSetup): IAlarmSetup = alarmSetup

    @Provides
    fun viewModelFactory(alarmControl: AlarmDataUseCase) = MainActivityViewModel.Factory(alarmControl)

    @Provides
    fun alarmViewModelFactory(alarmControl: AlarmDataUseCase) = AlarmViewModel.Factory(alarmControl)

    @Provides
    fun powerManager() = context.getSystemService() as PowerManager?

    @Provides
    fun alarmHandler(control: AlarmControl): AlarmEventHandler = control

}