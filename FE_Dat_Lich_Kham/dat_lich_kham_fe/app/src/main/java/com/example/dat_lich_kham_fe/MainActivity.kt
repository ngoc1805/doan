package com.example.dat_lich_kham_fe

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.dat_lich_kham_fe.ui.theme.Dat_lich_kham_feTheme
import com.example.dat_lich_kham_fe.navigation.AppnavHost
import com.example.dat_lich_kham_fe.util.DismissKeyboard
import com.example.dat_lich_kham_fe.util.LanguageLocalStore
import com.example.dat_lich_kham_fe.util.LocaleHelper
import com.example.dat_lich_kham_fe.viewmodel.InFoViewModel
import com.example.dat_lich_kham_fe.viewmodel.InFoViewModelFactory
import java.util.Locale

// Thay đổi từ ComponentActivity sang FragmentActivity để hỗ trợ BiometricPrompt
class MainActivity : FragmentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        requestNotificationPermission()
        enableEdgeToEdge()
        setContent {
            Dat_lich_kham_feTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DismissKeyboard {
                        val context = LocalContext.current
                        val navController = rememberNavController()
                        val inFoViewModel : InFoViewModel = viewModel(
                            factory = InFoViewModelFactory(context)
                        )
                        AppnavHost(navController, inFoViewModel)
                        handleDeeplink(intent, navController)
                    }
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    0
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setContent {
            Dat_lich_kham_feTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DismissKeyboard {
                        val context = LocalContext.current
                        val navController = rememberNavController()
                        val inFoViewModel: InFoViewModel = viewModel(
                            factory = InFoViewModelFactory(context)
                        )
                        AppnavHost(navController, inFoViewModel)
                        handleDeeplink(intent, navController)
                    }
                }
            }
        }
    }

    private fun handleDeeplink(intent: Intent?, navController: NavController) {
        val intentData = intent?.data
        if (intentData != null && intentData.scheme == "datlichkham" && intentData.host == "callback") {
            val momoSuccess = intentData.getQueryParameter("status") == "success" || intentData.getQueryParameter("resultCode") == "0"
            val amount = intentData.getQueryParameter("amount") ?: "0"
            navController.navigate("DepositScreen/${momoSuccess}/${amount}")
        }
    }

    override fun attachBaseContext(base: Context) {
        val languageStore = LanguageLocalStore(base)
        val savedLanguage = languageStore.getLanguage()
        val updatedContext = LocaleHelper.setLocale(base, savedLanguage)
        super.attachBaseContext(updatedContext)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        applySavedLanguage()
    }

    private fun applySavedLanguage() {
        val languageStore = LanguageLocalStore(this)
        val savedLanguage = languageStore.getLanguage()
        val locale = Locale(savedLanguage)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}