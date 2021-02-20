package com.fndt.alarm.data

import androidx.room.TypeConverter
import com.fndt.alarm.domain.dto.AlarmRepeat
import com.fndt.alarm.domain.utils.*
import kotlin.experimental.or

object AlarmConverter {
    //todo use byte value
    @TypeConverter
    @JvmStatic
    fun toByte(repeats: List<AlarmRepeat>): Byte {
        var byteRepeat: Byte = 0b0
        for (i in repeats) {
            byteRepeat = when (i) {
                AlarmRepeat.MONDAY -> byteRepeat or REPEAT_MONDAY
                AlarmRepeat.TUESDAY -> byteRepeat or REPEAT_TUESDAY
                AlarmRepeat.WEDNESDAY -> byteRepeat or REPEAT_WEDNESDAY
                AlarmRepeat.THURSDAY -> byteRepeat or REPEAT_THURSDAY
                AlarmRepeat.FRIDAY -> byteRepeat or REPEAT_FRIDAY
                AlarmRepeat.SATURDAY -> byteRepeat or REPEAT_SATURDAY
                AlarmRepeat.SUNDAY -> byteRepeat or REPEAT_SUNDAY
                AlarmRepeat.NONE -> REPEAT_NONE
                AlarmRepeat.ONCE_DESTROY -> REPEAT_ONCE_DESTROY
            }
        }
        return byteRepeat
    }

    @TypeConverter
    @JvmStatic
    fun fromByte(repeats: Byte): List<AlarmRepeat> {
        if (repeats < 0) return mutableListOf(AlarmRepeat.ONCE_DESTROY)
        val list = mutableListOf<AlarmRepeat>()
        for (i in REPEATS_LIST) {
            if (repeats == repeats or i) {
                list.add(
                    when (i) {
                        REPEAT_MONDAY -> AlarmRepeat.MONDAY
                        REPEAT_TUESDAY -> AlarmRepeat.TUESDAY
                        REPEAT_WEDNESDAY -> AlarmRepeat.WEDNESDAY
                        REPEAT_THURSDAY -> AlarmRepeat.THURSDAY
                        REPEAT_FRIDAY -> AlarmRepeat.FRIDAY
                        REPEAT_SATURDAY -> AlarmRepeat.SATURDAY
                        REPEAT_SUNDAY -> AlarmRepeat.SUNDAY
                        else -> AlarmRepeat.NONE
                    }
                )
            }
        }
        if (list.isEmpty()) list.add(AlarmRepeat.NONE)
        return list
    }
}