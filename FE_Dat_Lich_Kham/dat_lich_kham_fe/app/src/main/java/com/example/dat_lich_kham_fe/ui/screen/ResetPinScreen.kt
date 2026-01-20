package com.example.dat_lich_kham_fe.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.viewmodel.ForgotPinViewModel
import com.example.dat_lich_kham_fe.viewmodel.ForgotPinViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPinScreen(
    navController: NavController,
    resetToken: String
) {
    val context = LocalContext.current
    val viewModel: ForgotPinViewModel = viewModel(
        factory = ForgotPinViewModelFactory(context)
    )

    val resetPinState by viewModel.resetPinState.collectAsState()

    // Trạng thái: true = nhập PIN mới, false = xác nhận PIN
    var isEnteringNewPin by remember { mutableStateOf(true) }

    // Auto navigate khi thành công
    // Auto navigate khi thành công
    LaunchedEffect(resetPinState.resetSuccess) {
        if (resetPinState.resetSuccess) {
            kotlinx.coroutines.delay(2000)
            // Quay về màn trước đó và xóa tất cả màn liên quan đến quên PIN
            navController.popBackStack(
                route = navController.previousBackStackEntry?.destination?.route ?: "MainScreen",
                inclusive = false
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đặt mã PIN mới") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (!isEnteringNewPin) {
                            // Nếu đang ở màn xác nhận → quay về màn nhập mới
                            isEnteringNewPin = true
                            viewModel.clearConfirmPin()
                        } else {
                            // Nếu đang ở màn nhập mới → quay về màn trước
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.darkblue),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // Tiêu đề
                Text(
                    text = if (isEnteringNewPin) "Tạo mã PIN mới" else "Nhập lại mã PIN",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.darkblue)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Mã PIN phải là 6 chữ số",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Hiển thị lỗi
                resetPinState.errorMessage?.let { error ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        )
                    ) {
                        Text(
                            text = error,
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Hiển thị thành công
                if (resetPinState.resetSuccess) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE8F5E9)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "✓ ${resetPinState.successMessage}",
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Đang chuyển về màn đăng nhập...",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Vòng tròn hiển thị số ký tự đã nhập
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    val currentPin = if (isEnteringNewPin)
                        resetPinState.newPin
                    else
                        resetPinState.confirmPin

                    repeat(6) { index ->
                        Circle(index < currentPin.length)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // NumberPad
                NumberPad(
                    pin = if (isEnteringNewPin) resetPinState.newPin else resetPinState.confirmPin,
                    maxPinLength = 6,
                    onPinChange = { newPin ->
                        if (isEnteringNewPin) {
                            viewModel.updateNewPin(newPin)
                            // Khi nhập đủ 6 số → chuyển sang màn xác nhận
                            if (newPin.length == 6) {
                                isEnteringNewPin = false
                            }
                        } else {
                            viewModel.updateConfirmPin(newPin)
                            // Khi nhập đủ 6 số → gọi API reset
                            if (newPin.length == 6) {
                                viewModel.resetPin(resetToken) {
                                    // Success callback
                                }
                            }
                        }
                    }
                )

                // Loading indicator
                if (resetPinState.isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(
                        color = colorResource(id = R.color.darkblue)
                    )
                }
            }
        }
    }
}