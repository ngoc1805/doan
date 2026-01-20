package com.example.dat_lich_kham_fe.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.util.divideAndRound
import com.example.dat_lich_kham_fe.util.formatNumber

@Composable
fun DoctorCard(id: Int, name: String, code: String, examPrice: Int, department: String, onClicked: () -> Unit = {} ) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = (12 * divideAndRound(screenWidthValue)).dp,
                vertical = (6 * divideAndRound(screenWidthValue)).dp
            )
            .clickable { onClicked() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = (8 * divideAndRound(screenWidthValue)).dp,
            pressedElevation = (4 * divideAndRound(screenWidthValue)).dp
        ),
        shape = RoundedCornerShape((16 * divideAndRound(screenWidthValue)).dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            Color(0xFFF8FAFB)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .padding((20 * divideAndRound(screenWidthValue)).dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar container với gradient background
                Box(
                    modifier = Modifier
                        .size((64 * divideAndRound(screenWidthValue)).dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    colorResource(id = R.color.darkblue).copy(alpha = 0.1f),
                                    colorResource(id = R.color.darkblue).copy(alpha = 0.2f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_person_24),
                        contentDescription = null,
                        modifier = Modifier.size((36 * divideAndRound(screenWidthValue)).dp),
                        colorFilter = ColorFilter.tint(colorResource(id = R.color.darkblue))
                    )
                }

                Spacer(modifier = Modifier.width((20 * divideAndRound(screenWidthValue)).dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Doctor name với styling cải thiện
                    Text(
                        text = name,
                        fontSize = (18 * divideAndRound(screenWidthValue)).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A),
                        lineHeight = (22 * divideAndRound(screenWidthValue)).sp
                    )

                    Spacer(modifier = Modifier.height((6 * divideAndRound(screenWidthValue)).dp))

                    // Doctor code
                    Text(
                        text = "Mã BS: $code",
                        fontSize = (12 * divideAndRound(screenWidthValue)).sp,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height((10 * divideAndRound(screenWidthValue)).dp))

                    // Department với background subtle
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color(0xFFF0F4F8),
                                shape = RoundedCornerShape((8 * divideAndRound(screenWidthValue)).dp)
                            )
                            .padding(
                                horizontal = (12 * divideAndRound(screenWidthValue)).dp,
                                vertical = (6 * divideAndRound(screenWidthValue)).dp
                            )
                    ) {
                        Text(
                            text = "${stringResource(id = R.string.department)}: $department",
                            fontSize = (13 * divideAndRound(screenWidthValue)).sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height((12 * divideAndRound(screenWidthValue)).dp))

                    // Price section với icon và styling cải thiện
                    Box(
                        modifier = Modifier
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        colorResource(id = R.color.darkblue).copy(alpha = 0.1f),
                                        colorResource(id = R.color.darkblue).copy(alpha = 0.05f)
                                    )
                                ),
                                shape = RoundedCornerShape((10 * divideAndRound(screenWidthValue)).dp)
                            )
                            .padding(
                                horizontal = (14 * divideAndRound(screenWidthValue)).dp,
                                vertical = (8 * divideAndRound(screenWidthValue)).dp
                            )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size((24 * divideAndRound(screenWidthValue)).dp)
                                    .clip(CircleShape)
                                    .background(colorResource(id = R.color.darkblue).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.coin),
                                    contentDescription = null,
                                    modifier = Modifier.size((14 * divideAndRound(screenWidthValue)).dp),
                                    colorFilter = ColorFilter.tint(colorResource(id = R.color.darkblue))
                                )
                            }

                            Spacer(modifier = Modifier.width((10 * divideAndRound(screenWidthValue)).dp))

                            Text(
                                text = "${stringResource(id = R.string.exam_price)}: ${formatNumber(examPrice)} VND",
                                fontSize = (13 * divideAndRound(screenWidthValue)).sp,
                                fontWeight = FontWeight.Bold,
                                color = colorResource(id = R.color.darkblue)
                            )
                        }
                    }
                }
            }

            // Decorative accent line
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size((40 * divideAndRound(screenWidthValue)).dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                colorResource(id = R.color.darkblue).copy(alpha = 0.1f),
                                Color.Transparent
                            ),
                            radius = (20 * divideAndRound(screenWidthValue)).toFloat()
                        )
                    )
            )
        }
    }
}
