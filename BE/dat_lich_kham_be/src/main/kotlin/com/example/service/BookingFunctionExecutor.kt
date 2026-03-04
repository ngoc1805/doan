package com.example.service

import com.example.repository.*
import com.example.dto.Request.AppointmentRequest
import com.example.dao.UsersDAO
import kotlinx.datetime.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.serialization.json.*

/**
 * BookingFunctionExecutor
 * 
 * Service để thực thi các function calls từ Gemini AI.
 * Mỗi function call từ AI sẽ được route đến corresponding method để:
 * - Lấy dữ liệu từ database
 * - Validate input
 * - Thực hiện business logic
 * - Trả về kết quả cho AI
 */
class BookingFunctionExecutor(
    private val departmentRepo: DepartmentRepository,
    private val doctorRepo: DoctorRepository,
    private val appointmentRepo: AppointmentRepository
) {
    
    companion object {
        private const val DEPOSIT_AMOUNT = 100_000 // Tiền cọc: 100,000 VND
    }
    
    /**
     * Main executor - Route function call đến method tương ứng
     */
    fun execute(functionName: String, parameters: Map<String, Any?>): Any {
        return try {
            when (functionName) {
                "get_departments" -> getDepartments()
                "get_doctors_by_department" -> getDoctorsByDepartment(parameters)
                "get_available_time_slots" -> getAvailableTimeSlots(parameters)
                "create_appointment" -> createAppointment(parameters)
                "check_user_balance" -> checkUserBalance(parameters)
                else -> mapOf(
                    "success" to false,
                    "error" to "Unknown function: $functionName"
                )
            }
        } catch (e: Exception) {
            mapOf(
                "success" to false,
                "error" to "Error executing $functionName: ${e.message}"
            )
        }
    }
    
    /**
     * Function 1: Lấy danh sách tất cả các khoa
     */
    private fun getDepartments(): Map<String, Any> {
        val departments = departmentRepo.getAllDepartments()
        
        return mapOf(
            "success" to true,
            "count" to departments.size,
            "departments" to departments.map { dept ->
                mapOf(
                    "id" to dept.id,
                    "name" to dept.name,
                    "description" to dept.description
                )
            }
        )
    }
    
    /**
     * Function 2: Lấy danh sách bác sĩ theo khoa
     */
    private fun getDoctorsByDepartment(params: Map<String, Any?>): Map<String, Any> {
        // Validate input
        val departmentId = when (val id = params["department_id"]) {
            is Number -> id.toInt()
            is String -> id.toIntOrNull()
            else -> null
        }
        
        if (departmentId == null) {
            return mapOf(
                "success" to false,
                "error" to "department_id is required and must be a number"
            )
        }
        
        // Lấy danh sách bác sĩ (tối đa 20)
        val doctors = doctorRepo.getDoctorsByDepartmentPaged(
            departmentId = departmentId,
            page = 1,
            size = 20
        )
        
        if (doctors.isEmpty()) {
            return mapOf(
                "success" to true,
                "count" to 0,
                "message" to "Không có bác sĩ nào trong khoa này",
                "doctors" to emptyList<Any>()
            )
        }
        
        return mapOf(
            "success" to true,
            "count" to doctors.size,
            "doctors" to doctors.map { doctor ->
                mapOf(
                    "id" to doctor.id,
                    "name" to doctor.name,
                    "code" to doctor.code,
                    "examPrice" to doctor.examPrice,
                    "department" to doctor.department
                )
            }
        )
    }
    
    /**
     * Function 3: Lấy các khung giờ còn trống
     */
    private fun getAvailableTimeSlots(params: Map<String, Any?>): Map<String, Any> {
        // Validate doctor_id
        val doctorId = when (val id = params["doctor_id"]) {
            is Number -> id.toInt()
            is String -> id.toIntOrNull()
            else -> null
        }
        
        if (doctorId == null) {
            return mapOf(
                "success" to false,
                "error" to "doctor_id is required and must be a number"
            )
        }
        
        // Validate date
        val dateStr = params["date"] as? String
        if (dateStr.isNullOrBlank()) {
            return mapOf(
                "success" to false,
                "error" to "date is required in format yyyy-MM-dd (e.g., 2026-02-10)"
            )
        }
        
        // Parse date
        val date = try {
            LocalDate.parse(dateStr)
        } catch (e: Exception) {
            return mapOf(
                "success" to false,
                "error" to "Invalid date format. Use yyyy-MM-dd (e.g., 2026-02-10)"
            )
        }
        
        // Kiểm tra ngày Chủ nhật
        if (date.dayOfWeek == DayOfWeek.SUNDAY) {
            return mapOf(
                "success" to false,
                "error" to "Bệnh viện không làm việc vào Chủ nhật. Vui lòng chọn ngày khác (Thứ Hai đến Thứ Bảy)."
            )
        }
        
        // Kiểm tra ngày trong quá khứ
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        if (date < today) {
            return mapOf(
                "success" to false,
                "error" to "Không thể đặt lịch cho ngày trong quá khứ. Vui lòng chọn ngày từ hôm nay trở đi."
            )
        }
        
        // Danh sách tất cả khung giờ
        val allSlots = listOf(
            "07:00", "07:15", "07:30", "07:45", 
            "08:00", "08:15", "08:30", "08:45", "09:00",
            "14:00", "14:15", "14:30", "14:45", 
            "15:00", "15:15", "15:30", "15:45", "18:00"
        ).map { LocalTime.parse(it) }
        
        // Lấy lịch trống
        val freeSlots = appointmentRepo.getFreeTimeSlots(doctorId, date, allSlots)
        
        if (freeSlots.isEmpty()) {
            return mapOf(
                "success" to true,
                "count" to 0,
                "date" to dateStr,
                "message" to "Không có khung giờ trống vào ngày này. Vui lòng chọn ngày khác.",
                "freeSlots" to emptyList<String>()
            )
        }
        
        return mapOf(
            "success" to true,
            "count" to freeSlots.size,
            "date" to dateStr,
            "freeSlots" to freeSlots.map { it.toString() }
        )
    }
    
    /**
     * Function 4: Tạo appointment (đặt lịch hẹn)
     */
    private fun createAppointment(params: Map<String, Any?>): Map<String, Any> {
        // Validate user_id
        val userId = when (val id = params["user_id"]) {
            is Number -> id.toInt()
            is String -> id.toIntOrNull()
            else -> null
        }
        
        if (userId == null) {
            return mapOf(
                "success" to false,
                "error" to "user_id is required"
            )
        }
        
        // Validate doctor_id
        val doctorId = when (val id = params["doctor_id"]) {
            is Number -> id.toInt()
            is String -> id.toIntOrNull()
            else -> null
        }
        
        if (doctorId == null) {
            return mapOf(
                "success" to false,
                "error" to "doctor_id is required"
            )
        }
        
        // Validate exam_date
        val examDateStr = params["exam_date"] as? String
        if (examDateStr.isNullOrBlank()) {
            return mapOf(
                "success" to false,
                "error" to "exam_date is required (yyyy-MM-dd)"
            )
        }
        
        val examDate = try {
            LocalDate.parse(examDateStr)
        } catch (e: Exception) {
            return mapOf(
                "success" to false,
                "error" to "Invalid exam_date format. Use yyyy-MM-dd"
            )
        }
        
        // Validate exam_time
        val examTimeStr = params["exam_time"] as? String
        if (examTimeStr.isNullOrBlank()) {
            return mapOf(
                "success" to false,
                "error" to "exam_time is required (HH:mm)"
            )
        }
        
        val examTime = try {
            LocalTime.parse(examTimeStr)
        } catch (e: Exception) {
            return mapOf(
                "success" to false,
                "error" to "Invalid exam_time format. Use HH:mm (e.g., 14:00)"
            )
        }
        
        // Tạo appointment
        return try {
            val appointment = appointmentRepo.createAppointment(
                AppointmentRequest(
                    userId = userId,
                    doctorId = doctorId,
                    examDate = examDate,
                    examTime = examTime,
                    status = "Đã lên lịch"
                )
            )
            
            // Trừ tiền cọc
            transaction {
                val user = UsersDAO.findById(userId)
                if (user != null) {
                    user.balance = user.balance - DEPOSIT_AMOUNT
                }
            }
            
            mapOf(
                "success" to true,
                "appointmentId" to appointment.id,
                "status" to appointment.status,
                "depositPaid" to DEPOSIT_AMOUNT,
                "message" to "Đặt lịch thành công! Đã trừ ${formatMoney(DEPOSIT_AMOUNT)} tiền cọc."
            )
        } catch (e: Exception) {
            mapOf(
                "success" to false,
                "error" to "Lỗi khi đặt lịch: ${e.message}"
            )
        }
    }
    
    /**
     * Function 5: Kiểm tra số dư tài khoản
     */
    private fun checkUserBalance(params: Map<String, Any?>): Map<String, Any> {
        // Validate user_id
        val userId = when (val id = params["user_id"]) {
            is Number -> id.toInt()
            is String -> id.toIntOrNull()
            else -> null
        }
        
        if (userId == null) {
            return mapOf(
                "success" to false,
                "error" to "user_id is required"
            )
        }
        
        // Lấy balance từ database
        val balance = transaction {
            UsersDAO.findById(userId)?.balance ?: 0
        }
        
        val sufficient = balance >= DEPOSIT_AMOUNT
        
        return mapOf(
            "success" to true,
            "userId" to userId,
            "balance" to balance,
            "depositRequired" to DEPOSIT_AMOUNT,
            "sufficient" to sufficient,
            "message" to if (sufficient) {
                "Số dư đủ để thanh toán cọc"
            } else {
                "Số dư không đủ. Cần nạp thêm ${formatMoney(DEPOSIT_AMOUNT - balance)}"
            }
        )
    }
    
    /**
     * Helper: Format tiền
     */
    private fun formatMoney(amount: Int): String {
        return "%,d VND".format(amount)
    }
}
