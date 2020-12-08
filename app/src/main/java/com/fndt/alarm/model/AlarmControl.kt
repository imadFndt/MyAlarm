package com.fndt.alarm.model

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.fndt.alarm.model.db.AlarmRepository
import com.fndt.alarm.model.util.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmControl @Inject constructor(
    private var context: Context?,
    private val alarmSetup: AlarmSetup,
    private val wakelockProvider: WakelockProvider,
    var repository: AlarmRepository
) : AlarmEventHandler {
    private val repositoryScope = MainScope()

    val alarmList: LiveData<List<AlarmItem>> get() = repository.alarmList
    val nextAlarm: LiveData<NextAlarmItem?> get() = repository.nextAlarm
    val alarmingItem: LiveData<AlarmItem?> get() = alarmingItemData

    private val alarmingItemData: MutableLiveData<AlarmItem?> = MutableLiveData()

    private val nextAlarmObserver = Observer<NextAlarmItem?> { item ->
        Log.e("AlarmControl", "nextObserver received ${item?.alarmItem?.time}")
        if (savedValue != item) item?.let { alarmSetup.setAlarm(item) } ?: alarmSetup.cancelAlarm()
        savedValue = item
    }
    private var savedValue: NextAlarmItem? = null

    init {
        repository.nextAlarm.observeForever(nextAlarmObserver)
    }

    fun acquireWakeLock() {
        wakelockProvider.acquireServiceLock()
    }

    fun releaseWakeLock() {
        wakelockProvider.releaseServiceLock()
    }

    override fun handleEvent(intent: Intent) {
        Log.d("AlarmControl", "Received sync event ${intent.action}")
        val item = intent.getAlarmItem()
        item ?: return
        when (intent.action) {
            INTENT_FIRE_ALARM -> fireAlarm(item)
            INTENT_SNOOZE_ALARM -> snoozeAlarm(item)
            INTENT_STOP_ALARM -> stopAlarm(item)
        }
    }

    suspend fun addItem(item: AlarmItem) {
        repository.addItem(item)
    }

    suspend fun removeItem(item: AlarmItem) {
        repository.removeItem(item)
    }

    private fun fireAlarm(item: AlarmItem) {
        Log.d("AlarmControl", "fireAlarm(${item.id})")
        if (item.repeatPeriod.contains(AlarmRepeat.NONE)) item.isActive = !item.isActive
        repositoryScope.launch {
            if (item.repeatPeriod.contains(AlarmRepeat.ONCE_DESTROY)) {
                repository.removeItem(item)
            } else {
                repository.addItem(item)
            }
        }
        alarmingItemData.value = item
        context?.startService(INTENT_FIRE_ALARM, item)
    }

    private fun snoozeAlarm(item: AlarmItem) {
        repositoryScope.launch { repository.addItem(item.snoozed()) }
        stopAlarm(item)
    }

    private fun stopAlarm(item: AlarmItem) {
        context?.startService(INTENT_STOP_ALARM, item)
        alarmingItemData.value = null
    }

    private fun Context.startService(intentAction: String, alarmItem: AlarmItem) {
        val serviceIntent =
            alarmItem.toIntent(intentAction).setClass(this, AlarmService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    fun clear() {
        Log.e("AlarmControl", "clear()")
        wakelockProvider.releaseServiceLock()
        context = null
        repositoryScope.cancel()
    }
}
