package com.fndt.alarm.model.util

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.fndt.alarm.R
import com.fndt.alarm.model.AlarmItem
import com.fndt.alarm.model.AlarmItem.Companion.toTimeString
import com.fndt.alarm.view.MainActivity
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        const val NOTIFICATION_CODE = 2
        private const val CHANNEL_NAME = "mainChannel"
        private const val EVENT = "eventName"
        private const val ALARM_EVENT = "eventAlarm"
        private var NOTIFICATION_ID = 0
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.e("RECEIVED", "EVENT")
        val name: CharSequence = "Notifier"
        val channel: NotificationChannel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            channel = NotificationChannel(CHANNEL_NAME, name, importance)
            channel.description = "Alarm channel"
            val notificationManager = context.getSystemService(NotificationManager::class.java)!!
            notificationManager.createNotificationChannel(channel)
        }

        val bundle = intent.getBundleExtra(EVENT)
        val event = bundle?.getSerializable(ALARM_EVENT) as AlarmItem?

        val builder = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Notification.Builder(context, CHANNEL_NAME)
        } else {
            Notification.Builder(context)
        }

        val notifyIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, NOTIFICATION_CODE, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationCompat = builder.setContentTitle(event!!.name)
            .setContentText(event.time.toTimeString())
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
        val managerCompat = NotificationManagerCompat.from(context)
        managerCompat.notify(NOTIFICATION_ID++, notificationCompat)
        //TODO CALLNEXT
    }

    private fun setAlarm(context: Context, alarmItem: AlarmItem) {
        val cal = Calendar.getInstance()
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            val bundle = Bundle().apply { putSerializable(ALARM_EVENT, alarmItem) }
            putExtra(EVENT, bundle)
        }
        val sender =
            PendingIntent.getBroadcast(
                context, alarmItem.id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        val am = (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, sender)
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, cal.timeInMillis, sender)
        }
    }

    fun cancelNotifications(context: Context, alarmItem: AlarmItem) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val sender =
            PendingIntent.getBroadcast(
                context, alarmItem.id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        val am = (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
        am.cancel(sender)
    }
}