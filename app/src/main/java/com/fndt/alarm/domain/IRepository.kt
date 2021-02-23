package com.fndt.alarm.domain

import com.fndt.alarm.domain.dto.AlarmItem
import com.fndt.alarm.domain.dto.NextAlarmItem


interface IRepository {
    suspend fun addItem(alarmItem: AlarmItem)
    suspend fun removeItem(item: AlarmItem)
    fun setCallback(callback: Callback?)
    fun clear()
    interface Callback {
        fun onUpdateList(list: List<AlarmItem>)
        fun onUpdateNextItem(item: NextAlarmItem?)
    }
}
