package com.example.dat_lich_kham_fe.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.util.divideAndRound

@Composable
fun setCardPersonal(
    imageVector: ImageVector,
    text: String,
    onClick: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape((12 * divideAndRound(screenWidthValue)).dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation((2 * divideAndRound(screenWidthValue)).dp),
        onClick = {
            isPressed = true
            onClick()
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding((16 * divideAndRound(screenWidthValue)).dp)
        ) {
            // Icon container with gradient background
            Box(
                modifier = Modifier
                    .size((40 * divideAndRound(screenWidthValue)).dp)
                    .background(
                        color = Color(0xFFF3F4F6),
                        shape = RoundedCornerShape((10 * divideAndRound(screenWidthValue)).dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = null,
                    tint = Color(0xFF6B7280),
                    modifier = Modifier.size((20 * divideAndRound(screenWidthValue)).dp)
                )
            }

            Spacer(modifier = Modifier.width((16 * divideAndRound(screenWidthValue)).dp))

            // Text
            Text(
                text = text,
                fontSize = (15 * divideAndRound(screenWidthValue)).sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1F2937),
                modifier = Modifier.weight(1f)
            )

            // Arrow icon
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_arrow_forward_ios_24),
                contentDescription = null,
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size((16 * divideAndRound(screenWidthValue)).dp)
            )
        }
    }

    // Reset animation state after click
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(150)
            isPressed = false
        }
    }
}
