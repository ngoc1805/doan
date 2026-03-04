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

    companion object {
        const val PRICE_PER_DAY = 100000 // 100,000 VNĐ/ngày
    }

    /**
     * ✅ FIXED: Tạo chu kỳ đăng ký mới với logic tính toán ĐÚNG
     * @return Pair(cycleId, totalCost) hoặc null nếu thất bại
     */
    fun createCycle(inpatientId: Int, registrationDate: LocalDate): Pair<Int, Int>? = transaction {
        val inpatient = InpatientDAO.findById(inpatientId) ?: return@transaction null
        if (inpatient.status != "Đã nhập viện") return@transaction null

        // Kiểm tra xem đã có cycle pending_payment chưa
        val pendingCycle = MealSubscriptionCycleDAO.find {
            (MealSubscriptionCycles.inpatientId eq inpatientId) and
                    (MealSubscriptionCycles.status eq "pending_payment")
        }.firstOrNull()

        // Nếu đã có pending cycle, trả về cycleId và số tiền
        if (pendingCycle != null) {
            val cost = pendingCycle.expectedMealDays * PRICE_PER_DAY
            return@transaction Pair(pendingCycle.id.value, cost)
        }

        // Tính chu kỳ tiếp theo
        val lastCycle = MealSubscriptionCycleDAO.find {
            MealSubscriptionCycles.inpatientId eq inpatientId
        }.maxByOrNull { it.cycleNumber }

        val nextCycleNumber = (lastCycle?.cycleNumber ?: 0) + 1

        // ✅ FIXED: Logic tính ngày bắt đầu và kết thúc ĐÚNG
        val (startDate, endDate, daysCount) = calculateMealPeriod(registrationDate)

        println("📝 [MealRepository] Registration: $registrationDate")
        println("📅 [MealRepository] Start: $startDate, End: $endDate, Days: $daysCount")

        // Tính tổng tiền
        val totalCost = daysCount * PRICE_PER_DAY

        val cycle = MealSubscriptionCycleDAO.new {
            this.inpatientId = EntityID(inpatientId, Inpatients)
            this.cycleNumber = nextCycleNumber
            this.weekStartDate = java.time.LocalDate.parse(startDate.toString())
            this.weekEndDate = java.time.LocalDate.parse(endDate.toString())
            this.registrationDate = java.time.LocalDate.parse(registrationDate.toString())
            this.status = "pending_payment"
            this.daysInCycle = daysCount
            this.expectedMealDays = daysCount
        }

        println("✅ [MealRepository] Created cycle ${cycle.id.value} with status: ${cycle.status}")
        Pair(cycle.id.value, totalCost)
    }

    /**
     * ✅ FIXED: Logic tính toán ngày ĐÚNG
     *
     * LOGIC MỚI (ĐÚNG):
     * - Đăng ký T2-T5: Phục vụ từ ngày MAI đến hết T6 tuần hiện tại + T2-T6 tuần sau
     * - Đăng ký T6-CN: Phục vụ từ T2 tuần sau, 5 ngày (T2-T6)
     *
     * @return Triple(startDate, endDate, numberOfDays)
     */
    private fun calculateMealPeriod(registrationDate: LocalDate): Triple<LocalDate, LocalDate, Int> {
        val regJava = java.time.LocalDate.parse(registrationDate.toString())
        val dayOfWeek = regJava.dayOfWeek

        return when (dayOfWeek) {
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY -> {
                // T2-T5: Bắt đầu từ ngày MAI
                val startDate = LocalDate.parse(regJava.plusDays(1).toString())

                // Tìm thứ 6 của tuần hiện tại
                val daysUntilFriday = DayOfWeek.FRIDAY.value - dayOfWeek.value
                val thisFriday = regJava.plusDays(daysUntilFriday.toLong())

                // Tìm thứ 6 của tuần SAU (7 ngày sau thứ 6 này)
                val nextFriday = thisFriday.plusDays(7)

                // End date = Chủ nhật của tuần chứa nextFriday
                val endDate = nextFriday.plusDays((7 - nextFriday.dayOfWeek.value).toLong())

                // Đếm số ngày T2-T6 từ startDate đến nextFriday (bao gồm cả 2 đầu)
                var count = 0
                var current = regJava.plusDays(1) // Ngày mai
                while (!current.isAfter(nextFriday)) {
                    if (current.dayOfWeek != DayOfWeek.SATURDAY && current.dayOfWeek != DayOfWeek.SUNDAY) {
                        count++
                    }
                    current = current.plusDays(1)
                }

                Triple(startDate, LocalDate.parse(endDate.toString()), count)
            }

            DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY -> {
                // T6-CN: Bắt đầu từ T2 tuần sau
                val daysUntilNextMonday = when (dayOfWeek) {
                    DayOfWeek.FRIDAY -> 3    // T6 + 3 = T2
                    DayOfWeek.SATURDAY -> 2  // T7 + 2 = T2
                    DayOfWeek.SUNDAY -> 1    // CN + 1 = T2
                    else -> 0
                }
                val nextMonday = regJava.plusDays(daysUntilNextMonday.toLong())
                val startDate = LocalDate.parse(nextMonday.toString())

                // Phục vụ 5 ngày (T2-T6 của tuần đó)
                val nextFriday = nextMonday.plusDays(4) // T2+4=T6

                // End date = Chủ nhật của tuần đó
                val endDate = nextMonday.plusDays(6) // T2+6=CN

                Triple(startDate, LocalDate.parse(endDate.toString()), 5)
            }

            else -> throw IllegalStateException("Invalid day of week")
        }
    }

    /**
     * ✅ CRITICAL FIX: Kích hoạt chu kỳ sau khi thanh toán thành công
     * HÀM NÀY PHẢI ĐƯỢC GỌI BÊN TRONG TRANSACTION CỦA CALLER
     *
     * LƯU Ý QUAN TRỌNG:
     * - Hàm này KHÔNG có wrapper transaction riêng
     * - Nó phải được gọi từ bên trong một transaction block
     * - Điều này đảm bảo tất cả thay đổi được commit cùng nhau
     */
    fun activateCycle(cycleId: Int): Boolean {
        println("🔌 [MealRepository.activateCycle] Called with cycleId: $cycleId")

        val cycle = MealSubscriptionCycleDAO.findById(cycleId)
        if (cycle == null) {
            println("❌ [MealRepository.activateCycle] Cycle not found: $cycleId")
            return false
        }

        println("📝 [MealRepository.activateCycle] Before update - status: ${cycle.status}")

        // Cập nhật status
        cycle.status = "active"

        println("✅ [MealRepository.activateCycle] After update - status: ${cycle.status}")
        println("✅ [MealRepository.activateCycle] Cycle activated successfully!")

        return true
    }

    /**
     * Cắt cơm (chỉ được cắt trong ngày T2-T6)
     */
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

    /**
     * Lấy trạng thái ăn cơm hôm nay
     */
    fun getMealStatusToday(inpatientId: Int, today: LocalDate): Triple<String, MealSubscriptionCycleDAO?, Boolean> = transaction {
        val todayJava = java.time.LocalDate.parse(today.toString())

        // Kiểm tra có chu kỳ active không
        val cycle = MealSubscriptionCycleDAO.find {
            (MealSubscriptionCycles.inpatientId eq inpatientId) and
                    (MealSubscriptionCycles.status eq "active")
        }.firstOrNull()

        if (cycle == null) return@transaction Triple("Không đăng ký", null, false)

        // Kiểm tra ngày hôm nay có trong chu kỳ không
        if (todayJava.isBefore(cycle.weekStartDate) || todayJava.isAfter(cycle.weekEndDate)) {
            return@transaction Triple("Chu kỳ không active", cycle, false)
        }

        // Kiểm tra T7-CN
        if (todayJava.dayOfWeek == DayOfWeek.SATURDAY || todayJava.dayOfWeek == DayOfWeek.SUNDAY) {
            return@transaction Triple("Không phục vụ (T7-CN)", cycle, false)
        }

        // Kiểm tra đã cắt cơm chưa
        val skipped = MealSkipDateDAO.find {
            (MealSkipDates.cycleId eq cycle.id) and
                    (MealSkipDates.skipDate eq todayJava)
        }.firstOrNull()

        if (skipped != null) {
            return@transaction Triple("Đã cắt cơm", cycle, true)
        }

        Triple("Được ăn cơm", cycle, false)
    }

    /**
     * Đóng chu kỳ khi hết tuần hoặc xuất viện
     */
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

    /**
     * Lấy danh sách chu kỳ của bệnh nhân
     */
    fun getCyclesByInpatient(inpatientId: Int): List<MealSubscriptionCycleDAO> = transaction {
        MealSubscriptionCycleDAO.find {
            MealSubscriptionCycles.inpatientId eq inpatientId
        }.toList()
    }

    /**
     * Hoàn tiền khi xuất viện giữa chu kỳ
     * @return số tiền được hoàn lại
     */
    fun calculateRefund(inpatientId: Int, dischargeDate: LocalDate): Int = transaction {
        // Tìm chu kỳ đang active
        val cycle = MealSubscriptionCycleDAO.find {
            (MealSubscriptionCycles.inpatientId eq inpatientId) and
                    (MealSubscriptionCycles.status eq "active")
        }.firstOrNull() ?: return@transaction 0

        val dischargeDateJava = java.time.LocalDate.parse(dischargeDate.toString())

        // Đếm số ngày chưa sử dụng (từ ngày xuất viện đến hết chu kỳ)
        var unusedDays = 0
        var currentDate = dischargeDateJava.plusDays(1) // Ngày mai sau khi xuất viện
        val endDate = cycle.weekEndDate

        while (!currentDate.isAfter(endDate)) {
            // Chỉ đếm T2-T6
            if (currentDate.dayOfWeek != DayOfWeek.SATURDAY &&
                currentDate.dayOfWeek != DayOfWeek.SUNDAY) {
                unusedDays++
            }
            currentDate = currentDate.plusDays(1)
        }

        // Hoàn tiền = số ngày chưa dùng × giá
        unusedDays * PRICE_PER_DAY
    }

    /**
     * Kiểm tra xem có thể gia hạn không (từ T6 của tuần hiện tại)
     */
    fun canRenew(inpatientId: Int, today: LocalDate): Boolean = transaction {
        val todayJava = java.time.LocalDate.parse(today.toString())

        // Chỉ cho phép gia hạn từ T6, T7, CN
        if (todayJava.dayOfWeek != DayOfWeek.FRIDAY &&
            todayJava.dayOfWeek != DayOfWeek.SATURDAY &&
            todayJava.dayOfWeek != DayOfWeek.SUNDAY) {
            return@transaction false
        }

        // Kiểm tra có chu kỳ active không
        val activeCycle = MealSubscriptionCycleDAO.find {
            (MealSubscriptionCycles.inpatientId eq inpatientId) and
                    (MealSubscriptionCycles.status eq "active")
        }.firstOrNull() ?: return@transaction false

        // Kiểm tra chưa có chu kỳ pending cho tuần sau
        val hasPending = MealSubscriptionCycleDAO.find {
            (MealSubscriptionCycles.inpatientId eq inpatientId) and
                    (MealSubscriptionCycles.status eq "pending_payment") and
                    (MealSubscriptionCycles.cycleNumber eq (activeCycle.cycleNumber + 1))
        }.firstOrNull() != null

        !hasPending
    }

    /**
     * ✅ Thống kê suất ăn theo ngày cụ thể
     * Trả về danh sách tất cả các inpatient có chu kỳ active cho ngày đó
     */
    fun getMealStatisticsByDate(date: LocalDate): List<Triple<MealSubscriptionCycleDAO, InpatientDAO, Boolean>> = transaction {
        val dateJava = java.time.LocalDate.parse(date.toString())

        // Lấy tất cả chu kỳ active
        val activeCycles = MealSubscriptionCycleDAO.find {
            MealSubscriptionCycles.status eq "active"
        }.filter { cycle ->
            // Kiểm tra ngày có nằm trong chu kỳ không
            !dateJava.isBefore(cycle.weekStartDate) && !dateJava.isAfter(cycle.weekEndDate)
        }

        // Map với inpatient và trạng thái skip
        activeCycles.map { cycle ->
            val inpatient = InpatientDAO.findById(cycle.inpatientId.value)!!

            // Kiểm tra ngày này có bị skip không
            val isSkipped = MealSkipDateDAO.find {
                (MealSkipDates.cycleId eq cycle.id) and
                        (MealSkipDates.skipDate eq dateJava)
            }.firstOrNull() != null

            Triple(cycle, inpatient, isSkipped)
        }
    }

    /**
     * ✅ Thống kê suất ăn cho cả tuần
     */
    fun getMealStatisticsByWeek(weekStartDate: LocalDate): Map<LocalDate, List<Triple<MealSubscriptionCycleDAO, InpatientDAO, Boolean>>> = transaction {
        val result = mutableMapOf<LocalDate, List<Triple<MealSubscriptionCycleDAO, InpatientDAO, Boolean>>>()

        // Duyệt qua 5 ngày từ T2-T6
        for (i in 0..4) {
            val currentDate = LocalDate.parse(
                java.time.LocalDate.parse(weekStartDate.toString()).plusDays(i.toLong()).toString()
            )
            result[currentDate] = getMealStatisticsByDate(currentDate)
        }

        result
    }

    /**
     * ✅ Thống kê suất ăn theo tháng
     */
    fun getMealStatisticsByMonth(year: Int, month: Int): Map<LocalDate, List<Triple<MealSubscriptionCycleDAO, InpatientDAO, Boolean>>> = transaction {
        val result = mutableMapOf<LocalDate, List<Triple<MealSubscriptionCycleDAO, InpatientDAO, Boolean>>>()

        // Lấy số ngày trong tháng
        val firstDay = java.time.LocalDate.of(year, month, 1)
        val lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth())

        var currentDate = firstDay
        while (!currentDate.isAfter(lastDay)) {
            // Chỉ thống kê T2-T6
            if (currentDate.dayOfWeek != DayOfWeek.SATURDAY &&
                currentDate.dayOfWeek != DayOfWeek.SUNDAY) {
                val date = LocalDate.parse(currentDate.toString())
                result[date] = getMealStatisticsByDate(date)
            }
            currentDate = currentDate.plusDays(1)
        }

        result
    }
}