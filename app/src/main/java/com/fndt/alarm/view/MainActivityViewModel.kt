package com.fndt.alarm.view

import androidx.lifecycle.*
import com.fndt.alarm.model.AlarmItem
import com.fndt.alarm.model.AlarmRepository
import kotlinx.coroutines.launch

class MainActivityViewModel(private val repository: AlarmRepository) : ViewModel() {
    val alarmList: LiveData<List<AlarmItem>> get() = alarmListData
    private val alarmListData: MutableLiveData<List<AlarmItem>> = MutableLiveData()
    private val repositoryObserver = Observer<List<AlarmItem>> { alarmListData.postValue(it) }

    init {
        repository.alarmList.observeForever(repositoryObserver)
    }

    fun addItem(item: AlarmItem) {
        viewModelScope.launch { repository.addItem(item) }
    }

    override fun onCleared() {
        if (repository.alarmList.hasObservers()) {
            repository.alarmList.removeObserver(repositoryObserver)
        }
        super.onCleared()
    }

    class Factory(private val repository: AlarmRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainActivityViewModel(repository) as T
        }
    }
}