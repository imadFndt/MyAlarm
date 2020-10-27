package com.fndt.alarm.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.fndt.alarm.R
import com.fndt.alarm.databinding.MainActivityBinding
import com.fndt.alarm.model.util.AlarmApplication
import com.fndt.alarm.view.MainActivityViewModel.AlarmStatus

class MainActivity : AppCompatActivity() {
    lateinit var viewModelFactory: MainActivityViewModel.Factory
    private lateinit var binding: MainActivityBinding
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModelFactory = (application as AlarmApplication).component.getViewModelFactory()
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainActivityViewModel::class.java)
        viewModel.status.observe(this) { status ->
            when (status) {
                is AlarmStatus.Idle -> navigateTo(R.id.add_fragment, R.id.alarm_list)
                is AlarmStatus.EditStatus -> navigateTo(R.id.alarm_list, R.id.add_fragment)
            }
        }
    }

    private fun navigateTo(fromId: Int, toId: Int) {
        val controller = findNavController(R.id.nav_fragment)
        if (controller.currentDestination?.id == fromId) controller.navigate(toId)
    }
}