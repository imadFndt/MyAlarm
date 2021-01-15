package com.fndt.alarm.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.fndt.alarm.model.util.AlarmApplication
import javax.inject.Inject


class AlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var eventHandler: AlarmEventHandler

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("Received", "Event ${intent.action}")
        (context.applicationContext as AlarmApplication).component.inject(this)
        eventHandler.handleEvent(intent)
    }
}