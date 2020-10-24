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

    @Query("SELECT * FROM alarms ORDER BY time ASC")
    fun getAll(): LiveData<List<AlarmItem>>

    @Query("DELETE FROM alarms")
    fun wipeTable()
}