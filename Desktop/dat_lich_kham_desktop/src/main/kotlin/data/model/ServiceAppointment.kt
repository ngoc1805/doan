package data.model

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime

data class ServiceAppointmentRequest(
    val appointmentId: Int,
    val serviceRoomId: Int,
    val status: String,
    val examDate: String
)

data class ServiceRoomIdsResponse(
    val serviceRoomIds: List<Int>
)
//
data class ServiceAppointmentItem(
    val id: Int,
    val appointmentId: Int,
    val userId: Int,
    val userName: String,
    val gender: String,
    val birthDate: String,
    val homeTown: String,
    val cccd: String,
    val examDate: String,
    val examTime: String,
    val status : String,
    val fmctoken : String
)

data class ListServiceAppointment(
    val serviceappointments: List<ServiceAppointmentItem>
)

data class UpdateServiceAppointmentStatusRequest(
    val serviceAppointmentId: Int,
    val status: String,
)