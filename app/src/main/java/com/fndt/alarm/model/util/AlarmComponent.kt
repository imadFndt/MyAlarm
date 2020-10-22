package com.fndt.alarm.model.util

import com.fndt.alarm.view.MainActivity
import com.fndt.alarm.view.MainActivityViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AlarmModule::class])
interface AlarmComponent {
    fun getViewModelFactory(): MainActivityViewModel.Factory
}