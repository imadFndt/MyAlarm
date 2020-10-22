package com.fndt.alarm.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fndt.alarm.databinding.MainActivityBinding
import com.fndt.alarm.model.util.AlarmApplication

class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainActivityBinding
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        val viewModelFactory = (application as AlarmApplication).component.getViewModelFactory()
        viewModel = viewModelFactory.create(MainActivityViewModel::class.java)
        setContentView(binding.root)
    }
}