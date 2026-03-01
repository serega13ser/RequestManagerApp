package com.serega.requestmanager.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "requests")
data class RequestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "order_number")
    val orderNumber: String = "",

    @ColumnInfo(name = "address")
    val address: String = "",

    @ColumnInfo(name = "response_center")
    val responseCenter: String = "",

    @ColumnInfo(name = "division")
    val division: String = "",

    @ColumnInfo(name = "object_type")
    val objectType: String = "",

    @ColumnInfo(name = "problem_description")
    val problemDescription: String = "",

    @ColumnInfo(name = "client_contacts")
    val clientContacts: String = "",

    @ColumnInfo(name = "manager")
    val manager: String = "",

    @ColumnInfo(name = "request_date")
    val requestDate: String,

    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean = false,

    @ColumnInfo(name = "sync_error")
    val syncError: String? = null
)

