package com.fndt.alarm.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.fndt.alarm.databinding.DayBottomSheetBinding
import com.fndt.alarm.presentation.AlarmApplication
import com.fndt.alarm.presentation.util.DayChooseAdapter
import com.fndt.alarm.presentation.viewmodels.MainActivityViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DayChooseBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: DayBottomSheetBinding
    private val viewModel: MainActivityViewModel by activityViewModels {
        (requireActivity().application as AlarmApplication).component.getViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DayBottomSheetBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = DayChooseAdapter()
        binding.daysRecycler.adapter = adapter
        adapter.checkListener = { viewModel.updateEditedItem(it) }
        viewModel.itemEdited.observe(viewLifecycleOwner) { adapter.updateCurrentItem(it) }
    }

    companion object {
        @JvmStatic
        fun newInstance(): DayChooseBottomSheetFragment = DayChooseBottomSheetFragment()
    }
}