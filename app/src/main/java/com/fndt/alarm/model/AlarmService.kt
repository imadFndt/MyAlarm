package com.fndt.alarm.model

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.fndt.alarm.model.NotificationProvider.Companion.NOTIFICATION_ID
import com.fndt.alarm.model.util.AlarmApplication
import com.fndt.alarm.model.util.INTENT_FIRE_ALARM
import com.fndt.alarm.model.util.INTENT_STOP_ALARM
import com.fndt.alarm.model.util.ITEM_EXTRA
import javax.inject.Inject

class AlarmService : Service() {

    @Inject
    lateinit var alarmControl: AlarmControl

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        (application as AlarmApplication).component.inject(this)
        alarmControl.onServiceCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("SERVICE", "NEW INTENT WITH $intent")
        intent?.let {
            when (intent.action) {
                INTENT_FIRE_ALARM -> alarm(intent.getSerializableExtra(ITEM_EXTRA) as AlarmItem)
                INTENT_STOP_ALARM -> stop(intent.getSerializableExtra(ITEM_EXTRA) as AlarmItem)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun alarm(alarmItem: AlarmItem) {
        Log.e("SERIVCE FIRE", "EVENT ${alarmItem.id}")
        startForeground(NOTIFICATION_ID, alarmControl.notify(alarmItem))
        alarmControl.playSound()
    }

    private fun stop(alarmItem: AlarmItem) {
        Log.e("SERVICE", "STOP ${alarmItem.id}")
        stopForeground(true)
        alarmControl.stopAlarm()
    }
}