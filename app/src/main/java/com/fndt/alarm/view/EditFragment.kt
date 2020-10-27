package com.fndt.alarm.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.fndt.alarm.databinding.AddFragmentBinding
import com.fndt.alarm.model.AlarmItem
import com.fndt.alarm.model.util.AlarmApplication
import java.util.*

class EditFragment : Fragment() {
    private lateinit var binding: AddFragmentBinding
    private val viewModel: MainActivityViewModel by activityViewModels {
        (requireActivity().application as AlarmApplication).component.getViewModelFactory()
    }
    private lateinit var currentItem: AlarmItem

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = AddFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val time: Long
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        time = (hour * 60 + minute).toLong()
        binding.temporaryTimePicker.apply {
            setIs24HourView(true)
            setTime(hour, minute)
            setOnTimeChangedListener { _, hourOfDay, minute ->
                currentItem.time = (hourOfDay * 60 + minute).toLong()
            }
        }
        currentItem = AlarmItem(time, "DefaultName", true)
        binding.doneButton.setOnClickListener { viewModel.addItem(currentItem) }
        binding.closeButton.setOnClickListener { viewModel.cancelItemUpdate() }
    }

    private fun TimePicker.setTime(hour: Int, minute: Int) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            this.hour = hour
            this.minute = minute
        } else {
            this.currentHour = hour
            this.currentMinute = minute
        }
    }
}