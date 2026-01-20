//package com.example.dat_lich_kham_fe.ui.component
//
//import android.content.Context
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Card
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalConfiguration
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.colorResource
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.dat_lich_kham_fe.R
//import com.example.dat_lich_kham_fe.data.api.RetrofitInstance
//import com.example.dat_lich_kham_fe.data.model.FreeTimeRequest
//import com.example.dat_lich_kham_fe.util.divideAndRound
//import kotlinx.coroutines.launch
//import java.text.SimpleDateFormat
//import java.util.*
//
//@Composable
//fun TimeCard(
//    doctorId: Int,
//    selectedDate: String?, // ngày đã chọn, định dạng "dd/MM/yyyy"
//    onTimeSelected: (String) -> Unit
//) {
//    val times = listOf(
//        "07:00", "07:15", "07:30", "07:45", "08:00", "08:15",
//        "08:30", "08:45", "09:00", "14:00", "14:15", "14:30",
//        "14:45", "15:00","15:15", "15:30", "15:45", "18:00"
//    )
//
//    val configuration = LocalConfiguration.current
//    val screenWidth = configuration.screenWidthDp.dp
//    val screenWidthValue = screenWidth.value
//    val context = LocalContext.current
//
//    var selectedTime by remember { mutableStateOf<String?>(null) }
//    val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
//    val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
//    val isToday = selectedDate == currentDate
//
//    // State chứa danh sách giờ rảnh trả về từ API (freeSlots)
//    var freeSlots by remember { mutableStateOf<List<String>>(emptyList()) }
//
//    // Gọi API khi doctorId hoặc selectedDate thay đổi
//    LaunchedEffect(doctorId, selectedDate) {
//        if (doctorId != 0 && !selectedDate.isNullOrEmpty()) {
//            // Chuyển định dạng ngày sang yyyy-MM-dd (API yêu cầu)
//            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//            val dateApi = try {
//                val dateObj = inputFormat.parse(selectedDate)
//                outputFormat.format(dateObj)
//            } catch (e: Exception) {
//                selectedDate // fallback nếu lỗi parse
//            }
//
//            val api = RetrofitInstance.appointmentApi(context)
//            try {
//                val resp = api.freetime(
//                    FreeTimeRequest(
//                        doctorId = doctorId,
//                        date = dateApi,
//                        slots = times
//                    )
//                )
//                if (resp.isSuccessful) {
//                    freeSlots = resp.body()?.freeSlots ?: emptyList()
//                } else {
//                    freeSlots = emptyList()
//                }
//            } catch (e: Exception) {
//                freeSlots = emptyList()
//            }
//        }
//    }
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding((16 * divideAndRound(screenWidthValue)).dp),
//        shape = RoundedCornerShape((16 * divideAndRound(screenWidthValue)).dp),
//        elevation = androidx.compose.material3.CardDefaults.cardElevation((4 * divideAndRound(screenWidthValue)).dp)
//    ) {
//        Column(
//            modifier = Modifier.padding((16 * divideAndRound(screenWidthValue)).dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(
//                text = stringResource(id = R.string.choose_an_appointment_time),
//                fontSize = (16 * divideAndRound(screenWidthValue)).sp,
//                fontWeight = FontWeight.Bold,
//                color = colorResource(id = R.color.darkblue)
//            )
//            Spacer(modifier = Modifier.height((16 * divideAndRound(screenWidthValue)).dp))
//            LazyVerticalGrid(
//                columns = GridCells.Fixed(3),
//                verticalArrangement = Arrangement.spacedBy((8 * divideAndRound(screenWidthValue)).dp),
//                horizontalArrangement = Arrangement.spacedBy((8 * divideAndRound(screenWidthValue)).dp),
//                contentPadding = PaddingValues((4 * divideAndRound(screenWidthValue)).dp)
//            ) {
//                items(times.size) { index ->
//                    val time = times[index]
//                    // Đã trôi qua?
//                    val isPast = if (isToday) {
//                        val timeAsDate = SimpleDateFormat("H:mm", Locale.getDefault()).parse(time)
//                        timeAsDate?.before(SimpleDateFormat("H:mm", Locale.getDefault()).parse(currentTime)) ?: true
//                    } else {
//                        false
//                    }
//                    // Bận (not free)?
//                    val isBusy = !freeSlots.contains(time)
//                    val isDisabled = isPast || isBusy
//
//                    Box(
//                        contentAlignment = Alignment.Center,
//                        modifier = Modifier
//                            .size((80 * divideAndRound(screenWidthValue)).dp, (40 * divideAndRound(screenWidthValue)).dp)
//                            .clip(RoundedCornerShape((8 * divideAndRound(screenWidthValue)).dp))
//                            .background(
//                                color = when {
//                                    isDisabled -> Color.Gray
//                                    selectedTime == time -> colorResource(id = R.color.teal_700)
//                                    else -> Color.LightGray
//                                }
//                            )
//                            .clickable(enabled = !isDisabled) {
//                                selectedTime = time
//                                onTimeSelected(time)
//                            }
//                    ) {
//                        Text(
//                            text = time,
//                            color = if (isDisabled) Color.White else Color.Black,
//                            fontSize = (14 * divideAndRound(screenWidthValue)).sp,
//                            fontWeight = FontWeight.Bold
//                        )
//                    }
//                }
//            }
//        }
//    }
//}

