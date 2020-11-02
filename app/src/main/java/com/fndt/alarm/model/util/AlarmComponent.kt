package com.fndt.alarm.model.util

import com.fndt.alarm.view.main.MainActivityViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AlarmModule::class])
interface AlarmComponent {
    fun getViewModelFactory(): MainActivityViewModel.Factory
}