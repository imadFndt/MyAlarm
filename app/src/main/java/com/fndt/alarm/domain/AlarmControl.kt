package com.fndt.alarm.domain

import com.fndt.alarm.domain.dto.AlarmIntent
import com.fndt.alarm.domain.dto.AlarmItem
import com.fndt.alarm.domain.dto.AlarmRepeat
import com.fndt.alarm.domain.dto.NextAlarmItem
import com.fndt.alarm.domain.utils.INTENT_FIRE_ALARM
import com.fndt.alarm.domain.utils.INTENT_SNOOZE_ALARM
import com.fndt.alarm.domain.utils.INTENT_STOP_ALARM
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Singleton
class AlarmControl @Inject constructor(
    private val serviceHandler: IServiceHandler,
    private val alarmSetup: IAlarmSetup,
    private val wakelockProvider: IWakelockProvider,
    private val repository: IRepository
) : AlarmEventHandler, AlarmDataUseCase, WakeLockUseCase {
    override val alarmingItem: StateFlow<AlarmItem?> get() = _alarmingItem

    override val alarmListFlow = repository.itemList.flowOn(Dispatchers.IO)

    override val nextItemFlow = repository.nextItemFlow.flowOn(Dispatchers.IO).map { item ->
        if (savedValue != item) item?.let { alarmSetup.setAlarm(item) } ?: alarmSetup.cancelAlarm()
        savedValue = item
        item
    }

    private val _alarmingItem = MutableStateFlow<AlarmItem?>(null)

    private val repositoryScope = MainScope()

    private var savedValue: NextAlarmItem? = null

    override fun handleEvent(intent: AlarmIntent) {
        intent.item ?: return
        when (intent.action) {
            INTENT_FIRE_ALARM -> fireAlarm(intent.item)
            INTENT_SNOOZE_ALARM -> snoozeAlarm(intent.item)
            INTENT_STOP_ALARM -> stopAlarm(intent.item)
        }
    }

    override suspend fun addItem(item: AlarmItem) {
        repository.addItem(item)
    }

    override suspend fun removeItem(item: AlarmItem) {
        repository.removeItem(item)
    }

    override fun acquireWakeLock() {
        wakelockProvider.acquireServiceLock()
    }

    override fun releaseWakeLock() {
        wakelockProvider.releaseServiceLock()
    }

    private fun fireAlarm(item: AlarmItem) {
        if (item.repeatPeriod.contains(AlarmRepeat.NONE)) item.isActive = !item.isActive
        repositoryScope.launch(Dispatchers.IO) {
            val repeatOnceDestroy = item.repeatPeriod.contains(AlarmRepeat.ONCE_DESTROY)
            if (repeatOnceDestroy) repository.removeItem(item) else repository.addItem(item)
        }
        _alarmingItem.value = item
        serviceHandler.startService(INTENT_FIRE_ALARM, item)
    }

    private fun snoozeAlarm(item: AlarmItem) {
        repositoryScope.launch(Dispatchers.IO) { repository.addItem(item.snoozed()) }
        stopAlarm(item)
    }

    private fun stopAlarm(item: AlarmItem) {
        serviceHandler.startService(INTENT_STOP_ALARM, item)
        _alarmingItem.value = null
    }

    override fun clear() {
        wakelockProvider.releaseServiceLock()
        repositoryScope.cancel()
    }
}
