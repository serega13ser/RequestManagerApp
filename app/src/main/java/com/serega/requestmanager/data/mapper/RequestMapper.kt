package com.serega.requestmanager.data.mapper

import com.serega.requestmanager.data.local.RequestEntity
import com.serega.requestmanager.domain.model.Request

fun RequestEntity.toDomainRequest(): Request {
    return Request(
        id = this.id,
        orderNumber = this.orderNumber,
        address = this.address,
        responseCenter = this.responseCenter,
        division = this.division,
        objectType = this.objectType,
        problemDescription = this.problemDescription,
        clientContacts = this.clientContacts,
        manager = this.manager,
        requestDate = this.requestDate,
        isSynced = this.isSynced,
        syncError = this.syncError
    )
}

fun Request.toEntityRequest(): RequestEntity{
    return RequestEntity(
        id = this.id,
        orderNumber = this.orderNumber,
        address = this.address,
        responseCenter = this.responseCenter,
        division = this.division,
        objectType = this.objectType,
        problemDescription = this.problemDescription,
        clientContacts = this.clientContacts,
        manager = this.manager,
        requestDate = this.requestDate,
        isSynced = this.isSynced,
        syncError = this.syncError
    )
}