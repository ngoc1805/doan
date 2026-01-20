package data.model

data class DepartmentResponse(
    val id: Int,
    val name: String,
    val description: String,
)

data class CreateDepartmentRequest(
    val name: String,
    val description: String,
)