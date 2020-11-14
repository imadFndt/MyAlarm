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
        Log.e("RECEIVED", "EVENT ${intent.action}")
        (context.applicationContext as AlarmApplication).component.inject(this)
        control.handleEventSync(intent.transformToRegularIntent())
    }

    private fun Intent.transformToRegularIntent(): Intent {
        val item = AlarmItem.fromByteArray(
            this.getBundleExtra(BUNDLE_EXTRA)?.getSerializable(BYTE_ITEM_EXTRA) as ByteArray
        )
        return item.toIntent(INTENT_FIRE_ALARM)
    }
}