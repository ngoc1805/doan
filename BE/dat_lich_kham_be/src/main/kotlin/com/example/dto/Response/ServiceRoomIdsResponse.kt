package com.example.dto.Response

import kotlinx.serialization.Serializable

@Serializable
data class ServiceRoomIdsResponse(
    val serviceRoomIds: List<Int>
)
