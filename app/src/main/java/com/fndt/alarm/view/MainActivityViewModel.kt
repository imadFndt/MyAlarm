package com.fndt.alarm.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fndt.alarm.model.AlarmControl
import com.fndt.alarm.model.AlarmItem

class MainActivityViewModel(control: AlarmControl) : ViewModel() {
    val alarmList: LiveData<List<AlarmItem>> get() = alarmListData
    private val alarmListData: MutableLiveData<List<AlarmItem>> = MutableLiveData()

    init {
        //TODO OBSERVE TO MODEL
    }

    override fun onCleared() {
        //TODO CLEAR OBSERVERS
        super.onCleared()
    }

    class Factory(private val control: AlarmControl) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainActivityViewModel(control) as T
        }
    }
}