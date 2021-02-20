package com.fndt.alarm.presentation.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.fndt.alarm.R
import com.fndt.alarm.databinding.MainActivityBinding
import com.fndt.alarm.presentation.AlarmApplication
import com.fndt.alarm.presentation.viewmodels.MainActivityViewModel
import com.fndt.alarm.presentation.viewmodels.MainActivityViewModel.AlarmStatus

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
                is AlarmStatus.Idle -> navigateTo(R.id.add_fragment, R.id.to_list_fragment)
                is AlarmStatus.EditStatus -> navigateTo(R.id.alarm_list, R.id.to_add_fragment)
            }
        }
    }

    private fun navigateTo(fromId: Int, actionId: Int) {
        val controller = findNavController(R.id.nav_fragment)
        if (controller.currentDestination?.id == fromId) controller.navigate(actionId)
    }
}