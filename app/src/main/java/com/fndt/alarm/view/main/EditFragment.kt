package com.fndt.alarm.view.main

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.fndt.alarm.R
import com.fndt.alarm.databinding.AddFragmentBinding
import com.fndt.alarm.databinding.InputTextLayoutBinding
import com.fndt.alarm.model.AlarmItem
import com.fndt.alarm.model.util.AlarmApplication
import com.fndt.alarm.model.util.INTENT_ADD_ALARM
import com.fndt.alarm.model.util.ITEM_EXTRA
import com.fndt.alarm.model.util.REPEAT_NONE
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
        currentItem = AlarmItem(time, "DefaultName", true, REPEAT_NONE, 1)
        binding.temporaryTimePicker.apply {
            setIs24HourView(true)
            setTime(hour, minute)
            setOnTimeChangedListener { _, hourOfDay, minute ->
                currentItem.time = (hourOfDay * 60 + minute).toLong()
            }
        }
        binding.descriptionValue.text = currentItem.name
        binding.doneButton.setOnClickListener {
            val intent = Intent(INTENT_ADD_ALARM).apply {
                putExtra(ITEM_EXTRA, currentItem)
            }
            viewModel.addAlarm(intent)
        }
        binding.closeButton.setOnClickListener { viewModel.cancelItemUpdate() }
        binding.descriptionLayout.setOnClickListener { buildDescriptionDialog() }
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

    private fun buildDescriptionDialog() {
        val dialog = Dialog(requireContext())
        val dialogBinding = InputTextLayoutBinding.inflate(dialog.layoutInflater)
        dialogBinding.descriptionDoneButton.setOnClickListener {
            currentItem.name = dialogBinding.editText.text.toString()
            binding.descriptionValue.text = currentItem.name
            dialog.hide()
        }
        dialogBinding.descriptionCancelButton.setOnClickListener { dialog.hide() }
        dialogBinding.editText.text.append(currentItem.name)
        val height = resources.getDimension(R.dimen.dialogHeight).toInt()
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, height)
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.show()
    }
}