package com.fndt.alarm.presentation.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fndt.alarm.databinding.DayItemBinding
import com.fndt.alarm.domain.dto.AlarmRepeat
import com.fndt.alarm.domain.dto.AlarmItem

class DayChooseAdapter : RecyclerView.Adapter<DayChooseAdapter.DayChooseViewHolder>() {
    var checkListener: ((AlarmItem) -> Unit)? = null

    private var currentItem: AlarmItem? = null

    private val days = listOf(
        AlarmRepeat.MONDAY, AlarmRepeat.TUESDAY, AlarmRepeat.WEDNESDAY, AlarmRepeat.THURSDAY, AlarmRepeat.FRIDAY,
        AlarmRepeat.SATURDAY, AlarmRepeat.SUNDAY
    )

    //todo rework
    private val daysActual = mutableListOf<AlarmRepeat?>(null, null, null, null, null, null, null)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayChooseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DayItemBinding.inflate(inflater, parent, false)
        val holder = DayChooseViewHolder(binding)
        holder.binding.dayCheckbox.setOnClickListener {
            currentItem?.let { item ->
                val pos = holder.adapterPosition
                daysActual[pos] = if (holder.binding.dayCheckbox.isChecked) days[pos] else null
                currentItem?.repeatPeriod = daysActual.getPeriod()
                checkListener?.invoke(item)
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: DayChooseViewHolder, position: Int) {
        val dayRepeat = days[holder.adapterPosition]
        holder.binding.dayText.text = holder.binding.root.context.resources.getText(dayRepeat.text)
        holder.binding.dayCheckbox.isChecked = currentItem?.repeatPeriod?.contains(dayRepeat) ?: run { false }
    }

    override fun getItemCount(): Int = days.size

    fun updateCurrentItem(newItem: AlarmItem) {
        if (newItem != currentItem) {
            currentItem = newItem
            setDayActual(currentItem?.repeatPeriod)
            notifyDataSetChanged()
        }
    }

    private fun setDayActual(repeatPeriod: MutableList<AlarmRepeat>?) {
        for ((index, i) in days.withIndex()) {
            if (repeatPeriod?.contains(i) == true) daysActual[index] = i
        }
    }

    private fun MutableList<AlarmRepeat?>.getPeriod(): MutableList<AlarmRepeat> {
        val repeats = mutableListOf<AlarmRepeat>()
        for (i in this) i?.let { repeats.add(it) }
        if (repeats.isEmpty()) repeats.add(AlarmRepeat.NONE)
        return repeats
    }

    class DayChooseViewHolder(val binding: DayItemBinding) : RecyclerView.ViewHolder(binding.root)
}
