package com.example.nhom1_fe_datnhahang.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nhom1_fe_datnhahang.util.divideAndRound

@Composable
fun BottomBarItem(
    iconRes: Int,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = (4 * divideAndRound(screenWidthValue)).dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            tint = if (selected) Color(0xFFE30613) else Color.Unspecified,
            modifier = Modifier.size((32 * divideAndRound(screenWidthValue)).dp)
        )
        Text(
            text = label,
            fontSize = (13 * divideAndRound(screenWidthValue)).sp,
            color = if (selected) Color(0xFFE30613) else Color.Black
        )
    }
}
