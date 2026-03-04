package com.example.dat_lich_kham_fe.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.example.dat_lich_kham_fe.viewmodel.MealViewModel
import com.example.dat_lich_kham_fe.viewmodel.NotificationViewModel
import com.example.dat_lich_kham_fe.viewmodel.PaymentViewModel
import com.example.dat_lich_kham_fe.viewmodel.UserViewModel

@Composable
fun MealPaymentScreen(
    navController: NavController,
    cycleId: Int,
    inpatientId: Int
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    val scale = divideAndRound(screenWidthValue)

    var isPaid by remember { mutableStateOf(false) }
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
    val paymentViewModel = remember { PaymentViewModel(context) }
    val mealViewModel = remember { MealViewModel(context) }
    var showBiometricAuth by remember { mutableStateOf(false) }

    // Get meal cost info from ViewModel
    var mealCost by remember { mutableIntStateOf(500000) }
    var numberOfDays by remember { mutableIntStateOf(5) }
    var pricePerDay by remember { mutableIntStateOf(100000) }

    // Fetch payment info when screen loads
    LaunchedEffect(cycleId) {
        // TODO: Call API to get exact cost for this cycle
        // For now using default values
    }

    val onPaymentSuccess = {
        dialogMessage = "Thanh toán thành công"
        isPaid = true
        showDialog = true

        // ✅ QUAN TRỌNG: Refresh meal status để cập nhật UI
        mealViewModel.fetchMealStatus(inpatientId)

        // Optional: Cũng refresh meal history
        mealViewModel.fetchMealHistory(inpatientId)
    }

    Box {
        Column(modifier = Modifier.fillMaxSize()) {
            AppBarView(
                title = "Thanh toán suất ăn",
                color = R.color.white,
                backgroundColor = R.color.darkblue,
                alignment = Alignment.Center,
                onDeleteNavClicked = {
                    if (isPaid) {
                        // ✅ FIX: Navigate và force refresh
                        navController.navigate("InpatientMealScreen") {
                            popUpTo("InpatientMealScreen") { inclusive = true }
                        }
                    } else {
                        navController.popBackStack()
                    }
                },
                isVisible = true
            )

            Spacer(modifier = Modifier.height((20 * scale).dp))

            // Amount Card with gradient
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = (20 * scale).dp)
                    .shadow(
                        elevation = (12 * scale).dp,
                        shape = RoundedCornerShape((24 * scale).dp)
                    ),
                shape = RoundedCornerShape((24 * scale).dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((180 * scale).dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF4CAF50),
                                    Color(0xFF66BB6A),
                                    Color(0xFF81C784)
                                )
                            )
                        )
                ) {
                    // Decorative circles
                    Box(
                        modifier = Modifier
                            .size((120 * scale).dp)
                            .offset(x = (200 * scale).dp, y = (-30 * scale).dp)
                            .background(
                                color = Color.White.copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                    )
                    Box(
                        modifier = Modifier
                            .size((80 * scale).dp)
                            .offset(x = (-20 * scale).dp, y = (100 * scale).dp)
                            .background(
                                color = Color.White.copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding((24 * scale).dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Meal Icon - Using emoji in Text
                        Text(
                            text = "🍽️",
                            fontSize = (40 * scale).sp
                        )

                        Spacer(modifier = Modifier.height((12 * scale).dp))

                        Text(
                            text = "Chu kỳ suất ăn",
                            fontSize = (16 * scale).sp,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height((8 * scale).dp))

                        Text(
                            text = "${formatNumber(mealCost)} VNĐ",
                            fontSize = (42 * scale).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height((8 * scale).dp))

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size((16 * scale).dp)
                            )
                            Spacer(modifier = Modifier.width((6 * scale).dp))
                            Text(
                                text = "$numberOfDays ngày × ${formatNumber(pricePerDay)} VNĐ",
                                fontSize = (14 * scale).sp,
                                color = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height((24 * scale).dp))

            // Detail Information Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = (20 * scale).dp),
                shape = RoundedCornerShape((16 * scale).dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = (4 * scale).dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding((20 * scale).dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size((20 * scale).dp)
                        )
                        Spacer(modifier = Modifier.width((8 * scale).dp))
                        Text(
                            text = "Chi tiết chu kỳ",
                            fontSize = (16 * scale).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                    }

                    Spacer(modifier = Modifier.height((16 * scale).dp))

                    HorizontalDivider(
                        thickness = (1 * scale).dp,
                        color = Color(0xFFE0E0E0)
                    )

                    Spacer(modifier = Modifier.height((16 * scale).dp))

                    // Info items with emoji icons
                    InfoItemWithEmoji(
                        emoji = "📅",
                        label = "Ngày bắt đầu",
                        value = "01/01/2024",
                        scale = scale
                    )

                    Spacer(modifier = Modifier.height((12 * scale).dp))

                    InfoItemWithEmoji(
                        emoji = "⏰",
                        label = "Thời gian",
                        value = "$numberOfDays ngày",
                        scale = scale
                    )

                    Spacer(modifier = Modifier.height((12 * scale).dp))

                    InfoItemWithEmoji(
                        emoji = "☕",
                        label = "Bữa ăn hàng ngày",
                        value = "Sáng, Trưa, Tối",
                        scale = scale
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Balance Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = (20 * scale).dp),
                shape = RoundedCornerShape((16 * scale).dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF5F5F5)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding((16 * scale).dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Số dư hiện tại",
                            fontSize = (12 * scale).sp,
                            color = Color(0xFF757575)
                        )
                        Text(
                            text = "${formatNumber(balance)} VNĐ",
                            fontSize = (20 * scale).sp,
                            fontWeight = FontWeight.Bold,
                            color = if (balance >= mealCost) Color(0xFF4CAF50) else Color(0xFFF44336)
                        )
                    }
                    if (balance < mealCost) {
                        TextButton(
                            onClick = {
                                navController.navigate("DepositScreen/false")
                            }
                        ) {
                            Text(
                                "Nạp tiền",
                                color = Color(0xFF2196F3),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height((16 * scale).dp))

            // Payment Method Section with FaceID/Biometric
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = (20 * scale).dp),
                shape = RoundedCornerShape((16 * scale).dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = (2 * scale).dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding((16 * scale).dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (isChecked) Color(0xFF4CAF50) else Color(0xFF757575),
                            modifier = Modifier.size((24 * scale).dp)
                        )
                        Spacer(modifier = Modifier.width((12 * scale).dp))
                        Column {
                            Text(
                                text = "Sử dụng sinh trắc học",
                                fontSize = (14 * scale).sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF212121)
                            )
                            Text(
                                text = "Xác thực nhanh và bảo mật",
                                fontSize = (12 * scale).sp,
                                color = Color(0xFF757575)
                            )
                        }
                    }
                    Switch(
                        checked = isChecked,
                        onCheckedChange = { isChecked = it },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = Color(0xFF4CAF50),
                            checkedThumbColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height((20 * scale).dp))

            // Payment Button
            Button(
                onClick = {
                    if (balance < mealCost) {
                        dialogMessage = "Số dư không đủ để thanh toán"
                        showDialog = true
                    } else {
                        userViewModel.checkHasPin(userId) { exists ->
                            if (!exists) {
                                showCreatePinScreen = true
                            } else {
                                if (isChecked) {
                                    // Check if biometric is available
                                    showBiometricAuth = true
                                } else {
                                    showPinScreen = true
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = (20 * scale).dp)
                    .height((56 * scale).dp),
                shape = RoundedCornerShape((28 * scale).dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (balance >= mealCost) Color(0xFF4CAF50) else Color(0xFFBDBDBD)
                ),
                enabled = !isPaid
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isPaid) Icons.Default.CheckCircle else Icons.Default.Done,
                        contentDescription = null,
                        modifier = Modifier.size((24 * scale).dp)
                    )
                    Spacer(modifier = Modifier.width((8 * scale).dp))
                    Text(
                        text = if (isPaid) "Đã thanh toán" else "Thanh toán ngay",
                        fontSize = (16 * scale).sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height((20 * scale).dp))
        }

        // Dialog
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                            if (dialogMessage.contains("Số dư không đủ")) {
                                navController.navigate("DepositScreen/false")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3)
                        )
                    ) {
                        Text("Đồng ý")
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isPaid) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (isPaid) Color(0xFF4CAF50) else Color(0xFFF44336),
                            modifier = Modifier.size((24 * scale).dp)
                        )
                        Spacer(modifier = Modifier.width((8 * scale).dp))
                        Text(
                            if (isPaid) "Thành công" else "Thông báo",
                            fontSize = (20 * scale).sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                text = {
                    Text(
                        text = dialogMessage,
                        fontSize = (16 * scale).sp,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF424242)
                    )
                },
                shape = RoundedCornerShape((16 * scale).dp)
            )
        }

        // PIN Screens (existing code)
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

                                // Process payment
                                val newBalance = balance - mealCost
                                paymentViewModel.payMeal(
                                    userId = userId,
                                    cycleId = cycleId,
                                    amount = mealCost,
                                    onSuccess = {
                                        // Thanh toán thành công
                                        val newBalance = balance - mealCost

                                        // ✅ REFRESH MEAL STATUS NGAY SAU KHI THANH TOÁN
                                        mealViewModel.fetchMealStatus(inpatientId)
                                        mealViewModel.fetchMealHistory(inpatientId)

                                        // Send notifications
                                        notificationViewModel.postNotification(
                                            userId = userId,
                                            content = "Bạn đã thanh toán ${formatNumber(mealCost)} cho suất ăn. Số dư hiện tại: ${formatNumber(newBalance)} VND",
                                            path = "InpatientMealScreen"
                                        )

                                        chatViewModel.sendMessage(
                                            title = "Biến động số dư",
                                            body = "Bạn đã thanh toán ${formatNumber(mealCost)} cho suất ăn. Số dư hiện tại: ${formatNumber(newBalance)} VND",
                                            remoteToken = fcmToken,
                                            isBroadcast = false
                                        )

                                        // Show success dialog
                                        dialogMessage = "Thanh toán thành công"
                                        isPaid = true
                                        showDialog = true
                                    },
                                    onError = { errorMessage ->
                                        // Thanh toán thất bại
                                        dialogMessage = errorMessage
                                        showDialog = true
                                    }
                                )
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

        if (showBiometricAuth) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White
            ) {
                BiometricAuthScreen(
                    onSuccess = {
                        showBiometricAuth = false
                        pinError = ""

                        val newBalance = balance - mealCost
                        paymentViewModel.payMeal(userId, cycleId, mealCost)

                        // ✅ REFRESH MEAL STATUS NGAY SAU KHI THANH TOÁN
                        mealViewModel.fetchMealStatus(inpatientId)
                        mealViewModel.fetchMealHistory(inpatientId)

                        notificationViewModel.postNotification(
                            userId = userId,
                            content = "Bạn đã thanh toán ${formatNumber(mealCost)} cho suất ăn. Số dư hiện tại: ${formatNumber(newBalance)} VND",
                            path = "InpatientMealScreen"
                        )

                        chatViewModel.sendMessage(
                            title = "Biến động số dư",
                            body = "Bạn đã thanh toán ${formatNumber(mealCost)} cho suất ăn. Số dư hiện tại: ${formatNumber(newBalance)} VND",
                            remoteToken = fcmToken,
                            isBroadcast = false
                        )

                        dialogMessage = "Thanh toán thành công"
                        isPaid = true
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

// New component using emoji instead of icons
@Composable
fun InfoItemWithEmoji(
    emoji: String,
    label: String,
    value: String,
    scale: Float
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = emoji,
            fontSize = (20 * scale).sp,
            modifier = Modifier.size((20 * scale).dp)
        )
        Spacer(modifier = Modifier.width((12 * scale).dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = (12 * scale).sp,
                color = Color(0xFF757575)
            )
            Spacer(modifier = Modifier.height((4 * scale).dp))
            Text(
                text = value,
                fontSize = (14 * scale).sp,
                color = Color(0xFF212121),
                lineHeight = (18 * scale).sp
            )
        }
    }
}