package ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ui.compoment.NewsCard
import viewmodel.NewsViewModel


class HomeScreen : Screen {

    @Composable
    override fun Content() {
        val newsViewModel = remember { NewsViewModel() }
        val newsList by newsViewModel.newsList.collectAsState()
        val isRefreshing by newsViewModel.isRefreshing.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(Unit) {
            newsViewModel.fetchNews()
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // TopBar với nút refresh
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tin tức", style = MaterialTheme.typography.h6)

                IconButton(
                    onClick = { newsViewModel.fetchNews() },
                    enabled = !isRefreshing
                ) {
                    Image(
                        painter = painterResource("icons/refresh.png"),
                        contentDescription = "Làm mới",
                        modifier = Modifier.size(24.dp)
                            .rotate(if (isRefreshing) 360f else 0f)
                    )
                }
            }

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(newsList) { news ->
                    NewsCard(
                        news = news,
                        navigator = navigator
                    )
                }
            }
        }
    }
}