package com.example.dat_lich_kham_fe.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.util.divideAndRound
import kotlinx.coroutines.delay

@Composable
fun ImageSlider(){
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    val images = listOf(
        R.drawable.lacquan,
        R.drawable.phongchonghiv,
        R.drawable.sieuam,
        R.drawable.ruatay
    )
    var currentImageIndex by remember { mutableStateOf(0) }

    LaunchedEffect(currentImageIndex) {
        delay(3000L)
        currentImageIndex = (currentImageIndex + 1) % images.size  // Quay lại ảnh đầu tiên khi hết danh sách
    }
    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.3f)
//        .padding(horizontal = (16 * divideAndRound(screenWidthValue)).dp)
    ) {
        Image(
            painter = painterResource(id = images[currentImageIndex]),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
