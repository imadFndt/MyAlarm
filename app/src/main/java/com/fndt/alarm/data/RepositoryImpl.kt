package com.fndt.alarm.data

import android.util.Log
import androidx.lifecycle.asFlow
import com.fndt.alarm.domain.Repository
import com.fndt.alarm.domain.dto.AlarmItem
import com.fndt.alarm.domain.dto.AlarmRepeat
import com.fndt.alarm.domain.dto.NextAlarmItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryImpl @Inject constructor(private val alarmItemDao: AlarmItemDao) : Repository {
    override val itemList: Flow<List<AlarmItem>> = alarmItemDao.getAll()
        .asFlow()
        .map {
            it.map { entity -> entity.toAlarmItem() }
        }


    override val nextItemFlow: Flow<NextAlarmItem?> = alarmItemDao
        .getEnabled().asFlow()
        .map { getNextEnabledItem(it) }

    override suspend fun addItem(alarmItem: AlarmItem) = withContext(Dispatchers.IO) {
        Log.d("Repository", "Add item ${alarmItem.name} at ${alarmItem.time}")
        alarmItemDao.insert(alarmItem.toEntity())
    }

    override suspend fun removeItem(item: AlarmItem) = withContext(Dispatchers.IO) {
        alarmItemDao.remove(item.toEntity())
    }

    private fun getNextEnabledItem(items: List<AlarmItemEntity>): NextAlarmItem? {
        return items.getTimedList().findClosestItem()
    }

    private fun List<AlarmItemEntity>.getTimedList(): List<NextAlarmItem> {
        val newList = mutableListOf<NextAlarmItem>()
        forEach { newList.addAll(processItem(it)) }
        return newList
    }

    private fun processItem(item: AlarmItemEntity): List<NextAlarmItem> {
        val itemsAlarms = mutableListOf<NextAlarmItem>()
        for (i in item.repeatPeriod) {
            val nextItem = NextAlarmItem(
                item.toAlarmItem(), when (i) {
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

    private fun AlarmItem.toEntity(): AlarmItemEntity {
        return AlarmItemEntity(time, name, isActive, repeatPeriod, melody).also { it.id = this.id }
    }

    private fun AlarmRepeat.toCalendarDayOfWeek(): Int = when (this) {
        AlarmRepeat.MONDAY -> 2
        AlarmRepeat.TUESDAY -> 3
        AlarmRepeat.WEDNESDAY -> 4
        AlarmRepeat.THURSDAY -> 5
        AlarmRepeat.FRIDAY -> 6
        AlarmRepeat.SATURDAY -> 7
        AlarmRepeat.SUNDAY -> 1
        else -> 0
    }
}

