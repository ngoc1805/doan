package com.example.dto.Response

import com.example.models.MealSubscriptionCycle
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class MealStatusResponse(
    val canEat: Boolean,
    val status: String, // "Được ăn cơm", "Đã cắt cơm", "Không đăng ký", "Chu kỳ không active"
    val currentCycle: MealSubscriptionCycle?,
    val isSkippedToday: Boolean
)

@Serializable
data class MealCycleListResponse(
    val cycles: List<MealSubscriptionCycle>
)

@Serializable
data class MealStatisticsResponse(
    val totalCycles: Int,
    val totalExpectedDays: Int,
    val totalDaysAte: Int,
    val totalDaysSkipped: Int,
    val attendanceRate: Double
)

@Serializable
data class DailyMealListResponse(
    val date: LocalDate,
    val totalRegistered: Int,
    val patientsEating: List<PatientMealInfo>,
    val patientsSkipped: List<PatientMealInfo>
)

@Serializable
data class PatientMealInfo(
    val inpatientId: Int,
    val userId: Int,
    val fullName: String,
    val roomNumber: String?,
    val bedNumber: String?,
    val cycleNumber: Int
)