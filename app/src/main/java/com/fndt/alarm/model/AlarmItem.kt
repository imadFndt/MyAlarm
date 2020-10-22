package com.fndt.alarm.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class AlarmItem(
    var time: Long,
    var name: String,
    var isActive: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null

    companion object {
        fun Long.toTimeString() = "${this / 60}:${this % 60}"
    }
}

