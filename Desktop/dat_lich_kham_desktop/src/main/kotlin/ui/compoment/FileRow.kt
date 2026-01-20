package ui.compoment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import java.awt.Desktop
import java.net.URI
import java.net.URLEncoder

@Composable
fun FileRow(
    id: Int,
    fileName: String,
    filePath: String,
) {
    Surface(
        modifier = Modifier
            .width(140.dp)
            .padding(vertical = 6.dp, horizontal = 8.dp)
            .clickable {
                try {
                    // Encode tên file, thay + thành %20 cho đúng chuẩn URL
                    val folder = if (filePath.contains("/")) filePath.substringBeforeLast("/") else ""
                    val file = if (filePath.contains("/")) filePath.substringAfterLast("/") else filePath
                    val encodedFile = URLEncoder.encode(file, "UTF-8").replace("+", "%20")
                    val encodedPath = if (folder.isNotEmpty()) "$folder/$encodedFile" else encodedFile
                    val url = "http://localhost:8080/api$encodedPath"
                    Desktop.getDesktop().browse(URI(url))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
        shape = RoundedCornerShape(16.dp),
        elevation = 3.dp,
        color = Color(0xFFF2F5FF)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Icon(
                painter = painterResource("Icons/pdf.png"),
                contentDescription = "PDF",
                tint = Color.Unspecified,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = fileName,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1976D2),
                fontSize = 14.sp,
                maxLines = 2
            )
        }
    }
}