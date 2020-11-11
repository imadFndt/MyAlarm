package com.fndt.alarm.model.db

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.fndt.alarm.model.AlarmItem
import com.fndt.alarm.model.AlarmItem.Companion.toTimeString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmRepository @Inject constructor(private val alarmItemDao: AlarmItemDao) {
    val alarmList: LiveData<List<AlarmItem>>
        get() = alarmListData
    var alarm: (() -> Unit)? = null
    private val alarmListData: MutableLiveData<List<AlarmItem>> = MutableLiveData()
    private val dbObserver = Observer<List<AlarmItem>> { alarmListData.postValue(it) }
    private val nextObserver = Observer<AlarmItem> { alarmList }

    init {
        alarmItemDao.getAll().observeForever(dbObserver)
    }

    suspend fun addItem(alarmItem: AlarmItem) =
        withContext(Dispatchers.IO) {
            Log.d("REPOSITORY", "Add item ${alarmItem.name} at ${alarmItem.time.toTimeString()}")
            alarmItemDao.insert(alarmItem)
        }

    suspend fun changeAlarm(alarmItem: AlarmItem) =
        withContext(Dispatchers.IO) { alarmItemDao.insert(alarmItem) }

    suspend fun wipeData() =
        withContext(Dispatchers.IO) { alarmItemDao.wipeTable() }

    fun clear() {
        alarmItemDao.getAll().removeObserver(dbObserver)
    }
}