package com.example.nhom1_fe_datnhahang.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nhom1_fe_datnhahang.R
import com.example.nhom1_fe_datnhahang.util.PersistentCookieJar
import com.example.nhom1_fe_datnhahang.util.UserLocalStore
import com.example.nhom1_fe_datnhahang.util.divideAndRound
import com.example.nhom1_fe_datnhahang.util.formatNumber
import com.example.nhom1_fe_datnhahang.util.fullName
import com.example.nhom1_fe_datnhahang.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(navController: NavController) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    val scale = divideAndRound(screenWidthValue)
    val context = LocalContext.current

    val userStore = UserLocalStore(context)
    val userViewModel = remember { UserViewModel(context) }

    // Background gradient giống HomeScreen
    val gradientColors = listOf(
        Color(0xFFF8FAFC),
        Color(0xFFF1F5F9),
        Color(0xFFE2E8F0)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(gradientColors)
            )
    ) {
        // TopAppBar giống HomeScreen
        TopAppBar(
            title = {
                Text(
                    text = "Tài khoản",
                    fontSize = (24 * scale).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = (16 * scale).dp)
        ) {
            Spacer(modifier = Modifier.height((12 * scale).dp))

            // Card thông tin người dùng
            ProfileInfoCard(scale)

            Spacer(modifier = Modifier.height((16 * scale).dp))

            // Card Số dư
            MenuCard(
                icon = R.drawable.notice, // Thay bằng icon wallet của bạn
                title = "Số dư",
                subtitle = "${formatNumber(userViewModel.balance)} VND",
                gradientColors = listOf(Color(0xFF10B981), Color(0xFF34D399)),
                scale = scale,
                onClick = { /* TODO: Navigate to balance */ }
            )

            Spacer(modifier = Modifier.height((12 * scale).dp))

            // Card Lịch sử đơn hàng
            MenuCard(
                icon = R.drawable.notice, // Thay bằng icon history của bạn
                title = "Lịch sử đơn hàng",
                subtitle = "Xem các đơn hàng đã đặt",
                gradientColors = listOf(Color(0xFF3B82F6), Color(0xFF60A5FA)),
                scale = scale,
                onClick = {
                    navController.navigate("HistoryScreen")
                }
            )

            Spacer(modifier = Modifier.height((12 * scale).dp))

            // Card Đổi mật khẩu
            MenuCard(
                icon = R.drawable.notice, // Thay bằng icon lock của bạn
                title = "Đổi mật khẩu",
                subtitle = "Thay đổi mật khẩu của bạn",
                gradientColors = listOf(Color(0xFFF59E0B), Color(0xFFFBBF24)),
                scale = scale,
                onClick = { navController.navigate("ChangePasswordScreen") }
            )

            Spacer(modifier = Modifier.height((12 * scale).dp))

            // Card Đăng xuất
            MenuCard(
                icon = R.drawable.notice, // Thay bằng icon logout của bạn
                title = "Đăng xuất",
                subtitle = "Thoát khỏi tài khoản",
                gradientColors = listOf(Color(0xFFEF4444), Color(0xFFF87171)),
                scale = scale,
                onClick = {
                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                        PersistentCookieJar(context).logout()
                        userStore.clearUser()
                        withContext(kotlinx.coroutines.Dispatchers.Main) {
                            navController.navigate("LoginScreen")
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height((20 * scale).dp))
        }
    }
}

@Composable
fun ProfileInfoCard(scale: Float) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = (4 * scale).dp,
                shape = RoundedCornerShape((20 * scale).dp),
                spotColor = Color(0xFF3B82F6).copy(alpha = 0.15f)
            ),
        shape = RoundedCornerShape((20 * scale).dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Gradient background nhẹ nhàng
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((160 * scale).dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFDEEBFF),
                                Color(0xFFEFF6FF)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding((20 * scale).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size((80 * scale).dp)
                        .shadow(
                            elevation = (6 * scale).dp,
                            shape = CircleShape,
                            spotColor = Color(0xFF3B82F6).copy(alpha = 0.3f)
                        )
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF3B82F6),
                                    Color(0xFF60A5FA)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "👤",
                        fontSize = (40 * scale).sp
                    )
                }

                Spacer(modifier = Modifier.height((12 * scale).dp))

                Text(
                    text = fullName,
                    fontSize = (20 * scale).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height((4 * scale).dp))

            }

            // Accent corner decoration
            Box(
                modifier = Modifier
                    .size((60 * scale).dp)
                    .align(Alignment.TopEnd)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF3B82F6).copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}

@Composable
fun MenuCard(
    icon: Int,
    title: String,
    subtitle: String,
    gradientColors: List<Color>,
    scale: Float,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = (4 * scale).dp,
                shape = RoundedCornerShape((16 * scale).dp),
                spotColor = gradientColors[0].copy(alpha = 0.2f)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape((16 * scale).dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding((16 * scale).dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon container với gradient
                Box(
                    modifier = Modifier
                        .size((56 * scale).dp)
                        .shadow(
                            elevation = (4 * scale).dp,
                            shape = RoundedCornerShape((14 * scale).dp),
                            spotColor = gradientColors[0].copy(alpha = 0.3f)
                        )
                        .clip(RoundedCornerShape((14 * scale).dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = gradientColors
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        modifier = Modifier.size((28 * scale).dp),
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                }

                Spacer(modifier = Modifier.width((16 * scale).dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        fontSize = (16 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height((4 * scale).dp))

                    Text(
                        text = subtitle,
                        fontSize = (13 * scale).sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF64748B)
                    )
                }

                // Arrow icon
                Box(
                    modifier = Modifier
                        .size((32 * scale).dp)
                        .clip(CircleShape)
                        .background(
                            gradientColors[0].copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "→",
                        fontSize = (20 * scale).sp,
                        color = gradientColors[0],
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Accent line
            Box(
                modifier = Modifier
                    .width((4 * scale).dp)
                    .height((56 * scale).dp)
                    .align(Alignment.CenterStart)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = gradientColors
                        ),
                        shape = RoundedCornerShape(
                            topEnd = (8 * scale).dp,
                            bottomEnd = (8 * scale).dp
                        )
                    )
            )
        }
    }
}
