package com.fndt.alarm.model

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.content.getSystemService
import com.fndt.alarm.R
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.LoopingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import com.google.android.exoplayer2.util.Util
import javax.inject.Inject

class AlarmPlayer @Inject constructor(private val context: Context) {
    private lateinit var player: ExoPlayer
    private var vibrator: Vibrator? = context.getSystemService()
    private val vibratorPattern = longArrayOf(0, 400, 600)

    private var isPlaying = false

    private var audioFocusGain = false
    private val focusLock = Any()
    private val audioFocusListener = PlayerAudioFocusChangeListener()
    private val audioFocusRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setOnAudioFocusChangeListener(audioFocusListener)
            .build()
    } else {
        null
    }

    fun alarm() {
        if (isPlaying) {
            player.release()
            vibrator?.cancel()
        }
        player = getPlayer()
        val dataSourceFactory = DefaultDataSourceFactory(
            context, Util.getUserAgent(context, "task5"), null
        )
        //todo choose sound
        val source = LoopingMediaSource(
            ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
                MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(R.raw.radar))
            )
        )
        player.setMediaSource(source)
        player.prepare()
        requestAudioFocus()
        player.playWhenReady = true
        vibrator?.vibrateSDK(vibratorPattern, 0)
    }

    fun stop() {
        player.playWhenReady = false
        player.release()
        vibrator?.cancel()
        abandonAudioFocus()
    }

    @SuppressLint("NewApi")
    private fun requestAudioFocus() {
        val audioManager: AudioManager? = context.getSystemService()
        val result = audioFocusRequest?.let {
            audioManager?.requestAudioFocus(audioFocusRequest)
        } ?: run {
            audioManager?.requestAudioFocus(
                audioFocusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN
            )
        }
        synchronized(focusLock) {
            audioFocusGain = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
    }

    @SuppressLint("NewApi")
    private fun abandonAudioFocus() {
        val audioManager: AudioManager? = context.getSystemService()
        audioFocusRequest?.let { audioManager?.abandonAudioFocusRequest(audioFocusRequest) }
            ?: run { audioManager?.abandonAudioFocus(audioFocusListener) }
        synchronized(focusLock) { audioFocusGain = false }
    }

    private fun Vibrator.vibrateSDK(pattern: LongArray, repeat: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            this.vibrate(pattern, 0)
        }
    }

    private fun getPlayer() = SimpleExoPlayer.Builder(context).build().apply {
        setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_ALARM)
                .build(),
            false
        )
    }

    private inner class PlayerAudioFocusChangeListener : AudioManager.OnAudioFocusChangeListener {
        override fun onAudioFocusChange(focusChange: Int) {
            when (focusChange) {
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT, AudioManager.AUDIOFOCUS_LOSS -> {
                    synchronized(focusLock) {
                        audioFocusGain = false
                        player.playWhenReady = false
                    }
                }
                AudioManager.AUDIOFOCUS_GAIN, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT -> {
                    synchronized(focusLock) {
                        audioFocusGain = true
                        player.playWhenReady = true
                    }
                }
            }
        }
    }
}