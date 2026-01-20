//package com.example.dat_lich_kham_fe.ui.component
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material3.Icon
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.BiasAlignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalConfiguration
//import androidx.compose.ui.res.colorResource
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.dat_lich_kham_fe.R
//import com.example.dat_lich_kham_fe.util.divideAndRound
//
//@Composable
//fun BottomBarItem(
//    iconRes: Int,
//    label: String,
//    selected: Boolean,
//    showDot: Boolean = true,
//    onClick: () -> Unit,
//
//) {
//    val configuration = LocalConfiguration.current
//    val screenWidth = configuration.screenWidthDp.dp
//    val screenWidthValue = screenWidth.value
//    Box(
//        Modifier.wrapContentSize()
//    ) {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            modifier = Modifier
//                .clickable { onClick() }
//                .padding(
//                    vertical = (4 * divideAndRound(screenWidthValue)).dp,
//                    horizontal = (8 * divideAndRound(screenWidthValue)).dp)
//        ) {
//            Box(
//                modifier = Modifier
//                    .size((32 * divideAndRound(screenWidthValue)).dp)
//                    .background(
//                        if (selected) colorResource(id = R.color.darkblue) else Color.Transparent,
//                        shape = CircleShape
//                    ),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    painter = painterResource(id = iconRes),
//                    contentDescription = label,
//                    tint = if (selected) Color.White else Color.Gray,
//                    modifier = Modifier.size((20 * divideAndRound(screenWidthValue)).dp)
//                )
//            }
//            Spacer(modifier = Modifier.height((2 * divideAndRound(screenWidthValue)).dp))
//            Text(
//                text = label,
//                fontSize = (12 * divideAndRound(screenWidthValue)).sp,
//                color = if (selected) colorResource(id = R.color.darkblue) else Color.Gray
//            )
//            Spacer(modifier = Modifier.height((2 * divideAndRound(screenWidthValue)).dp))
//        }
//        // Chấm đỏ góc trên phải
//        if (!showDot) {
//            Box(
//                modifier = Modifier
//                    .size((10 * divideAndRound(screenWidthValue)).dp)
//                    .background(Color.Red, CircleShape)
//                    .align(BiasAlignment(0.6f, -0.8f))
//            )
//        }
//    }
//}

package com.example.dat_lich_kham_fe.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.util.divideAndRound

@Composable
fun BottomBarItem(
    iconRes: Int,
    label: String,
    selected: Boolean,
    showDot: Boolean = true,
    onClick: () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    val scale = divideAndRound(screenWidthValue)

    // Smooth animations
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.15f else 1f,
        animationSpec = tween(durationMillis = 300)
    )

    val iconColor by animateColorAsState(
        targetValue = if (selected) Color.White else Color(0xFF9E9E9E),
        animationSpec = tween(durationMillis = 300)
    )

    val textColor by animateColorAsState(
        targetValue = if (selected) {
            colorResource(id = R.color.darkblue)
        } else {
            Color(0xFF757575)
        },
        animationSpec = tween(durationMillis = 300)
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (selected) {
            colorResource(id = R.color.darkblue)
        } else {
            Color.Transparent
        },
        animationSpec = tween(durationMillis = 300)
    )

    Box(
        modifier = Modifier.wrapContentSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape((16 * scale).dp))
                .clickable(
                    onClick = onClick,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
                .background(
                    if (selected) {
                        Brush.verticalGradient(
                            colors = listOf(
                                colorResource(id = R.color.darkblue).copy(alpha = 0.08f),
                                colorResource(id = R.color.darkblue).copy(alpha = 0.05f)
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Transparent)
                        )
                    }
                )
                .padding(
                    vertical = (6 * scale).dp,
                    horizontal = (12 * scale).dp
                )
        ) {
            // Icon container
            Box(
                modifier = Modifier
                    .size((42 * scale).dp)
                    .scale(iconScale),
                contentAlignment = Alignment.Center
            ) {
                // Background circle với gradient khi selected
                if (selected) {
                    Box(
                        modifier = Modifier
                            .size((38 * scale).dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        colorResource(id = R.color.darkblue),
                                        colorResource(id = R.color.darkblue).copy(alpha = 0.9f)
                                    )
                                )
                            )
                    )
                } else {
                    // Subtle background khi không selected
                    Box(
                        modifier = Modifier
                            .size((38 * scale).dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF5F5F5))
                    )
                }

                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = label,
                    tint = iconColor,
                    modifier = Modifier.size((22 * scale).dp)
                )
            }

            Spacer(modifier = Modifier.height((4 * scale).dp))

            Text(
                text = label,
                fontSize = (11 * scale).sp,
                color = textColor,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1
            )

            // Indicator dot dưới text
            if (selected) {
                Spacer(modifier = Modifier.height((3 * scale).dp))
                Box(
                    modifier = Modifier
                        .width((4 * scale).dp)
                        .height((4 * scale).dp)
                        .clip(CircleShape)
                        .background(colorResource(id = R.color.darkblue))
                )
            }
        }

        // Notification dot ở góc trên phải
        if (!showDot) {
            Box(
                modifier = Modifier
                    .size((10 * scale).dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF5252))
                    .align(BiasAlignment(0.7f, -0.9f))
            ) {
                // Pulse effect border
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFF5252),
                                    Color(0xFFFF5252).copy(alpha = 0.5f)
                                )
                            )
                        )
                )
            }
        }
    }
}

// Thay thế trong MainScreen.kt - phần Surface
/*
Surface(
    modifier = Modifier.fillMaxWidth(),
    color = Color.White,
    shadowElevation = (12 * divideAndRound(screenWidthValue)).dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White,
                        Color(0xFFFAFAFA)
                    )
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height((72 * divideAndRound(screenWidthValue)).dp)
                .padding(
                    horizontal = (4 * divideAndRound(screenWidthValue)).dp,
                    vertical = (6 * divideAndRound(screenWidthValue)).dp
                ),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomBarItem(R.drawable.home, stringResource(R.string.home_title), selectedTab == 0, true) { selectedTab = 0 }
            BottomBarItem(R.drawable.appointment, stringResource(id = R.string.appointment_schedule_title), selectedTab == 1, true) { selectedTab = 1 }
            BottomBarItem(R.drawable.nofitication, stringResource(R.string.notification_title), selectedTab == 2, isDisplay) { selectedTab = 2 }
            BottomBarItem(R.drawable.person, stringResource(R.string.individual_title), selectedTab == 3, true) { selectedTab = 3 }
        }
    }
}
*/
