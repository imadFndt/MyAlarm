package com.fndt.alarm.view.main

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

class AlarmListFragment : Fragment() {
    private lateinit var binding: AlarmListFragmentBinding
    private val viewModel: MainActivityViewModel by activityViewModels {
        (requireActivity() as MainActivity).viewModelFactory
    }

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
            viewModel.setTurnAlarmRequest(item)
            item.isActive = !item.isActive
            viewModel.updateItem(item)
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
    }
}
