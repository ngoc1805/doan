//package com.example.dat_lich_kham_fe.ui.screen
//
//import android.os.Build
//import androidx.annotation.RequiresApi
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Lock
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.platform.LocalConfiguration
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.res.vectorResource
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//import com.example.dat_lich_kham_fe.R
//import com.example.dat_lich_kham_fe.ui.component.FirstCardPersonal
//import com.example.dat_lich_kham_fe.ui.component.SecondCardPersonal
//import com.example.dat_lich_kham_fe.ui.component.setCardPersonal
//import com.example.dat_lich_kham_fe.ui.component.LanguageSettingCard
//import com.example.dat_lich_kham_fe.util.PersistentCookieJar
//import com.example.dat_lich_kham_fe.util.UserLocalStore
//import com.example.dat_lich_kham_fe.util.LanguageLocalStore
//import com.example.dat_lich_kham_fe.util.divideAndRound
//import com.example.dat_lich_kham_fe.viewmodel.InFoViewModel
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun PersonalScreen(navController: NavController) {
//    val configuration = LocalConfiguration.current
//    val screenWidth = configuration.screenWidthDp.dp
//    val screenWidthValue = screenWidth.value
//    val context = LocalContext.current
//
//    val userStore = UserLocalStore(context)
//    val languageStore = LanguageLocalStore(context)
//    var imageurl by remember { mutableStateOf("") }
//
//    val inFoViewModel = remember { InFoViewModel(context) }
//
//    LaunchedEffect(Unit) {
//        inFoViewModel.reset()
//        val user = userStore.getUser()
//        imageurl = user?.imageUrl ?: "null"
//    }
//
//    // Gradient background
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                brush = Brush.verticalGradient(
//                    colors = listOf(
//                        Color(0xFFF8FAFE),
//                        Color(0xFFE8F4FD)
//                    )
//                )
//            )
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .verticalScroll(rememberScrollState())
//                .padding(horizontal = (16 * divideAndRound(screenWidthValue)).dp)
//        ) {
//            Spacer(modifier = Modifier.height((20 * divideAndRound(screenWidthValue)).dp))
//
//            // Profile Section
//            FirstCardPersonal(navController = navController)
//            Spacer(modifier = Modifier.height((16 * divideAndRound(screenWidthValue)).dp))
//
//            // Utilities Section
//            SecondCardPersonal(navController = navController)
//            Spacer(modifier = Modifier.height((20 * divideAndRound(screenWidthValue)).dp))
//
//            // Settings Section
//            Card(
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape((16 * divideAndRound(screenWidthValue)).dp),
//                colors = CardDefaults.cardColors(containerColor = Color.White),
//                elevation = CardDefaults.cardElevation((4 * divideAndRound(screenWidthValue)).dp)
//            ) {
//                Column(
//                    modifier = Modifier.padding((16 * divideAndRound(screenWidthValue)).dp)
//                ) {
//                    Text(
//                        text = "Cài đặt",
//                        style = MaterialTheme.typography.titleMedium,
//                        color = Color(0xFF2C3E50),
//                        modifier = Modifier.padding(bottom = (12 * divideAndRound(screenWidthValue)).dp)
//                    )
//
//                    LanguageSettingCard(navController = navController)
//                    Spacer(modifier = Modifier.height((12 * divideAndRound(screenWidthValue)).dp))
//
//                    setCardPersonal(
//                        imageVector = ImageVector.vectorResource(id = R.drawable.baseline_monetization_on_24),
//                        text = stringResource(id = R.string.deposit_money_title),
//                        onClick = {
//                            navController.navigate("DepositScreen/false")
//                        }
//                    )
//                    Spacer(modifier = Modifier.height((12 * divideAndRound(screenWidthValue)).dp))
//
//                    setCardPersonal(
//                        imageVector = ImageVector.vectorResource(id = R.drawable.baseline_monetization_on_24),
//                        text = "Lịch sử giao dịch",
//                        onClick = {
//                            navController.navigate("TransactionScreen")
//                        }
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height((20 * divideAndRound(screenWidthValue)).dp))
//
//            // Account Section
//            Card(
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape((16 * divideAndRound(screenWidthValue)).dp),
//                colors = CardDefaults.cardColors(containerColor = Color.White),
//                elevation = CardDefaults.cardElevation((4 * divideAndRound(screenWidthValue)).dp)
//            ) {
//                Column(
//                    modifier = Modifier.padding((16 * divideAndRound(screenWidthValue)).dp)
//                ) {
//                    Text(
//                        text = "Tài khoản",
//                        style = MaterialTheme.typography.titleMedium,
//                        color = Color(0xFF2C3E50),
//                        modifier = Modifier.padding(bottom = (12 * divideAndRound(screenWidthValue)).dp)
//                    )
//
//                    setCardPersonal(
//                        imageVector = Icons.Default.Lock,
//                        text = stringResource(id = R.string.personal_information_title),
//                        onClick = { navController.navigate("InFoScreen") }
//                    )
//                    Spacer(modifier = Modifier.height((8 * divideAndRound(screenWidthValue)).dp))
//
//                    setCardPersonal(
//                        imageVector = Icons.Default.Lock,
//                        text = stringResource(id = R.string.change_password),
//                        onClick = { navController.navigate("ChangePasswordScreen") }
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height((20 * divideAndRound(screenWidthValue)).dp))
//
//            // Logout Section
//            Card(
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape((16 * divideAndRound(screenWidthValue)).dp),
//                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5F5)),
//                elevation = CardDefaults.cardElevation((2 * divideAndRound(screenWidthValue)).dp)
//            ) {
//                Column(
//                    modifier = Modifier.padding((16 * divideAndRound(screenWidthValue)).dp)
//                ) {
//                    setCardPersonal(
//                        imageVector = ImageVector.vectorResource(id = R.drawable.baseline_logout_24),
//                        text = stringResource(id = R.string.logout),
//                        onClick = {
//                            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
//                                PersistentCookieJar(context).logout()
//                                userStore.clearUser()
//                                languageStore.clearLanguage()
//                                withContext(kotlinx.coroutines.Dispatchers.Main) {
//                                    navController.navigate("Login_RegisterScreen")
//                                }
//                            }
//                        }
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height((40 * divideAndRound(screenWidthValue)).dp))
//        }
//    }
//}

