package com.example.dat_lich_kham_fe.ui.component

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
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.util.BiometricHelper
import com.example.dat_lich_kham_fe.util.BiometricLocalStore
import com.example.dat_lich_kham_fe.util.divideAndRound
import com.example.dat_lich_kham_fe.util.userId
import com.example.dat_lich_kham_fe.viewmodel.UserViewModel

@Composable
fun BiometricSettingCard(
    onPinRequired: () -> Unit,
    scale: Float
) {
    val context = LocalContext.current
    val biometricHelper = remember { BiometricHelper(context) }
    val biometricStore = remember { BiometricLocalStore(context) }

    var isBiometricEnabled by remember { mutableStateOf(biometricStore.isBiometricEnabled()) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    val isBiometricAvailable = biometricHelper.isBiometricAvailable()
    val hasBiometricEnrolled = biometricHelper.hasBiometricEnrolled()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = (16 * scale).dp, vertical = (8 * scale).dp),
        shape = RoundedCornerShape((12 * scale).dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation((2 * scale).dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding((16 * scale).dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
//                Icon(
//                    imageVector = Icons.Default.Fingerprint,
//                    contentDescription = "Biometric",
//                    tint = colorResource(id = R.color.darkblue),
//                    modifier = Modifier.size((24 * scale).dp)
//                )

                Spacer(modifier = Modifier.width((12 * scale).dp))

                Column {
                    Text(
                        text = "Xác thực sinh trắc học",
                        fontSize = (16 * scale).sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1F2937)
                    )

                    if (!isBiometricAvailable) {
                        Text(
                            text = "Thiết bị không hỗ trợ",
                            fontSize = (12 * scale).sp,
                            color = Color(0xFF9CA3AF)
                        )
                    } else if (!hasBiometricEnrolled) {
                        Text(
                            text = "Chưa đăng ký sinh trắc học",
                            fontSize = (12 * scale).sp,
                            color = Color(0xFFF59E0B)
                        )
                    }
                }
            }

            Switch(
                checked = isBiometricEnabled,
                onCheckedChange = { enabled ->
                    if (!isBiometricAvailable) {
                        dialogMessage = "Thiết bị của bạn không hỗ trợ sinh trắc học"
                        showDialog = true
                        return@Switch
                    }

                    if (!hasBiometricEnrolled) {
                        dialogMessage = "Vui lòng đăng ký vân tay hoặc khuôn mặt trong cài đặt thiết bị"
                        showDialog = true
                        return@Switch
                    }

                    if (enabled) {
                        // Bật sinh trắc học lần đầu cần xác thực PIN
                        if (!biometricStore.isBiometricSetupDone()) {
                            onPinRequired()
                        } else {
                            biometricStore.setBiometricEnabled(true)
                            isBiometricEnabled = true
                        }
                    } else {
                        // Tắt sinh trắc học
                        biometricStore.setBiometricEnabled(false)
                        isBiometricEnabled = false
                    }
                },
                enabled = isBiometricAvailable && hasBiometricEnrolled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = colorResource(id = R.color.darkblue),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFE5E7EB)
                )
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Thông báo") },
            text = { Text(dialogMessage) },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}