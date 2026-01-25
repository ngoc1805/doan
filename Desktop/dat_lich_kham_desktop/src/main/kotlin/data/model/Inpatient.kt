package data.model


data class InpatientRequest(
    val userId: Int,
)

data class InpatientItem(
    val id: Int,
    val userId: Int,
    val fullname: String,
    val gender: String,
    val birthDate: String,
    val cccd: String,
    val hometown: String,
    val address: String,
    val admissionDate: String?, // Ngày nhập viện
    val dischargeDate: String?, // Ngày xuất viện
    val status: String,
    val createAt: String
)

data class InpatientListResponse(
    val inpatients: List<InpatientItem>
)

data class UpdateStatusInpatient(
    val id: Int
)

data class UpdateAddressRequest(
    val id: Int,
    val address: String
)