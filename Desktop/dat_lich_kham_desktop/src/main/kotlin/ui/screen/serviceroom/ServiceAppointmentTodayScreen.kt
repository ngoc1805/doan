package ui.screen.serviceroom

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import data.repository.ServiceRoomRepository
import ui.compoment.ServiceAppointmentCard
import viewmodel.ServiceAppointmentViewModel
import java.time.LocalDate

class ServiceAppointmentTodayScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = remember { ServiceAppointmentViewModel() }
        val currentDate = LocalDate.now()
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(Unit) {
            val serviceRoomRepository = ServiceRoomRepository()
            val serviceInfo = serviceRoomRepository.getServiceRoomInfoFromFile()
            val id = serviceInfo?.id ?: 0
            viewModel.listServiceAppointment(id, "Đã lên lịch", "Đã thanh toán", currentDate)
        }

        val serviceappointments = viewModel.serviceappointments

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
        ) {
            // Gradient Top AppBar với design đẹp
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF6A11CB),
                                Color(0xFF2575FC)
                            )
                        )
                    )
                    .shadow(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Lịch hẹn hôm nay",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${serviceappointments.size} lịch hẹn",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                            .clickable {
                                val serviceRoomRepository = ServiceRoomRepository()
                                val serviceInfo = serviceRoomRepository.getServiceRoomInfoFromFile()
                                val id = serviceInfo?.id ?: 0
                                viewModel.listServiceAppointment(id, "Đã lên lịch", "Đã thanh toán", currentDate)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource("Icons/refresh.png"),
                            contentDescription = "Reload",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header Card với gradient và shadow đẹp
                Card(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(12.dp),
                    backgroundColor = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp, horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "ID",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF6A11CB),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "Họ tên",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF6A11CB),
                            modifier = Modifier.weight(2f)
                        )
                        Text(
                            "Giới tính",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF6A11CB),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "Tuổi",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF6A11CB),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "Quê quán",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF6A11CB),
                            modifier = Modifier.weight(3f)
                        )
                    }
                }

                // List với spacing và empty state
                if (serviceappointments.isEmpty()) {
                    // Empty state đẹp
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "🏥",
                                fontSize = 64.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Text(
                                text = "Không có lịch hẹn",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF7F8C8D)
                            )
                            Text(
                                text = "Chưa có lịch hẹn nào hôm nay",
                                fontSize = 14.sp,
                                color = Color(0xFFBDC3C7),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(serviceappointments) { serviceappointment ->
                            ServiceAppointmentCard(
                                id = serviceappointment.id,
                                appointmentId = serviceappointment.appointmentId,
                                userId = serviceappointment.userId,
                                userName = serviceappointment.userName,
                                gender = serviceappointment.gender,
                                birthDate = serviceappointment.birthDate,
                                homeTown = serviceappointment.homeTown,
                                cccd = serviceappointment.cccd,
                                examDate = serviceappointment.examDate,
                                examTime = serviceappointment.examTime,
                                status = serviceappointment.status,
                                fmctoken = serviceappointment.fmctoken,
                                navigator = navigator
                            )
                        }
                    }
                }
            }
        }
    }
}