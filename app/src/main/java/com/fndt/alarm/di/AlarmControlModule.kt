package com.fndt.alarm.di

import com.fndt.alarm.domain.*
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Module(includes = [DataModule::class, AlarmUtilsModule::class])
class AlarmControlModule {
    @Provides
    @Singleton
    fun alarmDataUseCase(control: AlarmControl): AlarmDataUseCase = control

    @Provides
    @Singleton
    fun alarmHandler(control: AlarmControl): AlarmEventHandler = control

    @Provides
    @Singleton
    fun alarmControl(
        serviceHandler: ServiceHandler,
        alarmSetup: AlarmSetup,
        wakelockProvider: WakelockProvider,
        repository: Repository
    ) = AlarmControl(serviceHandler, alarmSetup, wakelockProvider, repository)
}