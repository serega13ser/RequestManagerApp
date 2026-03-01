package com.serega.requestmanager.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RequestDao {
    @Insert
    suspend fun insert(request: RequestEntity): Long

    @Query("SELECT * FROM requests ORDER BY id DESC")
    fun getAll(): Flow<List<RequestEntity>>

    @Query("SELECT * FROM requests WHERE is_synced = 0")
    suspend fun getUnsynced(): List<RequestEntity>

    @Query("SELECT * FROM requests WHERE id = :id")
    suspend fun getById(id: Long): RequestEntity?

    @Update
    suspend fun update(request: RequestEntity)

    @Query("DELETE FROM requests WHERE id = :id")
    suspend fun delete(id: Long)
}