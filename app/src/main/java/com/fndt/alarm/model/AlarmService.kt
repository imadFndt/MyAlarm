package com.fndt.alarm.model

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.fndt.alarm.model.util.AlarmApplication
import javax.inject.Inject

class AlarmService : Service() {

    @Inject
    lateinit var alarmControl: AlarmControl

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        (application as AlarmApplication).component.inject(this)
        alarmControl.onServiceCreate()
        alarmControl.playSound()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        alarmControl.clear()
        super.onDestroy()
    }
}