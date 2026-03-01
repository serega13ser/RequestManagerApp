package com.serega.requestmanager.domain.repository

import com.serega.requestmanager.domain.model.Request
import kotlinx.coroutines.flow.Flow

interface RequestRepository {
    suspend fun insertRequest(request: Request): Long
    fun getAllRequests(): Flow<List<Request>>
    suspend fun getRequestById(id: Long): Request?
    suspend fun deleteRequest(id: Long)
    suspend fun syncRequest(requestId: Long)
    suspend fun retryFailedSyncs()

}