package com.example.service

import com.example.dto.Request.MealSubscriptionRequest
import com.example.dto.Request.MealSkipRequest
import com.example.dto.Response.MealCycleListResponse
import com.example.dto.Response.MealStatisticsResponse
import com.example.dto.Response.MealStatusResponse
import com.example.repository.MealRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.jetbrains.exposed.sql.transactions.transaction

class MealService(private val mealRepository: MealRepository) {

    /**
     * Đăng ký suất ăn lần đầu
     * @return Pair(cycleId, totalCost) hoặc null nếu thất bại
     */
    fun registerMeal(request: MealSubscriptionRequest): Pair<Int, Int>? {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return mealRepository.createCycle(request.inpatientId, today)
    }

    /**
     * Gia hạn suất ăn (tạo chu kỳ mới cho tuần sau)
     * @return Pair(cycleId, totalCost) hoặc null nếu thất bại
     */
    fun renewMeal(inpatientId: Int): Pair<Int, Int>? {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return mealRepository.createCycle(inpatientId, today)
    }

    /**
     * Kiểm tra có thể gia hạn không (chỉ từ T6 trở đi)
     */
    fun canRenew(inpatientId: Int): Boolean {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return mealRepository.canRenew(inpatientId, today)
    }

    /**
     * Kích hoạt chu kỳ sau khi thanh toán thành công
     */
    fun activateCycle(cycleId: Int): Boolean {
        return mealRepository.activateCycle(cycleId)
    }

    /**
     * Cắt cơm (chỉ được cắt trong ngày T2-T6)
     */
    fun skipMeal(request: MealSkipRequest): Boolean {
        return mealRepository.skipMeal(request.inpatientId, request.skipDate)
    }

    /**
     * Lấy trạng thái ăn cơm hôm nay
     */
    fun getMealStatusToday(inpatientId: Int): MealStatusResponse {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val (status, cycle, isSkipped) = mealRepository.getMealStatusToday(inpatientId, today)

        return MealStatusResponse(
            canEat = status == "Được ăn cơm",
            status = status,
            currentCycle = cycle?.toModel(),
            isSkippedToday = isSkipped
        )
    }

    /**
     * Lấy lịch sử các chu kỳ suất ăn
     */
    fun getMealHistory(inpatientId: Int): MealCycleListResponse {
        val cycles = mealRepository.getCyclesByInpatient(inpatientId).map { it.toModel() }
        return MealCycleListResponse(cycles)
    }

