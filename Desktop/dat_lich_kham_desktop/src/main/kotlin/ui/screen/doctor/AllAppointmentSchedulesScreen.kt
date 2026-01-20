package ui.screen.doctor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import data.repository.DoctorRepository
import ui.compoment.HistoryOfDoctorCard
import ui.compoment.UpcomingAppointmentScheduleCard
import viewmodel.AppointmentViewModel

class AllAppointmentSchedulesScreen : Screen {
    @Composable
    override fun Content() {
        var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
        val tabTitles = listOf("Lịch khám sắp tới", "Lịch sử khám bệnh")

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
                                Color(0xFF0856A8),
                                Color(0xFF1976D2)
                            )
                        )
                    )
                    .shadow(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Lịch hẹn",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Custom TabRow với design đẹp hơn
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp)
            ) {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    backgroundColor = Color.White,
                    contentColor = Color(0xFF0856A8),
                    indicator = { tabPositions ->
                        Box(
                            Modifier
                                .tabIndicatorOffset(tabPositions[selectedTabIndex])
                                .fillMaxWidth()
                                .height(4.dp)
                                .padding(horizontal = 16.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF0856A8),
                                            Color(0xFF1976D2)
                                        )
                                    ),
                                    shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                )
                        )
                    }
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            modifier = Modifier.height(56.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 15.sp,
                                    color = if (selectedTabIndex == index) Color(0xFF0856A8) else Color(0xFF7F8C8D)
                                )
                            }
                        }
                    }
                }
            }

            // Content Area
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .padding(bottom = 20.dp),
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                backgroundColor = Color.White
            ) {
                when (selectedTabIndex) {
                    0 -> UpcomingAppointmentsTab()
                    1 -> AppointmentHistoryTab()
                }
            }
        }
    }
}

@Composable
fun UpcomingAppointmentsTab() {
    val viewmodel = remember { AppointmentViewModel() }

    LaunchedEffect(Unit) {
        val doctorRepository = DoctorRepository()
        val doctorInfo = doctorRepository.getDoctorInfoFromFile()
        val id = doctorInfo?.id ?: 0
        viewmodel.listAppointmentBydoctorId(id, examDate = null, listOf("Đã lên lịch"))
    }

    val appointments = viewmodel.appointmentsbydoctorid

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        // Header với icon và badge
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Lịch khám sắp tới",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF0856A8)
            )

            // Badge hiển thị số lượng
            Surface(
                color = Color(0xFF0856A8),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(4.dp)
            ) {
                Text(
                    text = "${appointments.size}",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        if (appointments.isEmpty()) {
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
                        text = "📅",
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
                        text = "Chưa có lịch khám nào được đặt",
                        fontSize = 14.sp,
                        color = Color(0xFFBDC3C7),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(appointments) { appointment ->
                    UpcomingAppointmentScheduleCard(
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
                    )
                }
            }
        }
    }
}

@Composable
fun AppointmentHistoryTab() {
    val viewmodel = remember { AppointmentViewModel() }

    LaunchedEffect(Unit) {
        val doctorRepository = DoctorRepository()
        val doctorInfo = doctorRepository.getDoctorInfoFromFile()
        val id = doctorInfo?.id ?: 0
        viewmodel.listAppointmentBydoctorId(id, examDate = null, listOf("Đã hoàn tất"))
    }

    val appointments = viewmodel.appointmentsbydoctorid

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        // Header với icon và badge
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Lịch sử khám bệnh",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF27AE60)
            )

            // Badge hiển thị số lượng
            Surface(
                color = Color(0xFF27AE60),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(4.dp)
            ) {
                Text(
                    text = "${appointments.size}",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        if (appointments.isEmpty()) {
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
                        text = "Chưa có buổi khám nào hoàn tất",
                        fontSize = 14.sp,
                        color = Color(0xFFBDC3C7),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(appointments) { appointment ->
                    HistoryOfDoctorCard(
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
                    )
                }
            }
        }
    }
}