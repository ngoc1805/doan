package com.example.repository

import com.example.Tables.Inpatients
import com.example.Tables.MealSkipDates
import com.example.Tables.MealSubscriptionCycles
import com.example.dao.InpatientDAO
import com.example.dao.MealSkipDateDAO
import com.example.dao.MealSubscriptionCycleDAO
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.DayOfWeek

class MealRepository {

    // Tạo chu kỳ đăng ký mới
    fun createCycle(inpatientId: Int, registrationDate: LocalDate): Int? = transaction {
        val inpatient = InpatientDAO.findById(inpatientId) ?: return@transaction null
        if (inpatient.status != "Đã nhập viện") return@transaction null

        // Tính chu kỳ tiếp theo
        val lastCycle = MealSubscriptionCycleDAO.find {
            MealSubscriptionCycles.inpatientId eq inpatientId
        }.maxByOrNull { it.cycleNumber }

        val nextCycleNumber = (lastCycle?.cycleNumber ?: 0) + 1

        // Tính ngày bắt đầu (thứ 2 tuần sau)
        val startDate = calculateNextMonday(registrationDate)
        //val endDate = startDate.plusDays(6) // Chủ nhật
        val endDate = kotlinx.datetime.LocalDate.parse(
            java.time.LocalDate.parse(startDate.toString()).plusDays(6).toString()
        )

        val cycle = MealSubscriptionCycleDAO.new {
            this.inpatientId = EntityID(inpatientId, Inpatients)
            this.cycleNumber = nextCycleNumber
            this.weekStartDate = java.time.LocalDate.parse(startDate.toString())
            this.weekEndDate = java.time.LocalDate.parse(endDate.toString())
            this.registrationDate = java.time.LocalDate.parse(registrationDate.toString())
            this.status = "pending_payment"
            this.daysInCycle = 5
            this.expectedMealDays = 5
        }

        cycle.id.value
    }

    // Tính thứ 2 tuần tới
//    private fun calculateNextMonday(from: LocalDate): LocalDate {
//        val dayOfWeek = java.time.LocalDate.parse(from.toString()).dayOfWeek
//        return when (dayOfWeek) {
//            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
//            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY -> {
//                // T2-T6: thứ 2 tuần sau
//                from.plusDays((8 - dayOfWeek.value).toLong())
//            }
//            else -> {
//                // T7-CN: thứ 2 tuần sau
//                from.plusDays((8 - dayOfWeek.value).toLong())
//            }
//        }
//    }
    private fun calculateNextMonday(from: LocalDate): LocalDate {
        // Chuyển từ kotlinx.datetime.LocalDate sang java.time.LocalDate
        val fromJava = java.time.LocalDate.parse(from.toString())
        val dayOfWeek = fromJava.dayOfWeek
        val nextMondayJava = fromJava.plusDays((8 - dayOfWeek.value).toLong())
        // Chuyển ngược về kotlinx.datetime.LocalDate để trả về kiểu đúng
        return LocalDate.parse(nextMondayJava.toString())
    }

    // Cập nhật trạng thái sau thanh toán
    fun activateCycle(cycleId: Int): Boolean = transaction {
        val cycle = MealSubscriptionCycleDAO.findById(cycleId) ?: return@transaction false
        cycle.status = "active"
        true
    }

    // Cắt cơm
    fun skipMeal(inpatientId: Int, skipDate: LocalDate): Boolean = transaction {
        // Tìm chu kỳ đang active
        val cycle = MealSubscriptionCycleDAO.find {
            (MealSubscriptionCycles.inpatientId eq inpatientId) and
                    (MealSubscriptionCycles.status eq "active")
        }.firstOrNull() ?: return@transaction false

        // Kiểm tra ngày có trong chu kỳ không
        val skipDateJava = java.time.LocalDate.parse(skipDate.toString())
        if (skipDateJava.isBefore(cycle.weekStartDate) || skipDateJava.isAfter(cycle.weekEndDate)) {
            return@transaction false
        }

        // Kiểm tra có phải T2-T6 không
        if (skipDateJava.dayOfWeek == DayOfWeek.SATURDAY || skipDateJava.dayOfWeek == DayOfWeek.SUNDAY) {
            return@transaction false
        }

        // Tạo record cắt cơm (hoặc update nếu đã tồn tại)
        MealSkipDateDAO.find {
            (MealSkipDates.cycleId eq cycle.id) and
                    (MealSkipDates.skipDate eq skipDateJava)
        }.firstOrNull() ?: MealSkipDateDAO.new {
            this.cycleId = cycle.id
            this.inpatientId = EntityID(inpatientId, Inpatients)
            this.skipDate = skipDateJava
        }

        true
    }

    // Lấy trạng thái ăn cơm hôm nay
    fun getMealStatusToday(inpatientId: Int, today: LocalDate): Pair<String, MealSubscriptionCycleDAO?> = transaction {
        val todayJava = java.time.LocalDate.parse(today.toString())

        // Kiểm tra có chu kỳ active không
        val cycle = MealSubscriptionCycleDAO.find {
            (MealSubscriptionCycles.inpatientId eq inpatientId) and
                    (MealSubscriptionCycles.status eq "active")
        }.firstOrNull()

        if (cycle == null) return@transaction Pair("Không đăng ký", null)

        // Kiểm tra ngày hôm nay có trong chu kỳ không
        if (todayJava.isBefore(cycle.weekStartDate) || todayJava.isAfter(cycle.weekEndDate)) {
            return@transaction Pair("Chu kỳ không active", cycle)
        }

        // Kiểm tra T7-CN
        if (todayJava.dayOfWeek == DayOfWeek.SATURDAY || todayJava.dayOfWeek == DayOfWeek.SUNDAY) {
            return@transaction Pair("Không phục vụ (T7-CN)", cycle)
        }

        // Kiểm tra đã cắt cơm chưa
        val skipped = MealSkipDateDAO.find {
            (MealSkipDates.cycleId eq cycle.id) and
                    (MealSkipDates.skipDate eq todayJava)
        }.firstOrNull()

        if (skipped != null) {
            return@transaction Pair("Đã cắt cơm", cycle)
        }

        Pair("Được ăn cơm", cycle)
    }

    // Đóng chu kỳ khi hết tuần
    fun completeCycle(cycleId: Int): Boolean = transaction {
        val cycle = MealSubscriptionCycleDAO.findById(cycleId) ?: return@transaction false

        // Tính số ngày thực tế ăn
        val totalDays = cycle.daysInCycle
        val skippedDays = MealSkipDateDAO.find {
            MealSkipDates.cycleId eq cycleId
        }.count().toInt()

        cycle.actualSkipDays = skippedDays
        cycle.actualMealDays = totalDays - skippedDays
        cycle.status = "completed"

        true
    }

    // Lấy danh sách chu kỳ của bệnh nhân
    fun getCyclesByInpatient(inpatientId: Int): List<MealSubscriptionCycleDAO> = transaction {
        MealSubscriptionCycleDAO.find {
            MealSubscriptionCycles.inpatientId eq inpatientId
        }.toList()
    }
}