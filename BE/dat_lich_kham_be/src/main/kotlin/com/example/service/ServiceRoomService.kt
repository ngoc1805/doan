package com.example.service

import com.example.repository.ServiceRoomRepository
import com.example.dto.Response.ServiceRoomItem
import com.example.dto.Response.ServiceRoomResponse

class ServiceRoomService(
    private val repository: ServiceRoomRepository = ServiceRoomRepository()
) {
    fun getAllRooms(): List<ServiceRoomItem> = repository.getAll()

    fun getRoomByAccountId(accountId: Int): ServiceRoomResponse? =
        repository.getByAccountId(accountId)
}