package com.fndt.alarm.di

import android.content.Context
import android.os.PowerManager
import androidx.core.content.getSystemService
import com.fndt.alarm.domain.AlarmSetup
import com.fndt.alarm.domain.ServiceHandler
import com.fndt.alarm.domain.WakelockProvider
import com.fndt.alarm.presentation.AlarmSetupImpl
import com.fndt.alarm.presentation.ServiceHandlerImpl
import com.fndt.alarm.presentation.WakelockProviderImpl
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Module(includes = [AppContextModule::class])
class AlarmUtilsModule {
    @Provides
    fun serviceHandler(serviceHandler: ServiceHandlerImpl): ServiceHandler = serviceHandler

    @Provides
    fun wakeLockProvider(wakelockProvider: WakelockProviderImpl): WakelockProvider = wakelockProvider

    @Provides
    fun alarmSetup(alarmSetup: AlarmSetupImpl): AlarmSetup = alarmSetup

    @Provides
    fun powerManager(context: Context) = context.getSystemService<PowerManager>()
}
