package com.example.dat_lich_kham_fe.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.dat_lich_kham_fe.util.formatNumber

@Composable
fun ServiceRoomRow(
    id: Int,
    name: String,
    address: String,
    examPrice: Int

    ){
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = " $name - " )
        Text(text = "$address")
        Spacer(modifier = Modifier.weight(1f))
        Text(text = " ${formatNumber(examPrice)}VNĐ")
    }
}
