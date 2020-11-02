package com.fndt.alarm.model.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.fndt.alarm.model.AlarmItem
import java.util.*

class AlarmControl {

    fun setAlarm(context: Context, alarmItem: AlarmItem) {
        val cal = Calendar.getInstance()
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            val bundle = Bundle().apply { putSerializable(AlarmReceiver.ALARM_EVENT, alarmItem) }
            putExtra(AlarmReceiver.EVENT, bundle)
        }
        //TODO ALARM SET
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        cal.set(Calendar.HOUR_OF_DAY, (alarmItem.time / 60).toInt())
        cal.set(Calendar.MINUTE, (alarmItem.time % 60).toInt())
        cal.set(Calendar.SECOND, 0)
        val sender = PendingIntent.getService(
            context, alarmItem.id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val am = (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
        Log.d("SET", "EVENT")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, sender)
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, cal.timeInMillis, sender)
        }
    }

    fun cancelAlarm(context: Context, alarmItem: AlarmItem) {
        Log.d("CANCEL", "EVENT")
        val intent = Intent(context, AlarmReceiver::class.java)
        val sender =
            PendingIntent.getBroadcast(
                context, alarmItem.id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        val am = (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
        am.cancel(sender)
    }
}