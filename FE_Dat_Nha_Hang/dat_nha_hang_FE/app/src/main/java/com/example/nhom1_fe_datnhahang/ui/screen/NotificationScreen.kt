package com.example.nhom1_fe_datnhahang.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nhom1_fe_datnhahang.ui.component.NotificationCard
import com.example.nhom1_fe_datnhahang.util.UserLocalStore
import com.example.nhom1_fe_datnhahang.util.divideAndRound
import com.example.nhom1_fe_datnhahang.viewmodel.NotificationViewModel


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationScreen(navController: NavController) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    val scale = divideAndRound(screenWidthValue)
    var userId by remember { mutableStateOf(0) }

    val notificationViewModel = remember { NotificationViewModel(context) }
    val userStore = UserLocalStore(context)

    val notifications = notificationViewModel.notifications
    val isLoading = notificationViewModel.isLoading
    val hasMore = notificationViewModel.hasMore
    val listState = rememberLazyListState()

    // Gradient màu giống HomeScreen và AccountScreen
    val gradientColors = listOf(
        Color(0xFFF8FAFC),
        Color(0xFFF1F5F9),
        Color(0xFFE2E8F0)
    )

    LaunchedEffect(Unit) {
        val user = userStore.getUser()
        userId = user?.Id ?: 0
        notificationViewModel.reset(userId)
        notificationViewModel.markAllReceived(userId)
    }

    var lastLoadedIndex by remember { mutableStateOf(-1) }

    LaunchedEffect(listState, notifications.size) {
        val user = userStore.getUser()
        userId = user?.Id ?: 0
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastIndex ->
                if (
                    lastIndex != null &&
                    lastIndex >= notifications.size - 5 &&
                    hasMore && !isLoading &&
                    lastLoadedIndex != lastIndex
                ) {
                    lastLoadedIndex = lastIndex
                    notificationViewModel.loadNextPage(userId)
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(gradientColors)
            )
    ) {
        // TopAppBar giống HomeScreen và AccountScreen
        TopAppBar(
            title = {
                Text(
                    text = "Thông báo",
                    fontSize = (24 * scale).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.padding(horizontal = (16 * scale).dp)
        ) {
            item {
                Spacer(modifier = Modifier.height((12 * scale).dp))
            }

            itemsIndexed(notifications) { index, item ->
                NotificationCard(
                    id = item.id,
                    content = item.content,
                    isSeen = item.isSeen,
                    createdAt = item.createdAt,
                    path = item.path,
                    onClicked = {
                        notificationViewModel.markNotificationSeen(item.id)
                        navController.navigate(item.path)
                    },
                )
                Spacer(modifier = Modifier.height((12 * scale).dp))
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((80 * scale).dp),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF3B82F6)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height((20 * scale).dp))
            }
        }
    }
}
