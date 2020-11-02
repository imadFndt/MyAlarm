package com.fndt.alarm.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fndt.alarm.databinding.AlarmItemBinding
import com.fndt.alarm.model.AlarmItem
import com.fndt.alarm.model.AlarmItem.Companion.toTimeString

class AlarmListAdapter : RecyclerView.Adapter<AlarmListAdapter.AlarmViewHolder>() {
    var itemClickListener: ((AlarmItem) -> Unit)? = null
    var itemSwitchClickListener: ((AlarmItem) -> Unit)? = null

    private val items: MutableList<AlarmItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AlarmItemBinding.inflate(inflater, parent, false)
        val holder = AlarmViewHolder(binding)
        holder.itemView.setOnClickListener { itemClickListener?.invoke(items[holder.adapterPosition]) }
        holder.binding.alarmSwitch.setOnClickListener { itemSwitchClickListener?.invoke(items[holder.adapterPosition]) }
        return holder
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
        val diff = DiffUtil.calculateDiff(AlarmItemCallback(items, newList))
        items.clear()
        items.addAll(newList)
        diff.dispatchUpdatesTo(this)
    }

    class AlarmViewHolder(val binding: AlarmItemBinding) : RecyclerView.ViewHolder(binding.root)
}