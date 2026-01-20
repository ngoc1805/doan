package data.model

import kotlinx.serialization.Serializable

data class InFoDoctorResponse(
    val id: Int,
    val name: String,
    val code: String,
    val examPrice: Int,
    val department: String,
    val balance : Int
)

data class DoctorListItem(
    val id: Int,
    val name: String,
    val code: String,
    val examPrice: Int,
    val department: String
)

data class DoctorListResponse(
    val doctors: List<DoctorListItem>
)

data class CreateDoctorRequest(
    val name: String,
    val code: String,
    val departmentId: Int,
    val examPrice: Int
)