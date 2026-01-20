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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.util.divideAndRound
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


data class Day(
    val name: String,
    val date: String,
    val fullDate: String,
    val isPast: Boolean = false,
    var isSelected: Boolean = false
)

fun getWeekDays(startDate: Calendar) : List<Day>{
    val daysOfWeek = mutableListOf<Day>()
    val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
    val fullDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val today = Calendar.getInstance()


    // Đặt ngày bắt đầu là thứ 2 đầu tuần
    val calendar = startDate.clone() as Calendar
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

    for (i in 0..6) {
        val dayName = if (i == 6) "CN" else "T${i + 2}" // "T2", "T3", ..., "CN"
        val fullDate = fullDateFormat.format(calendar.time)
        val date = dateFormat.format(calendar.time)
        val isPast = calendar.before(today) && calendar.get(Calendar.DAY_OF_YEAR) != today.get(Calendar.DAY_OF_YEAR)//|| dayName == "CN"
        val isSelected = calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

        daysOfWeek.add(Day(name = dayName, date = date,fullDate = fullDate, isPast = isPast, isSelected = isSelected))
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }
    return daysOfWeek

}

fun getWeeks(numWeeks: Int):  List<List<Day>>{
    val weeks = mutableListOf<List<Day>>()
    val calendar = Calendar.getInstance()

    // Tạo tuần hiện tại và các tuần tiếp theo
    for (i in 0 until numWeeks) {
        weeks.add(getWeekDays(calendar))
        calendar.add(Calendar.WEEK_OF_YEAR, 1) // Chuyển sang tuần tiếp theo
    }
    return weeks

}

@Composable
fun DayItem(
    day: Day,
    onClick: (Day) -> Unit,

    ) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    Box(
        modifier = Modifier
            .padding((2 * divideAndRound(screenWidthValue)).dp)
            .width((50 * divideAndRound(screenWidthValue)).dp)
            .height((60 * divideAndRound(screenWidthValue)).dp)
            .background(
                color = when {
                    day.isSelected -> colorResource(id = R.color.darkblue)
                    day.isPast -> Color(0xffdfe6eb)
                    else -> Color(0xFFE0F7FA)
                },
                shape = RoundedCornerShape((8 * divideAndRound(screenWidthValue)).dp)
            )
            .clickable(enabled = !day.isPast) { onClick(day) },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.name,
                fontWeight = FontWeight.Bold,
                color = when{
                    day.isSelected -> Color.White
                    day.isPast -> Color.Gray
                    else -> Color.Black
                }
            )
            Spacer(modifier = Modifier.height((4 * divideAndRound(screenWidthValue)).dp))
            Text(
                text = day.date,
                color = when{
                    day.isSelected -> Color.White
                    day.isPast -> Color.Gray
                    else -> Color.Gray
                },
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun WeekRow(week: List<Day>, onDaySelected: (Day) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        week.forEach { day ->
            DayItem(day = day) { selectedDay ->
                onDaySelected(selectedDay)
            }
        }
    }
}


@Composable
fun WeekDaysRow(onDaySelected: (Day) -> Unit) {

    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    val weeks = remember { mutableStateListOf<List<Day>>() }
    val calendar = Calendar.getInstance()
    val selectedDay = remember { mutableStateOf<Day?>(null) }
    val today = remember { mutableStateOf<Day?>(null) }

    LaunchedEffect(Unit) {
        repeat(1) {
            val weekDays = getWeekDays(calendar.clone() as Calendar)

            weeks.add(weekDays)
            today.value = weekDays.firstOrNull { it.isSelected } // Đánh dấu ngày hôm nay

            // Đặt ngày hôm nay là ngày được chọn ban đầu
            selectedDay.value = today.value
            selectedDay.value?.let { onDaySelected(it) }
            calendar.add(Calendar.WEEK_OF_YEAR, 1)
        }
    }

    LazyRow {
        itemsIndexed(weeks) { index, week ->
            WeekRow(week = week) { day ->
                // Cập nhật danh sách tuần với ngày được chọn
                val updatedWeeks = weeks.map { weekList ->
                    weekList.map { currentDay ->
                        if (currentDay == day) {
                            currentDay.copy(isSelected = true)
                        } else {
                            currentDay.copy(isSelected = false)
                        }
                    }
                }
                weeks.clear()
                weeks.addAll(updatedWeeks)

                selectedDay.value = day
                onDaySelected(day) // Truyền ngày được chọn lên `ClinicDetailScreen`
            }
            Spacer(modifier = Modifier.width((24 * divideAndRound(screenWidthValue)).dp))

            if (index == weeks.size - 1) {
                weeks.add(getWeekDays(calendar.clone() as Calendar))
                calendar.add(Calendar.WEEK_OF_YEAR, 1)
            }
        }
    }
}
