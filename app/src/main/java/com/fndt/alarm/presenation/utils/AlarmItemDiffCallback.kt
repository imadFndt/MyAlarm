package com.fndt.alarm.presenation.utils

import androidx.recyclerview.widget.DiffUtil
import com.fndt.alarm.model.AlarmItem

class AlarmItemDiffCallback(
    private val oldList: List<AlarmItem>,
    private val newList: List<AlarmItem>
) :
    DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].name == newList[newItemPosition].name &&
                oldList[oldItemPosition].time == newList[newItemPosition].time &&
                oldList[oldItemPosition].id == newList[newItemPosition].id &&
                (oldList[oldItemPosition].isActive == newList[newItemPosition].isActive)
}