package com.example.dat_lich_kham_fe.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.data.api.RetrofitInstance
import com.example.dat_lich_kham_fe.data.model.FreeTimeRequest
import com.example.dat_lich_kham_fe.util.divideAndRound
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TimeCard(
    doctorId: Int,
    selectedDate: String?,
    onTimeSelected: (String) -> Unit
) {
    val times = listOf(
        "07:00", "07:15", "07:30", "07:45", "08:00", "08:15",
        "08:30", "08:45", "09:00", "14:00", "14:15", "14:30",
        "14:45", "15:00","15:15", "15:30", "15:45", "18:00",
    )

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    val context = LocalContext.current

    var selectedTime by remember { mutableStateOf<String?>(null) }
    val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    val isToday = selectedDate == currentDate

    var freeSlots by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(doctorId, selectedDate) {
        if (doctorId != 0 && !selectedDate.isNullOrEmpty()) {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateApi = try {
                val dateObj = inputFormat.parse(selectedDate)
                outputFormat.format(dateObj)
            } catch (e: Exception) {
                selectedDate
            }

            val api = RetrofitInstance.appointmentApi(context)
            try {
                val resp = api.freetime(
                    FreeTimeRequest(
                        doctorId = doctorId,
                        date = dateApi,
                        slots = times
                    )
                )
                if (resp.isSuccessful) {
                    freeSlots = resp.body()?.freeSlots ?: emptyList()
                } else {
                    freeSlots = emptyList()
                }
            } catch (e: Exception) {
                freeSlots = emptyList()
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = (16 * divideAndRound(screenWidthValue)).dp)
            .shadow(
                elevation = (8 * divideAndRound(screenWidthValue)).dp,
                shape = RoundedCornerShape((20 * divideAndRound(screenWidthValue)).dp)
            ),
        shape = RoundedCornerShape((20 * divideAndRound(screenWidthValue)).dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            Color(0xFFF8FAFB)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding((20 * divideAndRound(screenWidthValue)).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    colorResource(id = R.color.darkblue).copy(alpha = 0.1f),
                                    colorResource(id = R.color.darkblue).copy(alpha = 0.05f)
                                )
                            ),
                            shape = RoundedCornerShape((12 * divideAndRound(screenWidthValue)).dp)
                        )
                        .padding(
                            horizontal = (16 * divideAndRound(screenWidthValue)).dp,
                            vertical = (12 * divideAndRound(screenWidthValue)).dp
                        )
                ) {
                    Text(
                        text = stringResource(id = R.string.choose_an_appointment_time),
                        fontSize = (16 * divideAndRound(screenWidthValue)).sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.darkblue)
                    )
                }

                Spacer(modifier = Modifier.height((20 * divideAndRound(screenWidthValue)).dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy((10 * divideAndRound(screenWidthValue)).dp),
                    horizontalArrangement = Arrangement.spacedBy((10 * divideAndRound(screenWidthValue)).dp),
                    contentPadding = PaddingValues((4 * divideAndRound(screenWidthValue)).dp)
                ) {
                    items(times.size) { index ->
                        val time = times[index]
                        val isPast = if (isToday) {
                            val timeAsDate = SimpleDateFormat("H:mm", Locale.getDefault()).parse(time)
                            timeAsDate?.before(SimpleDateFormat("H:mm", Locale.getDefault()).parse(currentTime)) ?: true
                        } else {
                            false
                        }
                        val isBusy = !freeSlots.contains(time)
                        val isDisabled = isPast || isBusy
                        val isSelected = selectedTime == time

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .height((48 * divideAndRound(screenWidthValue)).dp)
                                .clip(RoundedCornerShape((12 * divideAndRound(screenWidthValue)).dp))
                                .background(
                                    when {
                                        isSelected -> Brush.linearGradient(
                                            colors = listOf(
                                                colorResource(id = R.color.teal_700),
                                                colorResource(id = R.color.teal_700).copy(alpha = 0.8f)
                                            )
                                        )
                                        isDisabled -> Brush.linearGradient(
                                            colors = listOf(Color(0xFFE0E0E0), Color(0xFFEEEEEE))
                                        )
                                        else -> Brush.linearGradient(
                                            colors = listOf(Color(0xFFF5F5F5), Color(0xFFFFFFFF))
                                        )
                                    }
                                )
                                .then(
                                    if (!isDisabled && !isSelected) {
                                        Modifier.border(
                                            width = (1.5 * divideAndRound(screenWidthValue)).dp,
                                            color = colorResource(id = R.color.darkblue).copy(alpha = 0.3f),
                                            shape = RoundedCornerShape((12 * divideAndRound(screenWidthValue)).dp)
                                        )
                                    } else Modifier
                                )
                                .clickable(enabled = !isDisabled) {
                                    selectedTime = time
                                    onTimeSelected(time)
                                }
                        ) {
                            Text(
                                text = time,
                                color = when {
                                    isSelected -> Color.White
                                    isDisabled -> Color(0xFF9E9E9E)
                                    else -> Color(0xFF424242)
                                },
                                fontSize = (14 * divideAndRound(screenWidthValue)).sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
