package com.example.dat_lich_kham_fe.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.ui.component.AppBarView
import com.example.dat_lich_kham_fe.ui.component.ResultCard
import com.example.dat_lich_kham_fe.util.UserLocalStore
import com.example.dat_lich_kham_fe.util.divideAndRound
import com.example.dat_lich_kham_fe.viewmodel.ResultViewModel
import com.google.gson.Gson
import okhttp3.internal.http2.Header
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HealthRecordScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        val configuration = LocalConfiguration.current
        val context = LocalContext.current
        val screenWidth = configuration.screenWidthDp.dp
        val screenWidthValue = screenWidth.value
        val scale = divideAndRound(screenWidthValue)

        val resultViewModel = remember { ResultViewModel(context) }
        val userStore = UserLocalStore(context)
        var userId by remember { mutableStateOf(0) }
        LaunchedEffect(Unit) {
            val user = userStore.getUser()
            userId = user?.Id ?: 0
            resultViewModel.fetchResults(userId)
        }
        val results = resultViewModel.results

        AppBarView(
            title = stringResource(id = R.string.health_record_title),
            color = R.color.white,
            backgroundColor = R.color.darkblue,
            alignment = Alignment.Center,
            onDeleteNavClicked = { navController.popBackStack() },
            isVisible = true
        )

        if(results.isEmpty()){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding((32 * scale).dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow((4 * scale).dp, RoundedCornerShape((16 * scale).dp)),
                    shape = RoundedCornerShape((16 * scale).dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding((32 * scale).dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.medicalexaminationresults),
                            contentDescription = null,
                            tint = Color(0xFFE5E7EB),
                            modifier = Modifier.size((80 * scale).dp)
                        )
                        Spacer(modifier = Modifier.height((16 * scale).dp))
                        Text(
                            text = stringResource(id = R.string.no_health_record_yet),
                            fontSize = (16 * scale).sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF6B7280),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height((8 * scale).dp))
                        Text(
                            text = "Các kết quả khám sẽ hiển thị tại đây sau khi bạn hoàn thành khám bệnh",
                            fontSize = (14 * scale).sp,
                            color = Color(0xFF9CA3AF),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        } else {
            Column {
                // Header Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding((16 * scale).dp)
                        .shadow((4 * scale).dp, RoundedCornerShape((16 * scale).dp)),
                    shape = RoundedCornerShape((16 * scale).dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding((20 * scale).dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.medicalexaminationresults),
                            contentDescription = null,
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size((32 * scale).dp)
                        )
                        Spacer(modifier = Modifier.height((12 * scale).dp))
                        Text(
                            text = "Hồ sơ sức khỏe",
                            fontSize = (18 * scale).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "${results.size} kết quả khám bệnh",
                            fontSize = (14 * scale).sp,
                            color = Color(0xFF6B7280),
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier.padding(horizontal = (16 * scale).dp)
                ) {
                    items(results) { result ->
                        ResultCard(
                            appointmentId = result.appointmentId,
                            fullName = result.fullName,
                            comment = result.comment,
                            resultFiles = result.resultFiles,
                            examDate = result.examDate,
                            onClicked = {
                                val gson = Gson()
                                val resultFilesJson = gson.toJson(result.resultFiles)
                                val encodedResultFiles = URLEncoder.encode(resultFilesJson, StandardCharsets.UTF_8.toString())
                                val encodedFullName = URLEncoder.encode(result.fullName, StandardCharsets.UTF_8.toString())
                                val encodedComment = URLEncoder.encode(result.comment, StandardCharsets.UTF_8.toString())
                                val encodedExamDate = URLEncoder.encode(result.examDate, StandardCharsets.UTF_8.toString())

                                navController.navigate(
                                    "ExaminationResultScreen/${result.appointmentId}/" +
                                        "$encodedFullName/" +
                                        "$encodedComment/" +
                                        "$encodedResultFiles/" +
                                        "$encodedExamDate"
                                )
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height((16 * scale).dp))
                    }
                }
            }
        }
    }
}
