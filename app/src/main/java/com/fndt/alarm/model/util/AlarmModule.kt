package com.fndt.alarm.model.util

import android.content.Context
import com.fndt.alarm.model.AlarmControl
import com.fndt.alarm.model.db.AlarmDatabase
import com.fndt.alarm.view.MainActivityViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AlarmModule(private val context: Context) {
    @Provides
    @Singleton
    fun dao() = AlarmDatabase.buildDb(context).issueDao()

    @Provides
    fun viewModelFactory(alarmControl: AlarmControl) = MainActivityViewModel.Factory(alarmControl)
}