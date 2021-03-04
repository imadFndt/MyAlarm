package com.fndt.alarm.domain

import com.fndt.alarm.domain.dto.AlarmItem
import com.fndt.alarm.domain.dto.NextAlarmItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AlarmDataUseCase {
    @ExperimentalCoroutinesApi
    val alarmingItem: StateFlow<AlarmItem?>
    val alarmListFlow: Flow<List<AlarmItem>>
    val nextItemFlow: Flow<NextAlarmItem?>

    suspend fun addItem(item: AlarmItem)
    suspend fun removeItem(item: AlarmItem)
    fun clear()

}
