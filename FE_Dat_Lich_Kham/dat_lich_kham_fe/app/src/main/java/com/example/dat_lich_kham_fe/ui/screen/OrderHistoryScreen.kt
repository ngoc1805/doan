package com.example.dat_lich_kham_fe.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dat_lich_kham_fe.ui.component.OrderCard
import com.example.dat_lich_kham_fe.util.UserLocalStore
import com.example.dat_lich_kham_fe.viewmodel.OrderViewModel
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(navController: NavController) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value

    var userId by remember { mutableStateOf(0) }
    val userStore = UserLocalStore(context)

    val gradientColors = listOf(
        Color(0xFFF8FAFC),
        Color(0xFFF1F5F9),
        Color(0xFFE2E8F0)
    )

    val orderViewModel = remember { OrderViewModel(context) }

    LaunchedEffect(Unit) {
        val user = userStore.getUser()
        userId = user?.Id ?: 0
        orderViewModel.fetchOrders(userId,listOf("Đã giao hàng"))
    }

    // Collect StateFlow value
    val orders by orderViewModel.orders.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(gradientColors)
            )
    ) {
        // TopAppBar
        TopAppBar(
            title = {
                Text(
                    text = "Đơn hàng",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF1E293B)
                    )
                }
            }
        )

        LazyColumn {
            orders?.let { orderList ->
                items(orderList.reversed()) { order ->
                    OrderCard(
                        id = order.id,
                        userId = order.userId,
                        userName = order.userName,
                        fmctoken = order.fmctoken,
                        phone = order.phone,
                        address = order.address,
                        note = order.note,
                        status = order.status,
                        items = order.items,
                        totalPrice = order.totalPrice,
                        imageUrl = order.imageUrl,
                        onClicked = {
                            val gson = Gson()
                            val itemJson = gson.toJson(order.items)
                            val noteToSend = if (order.note.isEmpty()) "Không có ghi chú" else order.note


                            // Encode tất cả string parameters
                            val encodedUserName = URLEncoder.encode(order.userName, StandardCharsets.UTF_8.toString())
                            val encodedFmctoken = URLEncoder.encode(order.fmctoken, StandardCharsets.UTF_8.toString())
                            val encodedPhone = URLEncoder.encode(order.phone, StandardCharsets.UTF_8.toString())
                            val encodedAddress = URLEncoder.encode(order.address, StandardCharsets.UTF_8.toString())
                            val encodedNote = URLEncoder.encode(noteToSend, StandardCharsets.UTF_8.toString())
                            val encodedStatus = URLEncoder.encode(order.status, StandardCharsets.UTF_8.toString())
                            val encodedItems = URLEncoder.encode(itemJson, StandardCharsets.UTF_8.toString())
                            val encodedImageUrl = URLEncoder.encode(order.imageUrl, StandardCharsets.UTF_8.toString())

                            navController.navigate(
                                "OrderHistoryDetailScreen/${order.id}/${order.userId}/$encodedUserName/$encodedFmctoken/$encodedPhone/$encodedAddress/$encodedNote/$encodedStatus/$encodedItems/${order.totalPrice}/$encodedImageUrl"
                            )
                        }
                    )
                }
            }
        }
    }
}
