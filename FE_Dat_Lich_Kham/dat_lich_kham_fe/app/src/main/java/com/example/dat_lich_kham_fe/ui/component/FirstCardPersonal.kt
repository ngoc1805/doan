package com.example.dat_lich_kham_fe.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.util.UserLocalStore
import com.example.dat_lich_kham_fe.util.divideAndRound
import com.example.dat_lich_kham_fe.util.PersistentCookieJar

@Composable
fun FirstCardPersonal(navController: NavController){
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    val context = LocalContext.current

    val userStore = UserLocalStore(context)

    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        fullName = userStore.getFullName().toString()
        phone = PersistentCookieJar(context).getUsername().toString()
    }

    // Main profile card with gradient
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow((8 * divideAndRound(screenWidthValue)).dp, RoundedCornerShape((20 * divideAndRound(screenWidthValue)).dp)),
        shape = RoundedCornerShape((20 * divideAndRound(screenWidthValue)).dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box {
            // Gradient background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((120 * divideAndRound(screenWidthValue)).dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF3B82F6),
                                Color(0xFF1D4ED8)
                            )
                        ),
                        shape = RoundedCornerShape(
                            topStart = (20 * divideAndRound(screenWidthValue)).dp,
                            topEnd = (20 * divideAndRound(screenWidthValue)).dp
                        )
                    )
            )

            Column(
                modifier = Modifier.padding((20 * divideAndRound(screenWidthValue)).dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar with border and shadow
                    Box(
                        modifier = Modifier
                            .size((80 * divideAndRound(screenWidthValue)).dp)
                            .shadow((8 * divideAndRound(screenWidthValue)).dp, CircleShape)
                            .background(Color.White, CircleShape)
                            .border(
                                (3 * divideAndRound(screenWidthValue)).dp,
                                Color.White,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.anhtrang),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size((70 * divideAndRound(screenWidthValue)).dp)
                                .clip(CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width((16 * divideAndRound(screenWidthValue)).dp))

                    // User info
                    Column {
                        Text(
                            text = fullName.ifEmpty { "Tên người dùng" },
                            fontSize = (20 * divideAndRound(screenWidthValue)).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height((4 * divideAndRound(screenWidthValue)).dp))

                        // Phone number chip
                        Surface(
                            shape = RoundedCornerShape((20 * divideAndRound(screenWidthValue)).dp),
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.padding(top = (4 * divideAndRound(screenWidthValue)).dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(
                                    horizontal = (12 * divideAndRound(screenWidthValue)).dp,
                                    vertical = (6 * divideAndRound(screenWidthValue)).dp
                                )
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.phone),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size((16 * divideAndRound(screenWidthValue)).dp)
                                )
                                Spacer(modifier = Modifier.width((8 * divideAndRound(screenWidthValue)).dp))
                                Text(
                                    text = phone.ifEmpty { "Chưa có số điện thoại" },
                                    fontSize = (14 * divideAndRound(screenWidthValue)).sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height((20 * divideAndRound(screenWidthValue)).dp))

                // Status indicator
                Surface(
                    shape = RoundedCornerShape((12 * divideAndRound(screenWidthValue)).dp),
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding((16 * divideAndRound(screenWidthValue)).dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size((8 * divideAndRound(screenWidthValue)).dp)
                                .background(Color(0xFF10B981), CircleShape)
                        )
                        Spacer(modifier = Modifier.width((12 * divideAndRound(screenWidthValue)).dp))
                        Text(
                            text = stringResource(id = R.string.verified),
                            fontSize = (14 * divideAndRound(screenWidthValue)).sp,
                            color = Color(0xFF374151),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
