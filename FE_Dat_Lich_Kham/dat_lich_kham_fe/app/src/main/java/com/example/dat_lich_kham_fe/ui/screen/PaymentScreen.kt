package com.example.dat_lich_kham_fe.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dat_lich_kham_fe.Firebase.ChatViewModel
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.ui.component.AppBarView
import com.example.dat_lich_kham_fe.util.BiometricLocalStore
import com.example.dat_lich_kham_fe.util.balance
import com.example.dat_lich_kham_fe.util.divideAndRound
import com.example.dat_lich_kham_fe.util.fcmToken
import com.example.dat_lich_kham_fe.util.formatNumber
import com.example.dat_lich_kham_fe.util.userId
import com.example.dat_lich_kham_fe.viewmodel.AppointmentViewModel
import com.example.dat_lich_kham_fe.viewmodel.NotificationViewModel
import com.example.dat_lich_kham_fe.viewmodel.PaymentViewModel
import com.example.dat_lich_kham_fe.viewmodel.UserViewModel

@Composable
fun PaymentScreen(
    navController: NavController,
    doctorId : Int,
    examDate: String,
    examTime: String,
){
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    val scale = divideAndRound(screenWidthValue)

    var isAppoint by remember { mutableStateOf(false) }
    var deposit = 100000
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var isChecked by remember { mutableStateOf(false) }

    var showPinScreen by remember { mutableStateOf(false) }
    var pinError by remember { mutableStateOf("") }
    var showCreatePinScreen by remember { mutableStateOf(false) }
    var showConfirmPinScreen by remember { mutableStateOf(false) }
    var tempPin by remember { mutableStateOf("") }

    val userViewModel = remember { UserViewModel(context) }
    val notificationViewModel = remember { NotificationViewModel(context) }
    val chatViewModel = remember { ChatViewModel() }
    val appointmentViewModel = remember { AppointmentViewModel(context) }
    val paymentViewModel = remember { PaymentViewModel(context) }
    var showBiometricAuth by remember { mutableStateOf(false) }

    Box {
        Column(modifier = Modifier.fillMaxSize()) {
            AppBarView(
                title = stringResource(id = R.string.payment_tile),
                color = R.color.white,
                backgroundColor = R.color.darkblue,
                alignment = Alignment.Center,
                onDeleteNavClicked = {
                    if(isAppoint) navController.navigate("MainScreen")
                    else navController.popBackStack()
                },
                isVisible = true
            )

            Spacer(modifier = Modifier.height((16 * scale).dp))

            // Amount Card với gradient đẹp
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = (16 * scale).dp)
                    .height((140 * scale).dp)
                    .shadow(
                        elevation = (12 * scale).dp,
                        shape = RoundedCornerShape((20 * scale).dp)
                    ),
                shape = RoundedCornerShape((20 * scale).dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF1976D2),
                                    Color(0xFF2196F3),
                                    Color(0xFF42A5F5)
                                )
                            )
                        )
                ) {
                    // Decorative circles
                    Box(
                        modifier = Modifier
                            .size((100 * scale).dp)
                            .align(Alignment.TopEnd)
                            .background(
                                color = Color.White.copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                    )
                    Box(
                        modifier = Modifier
                            .size((60 * scale).dp)
                            .align(Alignment.BottomStart)
                            .background(
                                color = Color.White.copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                    )

                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.deposit),
                            fontSize = (14 * scale).sp,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height((8 * scale).dp))
                        Text(
                            text = "${formatNumber(deposit)} VNĐ",
                            fontSize = (36 * scale).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height((24 * scale).dp))

            // Warning items với design đẹp hơn
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = (16 * scale).dp)
            ) {
                WarningItem(
                    text = stringResource(id = R.string.attention1),
                    scale = scale
                )

                Spacer(modifier = Modifier.height((12 * scale).dp))

                WarningItem(
                    text = stringResource(id = R.string.attention2),
                    scale = scale
                )
            }

            Spacer(modifier = Modifier.height((24 * scale).dp))

            // Agreement checkbox với design đẹp
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = (16 * scale).dp),
                shape = RoundedCornerShape((12 * scale).dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isChecked) {
                        colorResource(id = R.color.darkblue).copy(alpha = 0.05f)
                    } else {
                        Color(0xFFF5F5F5)
                    }
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = (2 * scale).dp
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding((12 * scale).dp)
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { isChecked = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = colorResource(id = R.color.darkblue),
                            uncheckedColor = Color.Gray
                        )
                    )
                    Text(
                        text = stringResource(id = R.string.agree),
                        fontSize = (16 * scale).sp,
                        modifier = Modifier.padding(start = (8 * scale).dp),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bottom payment section
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(
                    topStart = (24 * scale).dp,
                    topEnd = (24 * scale).dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = (12 * scale).dp
                )
            ) {
                Column(
                    modifier = Modifier.padding((16 * scale).dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(id = R.string.deposit),
                                fontSize = (14 * scale).sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height((4 * scale).dp))
                            Text(
                                text = "${formatNumber(deposit)} VNĐ",
                                fontSize = (20 * scale).sp,
                                fontWeight = FontWeight.Bold,
                                color = colorResource(id = R.color.darkblue)
                            )
                        }

                        Button(
                            onClick = {
//                                isAppoint = true
//                                if(balance < deposit){
//                                    dialogMessage = "Số dư không đủ, hãy nạp thêm"
//                                    showDialog = true
//                                }else{
//                                    userViewModel.checkHasPin(userId) { hasPin ->
//                                        if (hasPin) {
//                                            showPinScreen = true
//                                        } else {
//                                            showCreatePinScreen = true
//                                        }
//                                    }
//                                }
                                isAppoint = true
                                if(balance < deposit){
                                    dialogMessage = "Số dư không đủ, hãy nạp thêm"
                                    showDialog = true
                                }else{
                                    val biometricStore = BiometricLocalStore(context)

                                    userViewModel.checkHasPin(userId) { hasPin ->
                                        if (!hasPin) {
                                            showCreatePinScreen = true
                                        } else {
                                            if (biometricStore.isBiometricEnabled()) {
                                                showBiometricAuth = true
                                            } else {
                                                showPinScreen = true
                                            }
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(id = R.color.darkblue),
                                disabledContainerColor = Color(0xFFE0E0E0)
                            ),
                            enabled = isChecked,
                            shape = RoundedCornerShape((12 * scale).dp),
                            modifier = Modifier
                                .height((56 * scale).dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.payment_tile),
                                fontWeight = FontWeight.Bold,
                                fontSize = (16 * scale).sp,
                                color = if (isChecked) Color.White else Color.Gray,
                                modifier = Modifier.padding(horizontal = (16 * scale).dp)
                            )
                        }
                    }
                }
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(text = "Thông báo") },
                    text = { Text(text = dialogMessage) },
                    confirmButton = {
                        TextButton(onClick = {
                            showDialog = false
                            if(dialogMessage == "Thanh toán thành công"){
                                navController.navigate("MainScreen")
                            }
                            else if(dialogMessage == "Số dư không đủ, hãy nạp thêm"){
                                navController.navigate("DepositScreen/false")
                            }
                        }) {
                            Text("OK")
                        }
                    }
                )
            }
        }

        // PIN Screens giữ nguyên logic
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
                                var newbalance = balance - deposit
