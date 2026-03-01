package com.serega.requestmanager.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [RequestEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RequestDatabase : RoomDatabase() {
    abstract fun requestDao(): RequestDao
}