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
        if (time > 1439) throw Exception()
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
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream: ObjectOutputStream
        objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
        objectOutputStream.writeObject(this)
        objectOutputStream.flush()
        val result = byteArrayOutputStream.toByteArray()
        byteArrayOutputStream.close()
        objectOutputStream.close()
        return result
    }

    companion object {
        fun fromByteArray(array: ByteArray): AlarmItem {
            val byteArrayInputStream = ByteArrayInputStream(array)
            val objectInput: ObjectInput
            objectInput = ObjectInputStream(byteArrayInputStream)
            val result = objectInput.readObject() as AlarmItem
            objectInput.close()
            byteArrayInputStream.close()
            return result
        }
    }
}

