package ui.screen.doctor

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import data.repository.DoctorRepository
import ui.compoment.AppointmentByIdDoctorCard
import viewmodel.AppointmentViewModel
import java.time.LocalDate

class AppointmentTodayScreen : Screen {
    @Composable
    override fun Content() {
        val viewmodel = remember { AppointmentViewModel() }
        val currentDate = LocalDate.now()
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(Unit){
            val doctorRepository = DoctorRepository()
            val doctorInfo = doctorRepository.getDoctorInfoFromFile()
            val id = doctorInfo?.id ?: 0
            viewmodel.listAppointmentBydoctorId(id, currentDate, listOf("Đã lên lịch", "Đang thanh toán", "Đã thanh toán"))
        }

        val appointments = viewmodel.appointmentsbydoctorid

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
        ) {
            // Gradient Top AppBar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF0856A8),
                                Color(0xFF1976D2)
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
                            text = "${appointments.size} cuộc hẹn",
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
                                val doctorRepository = DoctorRepository()
                                val doctorInfo = doctorRepository.getDoctorInfoFromFile()
                                val id = doctorInfo?.id ?: 0
                                viewmodel.listAppointmentBydoctorId(id, currentDate, listOf("Đã lên lịch", "Đang thanh toán", "Đã thanh toán"))
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
                // Header card với shadow đẹp hơn
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
                            color = Color(0xFF0856A8),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "Họ tên",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF0856A8),
                            modifier = Modifier.weight(2f)
                        )
                        Text(
                            "Giới tính",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF0856A8),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "Tuổi",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF0856A8),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "Giờ khám",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF0856A8),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // List với spacing đẹp hơn
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(appointments) { appointment ->
                        AppointmentByIdDoctorCard(
                            id = appointment.id,
                            userId = appointment.userId,
                            userName = appointment.userName,
                            gender = appointment.gender,
                            birthDate = appointment.birthDate,
                            homeTown = appointment.homeTown,
                            cccd = appointment.cccd,
                            examDate = appointment.examDate,
                            examTime = appointment.examTime,
                            status = appointment.status,
                            fmctoken = appointment.fmctoken,
                            navigator = navigator
                        )
                    }
                }
            }
        }
    }
}