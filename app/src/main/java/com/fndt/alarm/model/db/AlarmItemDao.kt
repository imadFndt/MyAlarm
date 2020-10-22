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
    suspend fun insert(alarmItems: List<AlarmItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarmItems: AlarmItem)

    @Query("SELECT * FROM alarms ORDER BY time ASC")
    fun getAll(): LiveData<List<AlarmItem>>
}