    /**
     * Lấy thống kê suất ăn
     */
    fun getMealStatistics(inpatientId: Int): MealStatisticsResponse {
        val cycles = mealRepository.getCyclesByInpatient(inpatientId)
            .filter { it.status == "completed" } // Chỉ tính các chu kỳ đã hoàn thành

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

    /**
     * Tính tiền hoàn lại khi xuất viện giữa chu kỳ
     */
    fun calculateRefund(inpatientId: Int, dischargeDate: LocalDate): Int {
        return mealRepository.calculateRefund(inpatientId, dischargeDate)
    }

    /**
     * Đóng chu kỳ khi hết tuần
     */
    fun completeCycle(cycleId: Int): Boolean {
        return mealRepository.completeCycle(cycleId)
    }

    /**
     * Admin: Thống kê suất ăn theo ngày
     */
    fun getDailyStatistics(date: LocalDate): com.example.dto.Response.MealDailyStatisticsResponse = transaction {
        val data = mealRepository.getMealStatisticsByDate(date)

        val totalRegistered = data.size
        val totalSkipped = data.count { it.third }
        val totalServing = totalRegistered - totalSkipped

        val mealDetails = data.map { (cycle, inpatient, isSkipped) ->
            val userDao = com.example.dao.UsersDAO.findById(inpatient.userId.value)
            val decryptedFullName = userDao?.fullName?.let { com.example.utils.EncryptionUtil.decrypt(it) } ?: ""

            com.example.dto.Response.MealDetailItem(
                inpatientId = inpatient.id.value,
                userId = inpatient.userId.value,
                fullName = decryptedFullName,
                address = inpatient.address ?: "",
                cycleId = cycle.id.value,
                cycleNumber = cycle.cycleNumber,
                status = if (isSkipped) "Đã cắt cơm" else "Được ăn cơm",
                isSkipped = isSkipped
            )
        }

        val dateJava = java.time.LocalDate.parse(date.toString())
        val dayOfWeek = when (dateJava.dayOfWeek) {
            java.time.DayOfWeek.MONDAY -> "Thứ 2"
            java.time.DayOfWeek.TUESDAY -> "Thứ 3"
            java.time.DayOfWeek.WEDNESDAY -> "Thứ 4"
            java.time.DayOfWeek.THURSDAY -> "Thứ 5"
            java.time.DayOfWeek.FRIDAY -> "Thứ 6"
            java.time.DayOfWeek.SATURDAY -> "Thứ 7"
            java.time.DayOfWeek.SUNDAY -> "Chủ nhật"
            else -> ""
        }

        com.example.dto.Response.MealDailyStatisticsResponse(
            date = date,
            dayOfWeek = dayOfWeek,
            totalRegistered = totalRegistered,
            totalSkipped = totalSkipped,
            totalServing = totalServing,
            mealDetails = mealDetails
        )
    }

    /**
     * Admin: Thống kê suất ăn theo tuần
     */
    fun getWeeklyStatistics(weekStartDate: LocalDate): com.example.dto.Response.MealWeeklyStatisticsResponse {
        val weekData = mealRepository.getMealStatisticsByWeek(weekStartDate)

        val dailyStats = weekData.map { (date, data) ->
            val totalRegistered = data.size
            val totalSkipped = data.count { it.third }
            val totalServing = totalRegistered - totalSkipped

            val mealDetails = data.map { (cycle, inpatient, isSkipped) ->
                val userDao = com.example.dao.UsersDAO.findById(inpatient.userId.value)
                val decryptedFullName = userDao?.fullName?.let { com.example.utils.EncryptionUtil.decrypt(it) } ?: ""

                com.example.dto.Response.MealDetailItem(
                    inpatientId = inpatient.id.value,
                    userId = inpatient.userId.value,
                    fullName = decryptedFullName,
                    address = inpatient.address ?: "",
                    cycleId = cycle.id.value,
                    cycleNumber = cycle.cycleNumber,
                    status = if (isSkipped) "Đã cắt cơm" else "Được ăn cơm",
                    isSkipped = isSkipped
                )
            }

            val dateJava = java.time.LocalDate.parse(date.toString())
            val dayOfWeek = when (dateJava.dayOfWeek) {
                java.time.DayOfWeek.MONDAY -> "Thứ 2"
                java.time.DayOfWeek.TUESDAY -> "Thứ 3"
                java.time.DayOfWeek.WEDNESDAY -> "Thứ 4"
                java.time.DayOfWeek.THURSDAY -> "Thứ 5"
                java.time.DayOfWeek.FRIDAY -> "Thứ 6"
                java.time.DayOfWeek.SATURDAY -> "Thứ 7"
                java.time.DayOfWeek.SUNDAY -> "Chủ nhật"
                else -> ""
            }

            com.example.dto.Response.MealDailyStatisticsResponse(
                date = date,
                dayOfWeek = dayOfWeek,
                totalRegistered = totalRegistered,
                totalSkipped = totalSkipped,
                totalServing = totalServing,
                mealDetails = mealDetails
            )
        }.sortedBy { it.date }

        val weekEndDate = LocalDate.parse(
            java.time.LocalDate.parse(weekStartDate.toString()).plusDays(6).toString()
        )

        return com.example.dto.Response.MealWeeklyStatisticsResponse(
            weekStart = weekStartDate,
            weekEnd = weekEndDate,
            dailyStats = dailyStats
        )
    }

    /**
     * Admin: Thống kê suất ăn theo tháng
     */
    fun getMonthlyStatistics(year: Int, month: Int): com.example.dto.Response.MealMonthlyStatisticsResponse {
        val monthData = mealRepository.getMealStatisticsByMonth(year, month)

        var totalMealsServed = 0
        var totalMealsSkipped = 0

        val dailyStats = monthData.map { (date, data) ->
            val totalRegistered = data.size
            val totalSkipped = data.count { it.third }
            val totalServing = totalRegistered - totalSkipped

            totalMealsServed += totalServing
            totalMealsSkipped += totalSkipped

            val mealDetails = data.map { (cycle, inpatient, isSkipped) ->
                val userDao = com.example.dao.UsersDAO.findById(inpatient.userId.value)
                val decryptedFullName = userDao?.fullName?.let { com.example.utils.EncryptionUtil.decrypt(it) } ?: ""

                com.example.dto.Response.MealDetailItem(
                    inpatientId = inpatient.id.value,
                    userId = inpatient.userId.value,
                    fullName = decryptedFullName,
                    address = inpatient.address ?: "",
                    cycleId = cycle.id.value,
                    cycleNumber = cycle.cycleNumber,
                    status = if (isSkipped) "Đã cắt cơm" else "Được ăn cơm",
                    isSkipped = isSkipped
                )
            }

            val dateJava = java.time.LocalDate.parse(date.toString())
            val dayOfWeek = when (dateJava.dayOfWeek) {
                java.time.DayOfWeek.MONDAY -> "Thứ 2"
                java.time.DayOfWeek.TUESDAY -> "Thứ 3"
                java.time.DayOfWeek.WEDNESDAY -> "Thứ 4"
                java.time.DayOfWeek.THURSDAY -> "Thứ 5"
                java.time.DayOfWeek.FRIDAY -> "Thứ 6"
                java.time.DayOfWeek.SATURDAY -> "Thứ 7"
                java.time.DayOfWeek.SUNDAY -> "Chủ nhật"
                else -> ""
            }

            com.example.dto.Response.MealDailyStatisticsResponse(
                date = date,
                dayOfWeek = dayOfWeek,
                totalRegistered = totalRegistered,
                totalSkipped = totalSkipped,
                totalServing = totalServing,
                mealDetails = mealDetails
            )
        }.sortedBy { it.date }

        val totalRevenue = totalMealsServed * com.example.repository.MealRepository.PRICE_PER_DAY

        return com.example.dto.Response.MealMonthlyStatisticsResponse(
            month = month,
            year = year,
            totalMealsServed = totalMealsServed,
            totalMealsSkipped = totalMealsSkipped,
            totalRevenue = totalRevenue,
            dailyStats = dailyStats
        )
    }
}