package com.example.nhom1_fe_datnhahang.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

fun formatNumber(number: Int): String {
    return "%,d".format(number).replace(',', '.')
}
@RequiresApi(Build.VERSION_CODES.O)
fun chuyenDoiNgay(date: String): String {
    if (date.isEmpty()) {
        // Nếu chuỗi rỗng, trả về chuỗi mặc định hoặc thông báo lỗi
        return "Ngày không hợp lệ"
    }

    return try {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd") // Định dạng đầu vào
        val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy") // Định dạng đầu ra
        val parsedDate = LocalDate.parse(date, inputFormatter) // Chuyển từ chuỗi sang LocalDate
        parsedDate.format(outputFormatter) // Chuyển từ LocalDate sang chuỗi theo định dạng mới
    } catch (e: DateTimeParseException) {
        // Xử lý khi không thể phân tích được ngày (ngày không hợp lệ)
        "Ngày không hợp lệ"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun chuyenDoiGio(time: String): String {
    val inputFormatter = DateTimeFormatter.ofPattern("HH:mm:ss") // Định dạng đầu vào
    val outputFormatter = DateTimeFormatter.ofPattern("HH:mm")  // Định dạng đầu ra
    val parsedTime = LocalTime.parse(time, inputFormatter)      // Chuyển từ chuỗi sang LocalTime
    return parsedTime.format(outputFormatter)                  // Chuyển từ LocalTime sang chuỗi theo định dạng mới
}
@RequiresApi(Build.VERSION_CODES.O)
fun soSanhThoiGian(thoiGian: String): String {
    // Parse thời gian từ chuỗi đầu vào (ISO format)
    val parsedTime = LocalDateTime.parse(thoiGian)

    // Lấy thời gian hiện tại
    val now = LocalDateTime.now()

    // Chuyển đổi sang Instant với ZoneOffset.UTC
    val nowInstant = now.toInstant(ZoneOffset.UTC)
    val parsedTimeInstant = parsedTime.toInstant(ZoneOffset.UTC)

    // Tính khoảng cách giữa hai thời điểm
    val duration = Duration.between(parsedTimeInstant, nowInstant)

    // Convert sang các đơn vị thời gian
    val minutes = duration.toMinutes()
    val hours = duration.toHours()
    val days = ChronoUnit.DAYS.between(parsedTime, now)

    // Định dạng thời gian theo định dạng ngày
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // Trả về chuỗi
    return when {
        days >= 3 -> parsedTime.format(dateFormatter) // Nếu lớn hơn hoặc bằng 3 ngày, trả về ngày cụ thể
        hours >= 24 -> "$days ngày trước"            // Nếu từ 1 đến dưới 3 ngày, trả về "x ngày trước"
        hours > 0 -> "$hours giờ trước"              // Nếu dưới 1 ngày, trả về "x giờ trước"
        else -> "$minutes phút trước"                // Nếu dưới 1 giờ, trả về "x phút trước"
    }
}

fun removeMilliseconds(dateTime: String): String {
    // Nếu có dấu . thì cắt đến trước dấu .
    val dotIndex = dateTime.indexOf('.')
    return if (dotIndex != -1) {
        dateTime.substring(0, dotIndex)
    } else {
        dateTime
    }
}
