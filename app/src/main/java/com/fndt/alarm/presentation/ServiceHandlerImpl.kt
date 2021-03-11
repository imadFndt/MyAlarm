package com.fndt.alarm.presentation

import android.content.Context
import androidx.core.content.ContextCompat
import com.fndt.alarm.domain.ServiceHandler
import com.fndt.alarm.domain.dto.AlarmItem
import com.fndt.alarm.presentation.util.toIntent
import javax.inject.Inject

class ServiceHandlerImpl @Inject constructor(val context: Context?) : ServiceHandler {
    override fun startService(action: String, alarmItem: AlarmItem) {
        context?.startService(action, alarmItem)
    }

    private fun Context.startService(intentAction: String, alarmItem: AlarmItem) {
        val serviceIntent = alarmItem.toIntent(intentAction).setClass(this, AlarmService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }
}