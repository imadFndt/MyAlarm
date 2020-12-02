package com.fndt.alarm.model

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.fndt.alarm.model.NotificationProvider.Companion.NOTIFICATION_ID
import com.fndt.alarm.model.util.AlarmApplication
import com.fndt.alarm.model.util.INTENT_FIRE_ALARM
import com.fndt.alarm.model.util.INTENT_STOP_ALARM
import com.fndt.alarm.model.util.getAlarmItem
import javax.inject.Inject

class AlarmService : Service() {

    @Inject
    lateinit var alarmControl: AlarmControl

    @Inject
    lateinit var player: AlarmPlayer

    @Inject
    lateinit var notificationProvider: NotificationProvider

    private var isForegroundService = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        (application as AlarmApplication).component.inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("AlarmService", "New intent with $intent")
        intent?.let {
            when (intent.action) {
                INTENT_FIRE_ALARM -> intent.getAlarmItem()?.let { item -> alarm(item) }
                INTENT_STOP_ALARM -> intent.getAlarmItem()?.let { item -> stop(item) }
                else -> Unit
            }
        }
        return START_STICKY
    }

    private fun alarm(alarmItem: AlarmItem) {
        Log.d("AlarmService", "Event ${alarmItem.id}")
        alarmControl.acquireWakeLock()
        if (!isForegroundService) {
            isForegroundService = true
            startForeground(NOTIFICATION_ID, notify(alarmItem))
            player.alarm(alarmItem, this)
        }
    }

    private fun stop(alarmItem: AlarmItem) {
        alarmControl.releaseWakeLock()
        isForegroundService = false
        Log.d("AlarmService", "Stop ${alarmItem.id}")
        stopForeground(true)
        notificationProvider.cancelNotification()
        player.stop()
    }

    private fun notify(alarmItem: AlarmItem) = notificationProvider.notify(alarmItem)

}