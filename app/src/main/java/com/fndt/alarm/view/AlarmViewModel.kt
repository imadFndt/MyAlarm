package com.fndt.alarm.view

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fndt.alarm.model.AlarmControl

class AlarmViewModel(private val alarmControl: AlarmControl) : ViewModel() {
    fun handleEvent(event: Intent) {
        alarmControl.handleEventSync(event)
    }

    class Factory(private val control: AlarmControl) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AlarmViewModel(control) as T
        }
    }
}