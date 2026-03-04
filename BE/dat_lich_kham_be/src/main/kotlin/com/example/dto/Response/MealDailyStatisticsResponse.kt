package com.example.dto.Response

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class MealDailyStatisticsResponse(
    val date: LocalDate,
    val dayOfWeek: String, // "Thứ 2", "Thứ 3", etc.
    val totalRegistered: Int, // Tổng số người đăng ký
    val totalSkipped: Int, // Số người đã cắt cơm
    val totalServing: Int, // Số suất ăn thực tế phục vụ
    val mealDetails: List<MealDetailItem>
)

@Serializable
data class MealDetailItem(
    val inpatientId: Int,
    val userId: Int,
    val fullName: String,
    val address: String,
    val cycleId: Int,
    val cycleNumber: Int,
    val status: String, // "Được ăn cơm" hoặc "Đã cắt cơm"
    val isSkipped: Boolean
)

@Serializable
data class MealWeeklyStatisticsResponse(
    val weekStart: LocalDate,
    val weekEnd: LocalDate,
    val dailyStats: List<MealDailyStatisticsResponse>
)

@Serializable
data class MealMonthlyStatisticsResponse(
    val month: Int,
    val year: Int,
    val totalMealsServed: Int,
    val totalMealsSkipped: Int,
    val totalRevenue: Int,
    val dailyStats: List<MealDailyStatisticsResponse>
)