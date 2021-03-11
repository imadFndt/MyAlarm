package com.fndt.alarm.presentation

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.fndt.alarm.domain.AlarmSetup
import com.fndt.alarm.domain.dto.NextAlarmItem
import com.fndt.alarm.domain.utils.INTENT_FIRE_ALARM
import com.fndt.alarm.presentation.util.BUNDLE_EXTRA
import com.fndt.alarm.presentation.util.BYTE_ITEM_EXTRA
import com.fndt.alarm.presentation.util.PENDING_REQUEST_CODE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
class AlarmSetupImpl @Inject constructor(private val context: Context) : AlarmSetup {

    override fun setAlarm(item: NextAlarmItem) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(BUNDLE_EXTRA, Bundle().apply { putByteArray(BYTE_ITEM_EXTRA, item.alarmItem.toByteArray()) })
            action = INTENT_FIRE_ALARM
        }
        val sender = PendingIntent.getBroadcast(
            context.applicationContext, PENDING_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val cal = item.alarmItem.time.getTimedCalendar()
        val am = (context.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
        Log.d("AlarmSetup", "Set event ${item.alarmItem.id} AT ${cal.time}")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            am.setAlarmClock(AlarmManager.AlarmClockInfo(item.timedCalendar.timeInMillis, sender), sender)
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, cal.timeInMillis, sender)
        }
    }

    override fun cancelAlarm() {
        Log.d("AlarmSetup", "Cancel")
        val intent = Intent(context, AlarmReceiver::class.java).apply { action = INTENT_FIRE_ALARM }
        val sender = PendingIntent.getBroadcast(
            context.applicationContext, PENDING_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val am = (context.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
        am.cancel(sender)
    }

    private fun Long.getTimedCalendar(): Calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, (this@getTimedCalendar / 60).toInt())
        set(Calendar.MINUTE, (this@getTimedCalendar % 60).toInt())
        set(Calendar.SECOND, 0)
    }
}