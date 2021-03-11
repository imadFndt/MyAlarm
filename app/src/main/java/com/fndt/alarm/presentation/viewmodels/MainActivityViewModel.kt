package com.fndt.alarm.presentation.viewmodels

import androidx.lifecycle.*
import com.fndt.alarm.domain.AlarmDataUseCase
import com.fndt.alarm.domain.dto.AlarmItem
import com.fndt.alarm.domain.dto.NextAlarmItem
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainActivityViewModel(private val control: AlarmDataUseCase) : ViewModel() {
    val alarmList: LiveData<List<AlarmItem>> get() = alarmListData
    val status: LiveData<AlarmStatus> get() = statusData
    val nextAlarm: LiveData<NextAlarmItem?> get() = control.nextItemFlow.asLiveData()
    val itemEdited: LiveData<AlarmItem> get() = itemEditedData

    private val alarmListData = MutableLiveData<List<AlarmItem>>()
    private val statusData = MutableLiveData<AlarmStatus>(AlarmStatus.Idle)
    private val itemEditedData = MutableLiveData<AlarmItem>()

    init {
        control.alarmListFlow.onEach { newList ->
            alarmListData.value = newList
        }.launchIn(viewModelScope)
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
        control.clear()
        super.onCleared()
    }

    sealed class AlarmStatus {
        object Idle : AlarmStatus()
        data class EditStatus(val item: AlarmItem?) : AlarmStatus()
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val control: AlarmDataUseCase) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainActivityViewModel(control) as T
        }
    }
}