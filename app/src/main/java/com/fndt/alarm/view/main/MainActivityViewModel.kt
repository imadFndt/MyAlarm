package com.fndt.alarm.view.main

import androidx.lifecycle.*
import com.fndt.alarm.model.AlarmItem
import com.fndt.alarm.model.AlarmRepository
import kotlinx.coroutines.launch

class MainActivityViewModel(private val repository: AlarmRepository) : ViewModel() {
    val alarmList: LiveData<List<AlarmItem>> get() = alarmListData
    val status: LiveData<AlarmStatus> get() = statusData
    val alarmRequest: LiveData<AlarmItem> get() = alarmRequestData
    private val alarmListData: MutableLiveData<List<AlarmItem>> = MutableLiveData()
    private val statusData: MutableLiveData<AlarmStatus> = MutableLiveData(AlarmStatus.Idle)
    private val alarmRequestData: MutableLiveData<AlarmItem> = MutableLiveData()
    private val repositoryObserver = Observer<List<AlarmItem>> { alarmListData.postValue(it) }

    init {
        repository.alarmList.observeForever(repositoryObserver)
    }

    fun setTurnAlarmRequest(item: AlarmItem) {
        alarmRequestData.value = item
    }

    fun cancelAlarmRequest() {
        alarmRequestData.value = null
    }

    fun addItem(item: AlarmItem) {
        viewModelScope.launch { repository.addItem(item) }
        statusData.value = AlarmStatus.Idle
    }

    fun updateItem(item: AlarmItem?) {
        viewModelScope.launch { item?.let { repository.changeAlarm(it) } }
    }

    fun editItem(item: AlarmItem?) {
        statusData.value = AlarmStatus.EditStatus(item)
    }

    fun cancelItemUpdate() {
        statusData.value = AlarmStatus.Idle
    }

    override fun onCleared() {
        if (repository.alarmList.hasObservers()) {
            repository.alarmList.removeObserver(repositoryObserver)
        }
        repository.clear()
        super.onCleared()
    }

    sealed class AlarmStatus {
        object Idle : AlarmStatus()
        data class EditStatus(val item: AlarmItem?) : AlarmStatus()
    }

    class Factory(private val repository: AlarmRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainActivityViewModel(repository) as T
        }
    }
}