package com.fndt.alarm.model.util

import android.content.Intent
import com.fndt.alarm.model.AlarmItem

fun AlarmItem.toIntent(action: String) = Intent().apply {
    setAction(action)
    putExtra(ITEM_EXTRA, this@toIntent)
}

fun Intent.getAlarmItem() = this.getSerializableExtra(ITEM_EXTRA) as AlarmItem?


