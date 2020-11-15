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
            context.applicationContext, 13, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val cal = alarmItem.time.getTimedCalendar()
        val am =
            (context.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
        Log.d(
            "SETUP SET",
            "EVENT ${alarmItem.id} AT ${cal.get(Calendar.HOUR_OF_DAY)} : ${cal.get(Calendar.MINUTE)}"
        )
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            am.setAlarmClock(AlarmManager.AlarmClockInfo(cal.timeInMillis, sender), sender)
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, cal.timeInMillis, sender)
        }
    }

    fun cancelAlarm() {
        Log.d("SETUP CANCEL", "ALARM")
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = INTENT_FIRE_ALARM
        }
        val sender =
            PendingIntent.getBroadcast(
                context.applicationContext, 13, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        val am =
            (context.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
        am.cancel(sender)
    }

    private fun Long.getTimedCalendar(): Calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, (this@getTimedCalendar / 60).toInt())
        set(Calendar.MINUTE, (this@getTimedCalendar % 60).toInt())
        set(Calendar.SECOND, 0)
    }
}