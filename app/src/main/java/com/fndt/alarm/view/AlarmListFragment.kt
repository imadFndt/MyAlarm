package com.fndt.alarm.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.fndt.alarm.databinding.AlarmListFragmentBinding

class AlarmListFragment : Fragment() {
    private lateinit var binding: AlarmListFragmentBinding
    private val viewModel: MainActivityViewModel by activityViewModels()

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
        binding.alarmList.apply {
            layoutManager = LinearLayoutManager(this@AlarmListFragment.context)
            adapter = listAdapter
        }
        viewModel.alarmList.observe(viewLifecycleOwner, Observer { list ->
            listAdapter.updateItems(list)
        })
    }
}