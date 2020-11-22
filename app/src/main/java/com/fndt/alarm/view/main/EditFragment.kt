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
import com.fndt.alarm.databinding.DayChooseLayout2Binding
import com.fndt.alarm.databinding.InputTextLayoutBinding
import com.fndt.alarm.model.AlarmItem
import com.fndt.alarm.model.util.*
import java.util.*
import kotlin.experimental.or

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
        viewModel.status.observe(viewLifecycleOwner) { status ->
            if (status is MainActivityViewModel.AlarmStatus.EditStatus) {
                status.item?.let {
                    currentItem = it
                } ?: run {
                    currentItem = AlarmItem(time, "Alarm!", true, REPEAT_NONE, 1)
                }
                updateView(currentItem)
            }
        }
        val daysAdapter = DaysAdapter()
        binding.dayChoose.daysList.adapter = daysAdapter

        binding.temporaryTimePicker.apply {
            setIs24HourView(true)
            setOnTimeChangedListener { _, hourOfDay, minute ->
                currentItem.time = (hourOfDay * 60 + minute).toLong()
            }
        }
        binding.doneButton.setOnClickListener {
            val intent = Intent(INTENT_ADD_ALARM).apply {
                putExtra(ITEM_EXTRA, currentItem)
            }
            viewModel.addAlarm(intent)
        }
        binding.closeButton.setOnClickListener { viewModel.cancelItemUpdate() }
        binding.repeatLayout.setOnClickListener { buildDayChooseDialog() }
        binding.descriptionLayout.setOnClickListener { buildDescriptionDialog() }
    }

    private fun updateView(currentItem: AlarmItem) {
        binding.temporaryTimePicker.setTime(currentItem.getHour(), currentItem.getMinute())
        binding.repeatValue.text = currentItem.repeatPeriod.toRepeatString()
        binding.descriptionValue.text = currentItem.name
        binding.soundValue // todo
    }

    private fun TimePicker.setTime(hour: Long, minute: Long) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            this.hour = hour.toInt()
            this.minute = minute.toInt()
        } else {
            this.currentHour = hour.toInt()
            this.currentMinute = minute.toInt()
        }
    }

    //todo make it better
    private fun buildDescriptionDialog() {
        val dialog = Dialog(requireContext())
        val dialogBinding = InputTextLayoutBinding.inflate(dialog.layoutInflater)
        dialog.apply {
            val height = resources.getDimension(R.dimen.descriptionDialogHeight).toInt()
            setContentView(dialogBinding.root)
            window?.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, height)
            window?.setGravity(Gravity.BOTTOM)
        }
        dialogBinding.descriptionDoneButton.setOnClickListener {
            currentItem.name = dialogBinding.editText.text.toString()
            binding.descriptionValue.text = currentItem.name
            dialog.hide()
        }
        dialogBinding.descriptionCancelButton.setOnClickListener { dialog.hide() }
        dialogBinding.editText.text.append(currentItem.name)
        dialog.show()
    }

    //todo make it better(recyclerview)
    private fun buildDayChooseDialog() {
        val dialog = Dialog(requireContext())
        val dialogBinding = DayChooseLayout2Binding.inflate(dialog.layoutInflater)
        dialog.apply {
            val height = resources.getDimension(R.dimen.dayChooseDialogHeight).toInt()
            setContentView(dialogBinding.root)
            window?.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, height)
            window?.setGravity(Gravity.BOTTOM)
        }
        dialogBinding.dayChooseDoneButton.setOnClickListener {
            currentItem.repeatPeriod = REPEAT_NONE
            if (dialogBinding.mondayCheckbox.isChecked) {
                currentItem.repeatPeriod = currentItem.repeatPeriod or REPEAT_MONDAY
            }
            if (dialogBinding.tuesdayCheckbox.isChecked) {
                currentItem.repeatPeriod = currentItem.repeatPeriod or REPEAT_TUESDAY
            }
            if (dialogBinding.wednesdayCheckbox.isChecked) {
                currentItem.repeatPeriod = currentItem.repeatPeriod or REPEAT_WEDNESDAY
            }
            if (dialogBinding.thursdayCheckbox.isChecked) {
                currentItem.repeatPeriod = currentItem.repeatPeriod or REPEAT_THURSDAY
            }
            if (dialogBinding.fridayCheckbox.isChecked) {
                currentItem.repeatPeriod = currentItem.repeatPeriod or REPEAT_FRIDAY
            }
            if (dialogBinding.saturdayCheckbox.isChecked) {
                currentItem.repeatPeriod = currentItem.repeatPeriod or REPEAT_SATURDAY
            }
            if (dialogBinding.sundayCheckbox.isChecked) {
                currentItem.repeatPeriod = currentItem.repeatPeriod or REPEAT_SUNDAY
            }
            binding.repeatValue.text = currentItem.repeatPeriod.toRepeatString()
            dialog.hide()
        }
        dialogBinding.mondayCheckbox.isChecked =
            currentItem.repeatPeriod == currentItem.repeatPeriod or REPEAT_MONDAY
        dialogBinding.tuesdayCheckbox.isChecked =
            currentItem.repeatPeriod == currentItem.repeatPeriod or REPEAT_TUESDAY
        dialogBinding.wednesdayCheckbox.isChecked =
            currentItem.repeatPeriod == currentItem.repeatPeriod or REPEAT_WEDNESDAY
        dialogBinding.thursdayCheckbox.isChecked =
            currentItem.repeatPeriod == currentItem.repeatPeriod or REPEAT_THURSDAY
        dialogBinding.fridayCheckbox.isChecked =
            currentItem.repeatPeriod == currentItem.repeatPeriod or REPEAT_FRIDAY
        dialogBinding.saturdayCheckbox.isChecked =
            currentItem.repeatPeriod == currentItem.repeatPeriod or REPEAT_SATURDAY

        dialogBinding.sundayCheckbox.isChecked =
            currentItem.repeatPeriod == currentItem.repeatPeriod or REPEAT_SUNDAY
        dialogBinding.dayChooseCancelButton.setOnClickListener { dialog.hide() }
        dialog.show()
    }
}
