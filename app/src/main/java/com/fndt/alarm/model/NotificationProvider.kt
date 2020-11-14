package com.fndt.alarm.model

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.getSystemService
import com.fndt.alarm.R
import com.fndt.alarm.model.AlarmItem.Companion.toTimeString
import com.fndt.alarm.model.util.ITEM_EXTRA
import com.fndt.alarm.view.AlarmActivity
import javax.inject.Inject


class NotificationProvider @Inject constructor(private var context: Context) {
    private fun Notification.Builder.setPriorityIfLowApi(priority: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) setPriority(priority)
    }

    fun notify(event: AlarmItem): Notification {
        Log.e("RECEIVED", "EVENT")
        val name: CharSequence = "Alarm Notifier"
        val channel: NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            channel = NotificationChannel(CHANNEL_NAME, name, importance)
            channel.importance = NotificationManager.IMPORTANCE_HIGH
            channel.description = "Alarm channel"
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val notificationManager = context.getSystemService(NotificationManager::class.java)!!
            notificationManager.createNotificationChannel(channel)
        }
        val builder = getBuilder(context)

        val notifyIntent = Intent(context, AlarmActivity::class.java).putExtra(ITEM_EXTRA, event)
        val pendingIntent = PendingIntent.getActivity(
            context, NOTIFICATION_CODE, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        return builder.setContentTitle(event.name)
            .setContentText(event.time.toTimeString())
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setCategory(Notification.CATEGORY_ALARM)
            .setFullScreenIntent(pendingIntent, true)
            .apply { setPriorityIfLowApi(Notification.PRIORITY_MAX) }
            .build()
    }

    fun cancelNotification() {
        val mNotificationManager: NotificationManager? = context.getSystemService()
        mNotificationManager?.cancel(NOTIFICATION_ID)
    }

    private fun getBuilder(context: Context) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Notification.Builder(context, CHANNEL_NAME)
    } else {
        Notification.Builder(context)
    }

    companion object {
        const val NOTIFICATION_CODE = 2
        private const val CHANNEL_NAME = "mainChannel"
        var NOTIFICATION_ID = 10
    }
}