package com.fndt.alarm.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.fndt.alarm.model.util.*
import javax.inject.Inject


class AlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var control: AlarmControl

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("Received", "Event ${intent.action}")
        (context.applicationContext as AlarmApplication).component.inject(this)
        when (intent.action) {
            INTENT_FIRE_ALARM -> control.handleEventSync(intent.transformToRegularIntent())
            INTENT_STOP_ALARM, INTENT_SNOOZE_ALARM -> control.handleEventSync(intent)
        }
    }

    private fun Intent.transformToRegularIntent(): Intent {
        val item = AlarmItem.fromByteArray(
            this.getBundleExtra(BUNDLE_EXTRA)?.getSerializable(BYTE_ITEM_EXTRA) as ByteArray
        )
        return item.toIntent(INTENT_FIRE_ALARM)
    }
}