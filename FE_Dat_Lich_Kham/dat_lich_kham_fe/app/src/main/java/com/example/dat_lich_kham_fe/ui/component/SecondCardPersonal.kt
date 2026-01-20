package com.example.dat_lich_kham_fe.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.util.divideAndRound

@Composable
fun SecondCardPersonal(navController: NavController){
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow((6 * divideAndRound(screenWidthValue)).dp, RoundedCornerShape((16 * divideAndRound(screenWidthValue)).dp)),
        shape = RoundedCornerShape((16 * divideAndRound(screenWidthValue)).dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding((20 * divideAndRound(screenWidthValue)).dp)
        ) {
            Text(
                text = "Tiện ích",
                fontSize = (18 * divideAndRound(screenWidthValue)).sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                modifier = Modifier.padding(bottom = (16 * divideAndRound(screenWidthValue)).dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy((12 * divideAndRound(screenWidthValue)).dp)
            ) {
                // Health Record Button
                UtilityButton(
                    modifier = Modifier.weight(1f),
                    iconRes = R.drawable.healthrecord,
                    title = stringResource(id = R.string.purchase_history),
                    backgroundColor = Color(0xFFEFF6FF),
                    iconColor = Color(0xFF3B82F6),
                    screenWidthValue = screenWidthValue,
                    onClick = {
                        navController.navigate("OrderHistoryScreen")
                    }
                )

                // History Button
                UtilityButton(
                    modifier = Modifier.weight(1f),
                    iconRes = R.drawable.lichsudatkham,
                    title = stringResource(id = R.string.history_title),
                    backgroundColor = Color(0xFFF0FDF4),
                    iconColor = Color(0xFF10B981),
                    screenWidthValue = screenWidthValue,
                    onClick = { navController.navigate("HistoryScreen") }
                )

                // Health Index Button
                UtilityButton(
                    modifier = Modifier.weight(1f),
                    iconRes = R.drawable.theodoichiso,
                    title = stringResource(id = R.string.monitor_the_index),
                    backgroundColor = Color(0xFFFEF3C7),
                    iconColor = Color(0xFFF59E0B),
                    screenWidthValue = screenWidthValue,
                    onClick = { navController.navigate("HealthIndex") }
                )
            }
        }
    }
}

@Composable
private fun UtilityButton(
    modifier: Modifier = Modifier,
    iconRes: Int,
    title: String,
    backgroundColor: Color,
    iconColor: Color,
    screenWidthValue: Float,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height((120 * divideAndRound(screenWidthValue)).dp)
            .clip(RoundedCornerShape((12 * divideAndRound(screenWidthValue)).dp))
            .clickable { onClick() },
        shape = RoundedCornerShape((12 * divideAndRound(screenWidthValue)).dp),
        color = backgroundColor
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding((16 * divideAndRound(screenWidthValue)).dp)
        ) {
            Box(
                modifier = Modifier
                    .size((48 * divideAndRound(screenWidthValue)).dp)
                    .background(Color.White, RoundedCornerShape((12 * divideAndRound(screenWidthValue)).dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size((24 * divideAndRound(screenWidthValue)).dp)
                )
            }

            Spacer(modifier = Modifier.height((12 * divideAndRound(screenWidthValue)).dp))

            Text(
                text = title,
                fontSize = (12 * divideAndRound(screenWidthValue)).sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF374151),
                maxLines = 2,
                lineHeight = (14 * divideAndRound(screenWidthValue)).sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
