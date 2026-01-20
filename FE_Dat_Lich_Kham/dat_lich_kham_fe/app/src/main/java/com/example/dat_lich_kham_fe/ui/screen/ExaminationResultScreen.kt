package com.example.dat_lich_kham_fe.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.data.api.address
import com.example.dat_lich_kham_fe.data.model.ResultFileItem
import com.example.dat_lich_kham_fe.ui.component.AppBarView
import com.example.dat_lich_kham_fe.ui.component.FileCard
import com.example.dat_lich_kham_fe.util.chuyenDoiNgay
import com.example.dat_lich_kham_fe.util.divideAndRound
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExaminationResultScreen(
    navController: NavController,
    appointmentId: Int,
    fullName: String,
    comment: String,
    resultFiles: List<ResultFileItem>,
    examDate: String,
    onClicked: () -> Unit = {},
) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    val scale = divideAndRound(screenWidthValue)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        AppBarView(
            title = stringResource(id = R.string.examination_result_title),
            color = R.color.white,
            backgroundColor = R.color.darkblue,
            alignment = Alignment.Center,
            isVisible = true,
            onDeleteNavClicked = { navController.popBackStack() }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Patient Info Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = (16 * scale).dp)
                        .padding(top = (16 * scale).dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = (6 * scale).dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape((12 * scale).dp)
                ) {
                    Column(
                        modifier = Modifier.padding((20 * scale).dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.patient_information),
                            fontSize = (18 * scale).sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.darkblue),
                            modifier = Modifier.padding(bottom = (16 * scale).dp)
                        )

                        // Patient Name
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = (12 * scale).dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Patient",
                                tint = colorResource(id = R.color.darkblue),
                                modifier = Modifier.size((20 * scale).dp)
                            )
                            Spacer(modifier = Modifier.width((12 * scale).dp))
                            Column {
                                Text(
                                    text = stringResource(id = R.string.full_name),
                                    fontSize = (12 * scale).sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = fullName,
                                    fontSize = (16 * scale).sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                            }
                        }

                        // Exam Date
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Date",
                                tint = colorResource(id = R.color.darkblue),
                                modifier = Modifier.size((20 * scale).dp)
                            )
                            Spacer(modifier = Modifier.width((12 * scale).dp))
                            Column {
                                Text(
                                    text = stringResource(id = R.string.appointment_date),
                                    fontSize = (12 * scale).sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = chuyenDoiNgay( examDate),
                                    fontSize = (16 * scale).sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }

            // Doctor's Comment Section
            if (comment.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = (16 * scale).dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = (6 * scale).dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape((12 * scale).dp)
                    ) {
                        Column(
                            modifier = Modifier.padding((20 * scale).dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = (12 * scale).dp)
                            ) {
                                Spacer(modifier = Modifier.width((12 * scale).dp))
                                Text(
                                    text = stringResource(id = R.string.comment),
                                    fontSize = ((18 * scale) * scale).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colorResource(id = R.color.darkblue)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Color(0xFFF8F9FA),
                                        RoundedCornerShape((8 * scale).dp)
                                    )
                                    .padding((16 * scale).dp)
                            ) {
                                Text(
                                    text = comment,
                                    fontSize = (14 * scale).sp,
                                    color = Color.Black,
                                    lineHeight = (20 * scale).sp
                                )
                            }
                        }
                    }
                }
            }

            // Files Section
            if (resultFiles.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = (16 * scale).dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = (6 * scale).dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape((12 * scale).dp)
                    ) {
                        Column(
                            modifier = Modifier.padding((20 * scale).dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = (16 * scale).dp)
                            ) {
                                Spacer(modifier = Modifier.width((12 * scale).dp))
                                Text(
                                    text = "${stringResource(id = R.string.document)} (${resultFiles.size})",
                                    fontSize = (18 * scale).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colorResource(id = R.color.darkblue)
                                )
                            }
                        }
                    }
                }
            }

            // File Items
            items(resultFiles) { file ->
                // 🐛 DEBUG: In ra filePath để kiểm tra
                println("📄 File ID: ${file.id}")
                println("   fileName: ${file.fileName}")
                println("   filePath: ${file.filePath}")

                FileCard(
                    id = file.id,
                    fileName = file.fileName,
                    filePath = file.filePath,
                    modifier = Modifier.padding(horizontal = (16 * scale).dp),
                    onClicked = { fileName, filePath ->
                        // Tạo URL từ filePath (đã là signed nếu file đã ký)
                        val encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                        val fileUrl = "${address}api/$filePath"  // Dùng filePath từ ResultFileItem
                        val encodedFileUrl = URLEncoder.encode(fileUrl, StandardCharsets.UTF_8.toString())

                        println("🔗 Opening URL: $fileUrl")
                        navController.navigate("pdf_viewer/$encodedFileName/$encodedFileUrl")
                    }
                )
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height((16 * scale).dp))
            }
        }
    }
}