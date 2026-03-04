package com.example.jobs

import com.example.service.ChatService
import com.example.dao.UsersDAO
import kotlinx.coroutines.*
import kotlinx.datetime.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Batch job chạy cuối ngày để consolidate memories
 */
class DailyMemoryConsolidationJob {

    private val chatService = ChatService()
    private val scheduler = Executors.newScheduledThreadPool(1)

    /**
     * Khởi động job - chạy hàng ngày lúc 0h
     */
    fun start() {
        // Tính thời gian đến 0h ngày mai
        val now = Clock.System.now()
        val currentZone = TimeZone.currentSystemDefault()
        val currentDateTime = now.toLocalDateTime(currentZone)

        val tomorrow = currentDateTime.date.plus(1, DateTimeUnit.DAY)
        val nextMidnight = LocalDateTime(tomorrow, LocalTime(0, 0))
        val nextMidnightInstant = nextMidnight.toInstant(currentZone)

        val delayMillis = (nextMidnightInstant - now).inWholeMilliseconds

        // Schedule initial run
        scheduler.schedule({
            runConsolidation()
        }, delayMillis, TimeUnit.MILLISECONDS)

        // Schedule daily runs
        scheduler.scheduleAtFixedRate(
            { runConsolidation() },
            delayMillis + TimeUnit.DAYS.toMillis(1),
            TimeUnit.DAYS.toMillis(1),
            TimeUnit.MILLISECONDS
        )

        println("Daily Memory Consolidation Job scheduled to run at midnight")
    }

    /**
     * Chạy consolidation cho tất cả users
     */
    private fun runConsolidation() {
        println("Starting Daily Memory Consolidation...")

        runBlocking {
            try {
                // Lấy tất cả users
                val userIds = transaction {
                    UsersDAO.all().map { it.id.value }
                }

                // Xử lý từng user
                val yesterday = Clock.System.now()
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .date
                    .minus(1, DateTimeUnit.DAY)

                userIds.forEach { userId ->
                    try {
                        consolidateUserMemories(userId, yesterday)
                    } catch (e: Exception) {
                        println("Error consolidating memories for user $userId: ${e.message}")
                    }
                }

                println("Daily Memory Consolidation completed successfully")

            } catch (e: Exception) {
                println("Error in Daily Memory Consolidation: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    /**
     * Consolidate memories cho một user
     */
    private suspend fun consolidateUserMemories(userId: Int, date: LocalDate) {
        println("Consolidating memories for user $userId on $date")

        // Gọi service để tóm tắt và tạo embedding
        chatService.consolidateDailyMemories(userId, date)

        // Cleanup old memories nếu cần (memories đã expired)
        cleanupExpiredMemories(userId)

        println("Completed consolidation for user $userId")
    }

    /**
     * Xóa memories đã hết hạn
     */
    private fun cleanupExpiredMemories(userId: Int) {
        transaction {
            // TODO: Implement cleanup logic
            // Xóa các ChatMemory có expiresAt < now
        }
    }

    /**
     * Dừng job
     */
    fun stop() {
        scheduler.shutdown()
        println("Daily Memory Consolidation Job stopped")
    }
}

/**
 * Manual trigger để test
 */
class ManualConsolidationTrigger {

    private val chatService = ChatService()

    /**
     * Chạy consolidation cho một user và ngày cụ thể
     */
    suspend fun consolidateForDate(userId: Int, date: LocalDate) {
        try {
            println("Manual consolidation for user $userId on $date")
            chatService.consolidateDailyMemories(userId, date)
            println("Manual consolidation completed")
        } catch (e: Exception) {
            println("Error: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Consolidate cho user trong khoảng thời gian
     */
    suspend fun consolidateForDateRange(
        userId: Int,
        startDate: LocalDate,
        endDate: LocalDate
    ) {
        var currentDate = startDate

        while (currentDate <= endDate) {
            consolidateForDate(userId, currentDate)
            currentDate = currentDate.plus(1, DateTimeUnit.DAY)
        }
    }
}