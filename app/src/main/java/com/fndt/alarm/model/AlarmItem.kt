package com.fndt.alarm.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "alarms")
data class AlarmItem(
    var time: Long,
    var name: String,
    var isActive: Boolean
) : Serializable {
    //TODO PARCELS
    init {
        if (time > 1439) throw Exception()
    }

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    companion object {
        fun Long.toTimeString(): String {
            val hoursString = "${this / 60}"
            val minutes = this % 60
            val minutesString = "${if (minutes < 10) "0" else ""}$minutes"
            return "$hoursString:$minutesString"
        }
    }
}

