package com.fndt.alarm

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fndt.alarm.AlarmItem.Companion.toTimeString
import com.fndt.alarm.databinding.AlarmItemBinding
import kotlin.random.Random

class AlarmListAdapter : RecyclerView.Adapter<AlarmListAdapter.AlarmViewHolder>() {
    private val alarmList: MutableList<AlarmItem> = mutableListOf()

    init {
        for (i in 0..5) {
            alarmList.add(
                AlarmItem(
                    i * 60 + Random(1).nextLong() % 60,
                    "ALORM",
                    arrayOf(0, 0, 0, 0, 0, 0, 0),
                    Random(2).nextBoolean()
                )
            )
        }
        Log.e("a","vse")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AlarmItemBinding.inflate(inflater, parent, false)
        return AlarmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.binding.apply {
            Log.e("111", "222")
            alarmName.text = alarmList[position].name
            alarmSwitch.isChecked = alarmList[position].isActive
            alarmTime.text = alarmList[position].time.toTimeString()
        }
    }

    override fun getItemCount(): Int = alarmList.size

    class AlarmViewHolder(val binding: AlarmItemBinding) : RecyclerView.ViewHolder(binding.root)
}