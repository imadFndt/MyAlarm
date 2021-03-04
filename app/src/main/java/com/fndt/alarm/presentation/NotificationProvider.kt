package com.fndt.alarm.presentation

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.fndt.alarm.R
import com.fndt.alarm.domain.dto.AlarmItem
import com.fndt.alarm.domain.utils.INTENT_STOP_ALARM
import com.fndt.alarm.presentation.activities.AlarmActivity
import com.fndt.alarm.presentation.util.toIntent
import com.fndt.alarm.presentation.util.toTimeString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class NotificationProvider @Inject constructor(private var context: Context) {
    fun notify(event: AlarmItem): Notification {
        Log.e("NotificationProvider", "Event")
        val name: CharSequence = "Alarm Notifier"
        val channel: NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            channel = NotificationChannel(CHANNEL_NAME, name, importance)
            channel.description = "Alarm channel"
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            channel.vibrationPattern = longArrayOf(0, 50)
            channel.setSound(null, null)
            val notificationManager = context.getSystemService(NotificationManager::class.java)!!
            notificationManager.createNotificationChannel(channel)
        }
        val builder = NotificationCompat.Builder(context, CHANNEL_NAME)

        val notifyIntent = Intent(context, AlarmActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, NOTIFICATION_CODE, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val turnoffIntent = event.toIntent(INTENT_STOP_ALARM).setClass(context, AlarmReceiver::class.java)
        val turnoffPendingIntent = PendingIntent.getBroadcast(
            context, TURNOFF_CODE, turnoffIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val turnoffAction =
            NotificationCompat.Action(0, context.resources.getString(R.string.turn_off), turnoffPendingIntent)

        val snoozeIntent = event.toIntent(INTENT_STOP_ALARM).setClass(context, AlarmReceiver::class.java)
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context, TURNOFF_CODE, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val snoozeAction =
            NotificationCompat.Action(0, context.resources.getString(R.string.snooze), snoozePendingIntent)

        return builder.setContentTitle(event.name)
            .setContentText(event.time.toTimeString())
            .setSmallIcon(R.drawable.ic_baseline_alarm_24)
            .setContentTitle(if (event.name.isEmpty()) context.resources.getText(R.string.alarm) else event.name)
            .setCategory(Notification.CATEGORY_ALARM)
            .addAction(turnoffAction)
            .addAction(snoozeAction)
            .setFullScreenIntent(pendingIntent, true)
            .apply {
                setPriorityIfLowApi(Notification.PRIORITY_MAX)
                setChannelIdIfLowApi(CHANNEL_NAME)
            }
            .build()
    }

    private fun NotificationCompat.Builder.setChannelIdIfLowApi(channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) setChannelId(channelName)
    }

    private fun NotificationCompat.Builder.setPriorityIfLowApi(priority: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) setPriority(priority)
    }


    fun cancelNotification() {
        val mNotificationManager: NotificationManager? = context.getSystemService()
        mNotificationManager?.cancel(NOTIFICATION_ID)
    }

    companion object {
        const val TURNOFF_CODE = 3
        const val NOTIFICATION_CODE = 2
        private const val CHANNEL_NAME = "mainChannel"
        var NOTIFICATION_ID = 10
    }
}