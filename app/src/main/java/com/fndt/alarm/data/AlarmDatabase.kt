package com.fndt.alarm.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [AlarmItemEntity::class], version = 1)
@TypeConverters(AlarmConverter::class)
abstract class AlarmDatabase : RoomDatabase() {
    abstract fun issueDao(): AlarmItemDao

    companion object {
        private const val DB_NAME = "alarms.db"

        fun buildDb(context: Context) = Room.databaseBuilder(
            context.applicationContext, AlarmDatabase::class.java, DB_NAME
        ).fallbackToDestructiveMigration().build()
    }
}