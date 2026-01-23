package com.example.service

import com.example.dto.Request.MealSubscriptionRequest
import com.example.dto.Request.MealSkipRequest
import com.example.dto.Response.MealCycleListResponse
import com.example.dto.Response.MealStatisticsResponse
import com.example.dto.Response.MealStatusResponse
import com.example.repository.MealRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class MealService(private val mealRepository: MealRepository) {

    fun registerMeal(request: MealSubscriptionRequest): Int? {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return mealRepository.createCycle(request.inpatientId, today)
    }

    fun renewMeal(inpatientId: Int): Int? {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return mealRepository.createCycle(inpatientId, today)
    }

    fun activateCycle(cycleId: Int): Boolean {
        return mealRepository.activateCycle(cycleId)
    }

    fun skipMeal(request: MealSkipRequest): Boolean {
        return mealRepository.skipMeal(request.inpatientId, request.skipDate)
    }

    fun getMealStatusToday(inpatientId: Int): MealStatusResponse {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val (status, cycle) = mealRepository.getMealStatusToday(inpatientId, today)

        return MealStatusResponse(
            canEat = status == "Được ăn cơm",
            status = status,
            currentCycle = cycle?.toModel(),
            isSkippedToday = status == "Đã cắt cơm"
        )
    }

    fun getMealHistory(inpatientId: Int): MealCycleListResponse {
        val cycles = mealRepository.getCyclesByInpatient(inpatientId).map { it.toModel() }
        return MealCycleListResponse(cycles)
    }

    fun getMealStatistics(inpatientId: Int): MealStatisticsResponse {
        val cycles = mealRepository.getCyclesByInpatient(inpatientId)

        val totalExpected = cycles.sumOf { it.expectedMealDays }
        val totalAte = cycles.sumOf { it.actualMealDays }
        val totalSkipped = cycles.sumOf { it.actualSkipDays }
        val attendanceRate = if (totalExpected > 0) {
            (totalAte.toDouble() / totalExpected) * 100
        } else 0.0

        return MealStatisticsResponse(
            totalCycles = cycles.size,
            totalExpectedDays = totalExpected,
            totalDaysAte = totalAte,
            totalDaysSkipped = totalSkipped,
            attendanceRate = attendanceRate
        )
    }
}