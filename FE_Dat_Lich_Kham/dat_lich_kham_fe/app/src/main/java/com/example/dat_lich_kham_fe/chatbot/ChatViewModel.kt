package com.example.dat_lich_kham_fe.chatbot

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dat_lich_kham_fe.Firebase.ChatViewModel
import com.example.dat_lich_kham_fe.data.model.*
import com.example.dat_lich_kham_fe.data.repository.AppointmentRepository
import com.example.dat_lich_kham_fe.util.UserLocalStore
import com.example.dat_lich_kham_fe.util.chuyenDoiNgay
import com.example.dat_lich_kham_fe.util.fcmToken
import com.example.dat_lich_kham_fe.util.formatNumber
import com.example.dat_lich_kham_fe.viewmodel.*
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.TextPart
import kotlinx.coroutines.delay

enum class ChatState {
    NORMAL_CHAT,
    WAITING_DEPARTMENT_SELECTION,
    WAITING_APPOINTMENT_CONFIRMATION,
    WAITING_DOCTOR_SELECTION,
    WAITING_DATE_SELECTION,
    WAITING_TIME_SELECTION,
    WAITING_PAYMENT_CONFIRMATION,
    WAITING_PIN_INPUT,
    WAITING_NEW_PIN_CREATION,
    WAITING_PIN_CONFIRMATION
}

