package util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun formatDate(input: String): String {
    return try {
        val date = LocalDate.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    } catch (e: Exception) {
        input // Nếu lỗi thì trả về giá trị gốc
    }
}

fun formatDateTime(dateTime: String): String {
    if (dateTime.isBlank()) return "Chưa cập nhật"

    return try {
        // Format: "2025-10-05T17:16:01.698Z" -> "05/10/2025"
        val date = dateTime.substringBefore("T")
        val parts = date.split("-")
        if (parts.size == 3) {
            "${parts[2]}/${parts[1]}/${parts[0]}"
        } else {
            dateTime
        }
    } catch (e: Exception) {
        dateTime
    }
}