package com.fndt.alarm.model

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import com.fndt.alarm.model.db.AlarmRepository
import com.fndt.alarm.model.util.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmControl @Inject constructor(
    private var context: Context?,
    private val player: AlarmPlayer,
    private val notificationProvider: NotificationProvider,
    private val alarmSetup: AlarmSetup,
    private val wakelockProvider: WakelockProvider,
    private val repository: AlarmRepository
) {
    private val repositoryScope = GlobalScope

    val alarmList: LiveData<List<AlarmItem>> get() = repository.alarmList

    init {
        alarmSetup.onChange =
            { alarmItem -> repositoryScope.launch { repository.changeAlarm(alarmItem) } }
    }

    fun onServiceCreate() {
        wakelockProvider.acquireServiceLock()
    }

    suspend fun handleEventAsync(intent: Intent) {
        val item = intent.extras?.get(ITEM_EXTRA) as AlarmItem?
        item ?: return
        when (intent.action) {
            INTENT_ADD_ALARM -> repository.addItem(item)
        }
    }

    fun handleEventSync(intent: Intent) {
        val bundle = intent.getBundleExtra(BUNDLE_EXTRA)
        val item = bundle?.getSerializable(ITEM_EXTRA) as AlarmItem?
        item ?: return
        Log.d("ALARMCONTROL", "Got event ${intent.action}")
        when (intent.action) {
            INTENT_FIRE_ALARM -> fireAlarm(item)
            INTENT_SETUP_ALARM -> setupAlarm(item)
            INTENT_CANCEL_ALARM -> cancelAlarm(item)
            INTENT_STOP_ALARM -> stopAlarm()
            INTENT_SNOOZE_ALARM -> TODO()
        }
    }

    fun playSound() {
        player.alarm()
    }

    fun notify(alarmItem: AlarmItem) = notificationProvider.notify(alarmItem)

    private fun setupAlarm(alarmItem: AlarmItem) {
        alarmSetup.setAlarm(alarmItem)
    }

    private fun cancelAlarm(alarmItem: AlarmItem) {
        alarmSetup.cancelAlarm(alarmItem)
    }

    private fun fireAlarm(alarmItem: AlarmItem) {
        Log.d("ALARMCONTROL", "FIRING ${alarmItem.id}")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context?.startService(
                Intent(context, AlarmService::class.java)
                    .putExtra(ITEM_EXTRA, alarmItem)
                    .setAction(INTENT_FIRE_ALARM)
            )
        }
    }

    fun stopAlarm() {
        context?.let { notificationProvider.cancelNotification() }
        player.stop()
    }

    fun clear() {
        Log.e("FUCK", "SERVICE")
        //repositoryScope.cancel()
        wakelockProvider.releaseServiceLock()
        context = null
    }
}