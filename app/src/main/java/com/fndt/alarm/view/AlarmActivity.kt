package com.fndt.alarm.view

import android.app.KeyguardManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.ViewModelProvider
import com.fndt.alarm.R
import com.fndt.alarm.databinding.AlarmActivityBinding
import com.fndt.alarm.model.AlarmItem
import com.fndt.alarm.model.AlarmReceiver
import com.fndt.alarm.model.util.*


class AlarmActivity : AppCompatActivity() {
    private lateinit var binding: AlarmActivityBinding
    private lateinit var viewModel: AlarmViewModel
    private var currentItem: AlarmItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("AlarmActivity", "OnCreate()")
        super.onCreate(savedInstanceState)
        binding = AlarmActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewModelFactory = (application as AlarmApplication).component.getAlarmViewModelFactory()
        viewModel = ViewModelProvider(this, viewModelFactory).get(AlarmViewModel::class.java)
        viewModel.alarmingItem.observe(this) { item ->
            item?.let {
                binding.alarmTime.text = item.time.toTimeString()
                binding.alarmName.text = item.name
                currentItem = item
            } ?: run { currentItem?.let { finish() } }
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

        turnScreenOnAndKeyguardOff()

        binding.turnoffButton.setOnClickListener {
            currentItem?.toIntent(INTENT_STOP_ALARM)?.setClass(this, AlarmReceiver::class.java)
                ?.let { sendBroadcast(it) }
        }
        binding.snoozeButton.setOnClickListener {
            currentItem?.toIntent(INTENT_SNOOZE_ALARM)?.setClass(this, AlarmReceiver::class.java)
                ?.let { sendBroadcast(it) }
        }
    }

    private fun turnScreenOnAndKeyguardOff() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            )
        }
        with(getSystemService() as KeyguardManager?) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val callback = object : KeyguardManager.KeyguardDismissCallback() {
                    override fun onDismissSucceeded() {
                        Log.d("AlarmActivity", "Dismissed keyguard")
                    }

                    override fun onDismissCancelled() {
                        Log.d("AlarmActivity", "Dismiss Cancelled")
                    }

                    override fun onDismissError() {
                        Log.d("AlarmActivity", "Dismiss error")
                    }
                }
                this?.requestDismissKeyguard(this@AlarmActivity, callback)
            }
        }
    }
}