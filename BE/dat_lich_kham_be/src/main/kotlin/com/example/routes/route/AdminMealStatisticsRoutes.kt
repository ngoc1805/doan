package com.example.routes.route


import com.example.service.MealService
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.serialization.json.encodeToJsonElement

/**
 * Routes cho Admin quản lý thống kê suất ăn
 */
fun Route.adminMealStatisticsRoutes(mealService: MealService) {

    route("/api/admin/meal-statistics") {

        /**
         * Thống kê suất ăn theo ngày cụ thể
         * GET /api/admin/meal-statistics/daily?date=2024-02-06
         *
         * Response:
         * {
         *   "date": "2024-02-06",
         *   "dayOfWeek": "Thứ 3",
         *   "totalRegistered": 50,
         *   "totalSkipped": 5,
         *   "totalServing": 45,
         *   "mealDetails": [
         *     {
         *       "inpatientId": 1,
         *       "userId": 10,
         *       "fullName": "Nguyễn Văn A",
         *       "address": "Phòng 101",
         *       "cycleId": 5,
         *       "cycleNumber": 2,
         *       "status": "Được ăn cơm",
         *       "isSkipped": false
         *     }
         *   ]
         * }
         */
        get("/daily") {
            val dateStr = call.request.queryParameters["date"]

            val date = if (dateStr != null) {
                try {
                    LocalDate.parse(dateStr)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Định dạng ngày không hợp lệ. Sử dụng: YYYY-MM-DD")
                    return@get
                }
            } else {
                // Nếu không có tham số date, lấy ngày hôm nay
                Clock.System.todayIn(TimeZone.currentSystemDefault())
            }

            val statistics = mealService.getDailyStatistics(date)
            call.respond(statistics)
        }

        /**
         * Thống kê suất ăn hôm nay (shortcut)
         * GET /api/admin/meal-statistics/today
         */
        get("/today") {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val statistics = mealService.getDailyStatistics(today)
            call.respond(statistics)
        }

        /**
         * Thống kê suất ăn theo tuần
         * GET /api/admin/meal-statistics/weekly?weekStart=2024-02-05
         * weekStart phải là ngày thứ 2
         *
         * Response:
         * {
         *   "weekStart": "2024-02-05",
         *   "weekEnd": "2024-02-11",
         *   "dailyStats": [...]
         * }
         */
        get("/weekly") {
            val weekStartStr = call.request.queryParameters["weekStart"]

            if (weekStartStr == null) {
                call.respond(HttpStatusCode.BadRequest, "Thiếu tham số weekStart (ngày thứ 2)")
                return@get
            }

            val weekStart = try {
                LocalDate.parse(weekStartStr)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Định dạng ngày không hợp lệ. Sử dụng: YYYY-MM-DD")
                return@get
            }

            // Kiểm tra có phải thứ 2 không
            val weekStartJava = java.time.LocalDate.parse(weekStart.toString())
            if (weekStartJava.dayOfWeek != java.time.DayOfWeek.MONDAY) {
                call.respond(HttpStatusCode.BadRequest, "weekStart phải là ngày thứ 2")
                return@get
            }

            val statistics = mealService.getWeeklyStatistics(weekStart)
            call.respond(statistics)
        }

        /**
         * Thống kê suất ăn tuần hiện tại (shortcut)
         * GET /api/admin/meal-statistics/current-week
         */
        get("/current-week") {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val todayJava = java.time.LocalDate.parse(today.toString())

            // Tìm thứ 2 đầu tuần
            val daysFromMonday = todayJava.dayOfWeek.value - java.time.DayOfWeek.MONDAY.value
            val monday = todayJava.minusDays(daysFromMonday.toLong())
            val weekStart = LocalDate.parse(monday.toString())

            val statistics = mealService.getWeeklyStatistics(weekStart)
            call.respond(statistics)
        }

        /**
         * Thống kê suất ăn theo tháng
         * GET /api/admin/meal-statistics/monthly?year=2024&month=2
         *
         * Response:
         * {
         *   "month": 2,
         *   "year": 2024,
         *   "totalMealsServed": 450,
         *   "totalMealsSkipped": 50,
         *   "totalRevenue": 45000000,
         *   "dailyStats": [...]
         * }
         */
        get("/monthly") {
            val yearStr = call.request.queryParameters["year"]
            val monthStr = call.request.queryParameters["month"]

            if (yearStr == null || monthStr == null) {
                call.respond(HttpStatusCode.BadRequest, "Thiếu tham số year hoặc month")
                return@get
            }

            val year = yearStr.toIntOrNull()
            val month = monthStr.toIntOrNull()

            if (year == null || month == null || month < 1 || month > 12) {
                call.respond(HttpStatusCode.BadRequest, "year hoặc month không hợp lệ")
                return@get
            }

            val statistics = mealService.getMonthlyStatistics(year, month)
            call.respond(statistics)
        }

        /**
         * Thống kê suất ăn tháng hiện tại (shortcut)
         * GET /api/admin/meal-statistics/current-month
         */
        get("/current-month") {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val todayJava = java.time.LocalDate.parse(today.toString())

            val year = todayJava.year
            val month = todayJava.monthValue

            val statistics = mealService.getMonthlyStatistics(year, month)
            call.respond(statistics)
        }

        /**
         * Thống kê tổng quan nhanh
         * GET /api/admin/meal-statistics/summary
         *
         * Trả về thống kê hôm nay + tuần này + tháng này
         */
        get("/summary") {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val todayJava = java.time.LocalDate.parse(today.toString())

            // Thống kê hôm nay
            val dailyStats = mealService.getDailyStatistics(today)

            // Thống kê tuần này
            val daysFromMonday = todayJava.dayOfWeek.value - java.time.DayOfWeek.MONDAY.value
            val monday = todayJava.minusDays(daysFromMonday.toLong())
            val weekStart = LocalDate.parse(monday.toString())
            val weeklyStats = mealService.getWeeklyStatistics(weekStart)

            // Thống kê tháng này
            val year = todayJava.year
            val month = todayJava.monthValue
            val monthlyStats = mealService.getMonthlyStatistics(year, month)

            call.respond(
                kotlinx.serialization.json.buildJsonObject {
                    put("today", kotlinx.serialization.json.Json.encodeToJsonElement(dailyStats))
                    put("thisWeek", kotlinx.serialization.json.Json.encodeToJsonElement(weeklyStats))
                    put("thisMonth", kotlinx.serialization.json.Json.encodeToJsonElement(monthlyStats))
                }
            )
        }
    }
}