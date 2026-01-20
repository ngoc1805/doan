package com.example.dat_lich_kham_fe.ui.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import com.example.dat_lich_kham_fe.util.balance
import com.example.dat_lich_kham_fe.util.chuyenDoiGio
import com.example.dat_lich_kham_fe.util.chuyenDoiNgay
import com.example.dat_lich_kham_fe.util.divideAndRound
import com.example.dat_lich_kham_fe.util.fcmToken
import com.example.dat_lich_kham_fe.util.formatNumber
import com.example.dat_lich_kham_fe.util.userId
import com.example.dat_lich_kham_fe.viewmodel.AppointmentViewModel
import com.example.dat_lich_kham_fe.viewmodel.NotificationViewModel
import com.example.dat_lich_kham_fe.viewmodel.PaymentViewModel
import com.example.dat_lich_kham_fe.viewmodel.UserViewModel
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentCard(
    navController: NavController,
    id: Int,
    doctorId: Int,
    doctorName: String,
    doctorCode: String,
    department: String,
    examPrice: Int,
    examDate: String,
    examTime: String,
    status: String,
    onClicked: () -> Unit = {},
) {

    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    val scale = divideAndRound(screenWidthValue)

    val appointmentViewModel = remember { AppointmentViewModel(context) }
    val userViewModel = remember { UserViewModel(context) }
    val notificationViewModel = remember { NotificationViewModel(context) }
    val chatViewModel = remember { ChatViewModel() }
    val paymentViewModel = remember { PaymentViewModel(context) }

    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    val currentDate = java.time.LocalDate.now()

    val appointmentDate = try {
        java.time.LocalDate.parse(chuyenDoiNgay( examDate), java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    } catch (e: Exception) {
        null
    }

    val isAppointmentValid = appointmentDate?.let {
        val daysDifference = ChronoUnit.DAYS.between(currentDate, it)
        daysDifference >= 3 //
    } ?: false

    val isLate = appointmentDate?.let {
        val daysDifference = ChronoUnit.DAYS.between(currentDate, it)
        daysDifference < 0
    } ?: false

    LaunchedEffect(isLate) {
        if(isLate){
            appointmentViewModel.updateStatus(id, "Đã trễ hẹn")
            navController.navigate("MainScreen/1")
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = (12 * scale).dp,
                vertical = (6 * scale).dp
            )
            .clickable { onClicked() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = (8 * scale).dp,
            pressedElevation = (4 * scale).dp
        ),
        shape = RoundedCornerShape((16 * scale).dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            Color(0xFFF8FAFB)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding((20 * scale).dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    // Doctor icon với gradient background
                    Box(
                        modifier = Modifier
                            .size((56 * scale).dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        colorResource(id = R.color.darkblue).copy(alpha = 0.15f),
                                        colorResource(id = R.color.darkblue).copy(alpha = 0.25f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.doctor_appointment),
                            contentDescription = null,
                            modifier = Modifier.size((32 * scale).dp),
                            colorFilter = ColorFilter.tint(colorResource(id = R.color.darkblue))
                        )
                    }

                    Spacer(modifier = Modifier.width((16 * scale).dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        // Doctor name với styling đẹp
                        Text(
                            text = "${stringResource(id = R.string.doctor)}: $doctorName - $doctorCode",
                            fontSize = (16 * scale).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A),
                            lineHeight = (20 * scale).sp
                        )

                        Spacer(modifier = Modifier.height((8 * scale).dp))

                        // Department với background
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color(0xFFE8F4FD),
                                    shape = RoundedCornerShape((8 * scale).dp)
                                )
                                .padding(
                                    horizontal = (10 * scale).dp,
                                    vertical = (4 * scale).dp
                                )
                        ) {
                            Text(
                                text = department,
                                fontSize = (12 * scale).sp,
                                color = colorResource(id = R.color.darkblue),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height((8 * scale).dp))

                        // Time info với icon
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size((20 * scale).dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF0F0F0)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "📅",
                                    fontSize = (10 * scale).sp
                                )
                            }
                            Spacer(modifier = Modifier.width((8 * scale).dp))
                            Text(
                                text = "${examTime} ngày ${chuyenDoiNgay(examDate)}",
                                fontSize = (13 * scale).sp,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Cancel button với design đẹp
                    Button(
                        onClick = {
                            dialogMessage = "Bạn chắc chắn muốn hủy"
                            showDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAppointmentValid)
                                colorResource(id = R.color.darkblue)
                            else
                                Color(0xFFE2E8F0),
                            disabledContainerColor = Color(0xFFE2E8F0)
                        ),
                        enabled = isAppointmentValid,
                        shape = RoundedCornerShape((12 * scale).dp),
                        modifier = Modifier
                            .height((36 * scale).dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.cancel),
                            fontWeight = FontWeight.Bold,
                            color = if (isAppointmentValid) Color.White else Color(0xFF94A3B8),
                            fontSize = (12 * scale).sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height((16 * scale).dp))

                // Price section với design đẹp
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    colorResource(id = R.color.darkblue).copy(alpha = 0.08f),
                                    colorResource(id = R.color.darkblue).copy(alpha = 0.04f)
                                )
                            ),
                            shape = RoundedCornerShape((12 * scale).dp)
                        )
                        .padding(
                            horizontal = (16 * scale).dp,
                            vertical = (12 * scale).dp
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size((28 * scale).dp)
                                .clip(CircleShape)
                                .background(colorResource(id = R.color.darkblue).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.coin),
                                contentDescription = null,
                                modifier = Modifier.size((16 * scale).dp),
                                colorFilter = ColorFilter.tint(colorResource(id = R.color.darkblue))
                            )
                        }

                        Spacer(modifier = Modifier.width((12 * scale).dp))

                        Text(
                            text = "${stringResource(id = R.string.exam_price)}: ${formatNumber(examPrice)} VND",
                            fontSize = (14 * scale).sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.darkblue)
                        )
                    }
                }
            }

            // Decorative accent
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size((50 * scale).dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                colorResource(id = R.color.darkblue).copy(alpha = 0.08f),
                                Color.Transparent
                            ),
                            radius = (25 * scale).toFloat()
                        )
                    )
            )
        }
    }

    // Dialog giữ nguyên
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Xác nhận hủy") },
            text = { Text(text = dialogMessage) },
            confirmButton = {
                TextButton(onClick = {
                    appointmentViewModel.updateStatus(id, "Đã hủy")
                    notificationViewModel.postNotification(
                        userId,
                        "Bạn đã hủy thành công một lịch hẹn",
                        "HistoryScreen"
                    )
                    var newbalance = balance + 100000
                    //userViewModel.updatebalance(userId, newbalance)
                    paymentViewModel.refundDeposit(userId)
                    chatViewModel.sendMessage(
                        title = "Biến động số dư",
                        body = "Bạn đã được hoàn 100.000VNĐ\nSố dư: ${formatNumber(newbalance)}VNĐ",
                        remoteToken = fcmToken,
                        isBroadcast = false
                    )
                    notificationViewModel.postNotification(
                        userId,
                        "Bạn đã được hoàn 100.000VNĐ, số dư: ${formatNumber(newbalance)}VNĐ",
                        "DepositScreen/false"
                    )

                    showDialog = false
                    navController.navigate("MainScreen/1")

                }) {
                    Text(text = "OK", color = colorResource(id = R.color.darkblue))
                }
            }
        )
    }
}
