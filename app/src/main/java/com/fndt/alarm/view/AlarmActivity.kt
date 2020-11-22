package com.fndt.alarm.view

import android.app.KeyguardManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.ViewModelProvider
import com.fndt.alarm.R
import com.fndt.alarm.databinding.AlarmActivityBinding
import com.fndt.alarm.model.AlarmService
import com.fndt.alarm.model.util.*


class AlarmActivity : AppCompatActivity() {
    private lateinit var binding: AlarmActivityBinding
    private lateinit var viewModel: AlarmViewModel
    private var kl: KeyguardManager.KeyguardLock? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AlarmActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewModelFactory =
            (application as AlarmApplication).component.getAlarmViewModelFactory()
        viewModel = ViewModelProvider(this, viewModelFactory).get(AlarmViewModel::class.java)
        val kg: KeyguardManager? = getSystemService()
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager: KeyguardManager? = getSystemService()
            keyguardManager?.requestDismissKeyguard(this, null)
        } else {
            this.window.addFlags(
                //TODO KEYGUARD
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        val item = intent?.getAlarmItem()
        item?.let {
            binding.alarmTime.text = item.time.toTimeString()
            binding.alarmName.text = "${item.name}dastardsdsfdsfs"
        }
        binding.turnoffButton.setOnClickListener {
            startService(
                Intent(this, AlarmService::class.java)
                    .putExtra(ITEM_EXTRA, item)
                    .setAction(INTENT_STOP_ALARM)
            )
            finish()
        }
    }

    companion object {
        const val EXTRA_ITEM: String = "extraItem"
    }
}