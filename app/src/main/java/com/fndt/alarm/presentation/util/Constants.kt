package com.fndt.alarm.presentation.util

import com.fndt.alarm.BuildConfig

const val WAKELOCK_TAG = "${BuildConfig.APPLICATION_ID}:WAKELOCK_TAG"
const val ONE_HOUR_IN_MS = 1000L * 60 * 60

const val DAY_CHOOSE_FRAGMENT_TAG = "DAY_CHOOSE_FRAGMENT"

//Intent shit
const val BUNDLE_EXTRA = "eventName"
const val BYTE_ITEM_EXTRA = "byteArrayEvent"
const val PENDING_REQUEST_CODE = 13
const val RINGTONE_REQ_CODE = 14
