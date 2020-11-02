package com.fndt.alarm.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fndt.alarm.databinding.AlarmActivityBinding
import com.fndt.alarm.model.util.AlarmPlayer

class AlarmActivity : AppCompatActivity() {
    private lateinit var binding: AlarmActivityBinding
    private val alarmPlayer by lazy { AlarmPlayer(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AlarmActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.turnoffButton.setOnClickListener {
            alarmPlayer.stop()
            finish()
        }
        alarmPlayer.alarm()
    }

    companion object {
        const val EXTRA_ITEM: String = "extraItem"
    }
}