package com.fndt.alarm.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fndt.alarm.R
import com.fndt.alarm.databinding.AlarmItemBinding
import com.fndt.alarm.model.AlarmItem
import com.fndt.alarm.model.util.toRepeatString
import com.fndt.alarm.model.util.toTimeString

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
        val item = items[position]
        holder.binding.apply {
            alarmName.text = if (item.name == "") {
                item.repeatPeriod.toRepeatString(holder.itemView.context)
            } else {
                holder.binding.root.resources.getString(
                    R.string.item_list_title, item.name, item.repeatPeriod.toRepeatString(holder.itemView.context)
                )
            }

            if (alarmSwitch.isChecked != item.isActive) alarmSwitch.isChecked = item.isActive
            alarmTime.text = item.time.toTimeString()
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newList: List<AlarmItem>) {
        val diff = DiffUtil.calculateDiff(AlarmItemDiffCallback(items, newList))
        items.clear()
        items.addAll(newList)
        diff.dispatchUpdatesTo(this)
    }

    class AlarmViewHolder(val binding: AlarmItemBinding) : RecyclerView.ViewHolder(binding.root)
}