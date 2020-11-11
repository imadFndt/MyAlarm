package com.fndt.alarm.view.main

import android.content.Intent
import androidx.lifecycle.*
import com.fndt.alarm.model.AlarmControl
import com.fndt.alarm.model.AlarmItem
import kotlinx.coroutines.launch

class MainActivityViewModel(private val control: AlarmControl) : ViewModel() {
    val alarmList: LiveData<List<AlarmItem>> get() = alarmListData
    val status: LiveData<AlarmStatus> get() = statusData
    val alarmRequest: LiveData<TurnAlarmStatus> get() = alarmRequestData
    private val alarmListData: MutableLiveData<List<AlarmItem>> = MutableLiveData()
    private val statusData: MutableLiveData<AlarmStatus> = MutableLiveData(AlarmStatus.Idle)
    private val alarmRequestData: MutableLiveData<TurnAlarmStatus> = MutableLiveData()
    private val repositoryObserver = Observer<List<AlarmItem>> { alarmListData.postValue(it) }

    init {
        control.alarmList.observeForever(repositoryObserver)
    }

    fun setTurnAlarmRequest(item: AlarmItem) {
        alarmRequestData.value =
            if (item.isActive) TurnAlarmStatus.TurnOff(item) else  TurnAlarmStatus.TurnOn(item)
        statusData.value = AlarmStatus.Idle
    }

    fun cancelAlarmRequest() {
        alarmRequestData.value = null
    }

    fun addAlarm(event: Intent) {
        viewModelScope.launch { control.handleEventAsync(event) }
        statusData.value = AlarmStatus.Idle
    }

    fun editItem(item: AlarmItem?) {
        statusData.value = AlarmStatus.EditStatus(item)
    }

    fun cancelItemUpdate() {
        statusData.value = AlarmStatus.Idle
    }

    fun sendEvent(event: Intent) {
        control.handleEventSync(event)
    }

    override fun onCleared() {
        if (control.alarmList.hasObservers()) control.alarmList.removeObserver(repositoryObserver)
        control.clear()
        super.onCleared()
    }

    sealed class AlarmStatus {
        object Idle : AlarmStatus()
        data class EditStatus(val item: AlarmItem?) : AlarmStatus()
    }

    sealed class TurnAlarmStatus {
        data class TurnOn(val item: AlarmItem) : TurnAlarmStatus()
        data class TurnOff(val item: AlarmItem) : TurnAlarmStatus()
    }

    class Factory(private val control: AlarmControl) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainActivityViewModel(control) as T
        }
    }
}