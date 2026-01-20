package data.model


data class AppointmentByDoctorIdItem(
    val id: Int,
    val userId: Int,
    val userName: String,
    val gender: String,
    val birthDate: String,
    val homeTown: String,
    val cccd: String,
    val examDate: String,
    val examTime: String,
    val status: String,
    val fmctoken: String
)

data class AppointmentByDoctorIdListResponse(
    val appointments: List<AppointmentByDoctorIdItem>
)

data class UpdateAppointmentSatatusRequest (
    val appointmentId: Int,
    val status : String
)