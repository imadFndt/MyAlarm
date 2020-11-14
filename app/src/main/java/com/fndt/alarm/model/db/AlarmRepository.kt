package com.fndt.alarm.model.db

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.fndt.alarm.model.AlarmItem
import com.fndt.alarm.model.AlarmItem.Companion.toTimeString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmRepository @Inject constructor(private val alarmItemDao: AlarmItemDao) {
    val alarmList: LiveData<List<AlarmItem>>
        get() = alarmItemDao.getAll()
    val nextAlarm: LiveData<AlarmItem?>
        get() = alarmItemDao.getEnabled().switchMap { MutableLiveData(getNextEnabledItem(it)) }
    var alarm: (() -> Unit)? = null

    suspend fun addItem(alarmItem: AlarmItem) =
        withContext(Dispatchers.IO) {
            Log.d("REPOSITORY", "Add item ${alarmItem.name} at ${alarmItem.time.toTimeString()}")
            alarmItemDao.insert(alarmItem)
        }

    suspend fun changeAlarm(alarmItem: AlarmItem) =
        withContext(Dispatchers.IO) { alarmItemDao.insert(alarmItem) }

    suspend fun wipeData() =
        withContext(Dispatchers.IO) { alarmItemDao.wipeTable() }

    private fun getNextEnabledItem(items: List<AlarmItem>): AlarmItem? {
        val cal = Calendar.getInstance()
        val now = (cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)).toLong()
        items.forEach {
            if (now < it.time) return it
        }
        return null
    }
}