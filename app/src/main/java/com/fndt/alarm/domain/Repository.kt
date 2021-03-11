package com.fndt.alarm.domain

import com.fndt.alarm.domain.dto.AlarmItem
import com.fndt.alarm.domain.dto.NextAlarmItem
import kotlinx.coroutines.flow.Flow


interface Repository {
    val itemList: Flow<List<AlarmItem>>
    val nextItemFlow: Flow<NextAlarmItem?>

    suspend fun addItem(alarmItem: AlarmItem)
    suspend fun removeItem(item: AlarmItem)
}
