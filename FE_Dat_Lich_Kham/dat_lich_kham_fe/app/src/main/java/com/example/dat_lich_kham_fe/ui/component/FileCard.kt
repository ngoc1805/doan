package com.example.dat_lich_kham_fe.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.util.divideAndRound

@Composable
fun FileCard(
    id: Int,
    fileName: String,
    filePath: String,
    modifier: Modifier = Modifier,
    onClicked: (String, String) -> Unit = { _, _ -> }, // Changed to pass fileName and filePath
) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    val scale = divideAndRound(screenWidthValue)

    // Determine file type and color based on file extension
    val fileExtension = fileName.substringAfterLast(".", "").lowercase()
    val badgeColor = when (fileExtension) {
        "pdf" -> Color(0xFFD32F2F)
        "jpg", "jpeg", "png", "gif", "bmp" -> Color(0xFF388E3C)
        "doc", "docx" -> Color(0xFF1976D2)
        else -> Color(0xFF757575)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClicked(fileName, filePath) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding((16 * scale).dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // File info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = fileName,
                    fontSize = (16 * scale).sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height((8 * scale).dp))

                // File type indicator
                if (fileExtension.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = badgeColor.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = fileExtension.uppercase(),
                            fontSize = (12 * scale).sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor
                        )
                    }
                }
            }
        }
    }
}
