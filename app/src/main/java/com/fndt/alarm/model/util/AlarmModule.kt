package com.fndt.alarm.model.util

import android.content.Context
import android.os.PowerManager
import androidx.core.content.getSystemService
import com.fndt.alarm.model.AlarmControl
import com.fndt.alarm.model.AlarmEventHandler
import com.fndt.alarm.model.db.AlarmDatabase
import com.fndt.alarm.view.AlarmViewModel
import com.fndt.alarm.view.main.MainActivityViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AlarmModule(private val context: Context) {
    @Provides
    fun context() = context

    @Provides
    @Singleton
    fun dao() = AlarmDatabase.buildDb(context).issueDao()

    @Provides
    fun viewModelFactory(alarmControl: AlarmControl) = MainActivityViewModel.Factory(alarmControl)

    @Provides
    fun alarmViewModelFactory(alarmControl: AlarmControl) = AlarmViewModel.Factory(alarmControl)

    @Provides
    fun powerManager() = context.getSystemService() as PowerManager?

    @Provides
    fun alarmHandler(control: AlarmControl): AlarmEventHandler = control

}