package com.example.dat_lich_kham_fe.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
fun ForgotPinScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel: ForgotPinViewModel = viewModel(
        factory = ForgotPinViewModelFactory(context)
    )

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quên mã PIN") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Xác thực số điện thoại",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.darkblue)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Nhập số điện thoại đã đăng ký để nhận mã OTP",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Input SĐT
                OutlinedTextField(
                    value = uiState.phone,
                    onValueChange = { viewModel.updatePhone(it) },
                    label = { Text("Số điện thoại") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    enabled = !uiState.otpSent,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Nút gửi OTP
                if (!uiState.otpSent) {
                    Button(
                        onClick = { viewModel.sendOtp() },
                        enabled = !uiState.isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.darkblue)
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White
                            )
                        } else {
                            Text("Gửi mã OTP")
                        }
                    }
                }

                // Phần nhập OTP
                if (uiState.otpSent) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Mã OTP đã được gửi đến số ${uiState.phone}",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = uiState.otp,
                        onValueChange = { if (it.length <= 6) viewModel.updateOtp(it) },
                        label = { Text("Nhập mã OTP") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Đếm ngược
                    if (uiState.remainingSeconds > 0) {
                        val minutes = uiState.remainingSeconds / 60
                        val seconds = uiState.remainingSeconds % 60
                        Text(
                            text = "Mã hết hạn sau: ${String.format("%02d:%02d", minutes, seconds)}",
                            fontSize = 14.sp,
                            color = colorResource(id = R.color.darkblue),
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = "Mã OTP đã hết hạn",
                            fontSize = 14.sp,
                            color = Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.verifyOtp { resetToken ->
                                navController.navigate("ResetPinScreen/$resetToken") {
                                    navController.currentBackStackEntry?.destination?.route?.let { route ->
                                        popUpTo(route) { inclusive = true }
                                    }
                                }
                            }
                        },
                        enabled = !uiState.isLoading && uiState.otp.length == 6,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.darkblue)
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White
                            )
                        } else {
                            Text("Xác thực OTP")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = { viewModel.sendOtp() },
                        enabled = uiState.remainingSeconds == 0
                    ) {
                        Text(
                            text = "Gửi lại mã OTP",
                            color = if (uiState.remainingSeconds == 0)
                                colorResource(id = R.color.darkblue)
                            else
                                Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hiển thị lỗi
                uiState.errorMessage?.let { error ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        )
                    ) {
                        Text(
                            text = error,
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                // Hiển thị thành công
                uiState.successMessage?.let { success ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE8F5E9)
                        )
                    ) {
                        Text(
                            text = success,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}