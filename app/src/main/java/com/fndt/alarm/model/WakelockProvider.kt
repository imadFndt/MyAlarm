package com.fndt.alarm.model

import android.os.PowerManager
import com.fndt.alarm.model.util.ONE_HOUR_IN_MS
import com.fndt.alarm.model.util.WAKELOCK_TAG
import javax.inject.Inject

class WakelockProvider @Inject constructor(pm: PowerManager?) {
    var serviceLock: PowerManager.WakeLock? =
        pm?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK_TAG)

    fun acquireServiceLock() {
        serviceLock?.acquire(ONE_HOUR_IN_MS)
    }

    fun releaseServiceLock() {
        if (serviceLock?.isHeld == true) serviceLock?.release()
    }
}