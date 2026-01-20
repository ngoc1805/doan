package com.example.nhom1_fe_datnhahang.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.nhom1_fe_datnhahang.data.api.address
import com.example.nhom1_fe_datnhahang.data.model.Menu

@Composable
fun SetTodayCard(
    menu: Menu,
    onDisplayChange: (Boolean) -> Unit = {}
) {
    // State local để quản lý checkbox, khởi tạo từ menu.isDisplay
    var isChecked by remember { mutableStateOf(menu.isDisplay) }
    val url = "$address/api/${menu.imageUrl}"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AsyncImage(
                model = url,
                contentDescription = menu.name,
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = menu.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = menu.description,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        maxLines = 2,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Danh mục: ${menu.category}",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Text(
                    text = "${menu.examPrice.toString().replace(Regex("(\\d)(?=(\\d{3})+$)"), "$1.")}đ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF059669)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Checkbox(
                checked = isChecked,
                onCheckedChange = { newValue ->
                    isChecked = newValue
                    onDisplayChange(newValue)
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF3B82F6),
                    uncheckedColor = Color(0xFF94A3B8)
                ),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}
