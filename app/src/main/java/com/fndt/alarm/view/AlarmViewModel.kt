package com.fndt.alarm.view

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fndt.alarm.model.AlarmControl
import com.fndt.alarm.model.AlarmItem

class AlarmViewModel(private val alarmControl: AlarmControl) : ViewModel() {
    val alarmingItem: LiveData<AlarmItem?> = alarmControl.alarmingItem

    fun handleEvent(event: Intent) {
        alarmControl.handleEventSync(event)
    }

    class Factory(private val control: AlarmControl) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AlarmViewModel(control) as T
        }
    }
}