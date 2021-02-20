package com.fndt.alarm.presentation

import android.content.Context
import androidx.core.content.ContextCompat
import com.fndt.alarm.domain.IServiceHandler
import com.fndt.alarm.model.AlarmItem
import com.fndt.alarm.model.util.toIntent
import javax.inject.Inject

class ServiceHandler @Inject constructor(val context: Context?): IServiceHandler {
    override fun startService(action: String, alarmItem: AlarmItem) {
        context?.startService(action, alarmItem)
    }

    private fun Context.startService(intentAction: String, alarmItem: AlarmItem) {
        val serviceIntent = alarmItem.toIntent(intentAction).setClass(this, AlarmService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }
}