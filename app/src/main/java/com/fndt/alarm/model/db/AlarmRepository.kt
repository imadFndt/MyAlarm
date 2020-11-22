package com.fndt.alarm.model.db

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.fndt.alarm.model.AlarmItem
import com.fndt.alarm.model.NextAlarmItem
import com.fndt.alarm.model.util.toTimeString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmRepository @Inject constructor(private val alarmItemDao: AlarmItemDao) {
    val alarmList: LiveData<List<AlarmItem>>
        get() = alarmItemDao.getAll()
    val nextAlarm: LiveData<NextAlarmItem?>
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

    private fun getNextEnabledItem(items: List<AlarmItem>): NextAlarmItem? {
        var alarmItem: AlarmItem?
        val nowCal = Calendar.getInstance()
        val now = (nowCal.get(Calendar.HOUR_OF_DAY) * 60 + nowCal.get(Calendar.MINUTE)).toLong()
        items.forEach {
            if (now < it.time) {
                val calendar = it.time.getTimedCalendar()
                if (calendar.before(nowCal)) calendar.add(Calendar.DATE, 1)
                return NextAlarmItem(it, calendar)
            }
        }
        if (items.isNotEmpty()) {
            val calendar = items[0].time.getTimedCalendar()
            if (calendar.before(nowCal)) calendar.add(Calendar.DATE, 1)
            return NextAlarmItem(items[0], calendar)
        }
        return null
    }

    private fun Long.getTimedCalendar(): Calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, (this@getTimedCalendar / 60).toInt())
        set(Calendar.MINUTE, (this@getTimedCalendar % 60).toInt())
        set(Calendar.SECOND, 0)
    }
}