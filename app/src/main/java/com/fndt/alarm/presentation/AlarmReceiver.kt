package com.fndt.alarm.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.fndt.alarm.domain.AlarmEventHandler
import com.fndt.alarm.domain.dto.AlarmIntent
import com.fndt.alarm.presentation.util.getAlarmItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class AlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var eventHandler: AlarmEventHandler

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("Received", "Event ${intent.action}")
        (context.applicationContext as AlarmApplication).component.inject(this)
        val alarmIntent = AlarmIntent(intent.getAlarmItem(), intent.action)
        eventHandler.handleEvent(alarmIntent)
    }
}