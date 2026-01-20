package com.example.dat_lich_kham_fe.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.util.BiometricHelper
import com.example.dat_lich_kham_fe.util.BiometricLocalStore
import kotlinx.coroutines.launch

@Composable
fun BiometricAuthScreen(
    onSuccess: () -> Unit,
    onUsePinInstead: () -> Unit,
    onClose: () -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val biometricHelper = remember { BiometricHelper(context) }
    val biometricStore = remember { BiometricLocalStore(context) }

    var failedAttempts by remember { mutableStateOf(0) }
    val maxFailedAttempts = 3

    var errorMessage by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    // Tự động hiển thị prompt khi màn hình được mở
    LaunchedEffect(Unit) {
        if (activity != null && biometricStore.isBiometricEnabled()) {
            biometricHelper.showBiometricPrompt(
                activity = activity,
                title = "Xác thực thanh toán",
                subtitle = "Sử dụng sinh trắc học để xác nhận",
                negativeButtonText = "Dùng mã PIN",
                onSuccess = {
                    onSuccess()
                },
                onError = { errorCode, errString ->
                    // errorCode == 13 nghĩa là user chọn "Dùng mã PIN"
                    if (errorCode == 13) {
                        onUsePinInstead()
                    } else {
                        errorMessage = errString.toString()
                        showError = true
                    }
                },
                onFailed = {
                    failedAttempts++
                    if (failedAttempts >= maxFailedAttempts) {
                        errorMessage = "Quá nhiều lần thất bại. Vui lòng sử dụng mã PIN"
                        showError = true
                        // Chuyển sang PIN sau 2 giây
                        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                            kotlinx.coroutines.delay(2000)
                            onUsePinInstead()
                        }
                    } else {
                        errorMessage = "Xác thực thất bại. Còn ${maxFailedAttempts - failedAttempts} lần thử"
                        showError = true
                    }
                }
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            // Biometric Icon
//            Icon(
//                imageVector = Icons.Default.Fingerprint,
//                contentDescription = "Biometric",
//                tint = colorResource(id = R.color.darkblue),
//                modifier = Modifier.size(80.dp)
//            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Xác thực sinh trắc học",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Chạm vào cảm biến để xác thực",
                fontSize = 14.sp,
                color = Color(0xFF6B7280)
            )

            if (showError) {
                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (failedAttempts >= maxFailedAttempts)
                            Color(0xFFFEF2F2)
                        else
                            Color(0xFFFFF7ED)
                    )
                ) {
                    Text(
                        text = errorMessage,
                        fontSize = 14.sp,
                        color = if (failedAttempts >= maxFailedAttempts)
                            Color(0xFFDC2626)
                        else
                            Color(0xFFF59E0B),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Try Again Button (nếu chưa quá giới hạn)
            if (failedAttempts > 0 && failedAttempts < maxFailedAttempts) {
                Button(
                    onClick = {
                        showError = false
                        if (activity != null) {
                            biometricHelper.showBiometricPrompt(
                                activity = activity,
                                title = "Xác thực thanh toán",
                                subtitle = "Sử dụng sinh trắc học để xác nhận",
                                negativeButtonText = "Dùng mã PIN",
                                onSuccess = onSuccess,
                                onError = { errorCode, errString ->
                                    if (errorCode == 13) {
                                        onUsePinInstead()
                                    } else {
                                        errorMessage = errString.toString()
                                        showError = true
                                    }
                                },
                                onFailed = {
                                    failedAttempts++
                                    if (failedAttempts >= maxFailedAttempts) {
                                        errorMessage = "Quá nhiều lần thất bại. Vui lòng sử dụng mã PIN"
                                        showError = true
                                        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                            kotlinx.coroutines.delay(2000)
                                            onUsePinInstead()
                                        }
                                    } else {
                                        errorMessage = "Xác thực thất bại. Còn ${maxFailedAttempts - failedAttempts} lần thử"
                                        showError = true
                                    }
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.darkblue)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Thử lại", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Use PIN Button
            OutlinedButton(
                onClick = onUsePinInstead,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colorResource(id = R.color.darkblue)
                )
            ) {
                Text("Sử dụng mã PIN", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Cancel Button
            TextButton(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Hủy", color = Color(0xFF6B7280))
            }
        }
    }
}