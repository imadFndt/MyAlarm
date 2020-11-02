package com.fndt.alarm.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.fndt.alarm.model.db.AlarmItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmRepository @Inject constructor(
    private val alarmItemDao: AlarmItemDao
) {
    val alarmList: LiveData<List<AlarmItem>>
        get() = alarmListData
    var alarm: (() -> Unit)? = null
    private val alarmListData: MutableLiveData<List<AlarmItem>> = MutableLiveData()
    private val dbObserver = Observer<List<AlarmItem>> { alarmListData.postValue(it) }

    init {
        alarmItemDao.getAll().observeForever(dbObserver)
    }


    suspend fun addItem(alarmItem: AlarmItem) =
        withContext(Dispatchers.IO) { alarmItemDao.insert(alarmItem) }

    suspend fun changeAlarm(alarmItem: AlarmItem) =
        withContext(Dispatchers.IO) { alarmItemDao.updateAlarm(alarmItem) }

    suspend fun wipeData() =
        withContext(Dispatchers.IO) { alarmItemDao.wipeTable() }

    suspend fun getNextAlarm(time: Long): AlarmItem =
        withContext(Dispatchers.IO) { return@withContext alarmItemDao.getNext(time) }

    fun clear() {
        alarmItemDao.getAll().removeObserver(dbObserver)
    }
}