package com.fndt.alarm.presentation.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TimePicker
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.fndt.alarm.data.defaultAlarmSound
import com.fndt.alarm.databinding.AddFragmentBinding
import com.fndt.alarm.domain.dto.AlarmRepeat
import com.fndt.alarm.domain.dto.AlarmItem
import com.fndt.alarm.presentation.util.DAY_CHOOSE_FRAGMENT_TAG
import com.fndt.alarm.presentation.util.RINGTONE_REQ_CODE
import com.fndt.alarm.presentation.AlarmApplication
import com.fndt.alarm.presentation.util.toRepeatString
import com.fndt.alarm.presentation.viewmodels.MainActivityViewModel
import java.util.*


class EditFragment : Fragment() {
    private lateinit var binding: AddFragmentBinding
    private val viewModel: MainActivityViewModel by activityViewModels {
        (requireActivity().application as AlarmApplication).component.getViewModelFactory()
    }
    private lateinit var currentItem: AlarmItem

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = AddFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTextHide(view)
        val time: Long
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        time = (hour * 60 + minute).toLong()
        viewModel.status.observe(viewLifecycleOwner) { status ->
            if (status is MainActivityViewModel.AlarmStatus.EditStatus) {
                status.item?.let { currentItem = it } ?: run {
                    currentItem = AlarmItem(time, "", true, mutableListOf(AlarmRepeat.NONE), defaultAlarmSound, 0)
                }
                viewModel.updateEditedItem(currentItem)
                updateView(currentItem)
            }
        }
        viewModel.itemEdited.observe(viewLifecycleOwner) {
            currentItem = it
            updateView(currentItem)
        }
        binding.temporaryTimePicker.apply {
            setIs24HourView(true)
            setOnTimeChangedListener { _, hourOfDay, minute ->
                currentItem.time = (hourOfDay * 60 + minute).toLong()
            }
        }
        binding.doneButton.setOnClickListener { viewModel.addAlarm(currentItem) }
        binding.closeButton.setOnClickListener { viewModel.cancelItemUpdate() }
        binding.repeatArrow.setOnClickListener { showDayChooseFragment() }
        binding.descriptionEditText.setOnKeyListener { v, keyCode, event ->
            currentItem.name = binding.descriptionEditText.text.toString()
            if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                v.clearFocus()
                hideKeyboard()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        binding.soundArrow.setOnClickListener { startPickActivity() }
    }

    private fun showDayChooseFragment() {
        val dayChooseBottomSheet = DayChooseBottomSheetFragment.newInstance()
        dayChooseBottomSheet.show(childFragmentManager, DAY_CHOOSE_FRAGMENT_TAG)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RINGTONE_REQ_CODE) {
            data?.let {
                val alert: String? =
                    data.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)?.toString()
                alert?.let {
                    currentItem.melody = alert
                    updateView(currentItem)
                }
            }
        }
    }


    private fun startPickActivity() {
        startActivityForResult(Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
            putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentItem.melody)

            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
            putExtra(
                RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI,
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            )

            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
            putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
        }, RINGTONE_REQ_CODE)
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager? = context?.getSystemService()
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun updateView(currentItem: AlarmItem) {
        binding.temporaryTimePicker.setTime(currentItem.getHour(), currentItem.getMinute())
        binding.repeatValue.text = currentItem.repeatPeriod.toRepeatString(requireContext())
        binding.descriptionEditText.setText(currentItem.name)
        binding.soundValue.text = currentItem.melody.getMelodyTitle(requireContext())
    }

    private fun String.getMelodyTitle(context: Context): String {
        RingtoneManager.getRingtone(context, Uri.parse(this)).apply {
            return getTitle(context)
        }
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

    @SuppressLint("ClickableViewAccessibility")
    fun setupTextHide(view: View) {
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                v.clearFocus()
                hideKeyboard()
                false
            }
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupTextHide(innerView)
            }
        }
    }
}
