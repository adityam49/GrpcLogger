package com.ducktappedapps.grpclogger.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ducktappedapps.grpclogger.data.CallStateConverters
import com.ducktappedapps.grpclogger.data.Log
import com.ducktappedapps.grpclogger.data.LogsDao

@TypeConverters(CallStateConverters::class)
@Database(entities = [Log::class], version = 1)
abstract class LogsDb : RoomDatabase() {
    abstract fun logsDao(): LogsDao
}