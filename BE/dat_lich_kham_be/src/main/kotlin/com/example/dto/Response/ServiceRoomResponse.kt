package com.example.dto.Response

import kotlinx.serialization.Serializable

@Serializable
data class ServiceRoomItem(
    val id: Int,
    val name: String,
    val code: String,
    val address: String,
    val examPrice: Int
)

@Serializable
data class ListServiceItemResponse(
    val servicerooms : List<ServiceRoomItem>
)


@Serializable
data class ServiceRoomResponse(
    val id: Int,
    val name: String,
    val code: String,
    val address: String,
    val examPrice: Int
)