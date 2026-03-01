package com.serega.requestmanager.ui.profile

import com.serega.requestmanager.data.local.RequestEntity

data class RequestGroup(
    val dateDisplay: String,
    val dayOfWeek: String,
    val requests: List<RequestEntity>
)

data class RequestListUiState(
    val groupedRequests: List<RequestGroup> = emptyList(),
    val totalCount: Int = 0,
    val localCount: Int = 0,
    val syncedCount: Int = 0,
    val filteredCount: Int = 0
)

data class RequestFormState(
    val orderNumber: String = "",
    val address: String = "",
    val responseCenter: String = "",
    val division: String = "",
    val objectType: String = "",
    val problemDescription: String = "",
    val clientContacts: String = "",
    val manager: String = "",
    val orderNumberError: String? = null,
    val addressError: String? = null,
    val clientContactsError: String? = null
)