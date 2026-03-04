package com.example.dat_lich_kham_fe.data.model

data class MealSubscriptionRequest(
    val inpatientId: Int
)

data class MealSkipRequest(
    val inpatientId: Int,
    val skipDate: String // yyyy-MM-dd
)

data class MealStatusResponse(
    val canEat: Boolean,
    val status: String,
    val currentCycle: MealSubscriptionCycle?,
    val isSkippedToday: Boolean
)

data class MealSubscriptionCycle(
    val id: Int,
    val inpatientId: Int,
    val cycleNumber: Int,
    val weekStartDate: String,
    val weekEndDate: String,
    val registrationDate: String,
    val status: String,
    val daysInCycle: Int,
    val expectedMealDays: Int,
    val actualMealDays: Int,
    val actualSkipDays: Int,
    val createdAt: String,
    val updatedAt: String
)

data class MealCycleListResponse(
    val cycles: List<MealSubscriptionCycle>
)

data class MealStatisticsResponse(
    val totalCycles: Int,
    val totalExpectedDays: Int,
    val totalDaysAte: Int,
    val totalDaysSkipped: Int,
    val attendanceRate: Double
)

data class InpatientItem(
    val id: Int,
    val userId: Int,
    val fullname: String,
    val gender: String,
    val birthDate: String,
    val cccd: String,
    val hometown: String,
    val address: String,
    val status: String,
    val createAt: String,
    val admissionDate: String?,
    val dischargeDate: String?,
    val roomNumber: String?,
    val bedNumber: String?
)

data class InpatientListResponse(
    val inpatients: List<InpatientItem>
)
