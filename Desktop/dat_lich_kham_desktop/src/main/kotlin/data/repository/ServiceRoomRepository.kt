package data.repository

import data.api.RetrofitInstance
import data.model.InFoDoctorResponse
import data.model.ListServiceItemResponse
import data.model.ServiceRoomIdsResponse
import data.model.ServiceRoomResponse
import org.json.JSONObject
import retrofit2.Response
import java.io.File

class ServiceRoomRepository {
    private val serviceRoomApi = RetrofitInstance.serviceRoomApi
    private val serviceroomInfoFile = File("serviceroom_info.json")

    suspend fun listServiceRoom() : Response<ListServiceItemResponse> {
        return serviceRoomApi.listServiceRoom()
    }

    suspend fun getServiceRoomByAccountId(accountId: Int) : Response<ServiceRoomResponse> {
        val response = serviceRoomApi.getInFoServiceRoom(accountId)
        response.body()?.let { info ->
            saveServiceRoomInfoToFile(info)
        }
        return response
    }

    // Lưu vào file serviceroom_info.json
    private fun saveServiceRoomInfoToFile(info: ServiceRoomResponse) {
        val json = JSONObject()
        json.put("id", info.id)
        json.put("name", info.name)
        json.put("code", info.code)
        json.put("address", info.address)
        json.put("examPrice", info.examPrice)
        serviceroomInfoFile.writeText(json.toString())
    }

    fun getServiceRoomInfoFromFile(): ServiceRoomResponse? {
        if (!serviceroomInfoFile.exists()) return null
        val json = JSONObject(serviceroomInfoFile.readText())
        return ServiceRoomResponse(
            id = json.getInt("id"),
            name = json.getString("name"),
            code = json.getString("code"),
            address = json.getString("address"),
            examPrice = json.getInt("examPrice"),

        )
    }

    fun clearServiceRoomInfo() {
        if (serviceroomInfoFile.exists()) {
            serviceroomInfoFile.delete()
        }
    }


}