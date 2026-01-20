package com.example.dat_lich_kham_fe.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dat_lich_kham_fe.ui.component.NewsCard
import com.example.dat_lich_kham_fe.viewmodel.NewsViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.example.dat_lich_kham_fe.ui.component.ImageSlider
import com.example.dat_lich_kham_fe.ui.component.ButtonGrid
import com.example.dat_lich_kham_fe.util.divideAndRound

@Composable
fun HomeScreen(
    navController: NavController
) {
    val newsViewModel = remember { NewsViewModel() }
    val newsList by newsViewModel.newsList.collectAsState()
    val isRefreshing by newsViewModel.isRefreshing.collectAsState()

    LaunchedEffect(Unit) {
        newsViewModel.fetchNews()
    }
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value

    Column(modifier = Modifier.fillMaxSize()) {
        ImageSlider()
        Spacer(modifier = Modifier.height((16 * divideAndRound(screenWidthValue)).dp))
        Spacer(modifier = Modifier.height((32 * divideAndRound(screenWidthValue)).dp))
        ButtonGrid(navController)
        Spacer(modifier = Modifier.height((16 * divideAndRound(screenWidthValue)).dp))
        Text(text = "     Tin tức", fontWeight = FontWeight.Bold, fontSize = (14 * divideAndRound(screenWidthValue)).sp)
        Spacer(modifier = Modifier.height((4 * divideAndRound(screenWidthValue)).dp))

        // Swipe to refresh wrapper
        SwipeRefresh(
            state = SwipeRefreshState(isRefreshing),
            onRefresh = { newsViewModel.fetchNews() }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(newsList) { news ->
                    NewsCard(news) {
                        navController.navigate("detailNews?link=${news.link}")
                    }
                }
            }
        }
    }
}
