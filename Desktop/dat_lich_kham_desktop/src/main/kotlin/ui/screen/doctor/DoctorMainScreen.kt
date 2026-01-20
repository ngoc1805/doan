package ui.screen.doctor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import data.repository.LoginRepository
import data.repository.DoctorRepository
import ui.screen.AccountScreen
import ui.screen.HomeScreen
import viewmodel.DoctorViewModel

class DoctorMainScreen(
    val tab: Int = 0
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = remember { DoctorViewModel() }

        val loginRepository = LoginRepository()
        val info = loginRepository.getUserInfo()
        val accountId = info?.accountId

        val doctorRepository = DoctorRepository()
        val doctorInfo = doctorRepository.getDoctorInfoFromFile()

        val id = doctorInfo?.id
        val name = doctorInfo?.name
        val department = doctorInfo?.department
        val balance = doctorInfo?.balance

        val tabs = listOf("Trang chủ", "Lịch khám hôm nay", "Tất cả lịch khám", "Tài khoản")
        var selectedTab by rememberSaveable() { mutableStateOf(tab) }

        LaunchedEffect(Unit){
            viewModel.fetchDoctorInfo(accountId ?: 0)
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Sidebar
                Box(
                    modifier = Modifier
                        .background(Color(0xFFFFF8DC), shape = RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .width(240.dp)
                            .fillMaxHeight()
                            .padding(8.dp)
                    ) {
                        // Phần thông tin bác sĩ với background
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp) // Giới hạn chiều cao cho phần thông tin
                        ) {
                            // Ảnh nền chỉ cho phần thông tin bác sĩ
                            Image(
                                painter = painterResource("Icons/bginfodoctor.png"),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Thông tin bác sĩ nằm trên ảnh nền
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Icon(
                                    painter = painterResource("Icons/doctor.png"),
                                    contentDescription = "",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(96.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Họ tên: $name",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Khoa: $department",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Số dư: $balance",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // TabBar không có background, nằm bên dưới
                        tabs.forEachIndexed { index, tab ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .background(
                                        color = if (selectedTab == index) Color(0xFF0856A8) else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedTab = index }
                                    .padding(vertical = 12.dp, horizontal = 8.dp)
                            ) {
                                Text(
                                    tab,
                                    color = if (selectedTab == index) Color.White else Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Main content area
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    when (selectedTab) {
                        0 -> {
                            HomeScreen().Content()
                        }
                        1 -> {
                            AppointmentTodayScreen().Content()
                        }
                        2 -> {
                            AllAppointmentSchedulesScreen().Content()
                        }
                        3 -> {
                            AccountScreen().Content()
                        }
                    }
                }
            }
        }
    }
}