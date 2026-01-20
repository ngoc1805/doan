package com.example.dat_lich_kham_fe.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect  // THÊM import
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dat_lich_kham_fe.data.api.address
import com.example.dat_lich_kham_fe.ui.component.NotificationCard
import com.example.dat_lich_kham_fe.util.UserLocalStore
import com.example.dat_lich_kham_fe.util.divideAndRound
import com.example.dat_lich_kham_fe.viewmodel.NotificationViewModel

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

    // SỬA LaunchedEffect - thêm kết nối WebSocket
    LaunchedEffect(Unit) {
        val user = userStore.getUser()
        userId = user?.Id ?: 0

        // THÊM DÒNG NÀY - Kết nối WebSocket
        // Thay "http://YOUR_BASE_URL" bằng URL server thực tế của bạn
        // Ví dụ: "http://192.168.1.100:8080" hoặc "https://your-domain.com"
        notificationViewModel.connectWebSocket(userId, address)

        notificationViewModel.reset(userId)
        notificationViewModel.markAllReceived(userId)
    }

    // THÊM CODE NÀY - Cleanup WebSocket khi thoát màn hình
    DisposableEffect(Unit) {
        onDispose {
            notificationViewModel.disconnectWebSocket()
        }
    }

    // GIỮ NGUYÊN phần còn lại
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

    LazyColumn(state = listState) {
        item {
            Spacer(modifier = Modifier.height((8 * divideAndRound(screenWidthValue)).dp))
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
        }
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((80 * scale).dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}