//                                userViewModel.updatebalance(userId, newbalance)
                                paymentViewModel.payDeposit(userId)
                                appointmentViewModel.bookappointment(
                                    userId = userId,
                                    doctorId = doctorId,
                                    examDate = examDate,
                                    examTime = examTime,
                                    status = "Đã lên lịch"
                                )
                                notificationViewModel.postNotification(
                                    userId = userId,
                                    content = "Bạn đã cọc ${formatNumber(deposit)}. Số dư hiện tại: ${formatNumber(newbalance)} VND",
                                    path = "DepositScreen/false"
                                )
                                notificationViewModel.postNotification(
                                    userId = userId,
                                    content = "Bạn đã đặt thành công một lịch khám. Vui lòng đến đúng giờ để được phục vụ tốt nhất.",
                                    path = "MainScreen/1"
                                )
                                chatViewModel.sendMessage(
                                    title = "Biến động số dư",
                                    body = "Bạn đã cọc ${formatNumber(deposit)}. Số dư hiện tại: ${formatNumber(newbalance)} VND\"",
                                    remoteToken = fcmToken,
                                    isBroadcast = false
                                )
                                chatViewModel.sendMessage(
                                    title = "Thông báo lịch khám",
                                    body = "Bạn đã đặt thành công một lịch khám. Vui lòng đến đúng giờ để được phục vụ tốt nhất.",
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
        ///
        if (showBiometricAuth) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White
            ) {
                BiometricAuthScreen(
                    onSuccess = {
                        showBiometricAuth = false
                        pinError = ""

                        var newbalance = balance - deposit
                        paymentViewModel.payDeposit(userId)
                        appointmentViewModel.bookappointment(
                            userId = userId,
                            doctorId = doctorId,
                            examDate = examDate,
                            examTime = examTime,
                            status = "Đã lên lịch"
                        )
                        notificationViewModel.postNotification(
                            userId = userId,
                            content = "Bạn đã cọc ${formatNumber(deposit)}. Số dư hiện tại: ${formatNumber(newbalance)} VND",
                            path = "DepositScreen/false"
                        )
                        notificationViewModel.postNotification(
                            userId = userId,
                            content = "Bạn đã đặt thành công một lịch khám. Vui lòng đến đúng giờ để được phục vụ tốt nhất.",
                            path = "MainScreen/1"
                        )
                        chatViewModel.sendMessage(
                            title = "Biến động số dư",
                            body = "Bạn đã cọc ${formatNumber(deposit)}. Số dư hiện tại: ${formatNumber(newbalance)} VND",
                            remoteToken = fcmToken,
                            isBroadcast = false
                        )
                        chatViewModel.sendMessage(
                            title = "Thông báo lịch khám",
                            body = "Bạn đã đặt thành công một lịch khám. Vui lòng đến đúng giờ để được phục vụ tốt nhất.",
                            remoteToken = fcmToken,
                            isBroadcast = false
                        )
                        dialogMessage = "Thanh toán thành công"
                        showDialog = true
                    },
                    onUsePinInstead = {
                        showBiometricAuth = false
                        showPinScreen = true
                    },
                    onClose = {
                        showBiometricAuth = false
                    },
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun WarningItem(text: String, scale: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape((12 * scale).dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF8E1)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = (2 * scale).dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding((16 * scale).dp)
        ) {
            Box(
                modifier = Modifier
                    .size((40 * scale).dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFC107).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.warning),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size((24 * scale).dp)
                )
            }

            Spacer(modifier = Modifier.width((12 * scale).dp))

            Text(
                text = text,
                fontSize = (15 * scale).sp,
                color = Color(0xFF5D4037),
                fontWeight = FontWeight.Medium,
                lineHeight = (20 * scale).sp
            )
        }
    }
}
