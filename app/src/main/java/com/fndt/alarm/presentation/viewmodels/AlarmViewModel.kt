package com.fndt.alarm.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.fndt.alarm.domain.AlarmDataUseCase
import com.fndt.alarm.domain.dto.AlarmItem
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class AlarmViewModel(private val alarmControl: AlarmDataUseCase) : ViewModel() {
    val alarmingItem: LiveData<AlarmItem?> get() = alarmControl.alarmingItem.asLiveData()

    override fun onCleared() {
        alarmControl.clear()
        super.onCleared()
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val control: AlarmDataUseCase) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AlarmViewModel(control) as T
        }
    }
}