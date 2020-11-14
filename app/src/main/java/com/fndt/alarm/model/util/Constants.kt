package com.fndt.alarm.model.util

import com.fndt.alarm.BuildConfig

const val WAKELOCK_TAG = "${BuildConfig.APPLICATION_ID}:WAKELOCK_TAG"

//Intent shit
const val BUNDLE_EXTRA = "eventName"
const val ITEM_EXTRA = "eventAlarm"
const val BYTE_ITEM_EXTRA = "byteArrayEvent"

const val INTENT_FIRE_ALARM = "${BuildConfig.APPLICATION_ID}.FIRE_ALARM"
const val INTENT_STOP_ALARM = "${BuildConfig.APPLICATION_ID}.STOP_ALARM"
const val INTENT_SNOOZE_ALARM = "${BuildConfig.APPLICATION_ID}.SNOOZE_ALARM"
const val INTENT_SETUP_ALARM = "${BuildConfig.APPLICATION_ID}.SETUP_ALARM"
const val INTENT_CANCEL_ALARM = "${BuildConfig.APPLICATION_ID}.CANCEL_ALARM"
const val INTENT_ADD_ALARM = "${BuildConfig.APPLICATION_ID}.ADD_ALARM"
const val INTENT_REMOVE_ALARM = "${BuildConfig.APPLICATION_ID}.ADD_ALARM"

//REPEAT PERIOD CODES
const val REPEAT_SUNDAY: Byte = 0b1000000
const val REPEAT_MONDAY: Byte = 0b0100000
const val REPEAT_TUESDAY: Byte = 0b0010000
const val REPEAT_WEDNESDAY: Byte = 0b0001000
const val REPEAT_THURSDAY: Byte = 0b0000100
const val REPEAT_FRIDAY: Byte = 0b0000010
const val REPEAT_SATURDAY: Byte = 0b0000001
const val REPEAT_NONE: Byte = 0b0000000
const val REPEAT_ONCE_DESTROY: Byte = -0b0000001

//WAKELOCK LITERALS
const val ONE_HOUR_IN_MS = 1000L * 60 * 60

//SERVICE COMMAND
const val SERVICE_START = "${BuildConfig.APPLICATION_ID}.SERVICE_START"
