package com.fndt.alarm.presentation.viewmodels

import androidx.lifecycle.*
import com.fndt.alarm.domain.AlarmDataUseCase
import com.fndt.alarm.domain.dto.NextAlarmItem
import com.fndt.alarm.domain.dto.AlarmItem
import kotlinx.coroutines.launch

class MainActivityViewModel(private val control: AlarmDataUseCase) : ViewModel() {
    val alarmList: LiveData<List<AlarmItem>> get() = alarmListData
    val status: LiveData<AlarmStatus> get() = statusData
    val nextAlarm: LiveData<NextAlarmItem?> get() = nextAlarmData
    val itemEdited: LiveData<AlarmItem> get() = itemEditedData

    private val statusData: MutableLiveData<AlarmStatus> = MutableLiveData(AlarmStatus.Idle)
    private val alarmListData: MutableLiveData<List<AlarmItem>> = MutableLiveData()
    private val nextAlarmData: MutableLiveData<NextAlarmItem?> = MutableLiveData()
    private val itemEditedData: MutableLiveData<AlarmItem> = MutableLiveData()

    private val callback = object : AlarmDataUseCase.Callback {
        override fun onUpdateAlarmList(list: List<AlarmItem>?) {
            alarmListData.value = list
        }

        override fun onUpdateNextItem(item: NextAlarmItem?) {
            nextAlarmData.value = item
        }

        override fun onUpdateAlarmingItem(item: AlarmItem?) {
            //no-op
        }
    }

    init {
        control.addDataUseCaseCallback(callback)
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
        control.removeDataUseCaseCallback(callback)
        control.clear()
        super.onCleared()
    }

    sealed class AlarmStatus {
        object Idle : AlarmStatus()
        data class EditStatus(val item: AlarmItem?) : AlarmStatus()
    }

    class Factory(private val control: AlarmDataUseCase) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainActivityViewModel(control) as T
        }
    }
}