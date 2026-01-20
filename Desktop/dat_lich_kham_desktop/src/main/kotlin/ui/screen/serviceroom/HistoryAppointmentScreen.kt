package ui.screen.serviceroom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import data.repository.ServiceRoomRepository
import ui.compoment.HistoryOfServiceRoomCard
import viewmodel.ServiceAppointmentViewModel

class HistoryAppointmentScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = remember { ServiceAppointmentViewModel() }

        LaunchedEffect(Unit) {
            val serviceRoomRepository = ServiceRoomRepository()
            val serviceInfo = serviceRoomRepository.getServiceRoomInfoFromFile()
            val id = serviceInfo?.id ?: 0
            viewModel.listServiceAppointment(id, "Đã khám xong", null, null)
        }

        val serviceappointments = viewModel.serviceappointments

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF0F4F8),
                            Color(0xFFFFFFFF)
                        )
                    )
                )
        ) {
            // Header với gradient đẹp
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF11998E),
                                Color(0xFF38EF7D)
                            )
                        )
                    )
                    .shadow(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Lịch sử khám bệnh",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Content area
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Info Card với badge
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    elevation = 6.dp,
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Tổng số lịch sử",
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = Color(0xFF7F8C8D)
                            )
                            Text(
                                text = "${serviceappointments.size} ca khám",
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = Color(0xFF11998E),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        // Badge icon
                        Surface(
                            color = Color(0xFF11998E).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.size(60.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = "✓",
                                    fontSize = 32.sp,
                                    color = Color(0xFF11998E),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // List section
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
                                text = "📋",
                                fontSize = 64.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Text(
                                text = "Chưa có lịch sử",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF7F8C8D)
                            )
                            Text(
                                text = "Chưa có ca khám nào hoàn tất",
                                fontSize = 14.sp,
                                color = Color(0xFFBDC3C7),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                } else {
                    // Header cho list
                    Text(
                        text = "Danh sách ca khám",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF2C3E50),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(serviceappointments) { serviceappointment ->
                            HistoryOfServiceRoomCard(
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
                                fmctoken = serviceappointment.fmctoken
                            )
                        }
                    }
                }
            }
        }
    }
}