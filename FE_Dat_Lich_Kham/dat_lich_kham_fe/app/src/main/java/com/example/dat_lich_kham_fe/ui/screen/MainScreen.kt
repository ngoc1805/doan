package com.example.dat_lich_kham_fe.ui.screen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.ui.component.AppBarView
import com.example.dat_lich_kham_fe.ui.component.BottomBarItem
import com.example.dat_lich_kham_fe.util.PersistentCookieJar
import com.example.dat_lich_kham_fe.util.UserLocalStore
import com.example.dat_lich_kham_fe.util.balance
import com.example.dat_lich_kham_fe.util.birthDate
import com.example.dat_lich_kham_fe.util.cccd
import com.example.dat_lich_kham_fe.util.divideAndRound
import com.example.dat_lich_kham_fe.util.fcmToken
import com.example.dat_lich_kham_fe.util.fullName
import com.example.dat_lich_kham_fe.util.gender
import com.example.dat_lich_kham_fe.util.imageUrl
import com.example.dat_lich_kham_fe.util.userId
import com.example.dat_lich_kham_fe.viewmodel.AccountViewModel
import com.example.dat_lich_kham_fe.viewmodel.LoginViewModel
import com.example.dat_lich_kham_fe.viewmodel.NotificationViewModel
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.tasks.await

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    navController: NavController,
    initialTab: Int = 0
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    var selectedTab by rememberSaveable { mutableStateOf(initialTab) }
    val context = LocalContext.current

    var color by remember { mutableStateOf(R.color.black) }
    var appbarBackgroundColor by remember { mutableStateOf(R.color.white) }
    var titles by remember{ mutableStateOf(" Xin chào") }
    var alignment by remember { mutableStateOf(Alignment.TopStart) }
    var isVisible by rememberSaveable { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(true) }
    //val hasnoitice by thongBaoViewModel.hasNotification.observeAsState()


    var tokenExists by remember { mutableStateOf<Boolean?>(null) }
    var accountId by remember { mutableStateOf(0) }

    val userStore = UserLocalStore(context)

    var accountViewModel = remember { AccountViewModel(context) }
    var notificationViewModel = remember { NotificationViewModel(context) }

    LaunchedEffect(Unit) {
        val user = userStore.getUser()
        userId = user?.Id ?: 0
        notificationViewModel.checkAllNotificationsReceived(userId)
    }
    var isDisplay = notificationViewModel.allNotificationsReceived ?: true

    LaunchedEffect(Unit) {
        tokenExists = PersistentCookieJar(context).hasToken()
    }

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
        accountId = PersistentCookieJar(context).getAccountId()?.toIntOrNull() ?: 0
        fcmToken = Firebase.messaging.token.await()
        accountViewModel.updateFcmToken(accountId, fcmToken)
        Log.d("fmctoken","$fcmToken")
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppBarView(
            title = titles,
            color = color,
            backgroundColor = appbarBackgroundColor,
            alignment = alignment,
            isVisible = isVisible
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
        ) {
            when (selectedTab) {
                0 -> {
                    HomeScreen(navController)
                    color = R.color.black
                    appbarBackgroundColor = R.color.white
                    titles = " ${stringResource(id = R.string.hello_title)} $fullName"
                    alignment = Alignment.TopStart
                    isVisible = true
                }
                1 -> {
                    if(tokenExists == true){
                        ListAppointmentScreen(navController)
                        color = R.color.white
                        appbarBackgroundColor = R.color.darkblue
                        titles = stringResource(id = R.string.appointment_schedule_title)
                        alignment = Alignment.TopCenter
                        isVisible = true
                    }
                    else {
                        Login_RegisterScreen(navController)
                        isVisible = false
                    }
                }
                2 -> {
                    if(tokenExists == true){
                        NotificationScreen(navController)
                        color = R.color.white
                        appbarBackgroundColor = R.color.darkblue
                        titles = stringResource(R.string.notification_title)
                        alignment = Alignment.BottomCenter
                        isVisible = true
                        isDisplay = true
                    }
                    else {
                        Login_RegisterScreen(navController)
                        isVisible = false
                    }
                }

                3 -> {
                    if(tokenExists == true){
                        PersonalScreen(navController)
                        color = R.color.white
                        appbarBackgroundColor = R.color.darkblue
                        titles = stringResource(R.string.individual_title)
                        alignment = Alignment.BottomCenter
                        isVisible = true
                    }
                    else {
                        Login_RegisterScreen(navController)
                        isVisible = false
                    }
                }
            }
        }
        Surface(
            color = Color(0xFFF4F6FB), // Nền xám nhạt cho thanh bar
            shadowElevation = (8 * divideAndRound(screenWidthValue)).dp
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height((96 * divideAndRound(screenWidthValue)).dp),
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
}
