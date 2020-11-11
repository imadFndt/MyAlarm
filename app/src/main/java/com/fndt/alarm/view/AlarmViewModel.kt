package com.fndt.alarm.view

import android.content.Intent
import androidx.lifecycle.*
import com.fndt.alarm.model.AlarmControl
import com.fndt.alarm.model.AlarmItem
import kotlinx.coroutines.launch

class AlarmViewModel(private val alarmControl: AlarmControl) : ViewModel() {
    val alarm: LiveData<AlarmItem> get() = alarmData
    private val alarmData: MutableLiveData<AlarmItem> = MutableLiveData()

    init {

    }

    fun requestNextAlarm(time: Long) {
        viewModelScope.launch {
            // alarmData.postValue(repository.getNextAlarm(time))
        }
    }

    fun handleEvent(event: Intent) {
        alarmControl.handleEventSync(event)
    }

    class Factory(private val control: AlarmControl) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AlarmViewModel(control) as T
        }
    }
}