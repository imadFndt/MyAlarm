package com.fndt.alarm.domain

interface IWakelockProvider {
    fun acquireServiceLock()
    fun releaseServiceLock()
}
