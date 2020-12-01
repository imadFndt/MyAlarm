package com.fndt.alarm.model

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
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
    private val repository: AlarmRepository
) {
    private val repositoryScope = MainScope()

    val alarmList: LiveData<List<AlarmItem>> get() = repository.alarmList
    val nextAlarm: LiveData<NextAlarmItem?> get() = repository.nextAlarm

    private val nextAlarmObserver = Observer<NextAlarmItem?> { item ->
        Log.e("AlarmControl", "nextObserver received ${item?.alarmItem?.time}")
        if (savedValue != item) item?.let { alarmSetup.setAlarm(item) } ?: alarmSetup.cancelAlarm()
        savedValue = item
    }
    private var savedValue: NextAlarmItem? = null

    init {
        repository.nextAlarm.observeForever(nextAlarmObserver)
    }

    fun onServiceCreate() {
        wakelockProvider.acquireServiceLock()
    }

    suspend fun handleEventAsync(intent: Intent) {
        val item = intent.getAlarmItem()
        item ?: return
        Log.d("AlarmControl", "Received async event ${intent.action}")
        when (intent.action) {
            INTENT_ADD_ALARM -> repository.addItem(item)
            INTENT_REMOVE_ALARM -> repository.remove(item)
        }
    }

    fun handleEventSync(intent: Intent) {
        val item = intent.getAlarmItem()
        item ?: return
        Log.d("AlarmControl", "Received sync event ${intent.action}")
        when (intent.action) {
            INTENT_FIRE_ALARM -> fireAlarm(item)
            INTENT_SNOOZE_ALARM -> snoozeAlarm(item)
        }
    }

    private fun fireAlarm(item: AlarmItem) {
        if (item.repeatPeriod.contains(AlarmRepeat.NONE)) item.isActive = !item.isActive
        repositoryScope.launch {
            if (item.repeatPeriod.contains(AlarmRepeat.ONCE_DESTROY)) {
                handleEventAsync(item.toIntent(INTENT_REMOVE_ALARM))
            } else {
                handleEventAsync(item.toIntent(INTENT_ADD_ALARM))
            }
        }
        Log.d("AlarmControl", "fireAlarm(${item.id})")
        val serviceIntent =
            Intent(context, AlarmService::class.java).putExtra(ITEM_EXTRA, item).setAction(INTENT_FIRE_ALARM)
        context?.let { ContextCompat.startForegroundService(it, serviceIntent) }
    }

    private fun snoozeAlarm(item: AlarmItem) {
        repositoryScope.launch { repository.addItem(item.snoozed()) }
    }

    fun clear() {
        Log.e("AlarmControl", "clear()")
        wakelockProvider.releaseServiceLock()
        context = null
        repositoryScope.cancel()
    }
}
