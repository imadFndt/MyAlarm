package com.fndt.alarm.presentation.fragments

import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.fndt.alarm.R
import com.fndt.alarm.databinding.AlarmListFragmentBinding
import com.fndt.alarm.domain.dto.AlarmRepeat
import com.fndt.alarm.domain.dto.NextAlarmItem
import com.fndt.alarm.presentation.activities.MainActivity
import com.fndt.alarm.presentation.util.AlarmItemGestureListener
import com.fndt.alarm.presentation.util.AlarmListAdapter
import com.fndt.alarm.presentation.util.toExtendedTimeString
import com.fndt.alarm.presentation.viewmodels.MainActivityViewModel
import kotlinx.coroutines.*
import java.util.*

class AlarmListFragment : Fragment() {
    private lateinit var binding: AlarmListFragmentBinding
    private val viewModel: MainActivityViewModel by activityViewModels {
        (requireActivity() as MainActivity).viewModelFactory
    }
    var trackJob: Job? = null
    var previousTrackJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AlarmListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val onGestureListener = AlarmItemGestureListener()
        onGestureListener.singleTapCallback = { viewModel.editItem(it) }
        onGestureListener.longPressedCallback = { item -> item?.let { viewModel.removeAlarm(it) } }
        val gestureDetector = GestureDetector(context, onGestureListener)
        val listAdapter = AlarmListAdapter()
        listAdapter.itemTouchListener = { item, event ->
            onGestureListener.currentItem = item
            gestureDetector.onTouchEvent(event)
        }
        listAdapter.itemSwitchClickListener = { item ->
            item.isActive = !item.isActive
            if (item.repeatPeriod.contains(AlarmRepeat.ONCE_DESTROY)) {
                viewModel.removeAlarm(item)
            } else {
                viewModel.addAlarm(item)
            }
        }
        binding.alarmList.apply {
            val decor = DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                ContextCompat.getDrawable(requireContext(), R.drawable.alarm_list_divider)?.let { setDrawable(it) }
            }
            addItemDecoration(decor)
            adapter = listAdapter
        }
        binding.addButton.setOnClickListener { viewModel.editItem(null) }
        viewModel.alarmList.observe(viewLifecycleOwner) { listAdapter.updateItems(it) }
        viewModel.nextAlarm.observe(viewLifecycleOwner) { nextItem ->
            nextItem?.let {
                stopTrackingTime(false)
                trackTime(nextItem)
            } ?: run { stopTrackingTime(true) }
        }
    }

    private fun trackTime(nextItem: NextAlarmItem) {
        trackJob = CoroutineScope(Dispatchers.Main).launch {
            previousTrackJob?.join()
            previousTrackJob = null
            while (isActive) {
                val cal = Calendar.getInstance()
                val diff = nextItem.timedCalendar.timeInMillis - cal.timeInMillis
                binding.nextAlarmRemainingText.text = diff.toExtendedTimeString(context)
                delay(100)
            }
        }
    }

    private fun stopTrackingTime(update: Boolean) {
        if (update) binding.nextAlarmRemainingText.text = resources.getString(R.string.nothing_set)
        previousTrackJob = trackJob
        trackJob?.cancel()
        trackJob = null
    }
}
