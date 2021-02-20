package com.fndt.alarm.domain

import com.fndt.alarm.model.AlarmIntent
import com.fndt.alarm.model.AlarmItem
import com.fndt.alarm.model.AlarmRepeat
import com.fndt.alarm.model.NextAlarmItem
import com.fndt.alarm.model.util.INTENT_FIRE_ALARM
import com.fndt.alarm.model.util.INTENT_SNOOZE_ALARM
import com.fndt.alarm.model.util.INTENT_STOP_ALARM
import com.fndt.alarm.model.util.snoozed
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.properties.Delegates

@Singleton
class AlarmControl @Inject constructor(
    private var serviceHandler: IServiceHandler,
    private val alarmSetup: IAlarmSetup,
    private val wakelockProvider: IWakelockProvider,
    var repository: IRepository
) : AlarmEventHandler, AlarmDataUseCase, WakeLockUseCase {
    var callbacks: MutableList<AlarmDataUseCase.Callback?> = mutableListOf()

    private var alarmingItem: AlarmItem? by Delegates.observable(null) { _, _, newValue ->
        callbacks.forEach { it?.onUpdateAlarmingItem(newValue) }
    }
    private var alarmList: List<AlarmItem>? by Delegates.observable(null) { _, _, newValue ->
        callbacks.forEach { it?.onUpdateAlarmList(newValue) }
    }
    private var nextAlarm: NextAlarmItem? by Delegates.observable(null) { _, _, newValue ->
        callbacks.forEach { it?.onUpdateNextItem(newValue) }
    }

    private val repositoryScope = MainScope()

    private var savedValue: NextAlarmItem? = null

    init {
        repository.callback = object : IRepository.Callback {
            override fun onUpdateList(list: List<AlarmItem>) {
                alarmList = list
            }

            override fun onUpdateNextItem(item: NextAlarmItem?) {
                nextAlarm = item
                if (savedValue != item) item?.let { alarmSetup.setAlarm(item) } ?: alarmSetup.cancelAlarm()
                savedValue = item
            }
        }
    }

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

    override fun addDataUseCaseCallback(callback: AlarmDataUseCase.Callback) {
        callbacks.add(callback)
    }

    override fun removeDataUseCaseCallback(callback: AlarmDataUseCase.Callback) {
        if (callbacks.contains(callback)) callbacks.remove(callback)
    }

    override fun requestAlarmingItem() {
        alarmingItem = alarmingItem
    }

    override fun acquireWakeLock() {
        wakelockProvider.acquireServiceLock()
    }

    override fun releaseWakeLock() {
        wakelockProvider.releaseServiceLock()
    }

    private fun fireAlarm(item: AlarmItem) {
        if (item.repeatPeriod.contains(AlarmRepeat.NONE)) item.isActive = !item.isActive
        repositoryScope.launch {
            if (item.repeatPeriod.contains(AlarmRepeat.ONCE_DESTROY)) {
                repository.removeItem(item)
            } else {
                repository.addItem(item)
            }
        }
        alarmingItem = item
        serviceHandler.startService(INTENT_FIRE_ALARM, item)
    }

    private fun snoozeAlarm(item: AlarmItem) {
        repositoryScope.launch { repository.addItem(item.snoozed()) }
        stopAlarm(item)
    }

    private fun stopAlarm(item: AlarmItem) {
        serviceHandler.startService(INTENT_STOP_ALARM, item)
        alarmingItem = null
    }

    override fun clear() {
        repository.callback = null
        wakelockProvider.releaseServiceLock()
        repositoryScope.cancel()
    }
}
