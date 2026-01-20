package data.model

import kotlinx.serialization.Serializable

data class ServiceRoomItem(
    val id: Int,
    val name: String,
    val code: String,
    val address: String,
    val examPrice: Int
)

data class ListServiceItemResponse(
    val servicerooms : List<ServiceRoomItem>
)

data class ServiceRoomResponse(
    val id: Int,
    val name: String,
    val code: String,
    val address: String,
    val examPrice: Int
)

data class CreateServiceRoomRequest(
    val name: String,
    val code: String,
    val address: String,
    val examPrice: Int
)