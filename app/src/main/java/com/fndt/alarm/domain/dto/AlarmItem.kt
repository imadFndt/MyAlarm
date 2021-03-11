package com.fndt.alarm.domain.dto

import com.fndt.alarm.domain.utils.FIVE_MINUTES
import java.io.*
import java.util.*

data class AlarmItem(
    var time: Long,
    var name: String,
    var isActive: Boolean,
    var repeatPeriod: MutableList<AlarmRepeat> = mutableListOf(AlarmRepeat.NONE),
    var melody: String,
    var id: Long
) : Serializable {
    init {
        if (time > 1439) throw IllegalStateException()
    }

    fun getHour(): Long = time / 60
    fun getMinute(): Long = time % 60

    fun snoozed(): AlarmItem {
        val currentTime: Int
        Calendar.getInstance().apply { currentTime = get(Calendar.MINUTE) + get(Calendar.HOUR_OF_DAY) * 60 }
        val newTime = (currentTime + FIVE_MINUTES).toLong()
        return AlarmItem(
            time = newTime,
            name = this.name,
            true,
            mutableListOf(AlarmRepeat.ONCE_DESTROY),
            melody = this.melody,
            id = this.id
        )
    }
    
    fun toByteArray(): ByteArray {
        ByteArrayOutputStream().use { byteStream ->
            ObjectOutputStream(byteStream).use { objectStream ->
                objectStream.writeObject(this)
                objectStream.flush()
                return byteStream.toByteArray()
            }
        }
    }

    companion object {
        fun fromByteArray(array: ByteArray?): AlarmItem? {
            array ?: return null
            ByteArrayInputStream(array).use { byteArrayStream ->
                ObjectInputStream(byteArrayStream).use { objectInput ->
                    return objectInput.readObject() as AlarmItem
                }
            }
        }
    }
}

