package com.serega.requestmanager.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton
import com.serega.requestmanager.data.local.RequestEntity

@Singleton
class GoogleSheetsService @Inject constructor() {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }


        install(HttpTimeout) {
            requestTimeoutMillis = 15000
            connectTimeoutMillis = 10000
        }

        install(Logging) {
            level = LogLevel.INFO
        }
    }

    suspend fun sendRequest(request: RequestEntity) {
        val scriptUrl = "https://script.google.com"

        try {
            val response = client.post(scriptUrl) {
                contentType(ContentType.Application.Json)

                setBody(mapOf(
                    "orderNumber" to request.orderNumber,
                    "address" to request.address,
                    "responseCenter" to request.responseCenter,
                    "division" to request.division,
                    "objectType" to request.objectType,
                    "problemDescription" to request.problemDescription,
                    "clientContacts" to request.clientContacts,
                    "manager" to request.manager,
                    "requestDate" to request.requestDate
                ))
            }

            if (!response.status.isSuccess()) {
                throw Exception("Google Script вернул ошибку: ${response.status}")
            }

        } catch (e: Exception) {

            throw Exception("Сеть: ${e.localizedMessage ?: "неизвестная ошибка"}")
        }
    }
}
