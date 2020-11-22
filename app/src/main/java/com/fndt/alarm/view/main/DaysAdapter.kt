package com.fndt.alarm.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fndt.alarm.databinding.DayLayoutBinding
import com.fndt.alarm.model.util.*

class DaysAdapter : RecyclerView.Adapter<DaysAdapter.DaysViewHolder>() {
    private val days = arrayListOf(
        REPEAT_MONDAY,
        REPEAT_TUESDAY,
        REPEAT_WEDNESDAY,
        REPEAT_THURSDAY,
        REPEAT_FRIDAY,
        REPEAT_SATURDAY,
        REPEAT_SUNDAY
    )
    var onClick: ((Byte) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaysViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DayLayoutBinding.inflate(inflater, parent, false)
        val holder = DaysViewHolder(binding)
        holder.binding.doneButton.setOnClickListener { onClick?.invoke(days[holder.adapterPosition]) }
        return holder
    }

    override fun onBindViewHolder(holder: DaysViewHolder, position: Int) {
        holder.binding.doneButton.text = days[position].toDayString()
    }

    override fun getItemCount(): Int = days.size

    class DaysViewHolder(val binding: DayLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)
}

