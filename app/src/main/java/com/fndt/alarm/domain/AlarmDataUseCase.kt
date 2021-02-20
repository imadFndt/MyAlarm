package com.fndt.alarm.domain

import com.fndt.alarm.domain.dto.NextAlarmItem
import com.fndt.alarm.domain.dto.AlarmItem

interface AlarmDataUseCase {
    suspend fun addItem(item: AlarmItem)
    suspend fun removeItem(item: AlarmItem)
    fun addDataUseCaseCallback(callback: Callback)
    fun removeDataUseCaseCallback(callback: Callback)
    fun requestAlarmingItem()
    fun clear()

    interface Callback {
        fun onUpdateAlarmList(list: List<AlarmItem>?)
        fun onUpdateNextItem(item: NextAlarmItem?)
        fun onUpdateAlarmingItem(item: AlarmItem?)
    }
}
