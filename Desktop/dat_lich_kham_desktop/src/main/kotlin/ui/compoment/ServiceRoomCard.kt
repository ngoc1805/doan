package ui.compoment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ServiceRoomCard(
    id: Int,
    name: String,
    address: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        elevation = 6.dp,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(vertical = 6.dp, horizontal = 0.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(Color(0xFFF5F8FF))
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF1976D2),
                    uncheckedColor = Color(0xFFBDBDBD)
                )
            )
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Color(0xFF1976D2),
                    maxLines = 1
                )
                Text(
                    text = address,
                    fontSize = 14.sp,
                    color = Color(0xFF757575),
                    maxLines = 1
                )
            }
        }
    }

    // Đã xóa LaunchedEffect - không in ra ngay khi checkbox được chọn nữa
}