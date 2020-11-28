package com.fndt.alarm.model.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fndt.alarm.model.AlarmItem

@Dao
interface AlarmItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(alarmItem: AlarmItem)

    @Query("DELETE FROM alarms WHERE id = :itemId")
    fun remove(itemId: Long)

    @Query("SELECT * FROM alarms WHERE isActive = 1 ORDER BY time ASC")
    fun getEnabled(): LiveData<List<AlarmItem>>

    @Query("SELECT * FROM alarms ORDER BY time ASC")
    fun getAll(): LiveData<List<AlarmItem>>

    @Query("DELETE FROM alarms")
    fun wipeTable()
}