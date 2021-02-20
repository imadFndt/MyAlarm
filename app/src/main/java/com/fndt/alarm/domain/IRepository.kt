package com.fndt.alarm.domain

import com.fndt.alarm.domain.dto.NextAlarmItem
import com.fndt.alarm.domain.dto.AlarmItem


interface IRepository {
    var callback: Callback?

    suspend fun addItem(alarmItem: AlarmItem)
    suspend fun removeItem(item: AlarmItem)
    fun clear()
    interface Callback {
        fun onUpdateList(list: List<AlarmItem>)
        fun onUpdateNextItem(item: NextAlarmItem?)
    }
}
