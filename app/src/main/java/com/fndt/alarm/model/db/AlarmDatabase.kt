package com.fndt.alarm.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fndt.alarm.model.AlarmItem

@Database(entities = [AlarmItem::class], version = 2)
abstract class AlarmDatabase : RoomDatabase() {
    abstract fun issueDao(): AlarmItemDao

    companion object {
        private const val DB_NAME = "alarms.db"

        fun buildDb(context: Context) = Room.databaseBuilder(
            context.applicationContext, AlarmDatabase::class.java, DB_NAME
        ).fallbackToDestructiveMigration().build()
    }
}