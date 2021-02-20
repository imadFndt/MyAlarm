package com.fndt.alarm.domain

import com.fndt.alarm.model.AlarmItem
import com.fndt.alarm.model.NextAlarmItem


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
