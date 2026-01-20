package com.example.nhom1_fe_datnhahang.util

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

import com.example.nhom1_fe_datnhahang.data.model.OrderItemDetail
import com.example.nhom1_fe_datnhahang.ui.screen.AddMenuScreen
import com.example.nhom1_fe_datnhahang.ui.screen.ChangePasswordScreen
import com.example.nhom1_fe_datnhahang.ui.screen.HistoryDetailScreen
import com.example.nhom1_fe_datnhahang.ui.screen.HistoryScreen
import com.example.nhom1_fe_datnhahang.ui.screen.LoginScreen
import com.example.nhom1_fe_datnhahang.ui.screen.MainScreen
import com.example.nhom1_fe_datnhahang.ui.screen.OrderDetailScreen
import com.example.nhom1_fe_datnhahang.ui.screen.QrScannerScreen
import com.example.nhom1_fe_datnhahang.ui.screen.SetTodayMenuScreen
import com.example.nhom1_fe_datnhahang.ui.screen.TableScreen
import com.example.nhom1_fe_datnhahang.viewmodel.LoginViewModel
import com.example.nhom1_fe_datnhahang.viewmodel.LoginViewModelFactory
import com.example.nhom1_fe_datnhahang.viewmodel.QrResultViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun AppNavHost(
    navController: NavHostController,
    qrResultViewModel: QrResultViewModel
) {
    val context = LocalContext.current
    val loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(context)
    )

    var startDestination by remember { mutableStateOf("LoginScreen") }
    var isCheckingAuth by remember { mutableStateOf(true) }

    // Kiểm tra token khi app khởi động
    LaunchedEffect(Unit) {
        loginViewModel.checkLoginStatus(
            onLoggedIn = {
                // Có token hợp lệ và role = "nhanvien"
                startDestination = "MainScreen"
                isCheckingAuth = false
            },
            onNotLoggedIn = {
                // Không có token hoặc role không đúng
                startDestination = "LoginScreen"
                isCheckingAuth = false
            }
        )
    }

    // Chờ kiểm tra xong mới hiển thị navigation
    if (!isCheckingAuth) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
        ) {
            composable("LoginScreen") {
                LoginScreen(
                    navController = navController,
                    viewModel = loginViewModel
                )
            }
            composable("MainScreen") {
                MainScreen(
                    navController,
                )
            }
            composable("MainScreen/{initialTab}"){backStackEntry ->
                val initialTab = backStackEntry.arguments?.getString("initialTab")?.toIntOrNull() ?: 0
                MainScreen(
                    navController,
                    initialTab = initialTab
                )
            }
            composable("QrScannerScreen") {
                QrScannerScreen(
                    navController,
                    qrResultViewModel
                )
            }
            composable("TableScreen") {
                TableScreen(
                    navController,
                    qrResultViewModel
                )
            }
            composable("AddMenuScreen") {
                AddMenuScreen(navController)
            }
            composable("SetTodayMenuScreen") {
                SetTodayMenuScreen(navController)
            }
            composable(
                route = "OrderDetailScreen/{id}/{userId}/{userName}/{fmctoken}/{phone}/{address}/{note}/{status}/{items}/{totalPrice}",
                arguments = listOf(
                    navArgument("id") { type = NavType.IntType },
                    navArgument("userId") { type = NavType.IntType },
                    navArgument("userName") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = ""
                    },
                    navArgument("fmctoken") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = ""
                    },
                    navArgument("phone") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = ""
                    },
                    navArgument("address") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = ""
                    },
                    navArgument("note") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = "Không có ghi chú"
                    },
                    navArgument("status") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = ""
                    },
                    navArgument("items") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = ""
                    },
                    navArgument("totalPrice") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: 0
                val userId = backStackEntry.arguments?.getInt("userId") ?: 0

                val userName = URLDecoder.decode(
                    backStackEntry.arguments?.getString("userName") ?: "",
                    StandardCharsets.UTF_8.toString()
                )
                val fmctoken = URLDecoder.decode(
                    backStackEntry.arguments?.getString("fmctoken") ?: "",
                    StandardCharsets.UTF_8.toString()
                )
                val phone = URLDecoder.decode(
                    backStackEntry.arguments?.getString("phone") ?: "",
                    StandardCharsets.UTF_8.toString()
                )
                val address = URLDecoder.decode(
                    backStackEntry.arguments?.getString("address") ?: "",
                    StandardCharsets.UTF_8.toString()
                )
                val note = URLDecoder.decode(
                    backStackEntry.arguments?.getString("note") ?: "",
                    StandardCharsets.UTF_8.toString()
                )
                val status = URLDecoder.decode(
                    backStackEntry.arguments?.getString("status") ?: "",
                    StandardCharsets.UTF_8.toString()
                )
                val encodedItems = backStackEntry.arguments?.getString("items") ?: ""
                val totalPrice = backStackEntry.arguments?.getInt("totalPrice") ?: 0

                val itemsJson = URLDecoder.decode(encodedItems, StandardCharsets.UTF_8.toString())
                val gson = Gson()
                val listType = object : TypeToken<List<OrderItemDetail>>() {}.type
                val items: List<OrderItemDetail> = try {
                    gson.fromJson(itemsJson, listType) ?: emptyList()
                } catch (e: Exception) {
                    emptyList()
                }

                OrderDetailScreen(
                    navController = navController,
                    id = id,
                    userId = userId,
                    userName = userName,
                    fmctoken = fmctoken,
                    phone = phone,
                    address = address,
                    note = note,
                    status = status,
                    items = items,
                    totalPrice = totalPrice
                )
            }
            //
            composable("HistoryScreen"){
                HistoryScreen(navController)
            }
            //
            composable(
                route = "HistoryDetailScreen/{id}/{userId}/{userName}/{fmctoken}/{phone}/{address}/{note}/{status}/{items}/{totalPrice}/{imageUrl}",
                arguments = listOf(
                    navArgument("id") { type = NavType.IntType },
                    navArgument("userId") { type = NavType.IntType },
                    navArgument("userName") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = ""
                    },
                    navArgument("fmctoken") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = ""
                    },
                    navArgument("phone") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = ""
                    },
                    navArgument("address") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = ""
                    },
                    navArgument("note") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = "Không có ghi chú"
                    },
                    navArgument("status") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = ""
                    },
                    navArgument("items") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = ""
                    },
                    navArgument("totalPrice") { type = NavType.IntType },
                    navArgument("imageUrl") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: 0
                val userId = backStackEntry.arguments?.getInt("userId") ?: 0

                val userName = URLDecoder.decode(
                    backStackEntry.arguments?.getString("userName") ?: "",
                    StandardCharsets.UTF_8.toString()
                )
                val fmctoken = URLDecoder.decode(
                    backStackEntry.arguments?.getString("fmctoken") ?: "",
                    StandardCharsets.UTF_8.toString()
                )
                val phone = URLDecoder.decode(
                    backStackEntry.arguments?.getString("phone") ?: "",
                    StandardCharsets.UTF_8.toString()
                )
                val address = URLDecoder.decode(
                    backStackEntry.arguments?.getString("address") ?: "",
                    StandardCharsets.UTF_8.toString()
                )
                val note = URLDecoder.decode(
                    backStackEntry.arguments?.getString("note") ?: "",
                    StandardCharsets.UTF_8.toString()
                )
                val status = URLDecoder.decode(
                    backStackEntry.arguments?.getString("status") ?: "",
                    StandardCharsets.UTF_8.toString()
                )
                val encodedItems = backStackEntry.arguments?.getString("items") ?: ""
                val totalPrice = backStackEntry.arguments?.getInt("totalPrice") ?: 0
                val imageUrl = URLDecoder.decode(
                    backStackEntry.arguments?.getString("imageUrl") ?: "",
                    StandardCharsets.UTF_8.toString()
                )

                val itemsJson = URLDecoder.decode(encodedItems, StandardCharsets.UTF_8.toString())
                val gson = Gson()
                val listType = object : TypeToken<List<OrderItemDetail>>() {}.type
                val items: List<OrderItemDetail> = try {
                    gson.fromJson(itemsJson, listType) ?: emptyList()
                } catch (e: Exception) {
                    emptyList()
                }

                HistoryDetailScreen(
                    navController = navController,
                    id = id,
                    userId = userId,
                    userName = userName,
                    fmctoken = fmctoken,
                    phone = phone,
                    address = address,
                    note = note,
                    status = status,
                    items = items,
                    totalPrice = totalPrice,
                    imageUrl = imageUrl
                )
            }
            //
            composable("ChangePasswordScreen"){
                ChangePasswordScreen(navController)
            }
        }
    }
}
