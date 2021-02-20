package com.fndt.alarm.data

import android.provider.Settings
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fndt.alarm.domain.dto.AlarmRepeat
import com.fndt.alarm.domain.dto.AlarmItem
import java.io.Serializable

val defaultAlarmSound = Settings.System.DEFAULT_ALARM_ALERT_URI.toString()

@Entity(tableName = "alarms")
data class AlarmItemEntity(
    var time: Long,
    var name: String,
    var isActive: Boolean,
    var repeatPeriod: MutableList<AlarmRepeat> = mutableListOf(AlarmRepeat.NONE),
    var melody: String = defaultAlarmSound
) : Serializable {
    fun toAlarmItem(): AlarmItem {
        return AlarmItem(
            time = this.time,
            name = this.name,
            isActive = this.isActive,
            repeatPeriod = this.repeatPeriod,
            melody = this.melody,
            id = this.id
        )
    }

    init {
        if (time > 1439) throw Exception()
    }

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

