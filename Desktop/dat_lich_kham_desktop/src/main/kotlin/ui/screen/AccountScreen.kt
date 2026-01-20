package ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import data.repository.DoctorRepository
import data.repository.LoginRepository
import data.repository.ServiceRoomRepository
import ui.compoment.AccountCard

class AccountScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val doctorRepository = DoctorRepository()
        val loginRepository = LoginRepository()
        val serviceRoomRepository = ServiceRoomRepository()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F1F5)) // Nền nhạt tổng thể
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color(0xFF0856A8)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tài khoản",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            // Card đổi mật khẩu
            AccountCard(
                icon = painterResource("Icons/lock.png"),
                text = "Đổi mật khẩu",
                onClick = { navigator.push(ChangePasswordScreen()) }
            )
            Spacer(modifier = Modifier.height(12.dp))
            // Card đăng xuất
            AccountCard(
                icon = painterResource("Icons/logout.png"),
                text = "Đăng xuất",
                onClick = {
                    loginRepository.logout()
                    doctorRepository.clearDoctorInfo()
                    serviceRoomRepository.clearServiceRoomInfo()
                    navigator.push(LoginScreen())
                }
            )
        }
    }
}

