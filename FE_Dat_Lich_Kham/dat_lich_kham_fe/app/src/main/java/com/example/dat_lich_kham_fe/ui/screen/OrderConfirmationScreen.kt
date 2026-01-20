package com.example.dat_lich_kham_fe.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.dat_lich_kham_fe.Firebase.ChatViewModel
import com.example.dat_lich_kham_fe.util.PersistentCookieJar
import com.example.dat_lich_kham_fe.util.UserLocalStore
import com.example.dat_lich_kham_fe.util.balance
import com.example.dat_lich_kham_fe.util.fcmToken
import com.example.dat_lich_kham_fe.util.formatNumber
import com.example.dat_lich_kham_fe.util.userId
import com.example.dat_lich_kham_fe.viewmodel.InpatientViewModel
import com.example.dat_lich_kham_fe.viewmodel.MenuViewModel
import com.example.dat_lich_kham_fe.viewmodel.NotificationViewModel
import com.example.dat_lich_kham_fe.viewmodel.OrderViewModel
import com.example.dat_lich_kham_fe.viewmodel.OrderItemViewModel
import com.example.dat_lich_kham_fe.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderConfirmationScreen(
    navController: NavController,
    cartData: String
) {
    val context = LocalContext.current
    val menuViewModel = remember { MenuViewModel(context) }
    val orderViewModel = remember { OrderViewModel(context) }
    val orderItemViewModel = remember { OrderItemViewModel(context) }
    val userViewModel = remember { UserViewModel(context) }
    val inpatientViewModel = remember { InpatientViewModel(context) }
    val notificationViewModel = remember { NotificationViewModel(context) }
    val chatViewModel = remember { ChatViewModel() }
    val userStore = remember { UserLocalStore(context) }
    val cookieJar = remember { PersistentCookieJar(context) }

    var showPinScreen by remember { mutableStateOf(false) }
    var pinError by remember { mutableStateOf("") }
    var showCreatePinScreen by remember { mutableStateOf(false) }
    var showConfirmPinScreen by remember { mutableStateOf(false) }
    var tempPin by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var isLoading by remember { mutableStateOf(false) }

    val cartItems = remember {
        mutableStateMapOf<Int, Int>().apply {
            cartData.split(",").mapNotNull { item ->
                val parts = item.split(":")
                if (parts.size == 2) {
                    val id = parts[0].toIntOrNull()
                    val qty = parts[1].toIntOrNull()
                    if (id != null && qty != null) {
                        this[id] = qty
                    }
                }
            }
        }
    }


    LaunchedEffect(Unit) {
        menuViewModel.fetchMenus(true)
        val user = userStore.getUser()
        val userId = user?.Id ?: 0
        inpatientViewModel.getAddress(userId)
        userViewModel.canteenInfo()
    }
    val canteen = userViewModel.canteen.value

    val menus = menuViewModel.menus
    val selectedMenus = menus.filter { cartItems.containsKey(it.id) }

    var noteText by remember { mutableStateOf("") }

    // Collect address từ StateFlow
    val addressFromApi by inpatientViewModel.addressResult.collectAsState()
    var addressText by remember { mutableStateOf("") }

    // Cập nhật addressText khi nhận được dữ liệu từ API
    LaunchedEffect(addressFromApi) {
        if (!addressFromApi.isNullOrEmpty()) {
            addressText = addressFromApi!!
        }
    }

    var selectedPaymentMethod by remember { mutableStateOf("cash") }
    var address by remember { mutableStateOf("") }

    val totalPrice = selectedMenus.sumOf { menu ->
        (cartItems[menu.id] ?: 0) * menu.examPrice
    }

    val gradientColors = listOf(
        Color(0xFFF8FAFC),
        Color(0xFFE8EAF6),
        Color(0xFFF3E5F5)
    )

    Box {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Xác nhận đơn hàng",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.popBackStack() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF1E293B)
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush = Brush.verticalGradient(gradientColors))
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        item {
                            Text(
                                text = "Chi tiết đơn hàng ",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        items(selectedMenus) { menu ->
                            OrderItemCard(
                                menu = menu,
                                quantity = cartItems[menu.id] ?: 0,
                                onIncrease = {
                                    cartItems[menu.id] = (cartItems[menu.id] ?: 0) + 1
                                },
                                onDecrease = {
                                    val currentQty = cartItems[menu.id] ?: 0
                                    if (currentQty > 1) {
                                        cartItems[menu.id] = currentQty - 1
                                    } else {
                                        cartItems.remove(menu.id)
                                    }
                                },
                                onRemove = {
                                    cartItems.remove(menu.id)
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    OrderSummarySection(
                        totalItems = cartItems.values.sum(),
                        totalPrice = totalPrice,
                        noteText = noteText,
                        onNoteChange = { noteText = it },
                        addressText = addressText,
                        onAddressChange = { addressText = it },
                        selectedPaymentMethod = selectedPaymentMethod,
                        onPaymentMethodChange = { selectedPaymentMethod = it },
                        isLoading = isLoading,
                        onConfirmOrder = {
                            address = addressText
                            if (addressText.isBlank()) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Vui lòng nhập địa chỉ")
                                }
                                return@OrderSummarySection
                            }

                            if (cartItems.isEmpty()) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Giỏ hàng trống")
                                }
                                return@OrderSummarySection
                            }

                            // Kiểm tra phương thức thanh toán
                            if (selectedPaymentMethod == "app_balance") {
//                            scope.launch {
//                                snackbarHostState.showSnackbar("Tính năng thanh toán bằng tiền trong app đang được phát triển. Vui lòng chọn thanh toán tiền mặt!")
//                            }
                                userViewModel.checkHasPin(userId) { hasPin ->
                                    if (hasPin) {
                                        showPinScreen = true
                                    } else {
                                        showCreatePinScreen = true
                                    }
                                }
                                return@OrderSummarySection
                            }

                            isLoading = true
                            scope.launch {
                                try {
                                    val user = userStore.getUser()
                                    val userId = user?.Id ?: 0
                                    val phone = cookieJar.getUsername().toString()

                                    val orderId = orderViewModel.createOrder(
                                        userId = userId,
                                        phone = phone,
                                        address = addressText,
                                        note = noteText,
                                        status = "Đã đặt hàng"
                                    )

                                    if (orderId != null) {
                                        cartItems.forEach { (menuId, quantity) ->
                                            orderItemViewModel.createOrderItem(
                                                orderId = orderId,
                                                menuId = menuId,
                                                quantity = quantity
                                            )
                                        }

                                        val canteenId = canteen?.userId
                                        val canteenFmc = canteen?.fmcToken
                                        if (canteenFmc != null) {
                                            chatViewModel.sendMessage(
                                                title = "Bạn có đơn hang mới",
                                                body = "Bạn có 1 đơn hàng mới",
                                                remoteToken = canteenFmc,
                                                isBroadcast = false
                                            )
                                        }
                                        if (canteenId != null) {
                                            notificationViewModel.postNotification(
                                                userId = canteenId,
                                                content = "Bạn có 1 đơn hàng mới",
                                                path = "MainScreen/1"
                                            )
                                        }

                                        snackbarHostState.showSnackbar("Đặt hàng thành công!")
                                        navController.popBackStack()
                                    } else {
                                        snackbarHostState.showSnackbar("Đặt hàng thất bại. Vui lòng thử lại!")
                                    }
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("Lỗi: ${e.message}")
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    )
                }
                //----------------
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
                                        //
                                        isLoading = true
                                        scope.launch {
                                            try {
                                                val user = userStore.getUser()
                                                val userId = user?.Id ?: 0
                                                val phone = cookieJar.getUsername().toString()

                                                val orderId = orderViewModel.createOrder(
                                                    userId = userId,
                                                    phone = phone,
                                                    address = addressText,
                                                    note = noteText,
                                                    status = "Đã thanh toán"
                                                )

                                                if (orderId != null) {
                                                    cartItems.forEach { (menuId, quantity) ->
                                                        orderItemViewModel.createOrderItem(
                                                            orderId = orderId,
                                                            menuId = menuId,
                                                            quantity = quantity
                                                        )
                                                    }

                                                    var newbalance = balance - totalPrice
                                                    userViewModel.updatebalance(com.example.dat_lich_kham_fe.util.userId, newbalance)
                                                    notificationViewModel.postNotification(
                                                        userId = com.example.dat_lich_kham_fe.util.userId,
                                                        content = "Bạn đã thanh toán ${formatNumber(totalPrice)}. Số dư hiện tại: ${formatNumber(newbalance)} VND",
                                                        path = "DepositScreen/false"
                                                    )
                                                    chatViewModel.sendMessage(
                                                        title = "Biến động số dư",
                                                        body = "Bạn đã thanh toán ${formatNumber(totalPrice)}. Số dư hiện tại: ${formatNumber(newbalance)} VND\"",
                                                        remoteToken = fcmToken,
                                                        isBroadcast = false
                                                    )

                                                    val canteenId = canteen?.userId
                                                    val canteenFmc = canteen?.fmcToken
                                                    if (canteenFmc != null) {
                                                        chatViewModel.sendMessage(
                                                            title = "Bạn có đơn hang mới",
                                                            body = "Bạn có 1 đơn hàng mới",
                                                            remoteToken = canteenFmc,
                                                            isBroadcast = false
                                                        )
                                                    }
                                                    if (canteenId != null) {
                                                        notificationViewModel.postNotification(
                                                            userId = canteenId,
                                                            content = "Bạn có 1 đơn hàng mới",
                                                            path = "MainScreen/1"
                                                        )
                                                    }

                                                    snackbarHostState.showSnackbar("Đặt hàng thành công!")
                                                    navController.popBackStack()
                                                } else {
                                                    snackbarHostState.showSnackbar("Đặt hàng thất bại. Vui lòng thử lại!")
                                                }
                                            } catch (e: Exception) {
                                                snackbarHostState.showSnackbar("Lỗi: ${e.message}")
                                            } finally {
                                                isLoading = false
                                            }
                                        }
                                        //
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
                            navController = navController,
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
                //----------------

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItemCard(
    menu: com.example.dat_lich_kham_fe.data.model.Menu,
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    val url = "${com.example.dat_lich_kham_fe.data.api.address}/api/${menu.imageUrl}"
    val itemTotal = menu.examPrice * quantity

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AsyncImage(
                model = url,
                contentDescription = menu.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = menu.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = menu.category,
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Xóa",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${menu.examPrice.toString().replace(Regex("(\\d)(?=(\\d{3})+$)"), "$1.")}đ/món",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )

                        Text(
                            text = "${itemTotal.toString().replace(Regex("(\\d)(?=(\\d{3})+$)"), "$1.")}đ",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF059669)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .background(
                                color = Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(4.dp)
                    ) {
                        IconButton(
                            onClick = onDecrease,
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    color = Color.White,
                                    shape = CircleShape
                                )
                        ) {
                            Text(
                                text = "−",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444)
                            )
                        }

                        Text(
                            text = quantity.toString(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B),
                            modifier = Modifier.width(32.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        IconButton(
                            onClick = onIncrease,
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    color = Color(0xFF10B981),
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Tăng",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderSummarySection(
    totalItems: Int,
    totalPrice: Int,
    noteText: String,
    onNoteChange: (String) -> Unit,
    addressText: String,
    onAddressChange: (String) -> Unit,
    selectedPaymentMethod: String,
    onPaymentMethodChange: (String) -> Unit,
    isLoading: Boolean,
    onConfirmOrder: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Tổng kết đơn hàng",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Tổng số món",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = "$totalItems món",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1E293B)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Tạm tính",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = "${totalPrice.toString().replace(Regex("(\\d)(?=(\\d{3})+$)"), "$1.")}đ",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1E293B)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider(
                color = Color(0xFFE2E8F0),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tổng cộng",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = "${totalPrice.toString().replace(Regex("(\\d)(?=(\\d{3})+$)"), "$1.")}đ",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF059669)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Địa chỉ giao hàng",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E293B),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = addressText,
                onValueChange = onAddressChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Nhập địa chỉ giao hàng...",
                        color = Color(0xFF94A3B8)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Ghi chú",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E293B),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = noteText,
                onValueChange = onNoteChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                placeholder = {
                    Text(
                        text = "Nhập ghi chú cho đơn hàng...",
                        color = Color(0xFF94A3B8)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Phương thức thanh toán",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E293B),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedPaymentMethod == "cash",
                    onClick = { onPaymentMethodChange("cash") },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color(0xFF10B981),
                        unselectedColor = Color(0xFF94A3B8)
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Thanh toán bằng tiền mặt",
                    fontSize = 14.sp,
                    color = Color(0xFF1E293B)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedPaymentMethod == "app_balance",
                    onClick = { onPaymentMethodChange("app_balance") },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color(0xFF10B981),
                        unselectedColor = Color(0xFF94A3B8)
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Thanh toán bằng tiền trong app",
                    fontSize = 14.sp,
                    color = Color(0xFF1E293B)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onConfirmOrder,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981)
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Xác nhận đặt đơn",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
