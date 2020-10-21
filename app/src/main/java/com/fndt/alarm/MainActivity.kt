package com.fndt.alarm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.fndt.alarm.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.alarmList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = AlarmListAdapter()
        }
    }
}