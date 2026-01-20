package com.example.dat_lich_kham_fe.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.ui.component.AppBarView
import com.example.dat_lich_kham_fe.ui.component.BMI
import com.example.dat_lich_kham_fe.ui.component.Huyetap
import com.example.dat_lich_kham_fe.ui.component.Temperature
import com.example.dat_lich_kham_fe.util.divideAndRound

@Composable
fun HealthIndex(navController: NavController) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    val context = LocalContext.current
    var selectedTab1 by remember { mutableStateOf(0) }
    var images by remember { mutableStateOf( R.drawable.healthindex) }
    Column(modifier = Modifier.fillMaxSize()) {
        AppBarView(
            title = stringResource(id = R.string.healthindex_title),
            color = R.color.white,
            backgroundColor = R.color.darkblue,
            alignment = Alignment.TopCenter,
            onDeleteNavClicked = {navController.navigate("MainScreen/3")},
            isVisible = true

        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((100 * divideAndRound(screenWidthValue)).dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = images) ,
                contentDescription = null,
                modifier = Modifier.size((56 * divideAndRound(screenWidthValue)).dp)
            )

        }

        TabRow(
            modifier = Modifier.fillMaxWidth(),
            selectedTabIndex = selectedTab1,
            //backgroundColor = Color.White,
            contentColor = Color(0xFF0856A8),
            divider = {
                Divider(
                    color = Color.LightGray,
                    thickness = (1 * divideAndRound(screenWidthValue)).dp
                )
            },
            indicator = { tabPositions ->
                // Chỉ định thanh chỉ báo cho Tab được chọn
                TabRowDefaults.Indicator(
                    Modifier
                        .tabIndicatorOffset(tabPositions[selectedTab1])
                        .height((3 * divideAndRound(screenWidthValue)).dp),
                    color =  Color(0xFF0856A8)
                )
            }
        ) {
            // tab BMI
            Tab(
                selected = selectedTab1 == 0,
                onClick = { selectedTab1 = 0 },
                modifier = Modifier.weight(1f),

                ){
                Text(
                    text = "BMI",
                    fontSize = (14 * divideAndRound(screenWidthValue)).sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedTab1 == 0) Color(0xFF0856A8) else Color.Gray,
                    modifier = Modifier.padding((8 * divideAndRound(screenWidthValue)).dp)

                )
            }
            // tab huyết áp
            Tab(
                selected = selectedTab1 == 1,
                onClick = { selectedTab1 = 1 },
                modifier = Modifier.weight(1f),

                ){
                Text(
                    text = stringResource(id = R.string.blood_pressure),
                    fontSize = (14 * divideAndRound(screenWidthValue)).sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedTab1 == 1) Color(0xFF0856A8) else Color.Gray,
                    modifier = Modifier.padding((8 * divideAndRound(screenWidthValue)).dp)

                )
            }
            //tab nhiệt độ
            Tab(
                selected = selectedTab1 == 2,
                onClick = { selectedTab1 = 2 },
                modifier = Modifier.weight(1f),

                ){
                Text(
                    text = stringResource(id = R.string.temperature),
                    fontSize = (14 * divideAndRound(screenWidthValue)).sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedTab1 == 2) Color(0xFF0856A8) else Color.Gray,
                    modifier = Modifier.padding((8 * divideAndRound(screenWidthValue)).dp)

                )
            }
        }
        LazyColumn(modifier = Modifier.weight(1f)) {
            item {
                if(selectedTab1 == 0)
                {
                    images = R.drawable.healthindex
                    BMI()
                }
                if(selectedTab1 == 1)
                {
                    images = R.drawable.bloodpressure
                    Huyetap()
                }
                if(selectedTab1 == 2)
                {
                    images = R.drawable.bodytemperature
                    Temperature()
                }

            }
        }
    }
}
