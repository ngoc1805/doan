package com.example.nhom1_fe_datnhahang.ui.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.nhom1_fe_datnhahang.R
import com.example.nhom1_fe_datnhahang.util.divideAndRound
import com.example.nhom1_fe_datnhahang.util.removeMilliseconds
import com.example.nhom1_fe_datnhahang.util.soSanhThoiGian

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationCard(
    id: Int,
    content: String,
    isSeen: Boolean = false,
    createdAt: String,
    path: String,
    onClicked: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    val scale = divideAndRound(screenWidthValue)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (!isSeen) (6 * scale).dp else (2 * scale).dp,
                shape = RoundedCornerShape((16 * scale).dp),
                spotColor = Color(0xFFFF6B35).copy(alpha = 0.15f)
            )
            .clickable { onClicked() },
        shape = RoundedCornerShape((16 * scale).dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Gradient overlay cho unseen notifications
            if (!isSeen) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .matchParentSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFFFE5D9),
                                    Color(0xFFFFF5ED)
                                )
                            ),
                            shape = RoundedCornerShape((16 * scale).dp)
                        )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding((16 * scale).dp),
                verticalAlignment = Alignment.Top
            ) {
                // Icon container với gradient background
                Box(
                    modifier = Modifier
                        .size((52 * scale).dp)
                        .shadow(
                            elevation = (4 * scale).dp,
                            shape = CircleShape,
                            spotColor = Color(0xFFFF6B35).copy(alpha = 0.3f)
                        )
                        .clip(CircleShape)
                        .background(
                            brush = if (!isSeen) {
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFFF6B35),
                                        Color(0xFFFF8C42)
                                    )
                                )
                            } else {
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFFFE5D9),
                                        Color(0xFFFFF0E6)
                                    )
                                )
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.notice),
                        contentDescription = null,
                        modifier = Modifier.size((26 * scale).dp),
                        colorFilter = ColorFilter.tint(
                            if (!isSeen) Color.White else Color(0xFFFF6B35)
                        )
                    )
                }

                Spacer(modifier = Modifier.width((14 * scale).dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Badge "Mới" cho thông báo chưa đọc
                    if (!isSeen) {
                        Box(
                            modifier = Modifier
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFFFF6B35),
                                            Color(0xFFFF8C42)
                                        )
                                    ),
                                    shape = RoundedCornerShape((8 * scale).dp)
                                )
                                .padding(
                                    horizontal = (8 * scale).dp,
                                    vertical = (4 * scale).dp
                                )
                        ) {
                            Text(
                                text = "Mới",
                                fontSize = (11 * scale).sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height((8 * scale).dp))
                    }

                    Text(
                        text = content,
                        fontSize = (15 * scale).sp,
                        fontWeight = if (!isSeen) FontWeight.SemiBold else FontWeight.Normal,
                        color = Color(0xFF2D3748),
                        maxLines = 3,
                        lineHeight = (22 * scale).sp
                    )

                    Spacer(modifier = Modifier.height((8 * scale).dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Clock icon
                        Image(
                            painter = painterResource(id = R.drawable.notice),
                            contentDescription = null,
                            modifier = Modifier.size((14 * scale).dp),
                            colorFilter = ColorFilter.tint(
                                Color(0xFF718096)
                            )
                        )

                        Spacer(modifier = Modifier.width((6 * scale).dp))

                        Text(
                            text = soSanhThoiGian(removeMilliseconds(createdAt)),
                            fontSize = (13 * scale).sp,
                            color = Color(0xFF718096),
                            fontWeight = FontWeight.Normal
                        )

                        // Unread indicator dot
                        if (!isSeen) {
                            Spacer(modifier = Modifier.width((10 * scale).dp))
                            Box(
                                modifier = Modifier
                                    .size((8 * scale).dp)
                                    .shadow(
                                        elevation = (2 * scale).dp,
                                        shape = CircleShape,
                                        spotColor = Color(0xFFFF6B35).copy(alpha = 0.5f)
                                    )
                                    .clip(CircleShape)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                Color(0xFFFF6B35),
                                                Color(0xFFFF8C42)
                                            )
                                        )
                                    )
                            )
                        }
                    }
                }
            }

            // Accent line cho thông báo chưa đọc
            if (!isSeen) {
                Box(
                    modifier = Modifier
                        .width((4 * scale).dp)
                        .height((70 * scale).dp)
                        .align(Alignment.CenterStart)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFFF6B35),
                                    Color(0xFFFF8C42)
                                )
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
}
