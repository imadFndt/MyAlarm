package com.fndt.alarm.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.switchMap
import com.fndt.alarm.domain.IRepository
import com.fndt.alarm.model.AlarmItem
import com.fndt.alarm.model.AlarmRepeat
import com.fndt.alarm.model.NextAlarmItem
import com.fndt.alarm.model.util.toCalendarDayOfWeek
import com.fndt.alarm.model.util.toTimeString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmRepository @Inject constructor(private val alarmItemDao: AlarmItemDao) : IRepository {
    override var callback: IRepository.Callback? = null

    private val listObserver = Observer<List<AlarmItem>> { callback?.onUpdateList(it) }
    private val nextObserver = Observer<NextAlarmItem?> { callback?.onUpdateNextItem(it) }

    private val nextDaoItem = alarmItemDao.getEnabled().switchMap { MutableLiveData(getNextEnabledItem(it)) }

    init {
        alarmItemDao.getAll().observeForever(listObserver)
        nextDaoItem.observeForever(nextObserver)
    }

    override suspend fun addItem(alarmItem: AlarmItem) =
        withContext(Dispatchers.IO) {
            Log.d("Repository", "Add item ${alarmItem.name} at ${alarmItem.time.toTimeString()}")
            alarmItemDao.insert(alarmItem)
        }

    override suspend fun removeItem(item: AlarmItem) = withContext(Dispatchers.IO) { alarmItemDao.remove(item) }

    override fun clear() {
        alarmItemDao.getAll().removeObserver(listObserver)
        nextDaoItem.removeObserver(nextObserver)
    }

    private fun getNextEnabledItem(items: List<AlarmItem>): NextAlarmItem? = items.getTimedList().findClosestItem()

    private fun List<AlarmItem>.getTimedList(): List<NextAlarmItem> {
        val newList = mutableListOf<NextAlarmItem>()
        forEach { newList.addAll(processItem(it)) }
        return newList
    }

    private fun processItem(item: AlarmItem): List<NextAlarmItem> {
        val itemsAlarms = mutableListOf<NextAlarmItem>()
        for (i in item.repeatPeriod) {
            val nextItem = NextAlarmItem(
                item, when (i) {
                    AlarmRepeat.NONE, AlarmRepeat.ONCE_DESTROY -> item.time.getTodayTimedCalendar()
                    else -> item.time.getWeekdayTimedCalendar(i)
                }
            )
            itemsAlarms.add(nextItem)
        }
        return itemsAlarms
    }

    private fun Long.getTodayTimedCalendar() = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, (this@getTodayTimedCalendar / 60).toInt())
        set(Calendar.MINUTE, (this@getTodayTimedCalendar % 60).toInt())
        set(Calendar.SECOND, 0)
        if (this.before(Calendar.getInstance())) add(Calendar.DATE, 1)
    }

    private fun Long.getWeekdayTimedCalendar(alarmRepeat: AlarmRepeat) = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, (this@getWeekdayTimedCalendar / 60).toInt())
        set(Calendar.MINUTE, (this@getWeekdayTimedCalendar % 60).toInt())
        set(Calendar.SECOND, 0)
        val targetDay = alarmRepeat.toCalendarDayOfWeek()
        set(Calendar.DAY_OF_WEEK, targetDay)
        if (this.before(Calendar.getInstance())) add(Calendar.DATE, 7)
    }

    private fun List<NextAlarmItem>.findClosestItem(): NextAlarmItem? {
        val now = Calendar.getInstance().timeInMillis
        val afterNow = mutableListOf<NextAlarmItem>()
        forEach { if (it.timedCalendar.timeInMillis > now) afterNow.add(it) }
        return afterNow.minByOrNull { it.timedCalendar.timeInMillis }
    }
}
