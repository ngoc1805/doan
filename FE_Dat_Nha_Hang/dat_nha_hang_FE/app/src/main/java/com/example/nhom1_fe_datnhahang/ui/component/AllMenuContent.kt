package com.example.nhom1_fe_datnhahang.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nhom1_fe_datnhahang.data.model.Menu
import com.example.nhom1_fe_datnhahang.viewmodel.MenuViewModel


@Composable
fun AllMenuContent(navController: NavController) {
    val context = LocalContext.current
    val menuViewModel = remember { MenuViewModel(context) }
    LaunchedEffect(Unit) {
        menuViewModel.fetchMenus(null)
    }
    val menus = menuViewModel.menus
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search or filter section
        AllMenuHeader(navController)

        Spacer(modifier = Modifier.height(16.dp))

        // All menu items
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(menus) {menu ->
                AllMenuCard(
                    id = menu.id,
                    name = menu.name,
                    examPrice = menu.examPrice,
                    description = menu.description,
                    category = menu.category,
                    isDisplay = menu.isDisplay,
                    imageUrl = menu.imageUrl
                )

            }
        }
    }
}

@Composable
fun AllMenuContent_Set(
    navController: NavController,
    menus: List<Menu>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // All menu items
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(menus) { menu ->
                SetTodayCard(
                    menu = menu,
                    onDisplayChange = { newValue ->
                        // Cập nhật trực tiếp vào object menu
                        menu.isDisplay = newValue
                    }
                )
            }
        }
    }
}

@Composable
fun AllMenuHeader(navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .clickable { navController.navigate("AddMenuScreen") },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFDCFCE7),
                                Color(0xFF86EFAC)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🍽️",
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Thêm món ăn mới",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = "Khám phá thực đơn đa dạng",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}
