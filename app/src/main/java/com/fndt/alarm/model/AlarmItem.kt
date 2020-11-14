package com.fndt.alarm.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fndt.alarm.model.util.REPEAT_NONE
import java.io.*

@Entity(tableName = "alarms")
data class AlarmItem(
    var time: Long,
    var name: String,
    var isActive: Boolean,
    var repeatPeriod: Byte = REPEAT_NONE,
    var melody: Int
) : Serializable {
    init {
        if (time > 1439) throw Exception()
    }

    @PrimaryKey(autoGenerate = true)
    var id: Long = 1

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
        fun Long.toTimeString(): String {
            val hoursString = "${this / 60}"
            val minutes = this % 60
            val minutesString = "${if (minutes < 10) "0" else ""}$minutes"
            return "$hoursString:$minutesString"
        }

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

