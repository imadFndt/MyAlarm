package com.fndt.alarm.presentation

import android.os.PowerManager
import com.fndt.alarm.domain.WakelockProvider
import com.fndt.alarm.presentation.util.ONE_HOUR_IN_MS
import com.fndt.alarm.presentation.util.WAKELOCK_TAG
import javax.inject.Inject

class WakelockProviderImpl @Inject constructor(pm: PowerManager?) : WakelockProvider {
    private var serviceLock: PowerManager.WakeLock? =
        pm?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK_TAG)

    override fun acquireServiceLock() {
        serviceLock?.acquire(ONE_HOUR_IN_MS)
    }

    override fun releaseServiceLock() {
        if (serviceLock?.isHeld == true) serviceLock?.release()
    }
}