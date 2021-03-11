package com.fndt.alarm.domain

interface WakelockProvider {
    fun acquireServiceLock()
    fun releaseServiceLock()
}
