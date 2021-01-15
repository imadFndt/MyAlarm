package com.fndt.alarm.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fndt.alarm.model.AlarmControl
import com.fndt.alarm.model.AlarmItem

class AlarmViewModel(alarmControl: AlarmControl) : ViewModel() {
    val alarmingItem: LiveData<AlarmItem?> = alarmControl.alarmingItem

    class Factory(private val control: AlarmControl) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AlarmViewModel(control) as T
        }
    }
}