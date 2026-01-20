package com.example.repository

import com.example.dao.ServiceRoomDAO
import com.example.dto.Response.ServiceRoomItem
import com.example.dto.Response.ServiceRoomResponse
import org.jetbrains.exposed.sql.transactions.transaction

class ServiceRoomRepository {
    fun getAll(): List<ServiceRoomItem> {
        return transaction {
            ServiceRoomDAO.all().map {
                ServiceRoomItem(
                    id = it.id.value,
                    name = it.name,
                    code = it.code,
                    address = it.address,
                    examPrice = it.examPrice
                )
            }
        }
    }

    fun getByAccountId(accountId: Int): ServiceRoomResponse? {
        return transaction {
            ServiceRoomDAO.find { com.example.Tables.ServiceRooms.accountId eq accountId }
                .firstOrNull()
                ?.let {
                    ServiceRoomResponse(
                        id = it.id.value,
                        name = it.name,
                        code = it.code,
                        address = it.address,
                        examPrice = it.examPrice
                    )
                }
        }
    }
}