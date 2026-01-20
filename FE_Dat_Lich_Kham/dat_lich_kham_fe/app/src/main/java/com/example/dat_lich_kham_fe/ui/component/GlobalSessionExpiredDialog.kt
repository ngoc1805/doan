package com.example.dat_lich_kham_fe.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.dat_lich_kham_fe.util.PersistentCookieJar
import com.example.dat_lich_kham_fe.util.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun GlobalSessionExpiredDialog(
    navController: NavController,
) {
    val context = LocalContext.current
    val sessionExpired by SessionManager.sessionExpired.collectAsState()

    if (sessionExpired) {
        AlertDialog(
            onDismissRequest = { /* Không cho dismiss */ },
            title = { Text("Phiên đăng nhập đã hết hạn") },
            text = { Text("Vui lòng đăng nhập lại để tiếp tục") },
            confirmButton = {
                TextButton(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                // Clear data
                                PersistentCookieJar(context).logout()
                                // userStore.clearUser()
                                // languageStore.clearLanguage()

                                withContext(Dispatchers.Main) {
                                    SessionManager.reset()
                                    navController.navigate("Login_RegisterScreen") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}

