//package data.repository
//
//import data.api.RetrofitInstance
//import data.model.DoctorListResponse
//import data.model.InFoDoctorResponse
//import retrofit2.Response
//import org.json.JSONObject
//import java.io.File
//
//class DoctorRepository {
//    private val doctorApi = RetrofitInstance.doctorApi
//    private val doctorInfoFile = File("doctor_info.json")
//
//    // Lấy từ API
//    suspend fun getDoctorByAccountId(accountId: Int): Response<InFoDoctorResponse> {
//        val response = doctorApi.getDoctorByAccountId(accountId)
//        response.body()?.let { info ->
//            saveDoctorInfoToFile(info)
//        }
//        return response
//    }
//
//    suspend fun getAllDoctors(): Response<DoctorListResponse>{
//        return doctorApi.getAllDoctors()
//    }
//
//    // Lưu vào file doctor_info.json
//    private fun saveDoctorInfoToFile(info: InFoDoctorResponse) {
//        val json = JSONObject()
//        json.put("id", info.id)
//        json.put("name", info.name)
//        json.put("code", info.code)
//        json.put("examPrice", info.examPrice)
//        json.put("department", info.department)
//        json.put("balance", info.balance)
//        doctorInfoFile.writeText(json.toString())
//    }
//
//    // Đọc từ file doctor_info.json
//    fun getDoctorInfoFromFile(): InFoDoctorResponse? {
//        if (!doctorInfoFile.exists()) return null
//        val json = JSONObject(doctorInfoFile.readText())
//        return InFoDoctorResponse(
//            id = json.getInt("id"),
//            name = json.getString("name"),
//            code = json.getString("code"),
//            examPrice = json.getInt("examPrice"),
//            department = json.getString("department"),
//            balance = json.getInt("balance")
//        )
//    }
//    fun clearDoctorInfo() {
//        if (doctorInfoFile.exists()) {
//            doctorInfoFile.delete()
//        }
//    }
//}

package data.repository

import data.api.RetrofitInstance
import data.model.DoctorListResponse
import data.model.InFoDoctorResponse
import retrofit2.Response
import org.json.JSONObject
import java.io.File

class DoctorRepository {
    private val doctorApi = RetrofitInstance.doctorApi
    private val doctorInfoFile = File("doctor_info.json")

    suspend fun getDoctorByAccountId(accountId: Int): Response<InFoDoctorResponse> {
        val response = doctorApi.getDoctorByAccountId(accountId)
        response.body()?.let { info ->
            saveDoctorInfoToFile(info)
        }
        return response
    }

    suspend fun getAllDoctors(): Response<DoctorListResponse>{
        return doctorApi.getAllDoctors()
    }

    private fun saveDoctorInfoToFile(info: InFoDoctorResponse) {
        val json = JSONObject()
        json.put("id", info.id)
        json.put("name", info.name)
        json.put("code", info.code)
        json.put("examPrice", info.examPrice)
        json.put("department", info.department)
        json.put("balance", info.balance)
        doctorInfoFile.writeText(json.toString())
    }

    fun getDoctorInfoFromFile(): InFoDoctorResponse? {
        if (!doctorInfoFile.exists()) return null
        val json = JSONObject(doctorInfoFile.readText())
        return InFoDoctorResponse(
            id = json.getInt("id"),
            name = json.getString("name"),
            code = json.getString("code"),
            examPrice = json.getInt("examPrice"),
            department = json.getString("department"),
            balance = json.getInt("balance")
        )
    }

    fun clearDoctorInfo() {
        if (doctorInfoFile.exists()) {
            doctorInfoFile.delete()
        }
        // Xóa luôn tokens khi clear doctor info
        File("access_token.txt").delete()
        File("refresh_token.txt").delete()
        File("user_info.json").delete()
    }
}