package ui.screen.doctor

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.input.TextFieldValue
import firebase.ChatViewModel
import ui.compoment.FileRow
import ui.compoment.ServiceRoomCard
import viewmodel.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MedicalExaminationScreen(
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
) : Screen {

    // Thay thế toàn bộ Content() method trong MedicalExaminationScreen.kt

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var comment by remember { mutableStateOf(TextFieldValue("")) }
        var dietRecommendation by remember { mutableStateOf(TextFieldValue("")) }
        var result by remember { mutableStateOf(TextFieldValue("")) }
        var isDisplay by remember { mutableStateOf(false) }
        val currentDate = LocalDate.now().toString()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val formattedBirthDate = try {
            LocalDate.parse(birthDate).format(formatter)
        } catch (e: Exception) {
            birthDate
        }

        // ViewModels
        val serviceRoomViewModel = remember { ServiceRoomViewModel() }
        val chatFireBaseViewModel = remember { ChatViewModel() }
        val serviceAppointmentViewModel = remember { ServiceAppointmentViewModel() }
        val notificationViewModel = remember { NotificationViewModel() }
        val appointmentViewModel = remember { AppointmentViewModel() }
        val fileViewModel = remember { FileViewModel() }
        val resultViewModel = remember { ResultViewModel() }
        val inpatientViewModel = remember { InpatientViewModel() }

        val isLoadingInpatient by inpatientViewModel.isLoading.collectAsState()
        val inpatientMessage by inpatientViewModel.resultMessage.collectAsState()
        val inpatientSuccess by inpatientViewModel.isSuccess.collectAsState()

        // Scaffold state cho Snackbar
        val scaffoldState = rememberScaffoldState()

        // State cho loading
        var isLoadingExistingRooms by remember { mutableStateOf(true) }

        // Trạng thái checked cho từng phòng
        var checkedMap by remember { mutableStateOf(mutableMapOf<Int, Boolean>()) }

        // Lưu trữ các phòng đã được gửi (từ API)
        var sentRoomIds by remember { mutableStateOf(setOf<Int>()) }

        // Quan sát state từ ResultViewModel
        val isLoadingResult by resultViewModel.isLoading.collectAsState()
        val resultMessage by resultViewModel.resultMessage.collectAsState()
        val isSuccess by resultViewModel.isSuccess.collectAsState()

        // Hiển thị thông báo khi có kết quả
        LaunchedEffect(resultMessage) {
            if (resultMessage.isNotEmpty()) {
                scaffoldState.snackbarHostState.showSnackbar(
                    message = resultMessage,
                    duration = if (isSuccess == true)
                        androidx.compose.material.SnackbarDuration.Short
                    else
                        androidx.compose.material.SnackbarDuration.Long
                )
            }
        }

        // Reset comment và dietRecommendation sau khi tạo thành công
        LaunchedEffect(isSuccess) {
            if (isSuccess == true) {
                comment = TextFieldValue("")
                dietRecommendation = TextFieldValue("")
                // Refresh danh sách file sau khi tạo kết quả thành công
                fileViewModel.listFileByAppointmentId(id)
            }
        }

        // Các LaunchedEffect khác giữ nguyên...
        LaunchedEffect(Unit) {
            serviceRoomViewModel.listServiceRoom()
        }

        LaunchedEffect(inpatientMessage) {
            if (inpatientMessage.isNotEmpty()) {
                scaffoldState.snackbarHostState.showSnackbar(
                    message = inpatientMessage,
                    duration = if (inpatientSuccess == true)
                        androidx.compose.material.SnackbarDuration.Short
                    else
                        androidx.compose.material.SnackbarDuration.Long
                )
            }
        }
        val servicerooms = serviceRoomViewModel.servicerooms
        val resultfiles = fileViewModel.resultfiles

        LaunchedEffect(id) {
            try {
                isLoadingExistingRooms = true
                serviceAppointmentViewModel.getListServiceRoomId(id) { existingRoomIds ->
                    sentRoomIds = existingRoomIds.toSet()
                    println("Đã load phòng đã có: $sentRoomIds")
                    isLoadingExistingRooms = false
                }
            } catch (e: Exception) {
                println("Lỗi khi load existing rooms: ${e.message}")
                isLoadingExistingRooms = false
            }
        }

        LaunchedEffect(servicerooms, sentRoomIds) {
            if (servicerooms.isNotEmpty()) {
                checkedMap = servicerooms.associate { room ->
                    room.id to sentRoomIds.contains(room.id)
                }.toMutableMap()

                println("Đã khởi tạo checkedMap:")
                checkedMap.forEach { (id, checked) ->
                    if (checked) {
                        val roomName = servicerooms.find { it.id == id }?.name ?: "Unknown"
                        println("✅ Phòng $roomName (ID: $id) đã được tích sẵn")
                    }
                }
            }
        }

        // Hiển thị loading nếu đang tải dữ liệu
        if (isLoadingExistingRooms) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color(0xFF1976D2))
                    Spacer(Modifier.height(16.dp))
                    Text("Đang tải dữ liệu...", color = Color.Gray)
                }
            }
            return
        }

        // Wrap với Scaffold để có Snackbar
        Scaffold(
            scaffoldState = scaffoldState,
            backgroundColor = Color(0xFFEAF1FB)
        ) { paddingValues ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Box(
                    Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .padding(vertical = 32.dp)
                ) {
                    Column(
                        Modifier
                            .align(Alignment.Center)
                            .widthIn(min = 400.dp, max = 900.dp)
                            .shadow(12.dp, RoundedCornerShape(24.dp))
                            .background(Color.White, RoundedCornerShape(24.dp))
                            .padding(36.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // AppBar đẹp - giữ nguyên
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        listOf(Color(0xFF1976D2), Color(0xFF62B3FF))
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Quay lại",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .padding(start = 12.dp)
                                        .clickable { navigator.push(DoctorMainScreen(1)) }
                                )
                                Box(
                                    Modifier.weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Bệnh nhân $userName",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp,
                                        letterSpacing = 1.sp,
                                    )
                                }
                            }
                        }

                        // Thông tin bệnh nhân - giữ nguyên
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(32.dp)
                        ) {
                            Column(
                                Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                OutlinedTextField("$id", {}, label = { Text("Id") }, enabled = false, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(userName, {}, label = { Text("Họ tên") }, enabled = false, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField("$formattedBirthDate", {}, label = { Text("Ngày sinh") }, enabled = false, modifier = Modifier.fillMaxWidth())
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    OutlinedTextField(gender, {}, label = { Text("Giới tính") }, enabled = false, modifier = Modifier.weight(1f))
                                    OutlinedTextField(cccd, {}, label = { Text("CCCD") }, enabled = false, modifier = Modifier.weight(1f))
                                }
                            }
                            Column(
                                Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                OutlinedTextField("$userId", {}, label = { Text("Mã bệnh nhân") }, enabled = false, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(homeTown, {}, label = { Text("Quê quán") }, enabled = false, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(examTime, {}, label = { Text("Giờ khám") }, enabled = false, modifier = Modifier.fillMaxWidth())
                            }
                        }

                        // Ô chọn phòng khám - giữ nguyên logic cũ
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier
                                    .weight(5f)
                                    .height(56.dp)
                                    .background(
                                        color = Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { isDisplay = true }
                                    .border(
                                        width = 1.dp,
                                        color = Color(0xFFBDBDBD),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                val selectedCount = checkedMap.values.count { it }
                                Text(
                                    text = if (selectedCount > 0) {
                                        "Đã chọn $selectedCount phòng khám"
                                    } else {
                                        "Chọn phòng khám..."
                                    },
                                    color = if (selectedCount > 0) Color(0xFF1976D2) else Color.Black.copy(alpha = 0.7f),
                                    fontSize = 16.sp,
                                    fontWeight = if (selectedCount > 0) FontWeight.Medium else FontWeight.Normal
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    appointmentViewModel.updateStatus(id, "Đang thanh toán")
                                    notificationViewModel.postNotification(
                                        userId = userId,
                                        content = "Hãy thanh toán để tiếp tục đi khám",
                                        path = "MainScreen/1"
                                    )
                                    chatFireBaseViewModel.sendMessage(
                                        title = "Yêu cầu thanh toán!!!",
                                        body = "Hãy thanh toán để tiếp tục đi khám",
                                        remoteToken = fmctoken,
                                        isBroadcast = false
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(Color(0xFF1976D2)),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier.height(48.dp).weight(2f),
                                enabled = (status!="Đã thanh toán")
                            ) {
                                Text(
                                    text = if(status == "Đã thanh toán") "Đã thanh toán" else "Yêu cầu thanh toán",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Nhận xét - CẬP NHẬT với validation
                        Column {
                            OutlinedTextField(
                                value = comment,
                                onValueChange = {
                                    comment = it
                                    // Reset message khi user bắt đầu nhập
                                    if (it.text.isNotEmpty() && !isLoadingResult) {
                                        resultViewModel.resetState()
                                    }
                                },
                                label = { Text("Nhận xét *") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp),
                                isError = comment.text.trim().isEmpty() && isSuccess == false,
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = if (comment.text.trim().isEmpty()) Color.Red else Color(0xFF1976D2),
                                    unfocusedBorderColor = if (comment.text.trim().isEmpty() && isSuccess == false) Color.Red else Color.Gray
                                )
                            )

                            // Text helper cho validation
                            if (comment.text.trim().isEmpty() && isSuccess == false) {
                                Text(
                                    text = "Nhận xét không được để trống",
                                    color = Color.Red,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                )
                            }
                        }

                        // Chỉ định ăn uống - trường tuỳ chọn
                        OutlinedTextField(
                            value = dietRecommendation,
                            onValueChange = { dietRecommendation = it },
                            label = { Text("Chỉ định ăn uống (tuỳ chọn)") },
                            placeholder = { Text("VD: Ăn nhiều rau, hoa quả. Tránh đồ cay nóng...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF1976D2),
                                unfocusedBorderColor = Color.Gray
                            )
                        )

                        // Kết quả khám & lấy file - giữ nguyên
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(120.dp),
                                shape = RoundedCornerShape(16.dp),
                                elevation = 6.dp,
                                backgroundColor = Color(0xFFF2F5FF)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(start = 8.dp, end = 8.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    LazyRow(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        items(resultfiles) { file ->
                                            FileRow(
                                                id = file.id,
                                                fileName = file.fileName,
                                                filePath = file.filePath
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.width(24.dp))
                            Button(
                                onClick = {
                                    fileViewModel.listFileByAppointmentId(id)
                                },
                                colors = ButtonDefaults.buttonColors(Color(0xFF1976D2)),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier.height(48.dp)
                            ) {
                                Text("Lấy file", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Gửi kết quả - CẬP NHẬT với loading và validation
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Nút "Điều trị nội trú" bên trái
                                Button(
                                    onClick = {
                                        // Kiểm tra xem có file kết quả chưa
                                        if (resultfiles.isEmpty()) {
                                            // Hiển thị thông báo cần có file kết quả trước
                                            inpatientViewModel.apply {
                                                _isSuccess.value = false
                                                _resultMessage.value = "Vui lòng lấy file kết quả trước khi chuyển nội trú"
                                            }
                                        } else {
                                            // Gọi API tạo inpatient với appointmentId
                                            inpatientViewModel.createInpatient(userId, id)

                                            // Nếu thành công, gửi thông báo cho bệnh nhân
                                            if (inpatientSuccess == true) {
                                                notificationViewModel.postNotification(
                                                    userId = userId,
                                                    content = "Bạn đã được chuyển vào điều trị nội trú",
                                                    path = "MainScreen/1"
                                                )
                                                chatFireBaseViewModel.sendMessage(
                                                    title = "Thông báo nội trú",
                                                    body = "Bạn đã được chuyển vào điều trị nội trú tại bệnh viện",
                                                    remoteToken = fmctoken,
                                                    isBroadcast = false
                                                )
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = when {
                                            isLoadingInpatient -> Color.Gray
                                            inpatientSuccess == true -> Color(0xFF4CAF50) // Màu xanh khi thành công
                                            else -> Color(0xFF1976D2)
                                        }
                                    ),
                                    shape = RoundedCornerShape(24.dp),
                                    modifier = Modifier
                                        .height(56.dp)
                                        .width(200.dp),
                                    enabled = !isLoadingInpatient && resultfiles.isNotEmpty()
                                ) {
                                    when {
                                        isLoadingInpatient -> {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(16.dp),
                                                    color = Color.White,
                                                    strokeWidth = 2.dp
                                                )
                                                Text("Đang xử lý...", color = Color.White, fontSize = 16.sp)
                                            }
                                        }
                                        inpatientSuccess == true -> {
                                            Text(
                                                "✓ Đã chuyển nội trú",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp
                                            )
                                        }
                                        else -> {
                                            Text(
                                                "Điều trị nội trú",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp
                                            )
                                        }
                                    }
                                }
                                // Nút "Gửi kết quả" bên phải
                                Button(
                                    onClick = {
                                        resultViewModel.createResult(id, comment.text, dietRecommendation.text)
                                        appointmentViewModel.updateStatus(id,"Đã hoàn tất")
                                        notificationViewModel.postNotification(
                                            userId,
                                            content = "Đã có kết quả khám của $userName",
                                            path = "HealthRecordScreen"
                                        )
                                        chatFireBaseViewModel.sendMessage(
                                            title = "Đã có kết quả khám ",
                                            body = "Đã có kết quả khám của $userName",
                                            remoteToken = fmctoken,
                                            isBroadcast = false
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = if (isLoadingResult) Color.Gray else Color(0xFF1976D2)
                                    ),
                                    shape = RoundedCornerShape(24.dp),
                                    modifier = Modifier
                                        .height(56.dp)
                                        .width(200.dp),
                                    enabled = !isLoadingResult && comment.text.trim().isNotEmpty()
                                ) {
                                    if (isLoadingResult) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(16.dp),
                                                color = Color.White,
                                                strokeWidth = 2.dp
                                            )
                                            Text("Đang gửi...", color = Color.White, fontSize = 16.sp)
                                        }
                                    } else {
                                        Text(
                                            "Gửi kết quả",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                    }
                                }
                                //
                            }
                        }
                    }

                    // Popup chọn phòng khám - giữ nguyên logic cũ
                    if (isDisplay) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color(0x80000000)),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                elevation = 16.dp,
                                modifier = Modifier.width(400.dp)
                            ) {
                                Column(
                                    Modifier
                                        .padding(24.dp)
                                        .fillMaxWidth()
                                ) {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "Danh sách phòng dịch vụ",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp
                                        )
                                        IconButton(
                                            onClick = { isDisplay = false }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Đóng",
                                                tint = Color.Gray
                                            )
                                        }
                                    }

                                    LazyColumn(
                                        Modifier
                                            .heightIn(max = 300.dp)
                                            .fillMaxWidth()
                                    ) {
                                        items(servicerooms) { serviceroom ->
                                            ServiceRoomCard(
                                                id = serviceroom.id,
                                                name = serviceroom.name,
                                                address = serviceroom.address,
                                                checked = checkedMap[serviceroom.id] ?: false,
                                                onCheckedChange = { isChecked ->
                                                    checkedMap = checkedMap.toMutableMap().apply {
                                                        put(serviceroom.id, isChecked)
                                                    }
                                                }
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(24.dp))
                                    Button(
                                        onClick = {
                                            isDisplay = false

                                            val allSelectedRooms = servicerooms.filter { checkedMap[it.id] == true }
                                            val newSelectedRooms = allSelectedRooms.filter { room ->
                                                !sentRoomIds.contains(room.id)
                                            }

                                            if (newSelectedRooms.isNotEmpty()) {
                                                println("=== PHÒNG MỚI ĐƯỢC CHỌN ===")
                                                newSelectedRooms.forEach { room ->
                                                    println("Gửi phòng mới: ${room.id} ${room.name}")
                                                    serviceAppointmentViewModel.createServiceAppointment(
                                                        id, room.id, "Đã lên lịch", currentDate
                                                    )
                                                    notificationViewModel.postNotification(
                                                        userId = userId,
                                                        content = "Đã lên lịch khám: ${room.name} tại ${room.address}",
                                                        path = "MainScreen/1"
                                                    )
                                                    chatFireBaseViewModel.sendMessage(
                                                        title = "Đi khám: ${room.name}",
                                                        body = "Đã lên lịch khám: ${room.name} tại ${room.address}",
                                                        remoteToken = fmctoken,
                                                        isBroadcast = false
                                                    )
                                                }

                                                sentRoomIds = sentRoomIds + newSelectedRooms.map { it.id }.toSet()
                                                println("Đã gửi tổng cộng: ${sentRoomIds.size} phòng")

                                            } else {
                                                println("Không có phòng mới để gửi!")
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(Color(0xFF1976D2)),
                                        shape = RoundedCornerShape(24.dp)
                                    ) {
                                        Text("Gửi", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}