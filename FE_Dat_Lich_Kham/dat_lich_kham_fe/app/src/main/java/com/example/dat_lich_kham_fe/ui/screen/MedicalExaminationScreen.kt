package com.example.dat_lich_kham_fe.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dat_lich_kham_fe.Firebase.ChatViewModel
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.ui.component.AppBarView
import com.example.dat_lich_kham_fe.ui.component.ServiceRoomRow
import com.example.dat_lich_kham_fe.util.balance
import com.example.dat_lich_kham_fe.util.birthDate
import com.example.dat_lich_kham_fe.util.cccd
import com.example.dat_lich_kham_fe.util.chuyenDoiNgay
import com.example.dat_lich_kham_fe.util.divideAndRound
import com.example.dat_lich_kham_fe.util.fcmToken
import com.example.dat_lich_kham_fe.util.formatNumber
import com.example.dat_lich_kham_fe.util.fullName
import com.example.dat_lich_kham_fe.util.gender
import com.example.dat_lich_kham_fe.util.userId
import com.example.dat_lich_kham_fe.viewmodel.AppointmentViewModel
import com.example.dat_lich_kham_fe.viewmodel.NotificationViewModel
import com.example.dat_lich_kham_fe.viewmodel.PaymentViewModel
import com.example.dat_lich_kham_fe.viewmodel.ServiceAppointmentViewModel
import com.example.dat_lich_kham_fe.viewmodel.UserViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MedicalExaminationScreen(
    navController: NavController,
    id : Int,
    doctorId : Int,
    doctorName : String,
    doctorCode : String,
    department :String ,
    examPrice : Int,
    examDate : String,
    examTime : String,
    status : String,
) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    val scale = divideAndRound(screenWidthValue)

    var tongTienKhamChucNang by remember { mutableStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var tienKhamChuaTra = examPrice - 100000
    var tongTien = tienKhamChuaTra+tongTienKhamChucNang

    var showPinScreen by remember { mutableStateOf(false) }
    var pinError by remember { mutableStateOf("") }
    var showCreatePinScreen by remember { mutableStateOf(false) }
    var showConfirmPinScreen by remember { mutableStateOf(false) }
    var tempPin by remember { mutableStateOf("") }

    val serviceAppointmentViewModel = remember { ServiceAppointmentViewModel(context) }
    val userViewModel = remember { UserViewModel(context) }
    val notificationViewModel = remember { NotificationViewModel(context) }
    val chatViewModel = remember { ChatViewModel() }
    val appointmentViewModel = remember { AppointmentViewModel(context) }
    val paymentViewModel = remember { PaymentViewModel(context) }
    val paymentUiState by paymentViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        serviceAppointmentViewModel.getServiceAppointments(id)
    }
    val serviceAppointments = serviceAppointmentViewModel.serviceAppointments
    tongTienKhamChucNang = serviceAppointments.sumOf { it.examPrice }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
        ) {
            AppBarView(
                title = stringResource(id = R.string.medical_examination_title),
                color = R.color.black,
                backgroundColor = R.color.white,
                alignment = Alignment.TopCenter,
                onDeleteNavClicked = { navController.navigate("MainScreen/1") },
                isVisible = true
            )

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = (16 * scale).dp)
            ) {
                Spacer(modifier = Modifier.height((16 * scale).dp))

                // Doctor & Patient Info Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow((8 * scale).dp, RoundedCornerShape((20 * scale).dp)),
                    shape = RoundedCornerShape((20 * scale).dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        // Doctor Section with Gradient Background
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFF1976D2),
                                            Color(0xFF1565C0)
                                        )
                                    ),
                                    shape = RoundedCornerShape(
                                        topStart = (20 * scale).dp,
                                        topEnd = (20 * scale).dp
                                    )
                                )
                                .padding((24 * scale).dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Bác sĩ khám",
                                        fontSize = (14 * scale).sp,
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height((4 * scale).dp))
                                    Text(
                                        text = "$doctorName",
                                        fontSize = (18 * scale).sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height((2 * scale).dp))
                                    Text(
                                        text = "Mã: $doctorCode",
                                        fontSize = (12 * scale).sp,
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height((8 * scale).dp))
                                    Card(
                                        shape = RoundedCornerShape((8 * scale).dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color.White.copy(alpha = 0.2f)
                                        )
                                    ) {
                                        Text(
                                            text = department,
                                            fontSize = (12 * scale).sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(
                                                horizontal = (12 * scale).dp,
                                                vertical = (6 * scale).dp
                                            )
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .size((90 * scale).dp)
                                        .shadow((8 * scale).dp, CircleShape)
                                        .background(Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.anhtrang),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size((80 * scale).dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }

                        // Patient Info Section
                        Column(
                            modifier = Modifier.padding((24 * scale).dp)
                        ) {
                            Text(
                                text = "Thông tin bệnh nhân",
                                fontSize = (16 * scale).sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937),
                                modifier = Modifier.padding(bottom = (16 * scale).dp)
                            )

                            PatientInfoRow(
                                label = stringResource(id = R.string.full_name),
                                value = fullName,
                                scale = scale
                            )
                            PatientInfoRow(
                                label = stringResource(id = R.string.gender),
                                value = gender,
                                scale = scale
                            )
                            PatientInfoRow(
                                label = stringResource(id = R.string.date_of_birth),
                                value = chuyenDoiNgay(birthDate),
                                scale = scale
                            )
                            PatientInfoRow(
                                label = stringResource(id = R.string.cccd_number),
                                value = cccd,
                                scale = scale,
                                isLast = true
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height((20 * scale).dp))

                // Appointment Info Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow((4 * scale).dp, RoundedCornerShape((16 * scale).dp)),
                    shape = RoundedCornerShape((16 * scale).dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding((20 * scale).dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Thông tin lịch khám",
                            fontSize = (16 * scale).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = (12 * scale).dp)
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape((12 * scale).dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F9FF))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding((16 * scale).dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "$examTime",
                                    fontSize = (18 * scale).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0369A1),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Text(
                                    text = "Ngày ${chuyenDoiNgay(examDate)}",
                                    fontSize = (14 * scale).sp,
                                    color = Color(0xFF0369A1),
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height((12 * scale).dp))

                        Text(
                            text = "ID Lịch khám: #$id",
                            fontSize = (14 * scale).sp,
                            color = Color(0xFF6B7280),
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height((20 * scale).dp))

                // Payment Summary Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow((4 * scale).dp, RoundedCornerShape((16 * scale).dp)),
                    shape = RoundedCornerShape((16 * scale).dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding((20 * scale).dp)) {
                        Text(
                            text = "Chi tiết thanh toán",
                            fontSize = (16 * scale).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937),
                            modifier = Modifier.padding(bottom = (16 * scale).dp)
                        )

                        PaymentRow(
                            label = stringResource(id = R.string.the_unpaid_amount),
                            amount = tienKhamChuaTra,
                            scale = scale,
                            isTotal = false
                        )

                        if (serviceAppointments.isNotEmpty()) {
                            Spacer(modifier = Modifier.height((12 * scale).dp))
                            Text(
                                text = "Dịch vụ khám chức năng",
                                fontSize = (14 * scale).sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF374151),
                                modifier = Modifier.padding(bottom = (8 * scale).dp)
                            )

                            serviceAppointments.forEach { serviceAppointment ->
                                ServiceRoomRow(
                                    id = serviceAppointment.id,
                                    name = serviceAppointment.name,
                                    address = serviceAppointment.address,
                                    examPrice = serviceAppointment.examPrice,
                                )
                                Spacer(modifier = Modifier.height((8 * scale).dp))
                            }
                        }

                        Spacer(modifier = Modifier.height((12 * scale).dp))

                        Card(
                            shape = RoundedCornerShape((12 * scale).dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4))
                        ) {
                            PaymentRow(
                                label = stringResource(id = R.string.total_amount),
                                amount = tienKhamChuaTra + tongTienKhamChucNang,
                                scale = scale,
                                isTotal = true,
                                modifier = Modifier.padding((16 * scale).dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height((32 * scale).dp))

                // Payment Button
                Button(
                    onClick = {
                        if(balance < (tienKhamChuaTra+tongTienKhamChucNang) ){
                            dialogMessage = "Số dư không đủ, hãy nạp thêm"
                            showDialog = true
                        }
                        else{
                            userViewModel.checkHasPin(userId) { hasPin ->
                                if (hasPin) {
                                    showPinScreen = true
                                } else {
                                    showCreatePinScreen = true
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((56 * scale).dp)
                        .shadow((8 * scale).dp, RoundedCornerShape((28 * scale).dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (status == "Đã thanh toán") Color(0xFF10B981) else colorResource(id = R.color.darkblue),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFD1D5DB)
                    ),
                    shape = RoundedCornerShape((28 * scale).dp),
                    enabled = (status == "Đang thanh toán"),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = if(status =="Đã thanh toán") stringResource(id = R.string.payment_has_been_made) else stringResource(id = R.string.payment_tile),
                        fontWeight = FontWeight.Bold,
                        fontSize = (16 * scale).sp
                    )
                }

                Spacer(modifier = Modifier.height((32 * scale).dp))
            }
        }

        // Dialog
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text(
                        text = "Thông báo",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                },
                text = {
                    Text(
                        text = dialogMessage,
                        color = Color(0xFF6B7280)
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                            if(dialogMessage == "Thanh toán thành công"){
                                navController.navigate("MainScreen")
                            }
                            else if(dialogMessage == "Số dư không đủ, hãy nạp thêm"){
                                navController.navigate("DepositScreen/false")
                            }
                        }
                    ) {
                        Text(
                            "OK",
                            color = colorResource(id = R.color.darkblue),
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                shape = RoundedCornerShape((16 * scale).dp)
            )
        }

        // Pin Screens (unchanged logic)
        if (showPinScreen) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White
            ) {
                PinCodeScreenV2(
                    onPinEntered = { pin ->
                        userViewModel.checkPin(userId, pin) { isCorrect ->
                            if (isCorrect) {
                                showPinScreen = false
                                pinError = ""
                                var newbalance = balance - tongTien
//                                userViewModel.updatebalance2()
                                paymentViewModel.payAppointment(id)

                                appointmentViewModel.updateStatus(id, "Đã thanh toán")
                                notificationViewModel.postNotification(
                                    userId = userId,
                                    content = "Bạn đã thanh toán ${formatNumber(tongTien)}VNĐ. Số dư hiện tại: ${formatNumber(newbalance)} VND",
                                    path = "DepositScreen/false"
                                )
                                chatViewModel.sendMessage(
                                    title = "Biến động số dư",
                                    body = "Bạn đã thanh toán ${formatNumber(tongTien)} VNĐ. Số dư hiện tại: ${
                                        formatNumber(
                                            newbalance
                                        )
                                    } VND\"",
                                    remoteToken = fcmToken,
                                    isBroadcast = false
                                )
                                dialogMessage = "Thanh toán thành công"
                                showDialog = true
                            } else {
                                pinError = "Mã PIN sai, vui lòng thử lại!"
                            }
                        }
                    },
                    onClose = {
                        showPinScreen = false
                        pinError = ""
                    },
                    errorMessage = pinError,
                    navController = navController
                )
            }
        }

        if (showCreatePinScreen) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White
            ) {
                CreatePinScreen(
                    onPinCreated = { pin ->
                        tempPin = pin
                        showCreatePinScreen = false
                        showConfirmPinScreen = true
                    },
                    onClose = {
                        showCreatePinScreen = false
                        tempPin = ""
                    }
                )
            }
        }

        if (showConfirmPinScreen) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White
            ) {
                ConfirmPinScreen(
                    originalPin = tempPin,
                    onPinConfirmed = { confirmedPin ->
                        userViewModel.createOrUpdatePin(userId, confirmedPin) { success ->
                            if (success) {
                                showConfirmPinScreen = false
                                tempPin = ""
                                showPinScreen = true
                            } else {
                                pinError = "Tạo mã PIN thất bại, thử lại!"
                            }
                        }
                    },
                    onPinMismatch = {
                        pinError = "Mã PIN không khớp, nhập lại!"
                        showConfirmPinScreen = false
                        showCreatePinScreen = true
                        tempPin = ""
                    },
                    onClose = {
                        showConfirmPinScreen = false
                        tempPin = ""
                    },
                    errorMessage = pinError
                )
            }
        }
    }
}

@Composable
private fun PatientInfoRow(
    label: String,
    value: String,
    scale: Float,
    isLast: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (isLast) 0.dp else (12 * scale).dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            fontSize = (14 * scale).sp,
            color = Color(0xFF6B7280),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = (14 * scale).sp,
            color = Color(0xFF1F2937),
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun PaymentRow(
    label: String,
    amount: Int,
    scale: Float,
    isTotal: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            fontSize = if (isTotal) (16 * scale).sp else (14 * scale).sp,
            color = if (isTotal) Color(0xFF1F2937) else Color(0xFF6B7280),
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Medium
        )
        Text(
            text = "${formatNumber(amount)} VNĐ",
            fontSize = if (isTotal) (18 * scale).sp else (14 * scale).sp,
            color = if (isTotal) Color(0xFF059669) else Color(0xFF1F2937),
            fontWeight = FontWeight.Bold
        )
    }
}
