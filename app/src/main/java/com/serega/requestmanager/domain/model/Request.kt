package com.serega.requestmanager.domain.model


data class Request (
    val id: Long = 0,
    val orderNumber: String = "",
    val address: String = "",
    val responseCenter: String = "",
    val division: String = "",
    val objectType: String = "",
    val problemDescription: String = "",
    val clientContacts: String = "",
    val manager: String = "",
    val requestDate: String,
    val isSynced: Boolean = false,
    val syncError: String? = null
)