package com.example.dat_lich_kham_fe.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.ui.component.FirstCardPersonal
import com.example.dat_lich_kham_fe.ui.component.SecondCardPersonal
import com.example.dat_lich_kham_fe.ui.component.setCardPersonal
import com.example.dat_lich_kham_fe.ui.component.LanguageSettingCard
import com.example.dat_lich_kham_fe.ui.component.BiometricSettingCard
import com.example.dat_lich_kham_fe.util.*
import com.example.dat_lich_kham_fe.viewmodel.InFoViewModel
import com.example.dat_lich_kham_fe.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PersonalScreen(navController: NavController) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    val context = LocalContext.current

    val userStore = UserLocalStore(context)
    val languageStore = LanguageLocalStore(context)
    val biometricStore = BiometricLocalStore(context)

    var imageurl by remember { mutableStateOf("") }
    var showPinScreen by remember { mutableStateOf(false) }
    var pinError by remember { mutableStateOf("") }
    val userViewModel = remember { UserViewModel(context) }
    val inFoViewModel = remember { InFoViewModel(context) }

    LaunchedEffect(Unit) {
        inFoViewModel.reset()
        val user = userStore.getUser()
        imageurl = user?.imageUrl ?: "null"
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Main Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF8FAFE),
                            Color(0xFFE8F4FD)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = (16 * divideAndRound(screenWidthValue)).dp)
            ) {
                Spacer(modifier = Modifier.height((20 * divideAndRound(screenWidthValue)).dp))

                // Profile Section
                FirstCardPersonal(navController = navController)
                Spacer(modifier = Modifier.height((16 * divideAndRound(screenWidthValue)).dp))

                // Utilities Section
                SecondCardPersonal(navController = navController)
                Spacer(modifier = Modifier.height((20 * divideAndRound(screenWidthValue)).dp))

                // Settings Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape((16 * divideAndRound(screenWidthValue)).dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation((4 * divideAndRound(screenWidthValue)).dp)
                ) {
                    Column(
                        modifier = Modifier.padding((16 * divideAndRound(screenWidthValue)).dp)
                    ) {
                        Text(
                            text = "Cài đặt",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF2C3E50),
                            modifier = Modifier.padding(bottom = (12 * divideAndRound(screenWidthValue)).dp)
                        )

                        LanguageSettingCard(navController = navController)
                        Spacer(modifier = Modifier.height((12 * divideAndRound(screenWidthValue)).dp))

                        // Biometric Setting Card
                        BiometricSettingCard(
                            onPinRequired = {
                                showPinScreen = true
                            },
                            scale = divideAndRound(screenWidthValue)
                        )
                        Spacer(modifier = Modifier.height((12 * divideAndRound(screenWidthValue)).dp))

                        setCardPersonal(
                            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_monetization_on_24),
                            text = stringResource(id = R.string.deposit_money_title),
                            onClick = {
                                navController.navigate("DepositScreen/false")
                            }
                        )
                        Spacer(modifier = Modifier.height((12 * divideAndRound(screenWidthValue)).dp))

                        setCardPersonal(
                            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_monetization_on_24),
                            text = "Lịch sử giao dịch",
                            onClick = {
                                navController.navigate("TransactionScreen")
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height((20 * divideAndRound(screenWidthValue)).dp))

                // Account Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape((16 * divideAndRound(screenWidthValue)).dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation((4 * divideAndRound(screenWidthValue)).dp)
                ) {
                    Column(
                        modifier = Modifier.padding((16 * divideAndRound(screenWidthValue)).dp)
                    ) {
                        Text(
                            text = "Tài khoản",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF2C3E50),
                            modifier = Modifier.padding(bottom = (12 * divideAndRound(screenWidthValue)).dp)
                        )

                        setCardPersonal(
                            imageVector = Icons.Default.Lock,
                            text = stringResource(id = R.string.personal_information_title),
                            onClick = { navController.navigate("InFoScreen") }
                        )
                        Spacer(modifier = Modifier.height((8 * divideAndRound(screenWidthValue)).dp))

                        setCardPersonal(
                            imageVector = Icons.Default.Lock,
                            text = stringResource(id = R.string.change_password),
                            onClick = { navController.navigate("ChangePasswordScreen") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height((20 * divideAndRound(screenWidthValue)).dp))

                // Logout Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape((16 * divideAndRound(screenWidthValue)).dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5F5)),
                    elevation = CardDefaults.cardElevation((2 * divideAndRound(screenWidthValue)).dp)
                ) {
                    Column(
                        modifier = Modifier.padding((16 * divideAndRound(screenWidthValue)).dp)
                    ) {
                        setCardPersonal(
                            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_logout_24),
                            text = stringResource(id = R.string.logout),
                            onClick = {
                                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                                    PersistentCookieJar(context).logout()
                                    userStore.clearUser()
                                    languageStore.clearLanguage()
                                    biometricStore.clearBiometricSettings()
                                    withContext(kotlinx.coroutines.Dispatchers.Main) {
                                        navController.navigate("Login_RegisterScreen")
                                    }
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height((40 * divideAndRound(screenWidthValue)).dp))
            }
        }

        // PIN Screen Overlay
        if (showPinScreen) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White
            ) {
                PinCodeScreenV2(
                    onPinEntered = { pin ->
                        userViewModel.checkPin(userId, pin) { isCorrect ->
                            if (isCorrect) {
                                showPinScreen = false
                                pinError = ""
                                // Đánh dấu đã setup sinh trắc học và bật nó lên
                                biometricStore.setBiometricSetupDone(true)
                                biometricStore.setBiometricEnabled(true)
                            } else {
                                pinError = "Mã PIN sai, vui lòng thử lại!"
                            }
                        }
                    },
                    onClose = {
                        showPinScreen = false
                        pinError = ""
                    },
                    errorMessage = pinError,
                    navController = navController
                )
            }
        }
    }
}