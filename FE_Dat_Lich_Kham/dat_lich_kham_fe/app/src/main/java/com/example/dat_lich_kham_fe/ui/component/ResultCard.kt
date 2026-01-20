package com.example.dat_lich_kham_fe.ui.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.data.model.ResultFileItem
import com.example.dat_lich_kham_fe.util.chuyenDoiNgay
import com.example.dat_lich_kham_fe.util.divideAndRound
import com.example.dat_lich_kham_fe.util.formatNumber

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ResultCard(
    appointmentId: Int,
    fullName: String,
    comment: String,
    resultFiles : List<ResultFileItem>,
    examDate: String,
    onClicked: () -> Unit = {},
) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    val scale = divideAndRound(screenWidthValue)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = (8 * scale).dp)
            .shadow((4 * scale).dp, RoundedCornerShape((16 * scale).dp))
            .clickable { onClicked() },
        shape = RoundedCornerShape((16 * scale).dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding((20 * scale).dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Container with Background
            Box(
                modifier = Modifier
                    .size((60 * scale).dp)
                    .background(
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape((16 * scale).dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.medicalexaminationresults),
                    contentDescription = null,
                    tint = Color(0xFF3B82F6),
                    modifier = Modifier.size((32 * scale).dp)
                )
            }

            Spacer(modifier = Modifier.width((16 * scale).dp))

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${stringResource(id = R.string.results_of)} $fullName",
                    fontSize = (16 * scale).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )

                Spacer(modifier = Modifier.height((6 * scale).dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_keyboard_arrow_down_24),
                        contentDescription = null,
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size((16 * scale).dp)
                    )
                    Spacer(modifier = Modifier.width((4 * scale).dp))
                    Text(
                        text = "Ngày ${chuyenDoiNgay(examDate)}",
                        fontSize = (14 * scale).sp,
                        color = Color(0xFF6B7280),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height((8 * scale).dp))

                // Additional Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        shape = RoundedCornerShape((8 * scale).dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4))
                    ) {
                        Text(
                            text = "ID: #$appointmentId",
                            fontSize = (12 * scale).sp,
                            color = Color(0xFF059669),
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(
                                horizontal = (8 * scale).dp,
                                vertical = (4 * scale).dp
                            )
                        )
                    }

                    if (resultFiles.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.medicalexaminationresults),
                                contentDescription = null,
                                tint = Color(0xFF8B5CF6),
                                modifier = Modifier.size((16 * scale).dp)
                            )
                            Spacer(modifier = Modifier.width((4 * scale).dp))
                            Text(
                                text = "${resultFiles.size} tệp",
                                fontSize = (12 * scale).sp,
                                color = Color(0xFF8B5CF6),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width((12 * scale).dp))

            // Arrow Icon
            Box(
                modifier = Modifier
                    .size((32 * scale).dp)
                    .background(
                        color = Color(0xFFF3F4F6),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_keyboard_arrow_down_24),
                    contentDescription = null,
                    tint = Color(0xFF6B7280),
                    modifier = Modifier.size((20 * scale).dp)
                )
            }
        }
    }
}
