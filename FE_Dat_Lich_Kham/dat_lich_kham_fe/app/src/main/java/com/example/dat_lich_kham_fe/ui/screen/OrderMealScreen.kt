package com.example.dat_lich_kham_fe.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dat_lich_kham_fe.util.UserLocalStore
import com.example.dat_lich_kham_fe.util.divideAndRound
import com.example.dat_lich_kham_fe.viewmodel.InpatientViewModel
import com.example.dat_lich_kham_fe.viewmodel.MenuViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.ui.component.MenuCard
import com.example.dat_lich_kham_fe.viewmodel.OrderViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderMealScreen(navController: NavController) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    val scale = divideAndRound(screenWidthValue)
    var userId by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    val userStore = UserLocalStore(context)
    val inpatientViewModel = remember { InpatientViewModel(context) }
    val menuViewModel = remember { MenuViewModel(context) }
    val orderViewModel = remember { OrderViewModel(context) }

    // Cart state: Map<menuId, quantity>
    val cartItems = remember { mutableStateMapOf<Int, Int>() }

    LaunchedEffect(Unit) {
        val user = userStore.getUser()
        userId = user?.Id ?: 0
        inpatientViewModel.checkAdmitted(userId)
        orderViewModel.fetchOrders(userId, listOf("Đã đặt hàng", "Đã thanh toán"))
        menuViewModel.fetchMenus(true)
    }
    val orders by orderViewModel.orders.collectAsState()

    val admittedResult = inpatientViewModel.admittedResult
    val menus = menuViewModel.menus

    // Lọc menu theo từ khóa tìm kiếm
    val filteredMenus = remember(menus, searchQuery) {
        if (searchQuery.isBlank()) {
            menus
        } else {
            menus.filter { menu ->
                menu.name.contains(searchQuery, ignoreCase = true) ||
                    menu.category.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // Tính tổng số lượng món trong giỏ
    val totalCartItems = cartItems.values.sum()

    // Tính tổng tiền
    val totalPrice = cartItems.entries.sumOf { (menuId, quantity) ->
        val menu = menus.find { it.id == menuId }
        (menu?.examPrice ?: 0) * quantity
    }

    val gradientColors = listOf(
        Color(0xFFF8FAFC),
        Color(0xFFE8EAF6),
        Color(0xFFF3E5F5)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Thực đơn món ăn",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1E293B)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (admittedResult.value == true) {
                                navController.navigate("OrderScreen")
                            }
                        }
                    ) {
                        BadgedBox(
                            badge = {
                                val orderCount = orders?.size ?: 0
                                if (orderCount > 0) {
                                    Badge(
                                        containerColor = Color(0xFFEF4444),
                                        contentColor = Color.White
                                    ) {
                                        Text(
                                            text = orderCount.toString(),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.bell),
                                contentDescription = "Đơn hàng",
                                tint = Color(0xFF1E293B),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            // Chỉ navigate khi có item trong giỏ
                            if (totalCartItems > 0) {
                                val cartData = cartItems.entries.joinToString(",") { "${it.key}:${it.value}" }
                                navController.navigate("orderConfirmation/$cartData")
                            }
                        }
                    ) {
                        BadgedBox(
                            badge = {
                                if (totalCartItems > 0) {
                                    Badge(
                                        containerColor = Color(0xFFEF4444),
                                        contentColor = Color.White
                                    ) {
                                        Text(
                                            text = totalCartItems.toString(),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Giỏ hàng",
                                tint = Color(0xFF1E293B)
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.verticalGradient(gradientColors))
                .padding(paddingValues)
        ) {
            if(admittedResult.collectAsState().value == false){
                EmptyStateMessage()
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Thanh tìm kiếm
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        placeholder = {
                            Text(
                                text = "Tìm kiếm món ăn hoặc danh mục...",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color(0xFF3B82F6)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = Color.Gray
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3B82F6),
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            cursorColor = Color(0xFF3B82F6),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        singleLine = true
                    )

                    // Hiển thị số kết quả
                    if (searchQuery.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tìm thấy ${filteredMenus.size} món",
                                fontSize = 14.sp,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Medium
                            )

                            if (filteredMenus.isNotEmpty()) {
                                Text(
                                    text = "Đang tìm: \"$searchQuery\"",
                                    fontSize = 12.sp,
                                    color = Color(0xFF3B82F6),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // Hiển thị danh sách hoặc thông báo không tìm thấy
                    if (filteredMenus.isEmpty() && searchQuery.isNotEmpty()) {
                        NoResultsMessage(searchQuery = searchQuery)
                    } else {
                        MenuListByCategory(
                            menus = filteredMenus,
                            cartItems = cartItems,
                            onAddToCart = { menuId ->
                                cartItems[menuId] = (cartItems[menuId] ?: 0) + 1
                            },
                            onIncreaseQuantity = { menuId ->
                                cartItems[menuId] = (cartItems[menuId] ?: 0) + 1
                            },
                            onDecreaseQuantity = { menuId ->
                                val currentQuantity = cartItems[menuId] ?: 0
                                if (currentQuantity > 1) {
                                    cartItems[menuId] = currentQuantity - 1
                                } else {
                                    cartItems.remove(menuId)
                                }
                            }
                        )
                    }
                }

                // Nút đặt đơn floating
                AnimatedVisibility(
                    visible = totalCartItems > 0,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            val cartData = cartItems.entries.joinToString(",") { "${it.key}:${it.value}" }
                            navController.navigate("orderConfirmation/$cartData")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 12.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Đặt đơn",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Text(
                                text = "${totalPrice.toString().replace(Regex("(\\d)(?=(\\d{3})+$)"), "$1.")}đ",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoResultsMessage(searchQuery: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(50.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color(0xFFF59E0B),
                modifier = Modifier.size(50.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Không tìm thấy món ăn",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Không có món nào khớp với \"$searchQuery\"",
            fontSize = 15.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Thử tìm kiếm với từ khóa khác",
            fontSize = 14.sp,
            color = Color(0xFF94A3B8),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MenuListByCategory(
    menus: List<com.example.dat_lich_kham_fe.data.model.Menu>,
    cartItems: Map<Int, Int>,
    onAddToCart: (Int) -> Unit,
    onIncreaseQuantity: (Int) -> Unit,
    onDecreaseQuantity: (Int) -> Unit
) {
    val menusByCategory = menus.groupBy { it.category }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
        }

        menusByCategory.forEach { (category, menuList) ->
            item {
                CategoryHeader(
                    categoryName = category,
                    itemCount = menuList.size
                )
            }

            items(menuList) { menu ->
                MenuCard (
                    menu = menu,
                    quantity = cartItems[menu.id] ?: 0,
                    onAddToCart = { onAddToCart(menu.id) },
                    onIncrease = { onIncreaseQuantity(menu.id) },
                    onDecrease = { onDecreaseQuantity(menu.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Thêm khoảng trống cuối để tránh bị nút đặt đơn che
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}


@Composable
fun CategoryHeader(categoryName: String, itemCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = categoryName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            Text(
                text = "$itemCount món",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF64748B)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF3B82F6),
                            Color(0xFF8B5CF6),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(2.dp)
                )
        )
    }
}

@Composable
fun EmptyStateMessage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = Color(0xFFE3F2FD),
                    shape = RoundedCornerShape(40.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = Color(0xFF1976D2),
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Chức năng không khả dụng",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Chỉ bệnh nhân nội trú mới có thể\nđặt suất ăn tại bệnh viện",
            fontSize = 16.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}
