package com.fndt.alarm

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class AlarmListAdapter : RecyclerView.Adapter<AlarmListAdapter.AlarmViewHolder>() {
    private val alarmList: MutableList<AlarmItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int = alarmList.size

    class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}