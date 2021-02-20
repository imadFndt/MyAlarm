package com.fndt.alarm.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AlarmItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(alarmItem: AlarmItemEntity)

    @Query("DELETE FROM alarms WHERE id = :itemId")
    fun remove(itemId: Long)

    @Delete
    fun remove(item: AlarmItemEntity)

    @Query("SELECT * FROM alarms WHERE isActive = 1 ORDER BY time ASC")
    fun getEnabled(): LiveData<List<AlarmItemEntity>>

    @Query("SELECT * FROM alarms ORDER BY time ASC")
    fun getAll(): LiveData<List<AlarmItemEntity>>

    @Query("DELETE FROM alarms")
    fun wipeTable()
}