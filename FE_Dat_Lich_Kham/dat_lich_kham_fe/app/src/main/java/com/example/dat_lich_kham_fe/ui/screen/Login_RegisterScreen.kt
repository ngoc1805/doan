package com.example.dat_lich_kham_fe.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.util.divideAndRound
import kotlin.math.roundToInt

@Composable
fun Login_RegisterScreen(navController: NavController) {
    var image by remember { mutableStateOf(R.drawable.login) }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    var offsetY by remember { mutableStateOf(0f) }
    var selectedTab by rememberSaveable { mutableStateOf(0) }
    var arrowdownColor by remember { mutableStateOf(Color.Blue) }
    Box( modifier = Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {

            // Top Image
            Box(
                modifier = Modifier

                    .fillMaxHeight(90/300f)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = image),
                    contentDescription = "Login Image",
                    contentScale = ContentScale.Crop,  // Hoặc ContentScale.FillWidth
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    onClick = { /* todo chuyen huong */
                        navController.navigate("MainScreen"){
                            popUpTo("Login_RegisterScreen"){
                                inclusive = true
                            }
                        }
                    },
                    modifier = Modifier.statusBarsPadding().offset(y = (8 * divideAndRound(screenWidthValue)).dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        tint = Color.White,
                        contentDescription = null,
                        modifier = Modifier.size((28 * divideAndRound(screenWidthValue)).dp),

                        )
                }

            }
            // Car
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(1f)
                    .offset { IntOffset(x = 0, y = offsetY.roundToInt()) },
                contentAlignment = Alignment.TopCenter
            ) {
                // Card for Tabs and Forms
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(), // Card takes full height of this section
//                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = androidx.compose.material3.CardDefaults.cardElevation(8.dp)

                ) {
                    Column(
//                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // TabRow for switching between Login and Sign Up
                        TabRow(
                            selectedTabIndex = selectedTab,
                            //backgroundColor = Color.White,
                            contentColor = Color.Blue,
                            indicator = { tabPositions ->
                                // Chỉ định thanh chỉ báo cho Tab được chọn
                                TabRowDefaults.Indicator(
                                    Modifier
                                        .tabIndicatorOffset(tabPositions[selectedTab])
                                        .height((2 * divideAndRound(screenWidthValue)).dp) // Không cần chỉ báo hiển thị trên ảnh mẫu
                                )
                            }
                        ) {
                            Tab(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                text = { Text(stringResource(id = R.string.login),
                                    fontSize = (18 * divideAndRound(screenWidthValue)).sp,
                                    color = colorResource(id = R.color.darkblue),
                                    fontWeight = FontWeight.Bold
                                ) }
                            )
                            Tab(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                text = { Text(
                                    stringResource(id = R.string.register),
                                    fontSize = (18 * divideAndRound(screenWidthValue)).sp,
                                    color = colorResource(id = R.color.darkblue),
                                    fontWeight = FontWeight.Bold
                                ) }
                            )
                        }

                        Spacer(modifier = Modifier.height((16 * divideAndRound(screenWidthValue)).dp))

                        // Show Login or Sign Up form based on selected tab
                        if (selectedTab == 0) {
                            // Show Login Form
                            image = R.drawable.login
                            arrowdownColor = colorResource(id = R.color.darkblue)
                            LoginScreen(navController)
                        } else {
                            // Show Sign Up Form
                            image = R.drawable.register
                            arrowdownColor = colorResource(id = R.color.black)
                            RegisterScreen(
                                navController = navController,
                                selectedTab = selectedTab,
                                setSelectedTab = { selectedTab = it }
                            )
                        }
                    }
                }

            }
        }
    }
}


