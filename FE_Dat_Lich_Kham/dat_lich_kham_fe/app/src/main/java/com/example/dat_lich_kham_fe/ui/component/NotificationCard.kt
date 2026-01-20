package com.example.dat_lich_kham_fe.ui.component

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
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.util.divideAndRound
import com.example.dat_lich_kham_fe.util.removeMilliseconds
import com.example.dat_lich_kham_fe.util.soSanhThoiGian

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

    val color = if(isSeen){
        colorResource(id = R.color.black)
    } else {
        colorResource(id = R.color.white)
    }
    val backGround = if(isSeen){
        colorResource(id = R.color.white)
    } else {
        colorResource(id = R.color.darkblue)
    }
    val colorIcon = if(isSeen){
        colorResource(id = R.color.darkblue)
    } else {
        colorResource(id = R.color.white)
    }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(backGround)
                .clickable { onClicked() }
        ) {
            // Gradient overlay cho unseen notifications
            if (!isSeen) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((80 * scale).dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    colorResource(id = R.color.darkblue),
                                    colorResource(id = R.color.darkblue).copy(alpha = 0.95f)
                                )
                            )
                        )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = (16 * scale).dp,
                        vertical = (14 * scale).dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon container với circle background
                Box(
                    modifier = Modifier
                        .size((44 * scale).dp)
                        .clip(CircleShape)
                        .background(
                            if (isSeen) {
                                colorResource(id = R.color.darkblue).copy(alpha = 0.1f)
                            } else {
                                Color.White.copy(alpha = 0.15f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.notice),
                        contentDescription = null,
                        modifier = Modifier.size((24 * scale).dp),
                        colorFilter = ColorFilter.tint(colorIcon)
                    )
                }

                Spacer(modifier = Modifier.width((14 * scale).dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = content,
                        fontSize = (14 * scale).sp,
                        fontWeight = if (!isSeen) FontWeight.Bold else FontWeight.Normal,
                        color = color,
                        maxLines = 2,
                        lineHeight = (20 * scale).sp
                    )

                    Spacer(modifier = Modifier.height((4 * scale).dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Unread indicator dot
                        if (!isSeen) {
                            Box(
                                modifier = Modifier
                                    .size((6 * scale).dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                            Spacer(modifier = Modifier.width((6 * scale).dp))
                        }

                        Text(
                            text = soSanhThoiGian(removeMilliseconds(createdAt)),
                            fontSize = (12 * scale).sp,
                            color = color.copy(alpha = if (isSeen) 0.6f else 0.8f),
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Divider giữa các notifications
        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = (0.5 * scale).dp,
            color = Color(0xFFE0E0E0)
        )
    }
}
