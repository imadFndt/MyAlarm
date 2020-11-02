package com.fndt.alarm.model.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import com.fndt.alarm.R
import com.fndt.alarm.model.AlarmItem
import com.fndt.alarm.model.AlarmItem.Companion.toTimeString
import com.fndt.alarm.view.AlarmActivity
import com.fndt.alarm.view.main.MainActivity

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.e("RECEIVED", "EVENT")
        val name: CharSequence = "Notifier"
        val channel: NotificationChannel
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            channel = NotificationChannel(CHANNEL_NAME, name, importance)
            channel.importance = NotificationManager.IMPORTANCE_HIGH
            channel.description = "Alarm channel"
            val notificationManager = context.getSystemService(NotificationManager::class.java)!!
            notificationManager.createNotificationChannel(channel)
        }

        val event = intent.getBundleExtra(EVENT)?.getSerializable(ALARM_EVENT) as AlarmItem

        val builder = if (VERSION.SDK_INT >= VERSION_CODES.O) {
            Notification.Builder(context, CHANNEL_NAME)
        } else {
            Notification.Builder(context)
        }

        val notifyIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, NOTIFICATION_CODE, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = builder.setContentTitle(event.name)
            .setContentText(event.time.toTimeString())
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .apply { setPriorityIfLowApi(Notification.PRIORITY_MAX) }
            .build()
        val managerCompat = NotificationManagerCompat.from(context)
        //managerCompat.notify(NOTIFICATION_ID++, notification)
        val connectivityManager: ConnectivityManager? = context.getSystemService()
        //connectivityManager?.activi
        context.startActivity(
            Intent(context, AlarmActivity::class.java).apply {
                putExtra(AlarmActivity.EXTRA_ITEM, event)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
    }

    private fun Notification.Builder.setPriorityIfLowApi(priority: Int) {
        if (VERSION.SDK_INT < VERSION_CODES.O) setPriority(priority)
    }

    companion object {
        const val NOTIFICATION_CODE = 2
        private const val CHANNEL_NAME = "mainChannel"
        const val EVENT = "eventName"
        const val ALARM_EVENT = "eventAlarm"
        private var NOTIFICATION_ID = 0
    }
}