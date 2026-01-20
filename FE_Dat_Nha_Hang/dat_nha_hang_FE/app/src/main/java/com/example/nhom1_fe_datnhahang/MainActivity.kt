package com.example.nhom1_fe_datnhahang

import android.Manifest
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.nhom1_fe_datnhahang.ui.screen.MainScreen
import com.example.nhom1_fe_datnhahang.ui.theme.Nhom1_FE_DatNhaHangTheme
import com.example.nhom1_fe_datnhahang.util.AppNavHost
import com.example.nhom1_fe_datnhahang.util.DismissKeyboard
import com.example.nhom1_fe_datnhahang.viewmodel.QrResultViewModel

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        requestNotificationPermission()
        enableEdgeToEdge()
        setContent {
            Nhom1_FE_DatNhaHangTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DismissKeyboard {
                        val navController = rememberNavController()
                        val qrResultViewModel: QrResultViewModel = viewModel()
                        AppNavHost(navController, qrResultViewModel)
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
}

