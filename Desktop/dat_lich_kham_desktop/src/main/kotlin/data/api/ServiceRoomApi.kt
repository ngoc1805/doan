package data.api

import data.model.ListServiceItemResponse
import data.model.ServiceRoomResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ServiceRoomApi {
    @GET("/api/service-rooms")
    suspend fun listServiceRoom(): Response<ListServiceItemResponse>

    @GET("/api/chucnang/service-room")
    suspend fun getInFoServiceRoom(@Query("accountId") accountId : Int) : Response<ServiceRoomResponse>
}