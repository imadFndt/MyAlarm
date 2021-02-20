package com.fndt.alarm.domain

interface WakeLockUseCase {
    fun acquireWakeLock()
    fun releaseWakeLock()
}
