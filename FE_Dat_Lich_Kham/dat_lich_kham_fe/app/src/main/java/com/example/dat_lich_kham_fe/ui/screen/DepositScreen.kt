
//---------------------------------------------------------- END OF FILE ---------------------------------------------------------------------
package com.example.dat_lich_kham_fe.ui.screen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.dat_lich_kham_fe.Firebase.ChatViewModel
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.ui.component.AppBarView
import com.example.dat_lich_kham_fe.util.*
import com.example.dat_lich_kham_fe.viewmodel.NotificationViewModel
import com.example.dat_lich_kham_fe.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DepositScreen(
    navController: NavController,
    momoSuccess: Boolean,
    amount: String? = null
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value

    val coroutineScope = rememberCoroutineScope()

    var amountInput by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var qrImageUrl by remember { mutableStateOf<String?>(null) }
    var payUrl by remember { mutableStateOf<String?>(null) }
    var selectedMethod by remember { mutableStateOf(0) } // 0=App, 1=QR
    var currentOrderId by remember { mutableStateOf<String?>(null) }
    var paymentStatus by remember { mutableStateOf<String?>(null) }
    var checkingStatus by remember { mutableStateOf(false) }

    var balance by remember { mutableStateOf(balance) }
    val userStore = UserLocalStore(context)
    var userId by remember { mutableStateOf(0) }

    var showPinScreen by remember { mutableStateOf(false) }
    var pinError by remember { mutableStateOf("") }
    var showCreatePinScreen by remember { mutableStateOf(false) }
    var showConfirmPinScreen by remember { mutableStateOf(false) }
    var tempPin by remember { mutableStateOf("") }

    val userViewModel = remember { UserViewModel(context) }
    val notificationViewModel = remember { NotificationViewModel(context) }
    val chatViewModel = remember { ChatViewModel() }
    var showBiometricAuth by remember { mutableStateOf(false) }

    // Load user info
    LaunchedEffect(Unit) {
        val user = userStore.getUser()

        userId = user?.Id ?: 0
        userViewModel.getUserBalance(userId) { fetchedBalance ->
            if (fetchedBalance != null) {
                balance = fetchedBalance
            }
        }
    }

    // Auto-check payment status khi có orderId
    LaunchedEffect(currentOrderId) {
        if (currentOrderId != null && selectedMethod == 1) {
            checkingStatus = true

            try {
                while (isActive && paymentStatus != "success" && paymentStatus != "failed") {
                    delay(3000)

                    // Kiểm tra lại trước khi gọi API
                    if (currentOrderId == null || paymentStatus == "success" || paymentStatus == "failed") {
                        break
                    }

                    checkPaymentStatus(context, currentOrderId!!) { status, transId, err ->
                        if (err == null && status != null) {
                            paymentStatus = status

                            if (status == "success") {
                                checkingStatus = false

                                // Update balance
                                userViewModel.getUserBalance(userId) { fetchedBalance ->
                                    if (fetchedBalance != null) {
                                        balance = fetchedBalance

                                        notificationViewModel.postNotification(
                                            userId = userId,
                                            content = "Bạn đã nạp ${formatNumber(amountInput.toIntOrNull() ?: 0)} VND vào tài khoản. Số dư hiện tại: ${formatNumber(fetchedBalance)} VND",
                                            path = "DepositScreen/false"
                                        )

                                        chatViewModel.sendMessage(
                                            title = "Biến động số dư",
                                            body = "Bạn đã nạp ${formatNumber(amountInput.toIntOrNull() ?: 0)} VND vào tài khoản thành công",
                                            remoteToken = fcmToken,
                                            isBroadcast = false
                                        )
                                    }
                                }

                            } else if (status == "failed") {
                                checkingStatus = false
                            }
                        } else if (err != null) {
                            println("Error checking payment status: $err")
                        }
                    }
                }
            } finally {
                checkingStatus = false
            }
        }
    }

    // Handle app MoMo callback
    LaunchedEffect(momoSuccess, amount) {
        if (momoSuccess && amount != null) {
            // SỬA: Chỉ cần set balance, LaunchedEffect sẽ tự save
            userViewModel.getUserBalance(userId) { fetchedBalance ->
                if (fetchedBalance != null) {
                    balance = fetchedBalance  // ← Chỉ set, không save

                    notificationViewModel.postNotification(
                        userId = userId,
                        content = "Bạn đã nạp ${formatNumber(amount.toLongOrNull()?.toInt() ?: 0)} VND vào tài khoản. Số dư hiện tại: ${formatNumber(fetchedBalance)} VND",
                        path = "DepositScreen/false"
                    )

                    chatViewModel.sendMessage(
                        title = "Biến động số dư",
                        body = "Bạn đã nạp ${formatNumber(amount.toLongOrNull()?.toInt() ?: 0)} VND vào tài khoản thành công",
                        remoteToken = fcmToken,
                        isBroadcast = false
                    )
                }
            }
        }
    }

    LaunchedEffect(balance) {
        if (balance > 0) {
            userStore.saveBalance(balance)
        }
    }

// 2. Handle app MoMo callback
    LaunchedEffect(momoSuccess, amount) {
        if (momoSuccess && amount != null) {
            // SỬA: Reload balance từ server
            userViewModel.getUserBalance(userId) { fetchedBalance ->
                if (fetchedBalance != null) {
                    balance = fetchedBalance

                    notificationViewModel.postNotification(
                        userId = userId,
                        content = "Bạn đã nạp ${formatNumber(amount.toLongOrNull()?.toInt() ?: 0)} VND vào tài khoản. Số dư hiện tại: ${formatNumber(fetchedBalance)} VND",
                        path = "DepositScreen/false"
                    )

                    chatViewModel.sendMessage(
                        title = "Biến động số dư",
                        body = "Bạn đã nạp ${formatNumber(amount.toLongOrNull()?.toInt() ?: 0)} VND vào tài khoản thành công",
                        remoteToken = fcmToken,
                        isBroadcast = false
                    )
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
        ) {
            AppBarView(
                title = stringResource(id = R.string.deposit_money_title),
                color = R.color.white,
                backgroundColor = R.color.darkblue,
                alignment = Alignment.TopCenter,
                onDeleteNavClicked = { navController.navigate("MainScreen/3") },
                isVisible = true
            )

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding((16 * divideAndRound(screenWidthValue)).dp)
            ) {
                Spacer(modifier = Modifier.height((16 * divideAndRound(screenWidthValue)).dp))

                // Balance Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow((4 * divideAndRound(screenWidthValue)).dp, RoundedCornerShape((16 * divideAndRound(screenWidthValue)).dp)),
                    shape = RoundedCornerShape((16 * divideAndRound(screenWidthValue)).dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding((20 * divideAndRound(screenWidthValue)).dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.balance),
                            fontSize = (14 * divideAndRound(screenWidthValue)).sp,
                            color = Color(0xFF6B7280),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height((8 * divideAndRound(screenWidthValue)).dp))
                        Text(
                            text = "${formatNumber(balance)} VND",
                            fontSize = (24 * divideAndRound(screenWidthValue)).sp,
                            color = colorResource(id = R.color.darkblue),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height((24 * divideAndRound(screenWidthValue)).dp))

                // Deposit Form Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow((4 * divideAndRound(screenWidthValue)).dp, RoundedCornerShape((16 * divideAndRound(screenWidthValue)).dp)),
                    shape = RoundedCornerShape((16 * divideAndRound(screenWidthValue)).dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding((20 * divideAndRound(screenWidthValue)).dp)
                    ) {
                        Text(
                            text = "Nạp tiền",
                            fontSize = (20 * divideAndRound(screenWidthValue)).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937),
                            modifier = Modifier.padding(bottom = (20 * divideAndRound(screenWidthValue)).dp)
                        )

                        // Amount Input
                        OutlinedTextField(
                            value = amountInput,
                            onValueChange = {
                                amountInput = it.filter { char -> char.isDigit() }
                            },
                            label = { Text(stringResource(id = R.string.enter_the_amount_of_money)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape((12 * divideAndRound(screenWidthValue)).dp),
                            enabled = !loading && paymentStatus != "success"
                        )

                        Spacer(modifier = Modifier.height((20 * divideAndRound(screenWidthValue)).dp))

                        // User ID Display (chỉ hiện khi chọn QR)
                        if (selectedMethod == 1) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape((8 * divideAndRound(screenWidthValue)).dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F9FF))
                            ) {
                                Column(
                                    modifier = Modifier.padding((12 * divideAndRound(screenWidthValue)).dp)
                                ) {
                                    Text(
                                        text = "Nội dung chuyển khoản",
                                        fontSize = (14 * divideAndRound(screenWidthValue)).sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF374151)
                                    )
                                    Spacer(modifier = Modifier.height((4 * divideAndRound(screenWidthValue)).dp))
                                    Text(
                                        text = "USER$userId",
                                        fontSize = (18 * divideAndRound(screenWidthValue)).sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colorResource(id = R.color.darkblue)
                                    )
                                    Spacer(modifier = Modifier.height((4 * divideAndRound(screenWidthValue)).dp))
                                    Text(
                                        text = "Nội dung này sẽ hiển thị trong lịch sử giao dịch MoMo",
                                        fontSize = (11 * divideAndRound(screenWidthValue)).sp,
                                        color = Color(0xFF6B7280)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height((16 * divideAndRound(screenWidthValue)).dp))
                        }

                        // Payment Method Selection
                        Text(
                            text = "Chọn phương thức thanh toán",
                            fontSize = (16 * divideAndRound(screenWidthValue)).sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF374151),
                            modifier = Modifier.padding(bottom = (12 * divideAndRound(screenWidthValue)).dp)
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape((8 * divideAndRound(screenWidthValue)).dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedMethod == 0) Color(0xFFEFF6FF) else Color(0xFFF9FAFB)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding((16 * divideAndRound(screenWidthValue)).dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedMethod == 0,
                                    onClick = {
                                        if (!loading && paymentStatus != "success") {
                                            selectedMethod = 0
                                            qrImageUrl = null
                                            currentOrderId = null
                                            paymentStatus = null
                                        }
                                    },
                                    enabled = !loading && paymentStatus != "success",
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = colorResource(id = R.color.darkblue),
                                        unselectedColor = Color(0xFF9CA3AF)
                                    )
                                )
                                Text(
                                    stringResource(id = R.string.transfer_using_the_momo_app),
                                    color = Color(0xFF1F2937),
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(start = (8 * divideAndRound(screenWidthValue)).dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height((8 * divideAndRound(screenWidthValue)).dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape((8 * divideAndRound(screenWidthValue)).dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedMethod == 1) Color(0xFFEFF6FF) else Color(0xFFF9FAFB)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding((16 * divideAndRound(screenWidthValue)).dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedMethod == 1,
                                    onClick = {
                                        if (!loading && paymentStatus != "success") {
                                            selectedMethod = 1
                                            payUrl = null
                                            currentOrderId = null
                                            paymentStatus = null
                                        }
                                    },
                                    enabled = !loading && paymentStatus != "success",
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = colorResource(id = R.color.darkblue),
                                        unselectedColor = Color(0xFF9CA3AF)
                                    )
                                )
                                Text(
                                    stringResource(id = R.string.scan_the_momo_qr_code),
                                    color = Color(0xFF1F2937),
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(start = (8 * divideAndRound(screenWidthValue)).dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height((24 * divideAndRound(screenWidthValue)).dp))

                        Button(
                            onClick = {
//                                userViewModel.checkHasPin(userId) { hasPin ->
//                                    if (hasPin) {
//                                        showPinScreen = true
//                                    } else {
//                                        showCreatePinScreen = true
//                                    }
//                                }
                                val biometricStore = BiometricLocalStore(context)

                                userViewModel.checkHasPin(userId) { hasPin ->
                                    if (!hasPin) {
                                        // Chưa có PIN -> tạo PIN mới
                                        showCreatePinScreen = true
                                    } else {
                                        // Đã có PIN -> kiểm tra sinh trắc học
                                        if (biometricStore.isBiometricEnabled()) {
                                            // Dùng sinh trắc học
                                            showBiometricAuth = true
                                        } else {
                                            // Dùng PIN
                                            showPinScreen = true
                                        }
                                    }
                                }
                            },
                            enabled = !loading && amountInput.isNotBlank() && paymentStatus != "success",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((56 * divideAndRound(screenWidthValue)).dp)
                                .shadow((8 * divideAndRound(screenWidthValue)).dp, RoundedCornerShape((12 * divideAndRound(screenWidthValue)).dp)),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(id = R.color.darkblue),
                                disabledContainerColor = Color(0xFFD1D5DB)
                            ),
                            shape = RoundedCornerShape((12 * divideAndRound(screenWidthValue)).dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Text(
                                text = if (selectedMethod == 0)
                                    stringResource(id = R.string.transfer_using_the_momo_app)
                                else
                                    stringResource(id = R.string.scan_the_momo_qr_code),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = (16 * divideAndRound(screenWidthValue)).sp
                            )
                        }
                    }
                }

                // Status Section
                if (loading || checkingStatus || payUrl != null || qrImageUrl != null || (momoSuccess && amount != null)) {
                    Spacer(modifier = Modifier.height((24 * divideAndRound(screenWidthValue)).dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow((4 * divideAndRound(screenWidthValue)).dp, RoundedCornerShape((16 * divideAndRound(screenWidthValue)).dp)),
                        shape = RoundedCornerShape((16 * divideAndRound(screenWidthValue)).dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding((20 * divideAndRound(screenWidthValue)).dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (loading) {
                                CircularProgressIndicator(
                                    color = colorResource(id = R.color.darkblue),
                                    modifier = Modifier.size((32 * divideAndRound(screenWidthValue)).dp)
                                )
                                Spacer(modifier = Modifier.height((16 * divideAndRound(screenWidthValue)).dp))
                                Text(
                                    "Đang xử lý...",
                                    color = colorResource(id = R.color.darkblue),
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            if (payUrl != null) {
                                Text(
                                    "${stringResource(id = R.string.switching_to_the_momo_app)}...",
                                    color = colorResource(id = R.color.darkblue),
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                            }

                            if (qrImageUrl != null) {
                                Text(
                                    "${stringResource(id = R.string.scan_the_qr_code)}:",
                                    fontSize = (18 * divideAndRound(screenWidthValue)).sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1F2937),
                                    modifier = Modifier.padding(bottom = (8 * divideAndRound(screenWidthValue)).dp)
                                )

                                Text(
                                    text = "Số tiền: ${formatNumber(amountInput.toIntOrNull() ?: 0)} VND",
                                    fontSize = (14 * divideAndRound(screenWidthValue)).sp,
                                    color = Color(0xFF6B7280)
                                )
                                Text(
                                    text = "Nội dung: USER$userId",
                                    fontSize = (14 * divideAndRound(screenWidthValue)).sp,
                                    color = Color(0xFF6B7280),
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.height((16 * divideAndRound(screenWidthValue)).dp))

                                Image(
                                    painter = rememberAsyncImagePainter(qrImageUrl),
                                    contentDescription = "QR MoMo",
                                    modifier = Modifier
                                        .size((250 * divideAndRound(screenWidthValue)).dp)
                                        .shadow((4 * divideAndRound(screenWidthValue)).dp, RoundedCornerShape((12 * divideAndRound(screenWidthValue)).dp))
                                )

                                Spacer(modifier = Modifier.height((16 * divideAndRound(screenWidthValue)).dp))

                                // Payment Status
                                when (paymentStatus) {
                                    "pending" -> {
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding((12 * divideAndRound(screenWidthValue)).dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size((20 * divideAndRound(screenWidthValue)).dp),
                                                    color = Color(0xFFFF9800)
                                                )
                                                Spacer(Modifier.width((8 * divideAndRound(screenWidthValue)).dp))
                                                Text(
                                                    text = "Đang chờ thanh toán...",
                                                    color = Color(0xFFFF9800),
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                    "success" -> {
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFC8E6C9)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(
                                                modifier = Modifier.padding((16 * divideAndRound(screenWidthValue)).dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = "✓ Thanh toán thành công!",
                                                    color = Color(0xFF2E7D32),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = (18 * divideAndRound(screenWidthValue)).sp
                                                )
                                                Spacer(Modifier.height((12 * divideAndRound(screenWidthValue)).dp))
                                                Button(
                                                    onClick = {
                                                        // Reset form
                                                        amountInput = ""
                                                        qrImageUrl = null
                                                        currentOrderId = null
                                                        paymentStatus = null
                                                        error = null
                                                    },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = Color(0xFF4CAF50)
                                                    )
                                                ) {
                                                    Text("Tạo giao dịch mới", color = Color.White)
                                                }
                                            }
                                        }
                                    }
                                    "failed" -> {
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCDD2)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "✗ Thanh toán thất bại",
                                                color = Color(0xFFD32F2F),
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier.padding((12 * divideAndRound(screenWidthValue)).dp)
                                            )
                                        }
                                    }
                                }
                            }

                            if (momoSuccess && amount != null) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFC8E6C9)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "✓ ${stringResource(id = R.string.payment_successful)}!",
                                        color = Color(0xFF2E7D32),
                                        fontSize = (18 * divideAndRound(screenWidthValue)).sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding((16 * divideAndRound(screenWidthValue)).dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Error Section
                error?.let {
                    Spacer(modifier = Modifier.height((16 * divideAndRound(screenWidthValue)).dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape((12 * divideAndRound(screenWidthValue)).dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Text(
                            text = it,
                            color = Color(0xFFDC2626),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding((16 * divideAndRound(screenWidthValue)).dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height((32 * divideAndRound(screenWidthValue)).dp))
            }
        }

        // Pin Screens
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
                                loading = true
                                error = null
                                qrImageUrl = null
                                payUrl = null

                                if (selectedMethod == 0) {
                                    // App MoMo
                                    requestPayUrl(context, amountInput) { url, err ->
                                        loading = false
                                        if (url != null) {
                                            payUrl = url
                                            openMomoApp(context, url)
                                        } else {
                                            error = err ?: "Lỗi không xác định"
                                        }
                                    }
                                } else {
                                    // QR Code - gửi userId làm description
                                    val description = "USER$userId"
                                    requestQrUrl(context, amountInput, description) { qrUrl, orderId, err ->
                                        loading = false
                                        if (qrUrl != null && orderId != null) {
                                            qrImageUrl = qrUrl
                                            currentOrderId = orderId
                                            paymentStatus = "pending"
                                        } else {
                                            error = err ?: "Không tạo được QR"
                                        }
                                    }
                                }
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
        //
        if (showBiometricAuth) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White
            ) {
                BiometricAuthScreen(
                    onSuccess = {
                        // Xác thực thành công -> thực hiện logic payment
                        showBiometricAuth = false
                        pinError = ""
                        loading = true
                        error = null
                        qrImageUrl = null
                        payUrl = null

                        if (selectedMethod == 0) {
                            // App MoMo
                            requestPayUrl(context, amountInput) { url, err ->
                                loading = false
                                if (url != null) {
                                    payUrl = url
                                    openMomoApp(context, url)
                                } else {
                                    error = err ?: "Lỗi không xác định"
                                }
                            }
                        } else {
                            // QR Code
                            val description = "USER$userId"
                            requestQrUrl(context, amountInput, description) { qrUrl, orderId, err ->
                                loading = false
                                if (qrUrl != null && orderId != null) {
                                    qrImageUrl = qrUrl
                                    currentOrderId = orderId
                                    paymentStatus = "pending"
                                } else {
                                    error = err ?: "Không tạo được QR"
                                }
                            }
                        }
                    },
                    onUsePinInstead = {
                        // User chọn dùng PIN thay vì biometric
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