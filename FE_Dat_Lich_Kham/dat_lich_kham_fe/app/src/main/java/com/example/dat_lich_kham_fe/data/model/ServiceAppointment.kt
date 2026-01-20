package com.example.dat_lich_kham_fe.data.model


data class ServiceRoomItem(
    val id: Int,
    val name: String,
    val address: String,
    val examPrice: Int
)

data class ListServiceItemResponse(
    val servicerooms : List<ServiceRoomItem>
)
