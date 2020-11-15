package com.fndt.alarm.model

import android.content.Context
import android.content.Intent
import android.util.Log
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
    private val repository: AlarmRepository
) {
    private val repositoryScope = MainScope()

    val alarmList: LiveData<List<AlarmItem>> get() = repository.alarmList
    val action: LiveData<Action> get() = actionData

    private val actionData: MutableLiveData<Action> = MutableLiveData()

    private val nextAlarmObserver = Observer<AlarmItem?> { item ->
        Log.e("control nextObserver", "received ${item?.time}")
        if (savedValue != item) item?.let { setupAlarm(item) } ?: cancelAlarm()
        savedValue = item
    }
    private var savedValue: AlarmItem? = null

    init {
        alarmSetup.onChange =
            { alarmItem -> repositoryScope.launch { repository.changeAlarm(alarmItem) } }
        repository.nextAlarm.observeForever(nextAlarmObserver)
    }

    fun onServiceCreate() {
        wakelockProvider.acquireServiceLock()
    }

    suspend fun handleEventAsync(intent: Intent) {
        val item = intent.getAlarmItem()
        item ?: return
        when (intent.action) {
            INTENT_ADD_ALARM -> repository.addItem(item)
        }
    }

    fun handleEventSync(intent: Intent) {
        val item = intent.getAlarmItem()
        item ?: return
        Log.d("ALARMCONTROL", "Got event ${intent.action}")
        when (intent.action) {
            INTENT_FIRE_ALARM -> fireAlarm(item)
            INTENT_SNOOZE_ALARM -> TODO()
        }
    }

    private fun setupAlarm(alarmItem: AlarmItem) {
        alarmSetup.setAlarm(alarmItem)
    }

    private fun cancelAlarm() {
        alarmSetup.cancelAlarm()
    }

    private fun fireAlarm(alarmItem: AlarmItem) {
        //TODO REMOVE
        alarmItem.isActive = !alarmItem.isActive
        repositoryScope.launch { handleEventAsync(alarmItem.toIntent(INTENT_ADD_ALARM)) }
        Log.d("ALARMCONTROL", "FIRING CONTEXT IS NULL: ${context == null}")

        Log.d("ALARMCONTROL", "FIRING ${alarmItem.id}")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context?.startService(
                Intent(context, AlarmService::class.java)
                    .putExtra(ITEM_EXTRA, alarmItem)
                    .setAction(INTENT_FIRE_ALARM)
            )
        }
    }

    fun clear() {
        Log.e("CONTROL", "Clear")
        //TODO Cancel async jobs
        wakelockProvider.releaseServiceLock()
        context = null
        repositoryScope.cancel()
    }

    sealed class Action {
        object Nothing : Action()
        data class FireAlarm(val alarmItem: AlarmItem) : Action()
    }
}