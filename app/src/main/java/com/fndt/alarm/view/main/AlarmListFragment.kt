package com.fndt.alarm.view.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.fndt.alarm.R
import com.fndt.alarm.databinding.AlarmListFragmentBinding
import com.fndt.alarm.model.NextAlarmItem
import com.fndt.alarm.model.util.INTENT_ADD_ALARM
import com.fndt.alarm.model.util.ITEM_EXTRA
import com.fndt.alarm.model.util.toExtendedTimeString
import kotlinx.coroutines.*
import java.util.*

class AlarmListFragment : Fragment() {
    private lateinit var binding: AlarmListFragmentBinding
    private val viewModel: MainActivityViewModel by activityViewModels {
        (requireActivity() as MainActivity).viewModelFactory
    }
    var trackJob: Job? = null
    var previousTrackJob: Job? = null
    var nextItem: NextAlarmItem? = null

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
        val listAdapter = AlarmListAdapter()
        listAdapter.itemClickListener = { viewModel.editItem(it) }
        listAdapter.itemSwitchClickListener = { item ->
            item.isActive = !item.isActive
            viewModel.addAlarm(Intent(INTENT_ADD_ALARM).putExtra(ITEM_EXTRA, item))
        }
        binding.alarmList.apply {
            val decor = DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                ContextCompat.getDrawable(requireContext(), R.drawable.alarm_list_divider)
                    ?.let { setDrawable(it) }
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