class ChatViewModel(
    private val context: Context
) : ViewModel() {

    val messageList by lazy {
        mutableStateListOf<MessageModel>()
    }
    private var currentChatState = ChatState.NORMAL_CHAT

    private var selectedDepartment: DepartmentResponse? = null
    private var selectedDoctor: DoctorResponse? = null
    private var selectedDate = ""
    private var selectedTimeSlot = ""
    private var availableTimeSlots = listOf<String>()
    private var newPinToConfirm = ""
    private var userId: Int? = null
    private var pinInputStep = false

    // ViewModels
    private val departmentVM = DepartmentViewModel(context)
    private val doctorVM = DoctorViewModel(context)
    private val appointmentVM = AppointmentViewModel(context)
    private val userVM = UserViewModel(context)
    private val notiVM = NotificationViewModel(context)
    private val fireVM = ChatViewModel()

    // AI model
    val generativeModel: GenerativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = Constants.apiKey
    )

    // ====== Hàm public =======
    fun setUserId(id: Int) {
        userId = id
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun sendMessage(question: String) {
        viewModelScope.launch {
            try {
                when (currentChatState) {
                    ChatState.NORMAL_CHAT -> handleNormalChat(question)
                    ChatState.WAITING_APPOINTMENT_CONFIRMATION -> handleAppointmentConfirmation(question)
                    ChatState.WAITING_DOCTOR_SELECTION -> handleDoctorSelection(question)
                    ChatState.WAITING_DATE_SELECTION -> handleDateSelection(question)
                    ChatState.WAITING_TIME_SELECTION -> handleTimeSelection(question)
                    ChatState.WAITING_PAYMENT_CONFIRMATION -> handlePaymentConfirmation(question)
                    ChatState.WAITING_PIN_INPUT -> handlePinInput(question)
                    ChatState.WAITING_NEW_PIN_CREATION -> handleNewPinCreation(question)
                    ChatState.WAITING_PIN_CONFIRMATION -> handlePinConfirmation(question)
                    else -> handleNormalChat(question)
                }
            } catch (e: Exception) {
                messageList.add(MessageModel("Lỗi: ${e.message}", "model"))
            }
        }
    }

    // =================== BƯỚC 1: XỬ LÝ CHAT BÌNH THƯỜNG ===================
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private suspend fun handleNormalChat(question: String) {
        messageList.add(MessageModel(question, "user"))

        // Kiểm tra xem câu hỏi có liên quan đến bệnh tật không
        if (isHealthRelatedQuestion(question)) {
            // Lấy department gợi ý
            val suggestedDepartmentName = getSuggestedDepartment(question)
            val department = getDepartmentByName(suggestedDepartmentName)
            selectedDepartment = department

            val response = "Dựa trên triệu chứng bạn mô tả, tôi khuyên bạn nên đi khám tại **${department?.name ?: suggestedDepartmentName}**.\n\nBạn có muốn tôi giúp bạn đặt lịch khám không?"

            messageList.add(MessageModel(response, "model"))
            currentChatState = ChatState.WAITING_APPOINTMENT_CONFIRMATION
        } else {
            // Chat bình thường với Gemini
            messageList.add(MessageModel("...", "model"))

            val chat = generativeModel.startChat(
                history = messageList.dropLast(1).map {
                    Content(
                        role = it.role, // "user" hoặc "model"
                        parts = listOf(TextPart(it.message))
                    )
                }
            )
            val response = chat.sendMessage(question)
            messageList.removeLast()
            messageList.add(MessageModel(response.text.toString(), "model"))
        }
    }

    private suspend fun isHealthRelatedQuestion(question: String): Boolean {
        val prompt = """
            Phân tích câu hỏi sau và trả lời chỉ "CÓ" hoặc "KHÔNG":
            Câu hỏi có liên quan đến triệu chứng bệnh, sức khỏe, hoặc vấn đề y tế không?

            Câu hỏi: "$question"
            Trả lời:
        """.trimIndent()

        val response = generativeModel.generateContent(prompt)
        return response.text?.trim()?.uppercase()?.contains("CÓ") == true
    }

    private suspend fun getSuggestedDepartment(question: String): String {
        // Lấy danh sách tên khoa từ DepartmentViewModel (cache nếu có)
        val departments = if (departmentVM.departments.isNotEmpty())
            departmentVM.departments.map { it.name }
        else {
            departmentVM.fetchDepartments()
            // Chờ lấy xong danh sách khoa nếu chưa có cache
            var loopCount = 0
            while (departmentVM.isLoading && loopCount < 100) {
                delay(50)
                loopCount++
            }
            departmentVM.departments.map { it.name }
        }
        val prompt = """
            Dựa trên triệu chứng được mô tả, hãy chọn khoa phù hợp nhất từ danh sách sau:
            ${departments.joinToString(", ")}

            Triệu chứng: "$question"
            Chỉ trả lời tên khoa, không giải thích:
        """.trimIndent()

        val response = generativeModel.generateContent(prompt)
        val suggested = response.text?.trim() ?: ""
        return departments.find { it.contains(suggested, ignoreCase = true) }
            ?: departments.find { suggested.contains(it, ignoreCase = true) }
            ?: departments.firstOrNull() ?: "Khoa nội"
    }

    private fun getDepartmentByName(name: String): DepartmentResponse? {
        return departmentVM.departments.find { it.name.equals(name, ignoreCase = true) }
    }

    // =================== BƯỚC 2: XÁC NHẬN ĐẶT LỊCH ===================
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private suspend fun handleAppointmentConfirmation(answer: String) {
        messageList.add(MessageModel(answer, "user"))
        if (isPositiveAnswer(answer)) {
            messageList.add(MessageModel("...", "model"))
            val departmentId = selectedDepartment?.id ?: 0
            doctorVM.resetPaging()
            doctorVM.fetchDoctors(departmentId, false)
            // Chờ lấy xong danh sách bác sĩ (bất đồng bộ)
            var loopCount = 0
            while (doctorVM.isLoading && loopCount < 100) {
                delay(50)
                loopCount++
            }
            val doctors = doctorVM.doctors
            messageList.removeLast()
            if (doctors.isNotEmpty()) {
                val doctorList = doctors.mapIndexed { index, doctor ->
                    "${index + 1}. ${doctor.name} (${doctor.code}) - Giá khám: ${formatPrice(doctor.examPrice)}"
                }.joinToString("\n")
                val response = "Đây là danh sách bác sĩ tại ${selectedDepartment?.name ?: ""}:\n\n$doctorList\n\nVui lòng chọn số thứ tự bác sĩ bạn muốn khám:"
                messageList.add(MessageModel(response, "model"))
                currentChatState = ChatState.WAITING_DOCTOR_SELECTION
            } else {
                messageList.add(MessageModel("Rất tiếc, hiện tại không có bác sĩ nào tại ${selectedDepartment?.name}. Vui lòng thử lại sau hoặc chọn khoa khác.", "model"))
                currentChatState = ChatState.NORMAL_CHAT
                resetBookingData()
            }
        } else {
            messageList.add(MessageModel("Được rồi, nếu bạn cần hỗ trợ gì khác, hãy cho tôi biết nhé!", "model"))
            currentChatState = ChatState.NORMAL_CHAT
        }
    }

    // =================== BƯỚC 3: CHỌN BÁC SĨ ===================
    private suspend fun handleDoctorSelection(selection: String) {
        messageList.add(MessageModel(selection, "user"))
        val doctorIndex = selection.trim().toIntOrNull()
        val doctors = doctorVM.doctors
        if (doctorIndex != null && doctorIndex in 1..doctors.size) {
            selectedDoctor = doctors[doctorIndex - 1]
            val response = "Bạn đã chọn ${selectedDoctor!!.name} (${selectedDoctor!!.code}).\n\nVui lòng cho tôi biết ngày bạn muốn đi khám (định dạng: dd/MM/yyyy):"
            messageList.add(MessageModel(response, "model"))
            currentChatState = ChatState.WAITING_DATE_SELECTION
        } else {
            messageList.add(MessageModel("Vui lòng chọn số thứ tự hợp lệ từ danh sách bác sĩ.", "model"))
        }
    }

    // =================== BƯỚC 4: CHỌN NGÀY KHÁM ===================
    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleDateSelection(date: String) {
        messageList.add(MessageModel(date, "user"))
        if (isValidDate(date)) {
            if (isSunday(date)) {
                messageList.add(MessageModel("⚠️ Rất tiếc, bệnh viện không làm việc vào Chủ nhật.\n\nVui lòng chọn ngày khác (từ Thứ Hai đến Thứ Bảy):", "model"))
                return
            }
            selectedDate = date
            messageList.add(MessageModel("...", "model"))

            val allSlots = listOf(
                "07:00", "07:15", "07:30", "07:45", "08:00", "08:15", "08:30", "08:45", "09:00",
                "14:00", "14:15", "14:30", "14:45", "15:00", "15:15", "15:30", "15:45", "18:00"
            )
            val doctorId = selectedDoctor?.id ?: 0
            val repo = AppointmentRepository(context)

            // Chuyển từ dd/MM/yyyy sang yyyy-MM-dd
            val apiDate = try {
                val inputFormatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val outputFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
                java.time.LocalDate.parse(selectedDate, inputFormatter).format(outputFormatter)
            } catch (e: Exception) {
                "Ngày không hợp lệ"
            }

            Log.d("ChatViewModel", "getFreeTime doctorId=$doctorId date=$apiDate slots=$allSlots")

            viewModelScope.launch {
                val freeSlots = repo.getFreeTime(context, doctorId, apiDate, allSlots)
                Log.d("ChatViewModel", "Free slots nhận về: $freeSlots")
                availableTimeSlots = freeSlots

                messageList.removeLast()
                if (availableTimeSlots.isNotEmpty()) {
                    val timeList = availableTimeSlots.mapIndexed { index, slot ->
                        "${index + 1}. $slot"
                    }.joinToString("\n")
                    val response = "Các khung giờ còn trống vào ngày $selectedDate:\n\n$timeList\n\nVui lòng chọn số thứ tự khung giờ:"
                    messageList.add(MessageModel(response, "model"))
                    currentChatState = ChatState.WAITING_TIME_SELECTION
                } else {
                    messageList.add(MessageModel("Rất tiếc, ngày $selectedDate không có khung giờ trống. Vui lòng chọn ngày khác.", "model"))
                    currentChatState = ChatState.WAITING_DATE_SELECTION
                }
            }
        } else {
            messageList.add(MessageModel("Định dạng ngày không hợp lệ. Vui lòng nhập theo định dạng dd/MM/yyyy (ví dụ: 25/12/2024).", "model"))
        }
    }
    private fun isValidDate(date: String): Boolean {
        val regex = Regex("""\d{1,2}/\d{1,2}/\d{4}""")
        return regex.matches(date.trim())
    }

    private fun isSunday(date: String): Boolean {
        return try {
            val parts = date.split("/")
            if (parts.size != 3) return false

            val day = parts[0].toInt()
            val month = parts[1].toInt()
            val year = parts[2].toInt()

            val adjustedMonth = if (month < 3) month + 12 else month
            val adjustedYear = if (month < 3) year - 1 else year

            val q = day
            val m = adjustedMonth
            val k = adjustedYear % 100
            val j = adjustedYear / 100

            val h = (q + ((13 * (m + 1)) / 5) + k + (k / 4) + (j / 4) - 2 * j) % 7

            return h == 1
        } catch (e: Exception) {
            Log.e("ChatViewModel", "Error checking if Sunday: ${e.message}")
            false
        }
//        // ... (Giữ nguyên hàm cũ)
//        return false // TODO: Viết lại kiểm tra ngày Chủ nhật nếu cần
    }

    // =================== BƯỚC 5: CHỌN GIỜ KHÁM ===================
    private suspend fun handleTimeSelection(selection: String) {
        messageList.add(MessageModel(selection, "user"))
        val timeIndex = selection.trim().toIntOrNull()
        if (timeIndex != null && timeIndex in 1..availableTimeSlots.size) {
            selectedTimeSlot = availableTimeSlots[timeIndex - 1]
            val response = """
                📋 Xác nhận thông tin đặt lịch:
                👨‍⚕️ Bác sĩ: ${selectedDoctor!!.name} (${selectedDoctor!!.code})
                🏥 Khoa: ${selectedDepartment!!.name}
                📅 Ngày: $selectedDate
                ⏰ Giờ: $selectedTimeSlot
                💰 Giá khám: ${formatPrice(selectedDoctor!!.examPrice)}
                💳 Để xác nhận lịch khám, bạn cần thanh toán cọc 100,000 VND.
                Bạn có đồng ý thanh toán cọc không?
            """.trimIndent()
            messageList.add(MessageModel(response, "model"))
            currentChatState = ChatState.WAITING_PAYMENT_CONFIRMATION
        } else {
            messageList.add(MessageModel("Vui lòng chọn số thứ tự hợp lệ từ danh sách khung giờ.", "model"))
        }
    }

    // =================== BƯỚC 6: XÁC NHẬN THANH TOÁN ===================
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private suspend fun handlePaymentConfirmation(answer: String) {
        messageList.add(MessageModel(answer, "user"))
        if (isPositiveAnswer(answer)) {
            val userStore = UserLocalStore(context)
            val user = userStore.getUser()
            userId = user?.Id ?: 0
            val uid = userId
            if (uid == null) {
                messageList.add(MessageModel("❌ Lỗi: Không tìm thấy thông tin bệnh nhân. Vui lòng đăng nhập lại.", "model"))
                currentChatState = ChatState.NORMAL_CHAT
                resetBookingData()
                return
            }
            messageList.add(MessageModel("⏳ Đang kiểm tra thông tin thanh toán...", "model"))
            // Kiểm tra PIN
            userVM.checkHasPin(uid) { hasPin ->
                messageList.removeLast()
                if (hasPin) {
                    messageList.add(MessageModel("🔐 Để thanh toán cọc, vui lòng nhập mã PIN của bạn:", "model"))
                    currentChatState = ChatState.WAITING_PIN_INPUT
                } else {
                    messageList.add(MessageModel("🔐 Bạn chưa có mã PIN để thanh toán.\n\nVui lòng tạo mã PIN mới (6 chữ số):", "model"))
                    currentChatState = ChatState.WAITING_NEW_PIN_CREATION
                }
            }
        } else {
            messageList.add(MessageModel("Không vấn đề gì! Nếu bạn muốn đặt lịch sau, hãy cho tôi biết nhé.", "model"))
            currentChatState = ChatState.NORMAL_CHAT
            resetBookingData()
        }
    }

    // =================== BƯỚC 7A: TẠO PIN MỚI ===================
    private suspend fun handleNewPinCreation(pin: String) {
        messageList.add(MessageModel(pin, "user"))
        if (pin.trim().length == 6 && pin.trim().all { it.isDigit() }) {
            newPinToConfirm = pin.trim()
            messageList.add(MessageModel("🔐 Vui lòng nhập lại mã PIN để xác nhận:", "model"))
            currentChatState = ChatState.WAITING_PIN_CONFIRMATION
        } else {
            messageList.add(MessageModel("❌ Mã PIN phải có đúng 6 chữ số. Vui lòng nhập lại:", "model"))
        }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private suspend fun handlePinConfirmation(pin: String) {
        messageList.add(MessageModel(pin, "user"))
        val uid = userId ?: return
        if (pin.trim() == newPinToConfirm) {
            messageList.add(MessageModel("⏳ Đang tạo mã PIN...", "model"))
            userVM.createOrUpdatePin(uid, newPinToConfirm) { success ->
                messageList.removeLast()
                if (success) {
                    messageList.add(MessageModel("✅ Tạo mã PIN thành công!\n\n⏳ Đang xử lý thanh toán cọc 100,000 VND...", "model"))
                    viewModelScope.launch {
                        processPayment()
                    }
                } else {
                    messageList.add(MessageModel("❌ Lỗi tạo mã PIN. Vui lòng thử lại.", "model"))
                    currentChatState = ChatState.WAITING_NEW_PIN_CREATION
                }
            }
        } else {
            messageList.add(MessageModel("❌ Mã PIN xác nhận không khớp. Vui lòng nhập lại:", "model"))
        }
    }

    // =================== BƯỚC 7B: NHẬP PIN CÓ SẴN ===================
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private suspend fun handlePinInput(pin: String) {
        messageList.add(MessageModel("******", "user")) // Ẩn PIN khi hiển thị
        messageList.add(MessageModel("⏳ Đang xác thực mã PIN...", "model"))
        val uid = userId ?: return
        userVM.checkPin(uid, pin.trim()) { success ->
            messageList.removeLast()
            if (success) {
                messageList.add(MessageModel("✅ Xác thực thành công!\n\n⏳ Đang xử lý thanh toán cọc 100,000 VND...", "model"))
                viewModelScope.launch {
                    processPayment()
                }
            } else {
                messageList.add(MessageModel("❌ Mã PIN không chính xác. Vui lòng thử lại:", "model"))
            }
        }
    }

    // =================== XỬ LÝ THANH TOÁN & ĐẶT LỊCH ===================
    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun processPayment() {
        val userStore = UserLocalStore(context)
        val user = userStore.getUser()
        userId = user?.Id ?: 0
        val uid = user?.Id ?: 0
        val doctor = selectedDoctor ?: return
        val department = selectedDepartment ?: return
        val examPrice = doctor.examPrice
        val deposit = 100_000

        // Chuyển selectedDate từ dd/MM/yyyy sang yyyy-MM-dd trước khi gửi cho API
        val apiExamDate = try {
            val inputFormatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val outputFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
            java.time.LocalDate.parse(selectedDate, inputFormatter).format(outputFormatter)
        } catch (e: Exception) {
            selectedDate // fallback nếu lỗi
        }

        Log.d(
            "processPayment",
            "userId=$uid, doctorId=${doctor.id}, examDate=$apiExamDate, examTime=$selectedTimeSlot, status=Đã lên lịch"
        )

        // Trừ tiền và đặt lịch
        userVM.updatebalance(uid, userVM.balance - deposit)
        appointmentVM.bookappointment(
            userId = uid,
            doctorId = doctor.id,
            examDate = apiExamDate, // Dùng ngày đã format đúng
            examTime = selectedTimeSlot,
            status = "Đã lên lịch"
        )
        notiVM.postNotification(
            userId = com.example.dat_lich_kham_fe.util.userId,
            content = "Bạn đã cọc ${formatNumber(deposit)}. Số dư hiện tại: ${formatNumber((userVM.balance - deposit))} VND",
            path = "DepositScreen/false"
        )
        notiVM.postNotification(
            userId = uid,
            content = "Bạn đã đặt thành công một lịch khám. Vui lòng đến đúng giờ để được phục vụ tốt nhất.",
            path = "MainScreen/1"
        )
        fireVM.sendMessage(
            title = "Biến động số dư",
            body = "Bạn đã cọc ${formatNumber(deposit)}. Số dư hiện tại: ${
                formatNumber(
                    (userVM.balance - deposit)
                )
            } VND\"",
            remoteToken = fcmToken,
            isBroadcast = false
        )
        fireVM.sendMessage(
            title = "Thông báo lịch khám",
            body = "Bạn đã đặt thành công một lịch khám. Vui lòng đến đúng giờ để được phục vụ tốt nhất.",
            remoteToken = fcmToken,
            isBroadcast = false
        )


        // Thông báo
        val response = """
        🎉 Đặt lịch và thanh toán thành công!

        📋 Thông tin lịch khám:
        👨‍⚕️ Bác sĩ: ${doctor.name} (${doctor.code})
        🏥 Khoa: ${department.name}
        📅 Ngày: $selectedDate
        ⏰ Giờ: $selectedTimeSlot
        💰 Giá khám: ${formatPrice(examPrice)}
        💳 Đã thanh toán cọc: 100,000 VND

        📝 Lưu ý:
        • Vui lòng đến trước giờ hẹn 15 phút để làm thủ tục
        • Mang theo CCCD và các giấy tờ liên quan
        • Số tiền cọc sẽ được trừ vào tổng chi phí khám

        Chúc bạn sức khỏe tốt! 💚
    """.trimIndent()
        messageList.add(MessageModel(response, "model"))
        currentChatState = ChatState.NORMAL_CHAT
        resetBookingData()
    }

    // =================== TIỆN ÍCH ===================
    private fun isPositiveAnswer(answer: String): Boolean {
        val positiveWords = listOf("có", "được", "đồng ý", "ok", "yes", "vâng", "ừ", "uhm", "đúng")
        return positiveWords.any { answer.lowercase().contains(it) }
    }

    private fun formatPrice(price: Int): String {
        return "%,d VND".format(price)
    }

    private fun resetBookingData() {
        selectedDepartment = null
        selectedDoctor = null
        selectedDate = ""
        selectedTimeSlot = ""
        availableTimeSlots = emptyList()
        newPinToConfirm = ""
    }
}
