package com.fndt.alarm.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fndt.alarm.model.AlarmItem

@Dao
interface AlarmItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(alarmItem: AlarmItem)

    @Query("DELETE FROM alarms WHERE id = :itemId")
    fun remove(itemId: Long)

    @Delete
    fun remove(item: AlarmItem)

    @Query("SELECT * FROM alarms WHERE isActive = 1 ORDER BY time ASC")
    fun getEnabled(): LiveData<List<AlarmItem>>

    @Query("SELECT * FROM alarms ORDER BY time ASC")
    fun getAll(): LiveData<List<AlarmItem>>

    @Query("DELETE FROM alarms")
    fun wipeTable()
}