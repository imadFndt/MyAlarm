package com.fndt.alarm.view.main

import androidx.lifecycle.*
import com.fndt.alarm.model.AlarmControl
import com.fndt.alarm.model.AlarmItem
import com.fndt.alarm.model.NextAlarmItem
import kotlinx.coroutines.launch

class MainActivityViewModel(private val control: AlarmControl) : ViewModel() {
    val alarmList: LiveData<List<AlarmItem>> get() = alarmListData
    val status: LiveData<AlarmStatus> get() = statusData
    val nextAlarm: LiveData<NextAlarmItem?> get() = control.nextAlarm
    val itemEdited: LiveData<AlarmItem> get() = itemEditedData

    private val alarmListData: MutableLiveData<List<AlarmItem>> = MutableLiveData()
    private val statusData: MutableLiveData<AlarmStatus> = MutableLiveData(AlarmStatus.Idle)
    private val itemEditedData: MutableLiveData<AlarmItem> = MutableLiveData()

    private val repositoryObserver = Observer<List<AlarmItem>> { alarmListData.postValue(it) }

    init {
        control.alarmList.observeForever(repositoryObserver)
    }

    fun addAlarm(item: AlarmItem) {
        viewModelScope.launch { control.addItem(item) }
        statusData.value = AlarmStatus.Idle
    }

    fun removeAlarm(item: AlarmItem) {
        viewModelScope.launch { control.removeItem(item) }
    }

    fun editItem(item: AlarmItem?) {
        statusData.value = AlarmStatus.EditStatus(item)
    }

    fun cancelItemUpdate() {
        statusData.value = AlarmStatus.Idle
    }

    fun updateEditedItem(alarmItem: AlarmItem) {
        itemEditedData.value = alarmItem
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

    class Factory(private val control: AlarmControl) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainActivityViewModel(control) as T
        }
    }
}