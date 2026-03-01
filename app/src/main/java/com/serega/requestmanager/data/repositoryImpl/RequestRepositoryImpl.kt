package com.serega.requestmanager.data.repositoryImpl

import com.serega.requestmanager.data.local.RequestDao
import com.serega.requestmanager.data.mapper.toDomainRequest
import com.serega.requestmanager.data.mapper.toEntityRequest
import com.serega.requestmanager.data.remote.GoogleSheetsService
import com.serega.requestmanager.domain.repository.RequestRepository
import com.serega.requestmanager.domain.model.Request
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.collections.map


class RequestRepositoryImpl @Inject constructor(
    private val requestDao: RequestDao,
    private val googleSheetsService: GoogleSheetsService,
    private val ioDispatcher: kotlin.coroutines.CoroutineContext,
): RequestRepository {


    override suspend fun getRequestById(id: Long): Request? {
        return requestDao.getById(id)?.toDomainRequest()
    }

    override suspend fun deleteRequest(id: Long) {
        requestDao.delete(id)
    }

    override suspend fun insertRequest(request: Request): Long = withContext(ioDispatcher) {
        requestDao.insert(request.toEntityRequest())
    }

    override fun getAllRequests(): Flow<List<Request>> {
        return requestDao.getAll().map { list ->
            list.map { it.toDomainRequest() }
        }
    }

    override suspend fun syncRequest(requestId: Long) = withContext(ioDispatcher) {
        val request = requestDao.getById(requestId) ?: return@withContext

        try {
            googleSheetsService.sendRequest(request)

            requestDao.update(request.copy(isSynced = true, syncError = null))
        } catch (e: Exception) {
            requestDao.update(request.copy(syncError = e.message))
            throw e
        }
    }

    override suspend fun retryFailedSyncs() = withContext(ioDispatcher) {
        val unsynced = requestDao.getUnsynced()
        for (request in unsynced) {
            try {
                googleSheetsService.sendRequest(request)
                requestDao.update(request.copy(isSynced = true, syncError = null))
            } catch (e: Exception) {
                break
            }
        }
    }
}