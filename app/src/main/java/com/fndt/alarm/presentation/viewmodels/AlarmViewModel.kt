package com.fndt.alarm.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fndt.alarm.domain.AlarmDataUseCase
import com.fndt.alarm.domain.dto.NextAlarmItem
import com.fndt.alarm.domain.dto.AlarmItem

class AlarmViewModel(private val alarmControl: AlarmDataUseCase) : ViewModel() {
    fun requestAlarmingItem() {
        alarmControl.requestAlarmingItem()
    }

    val alarmingItem: LiveData<AlarmItem?> get() = alarmingItemData
    private val alarmingItemData: MutableLiveData<AlarmItem?> = MutableLiveData()

    private val callback = object : AlarmDataUseCase.Callback {
        override fun onUpdateAlarmList(list: List<AlarmItem>?) {
            //no-op
        }

        override fun onUpdateNextItem(item: NextAlarmItem?) {
            //no-op
        }

        override fun onUpdateAlarmingItem(item: AlarmItem?) {
            alarmingItemData.value = item
        }
    }

    init {
        alarmControl.addDataUseCaseCallback(callback)
    }

    override fun onCleared() {
        alarmControl.removeDataUseCaseCallback(callback)
        super.onCleared()
    }

    class Factory(private val control: AlarmDataUseCase) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AlarmViewModel(control) as T
        }
    }
}