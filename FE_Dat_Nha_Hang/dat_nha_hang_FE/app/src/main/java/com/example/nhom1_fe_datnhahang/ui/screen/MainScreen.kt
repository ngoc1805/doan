package com.example.nhom1_fe_datnhahang.ui.screen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nhom1_fe_datnhahang.R
import com.example.nhom1_fe_datnhahang.ui.component.BottomBarItem
import com.example.nhom1_fe_datnhahang.util.PersistentCookieJar
import com.example.nhom1_fe_datnhahang.util.UserLocalStore
import com.example.nhom1_fe_datnhahang.util.balance
import com.example.nhom1_fe_datnhahang.util.birthDate
import com.example.nhom1_fe_datnhahang.util.cccd
import com.example.nhom1_fe_datnhahang.util.divideAndRound
import com.example.nhom1_fe_datnhahang.util.fullName
import com.example.nhom1_fe_datnhahang.util.gender
import com.example.nhom1_fe_datnhahang.util.imageUrl
import com.example.nhom1_fe_datnhahang.util.userId
import com.example.nhom1_fe_datnhahang.viewmodel.AccountViewModel

import com.example.nhom1_fe_datnhahang.viewmodel.QrResultViewModel
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.tasks.await

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    navController: NavController,
    initialTab: Int = 0
) {
    var selectedTab by rememberSaveable { mutableStateOf(initialTab) }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    val context = LocalContext.current

    var accountViewModel = remember { AccountViewModel(context) }

    val userStore = UserLocalStore(context)
    LaunchedEffect(Unit) {
        val user = userStore.getUser()
        fullName = userStore.getFullName() ?: ""
        balance = userStore.getBalance() ?: 0
        userId = user?.Id ?: 0
        imageUrl = user?.imageUrl ?: ""
        birthDate = user?.birthDate ?: ""
        gender = user?.gender ?: ""
        cccd = user?.cccd ?: ""
    }
    LaunchedEffect(Unit) {
        val accountId = PersistentCookieJar(context).getaccountId()?.toIntOrNull() ?: 0
        val FcmToken = Firebase.messaging.token.await()
        accountViewModel.updateFcmToken(accountId, FcmToken)
        Log.d("fmctoken","$FcmToken")
    }



    Column (modifier = Modifier.fillMaxSize()) {

        //
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (selectedTab) {
                0 -> HomeScreen(navController)
                1 -> OrderScreen(navController)
                2 -> NotificationScreen(navController)
                3 -> AccountScreen(navController)
            }
        }

        //
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((70 * divideAndRound(screenWidthValue)).dp)
                //.align(Alignment.BottomCenter)
        ) {
            // Thanh tab chính
            Row(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.White)
                    .height((80 * divideAndRound(screenWidthValue)).dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomBarItem(R.drawable.home, "Trang chủ", selectedTab == 0) { selectedTab = 0 }
                BottomBarItem(R.drawable.booking, "Đơn hàng", selectedTab == 1) { selectedTab = 1 }
                BottomBarItem(R.drawable.nofitication, "Thông báo", selectedTab == 2) { selectedTab = 2 }
                BottomBarItem(R.drawable.person, "Tài khoản", selectedTab == 3) { selectedTab = 3 }
            }
        }
    }
}
