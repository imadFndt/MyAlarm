package com.fndt.alarm.model

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.content.getSystemService
import javax.inject.Inject

class AlarmPlayer @Inject constructor(private val context: Context) {
    private var vibrator: Vibrator? = context.getSystemService()
    private val vibratorPattern = longArrayOf(0, 400, 600)

    private var ringtone: Ringtone? = null

    fun alarm(alarmItem: AlarmItem) {
        Log.d("AlarmPlayer", "Playing alarm")
        if (ringtone?.isPlaying == true) {
            ringtone?.stop()
            vibrator?.cancel()
        }
        ringtone = RingtoneManager.getRingtone(context, Uri.parse(alarmItem.melody))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) ringtone?.isLooping = true
        ringtone?.play()
        vibrator?.vibrateSDK(vibratorPattern, 0)
    }

    fun stop() {
        Log.d("AlarmPlayer", "Stop playing alarm")
        if (ringtone?.isPlaying == true) ringtone?.stop()
        vibrator?.cancel()
    }

    private fun Vibrator.vibrateSDK(pattern: LongArray, repeat: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            this.vibrate(pattern, 0)
        }
    }
}