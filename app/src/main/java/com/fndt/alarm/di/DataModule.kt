package com.fndt.alarm.di

import android.content.Context
import com.fndt.alarm.data.AlarmDatabase
import com.fndt.alarm.data.RepositoryImpl
import com.fndt.alarm.domain.Repository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [AppContextModule::class])
class DataModule {
    @Provides
    @Singleton
    fun repository(repository: RepositoryImpl): Repository = repository

    @Provides
    fun dao(context: Context) = AlarmDatabase.buildDb(context).issueDao()
}