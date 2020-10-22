package com.fndt.alarm.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fndt.alarm.databinding.AlarmItemBinding
import com.fndt.alarm.model.AlarmItem
import com.fndt.alarm.model.AlarmItem.Companion.toTimeString

class AlarmListAdapter : RecyclerView.Adapter<AlarmListAdapter.AlarmViewHolder>() {
    private val items: MutableList<AlarmItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AlarmItemBinding.inflate(inflater, parent, false)
        return AlarmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.binding.apply {
            alarmName.text = items[position].name
            alarmSwitch.isChecked = items[position].isActive
            alarmTime.text = items[position].time.toTimeString()
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newList: List<AlarmItem>) {
        //TODO DIFFUTIL
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    class AlarmViewHolder(val binding: AlarmItemBinding) : RecyclerView.ViewHolder(binding.root)
}