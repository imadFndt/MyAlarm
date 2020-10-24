package com.fndt.alarm.view

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
import com.fndt.alarm.model.AlarmItem
import com.fndt.alarm.model.util.AlarmApplication
import java.util.*
import kotlin.random.Random

class AlarmListFragment : Fragment() {
    private lateinit var binding: AlarmListFragmentBinding
    private val viewModel: MainActivityViewModel by activityViewModels {
        (requireActivity().application as AlarmApplication).component.getViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AlarmListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    //TODO REMOVE
    private var incrementalPlus = 1
    private val rand = Random(10)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listAdapter = AlarmListAdapter()
        binding.alarmList.apply {
            val decor = DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                val divider =
                    ContextCompat.getDrawable(requireContext(), R.drawable.alarm_list_divider)
                divider?.let { setDrawable(it) }
            }
            addItemDecoration(decor)
            adapter = listAdapter
        }

        binding.addButton.setOnClickListener {
            //TODO FRAGMENT
            val calendar = Calendar.getInstance()
            val hours = calendar.get(Calendar.HOUR_OF_DAY) * 60
            val minutes = calendar.get(Calendar.MINUTE) + incrementalPlus
            val time: Long = (hours + minutes).toLong()
            incrementalPlus += 1
            val item = AlarmItem(time, "test${rand.nextInt() % 30}", rand.nextBoolean())
            viewModel.addItem(item)
        }
        viewModel.alarmList.observe(viewLifecycleOwner, { listAdapter.updateItems(it) })
    }
}
