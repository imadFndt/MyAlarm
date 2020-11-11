package com.fndt.alarm.model

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.fndt.alarm.model.util.BUNDLE_EXTRA
import com.fndt.alarm.model.util.BYTE_ITEM_EXTRA
import com.fndt.alarm.model.util.INTENT_FIRE_ALARM
import java.util.*
import javax.inject.Inject

class AlarmSetup @Inject constructor(private val context: Context) {
    var onChange: ((AlarmItem) -> Unit)? = null

    fun setAlarm(alarmItem: AlarmItem) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(BUNDLE_EXTRA, Bundle().apply {
                putByteArray(BYTE_ITEM_EXTRA, alarmItem.toByteArray())
            })
            action = INTENT_FIRE_ALARM
        }
        val sender = PendingIntent.getBroadcast(
            context, alarmItem.id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val cal = alarmItem.time.getTimedCalendar()
        val am = (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
        Log.d(
            "SET",
            "EVENT AT ${cal.get(Calendar.HOUR_OF_DAY)} : ${cal.get(Calendar.MINUTE)}"
        )
        alarmItem.isActive = true
        //TODO setAlarmClock
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, sender)
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, cal.timeInMillis, sender)
        }
        onChange?.invoke(alarmItem)
    }

    fun cancelAlarm(alarmItem: AlarmItem) {
        Log.d("CANCELED", "EVENT$alarmItem")
        val intent = Intent(context, AlarmReceiver::class.java)
        alarmItem.isActive = false
        val sender =
            PendingIntent.getBroadcast(
                context, alarmItem.id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        val am = (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
        am.cancel(sender)
        onChange?.invoke(alarmItem)
    }

    private fun Long.getTimedCalendar(): Calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, (this@getTimedCalendar / 60).toInt())
        set(Calendar.MINUTE, (this@getTimedCalendar % 60).toInt())
        set(Calendar.SECOND, 0)
    }
}