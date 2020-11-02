package com.fndt.alarm.view

import androidx.lifecycle.*
import com.fndt.alarm.model.AlarmItem
import com.fndt.alarm.model.AlarmRepository
import kotlinx.coroutines.launch

class AlarmViewModel(private val repository: AlarmRepository) : ViewModel() {
    val alarm: LiveData<AlarmItem> get() = alarmData
    private val alarmData: MutableLiveData<AlarmItem> = MutableLiveData()

    init {

    }

    fun requestNextAlarm(time: Long) {
        viewModelScope.launch {
            alarmData.postValue(repository.getNextAlarm(time))
        }
    }

    class Factory(private val repository: AlarmRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AlarmViewModel(repository) as T
        }
    }
}