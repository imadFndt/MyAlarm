package com.fndt.alarm.model.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fndt.alarm.model.AlarmItem

@Dao
interface AlarmItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(alarmItem: AlarmItem)

    @Update
    fun updateAlarm(alarmItem: AlarmItem)

    @Query("SELECT * FROM alarms WHERE isActive = 1 ORDER BY time ASC")
    fun getEnabled(): LiveData<List<AlarmItem>>

    @Query("SELECT * FROM alarms ORDER BY time ASC")
    fun getAll(): LiveData<List<AlarmItem>>

    @Query("SELECT * FROM alarms WHERE time > :time ORDER BY time ASC LIMIT 1")
    fun getNext(time: Long): LiveData<AlarmItem>

    @Query("DELETE FROM alarms")
    fun wipeTable()
}