package com.fndt.alarm.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.*

@Entity(tableName = "alarms")
data class AlarmItem(
    var time: Long,
    var name: String,
    var isActive: Boolean,
    var repeatPeriod: MutableList<AlarmRepeat> = mutableListOf(AlarmRepeat.NONE),
    var melody: Int
) : Serializable {
    init {
        if (time > 1439) throw Exception()
    }

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

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

    fun getHour(): Long = time / 60
    fun getMinute(): Long = time % 60

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

