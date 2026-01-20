package com.example.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import io.ktor.server.application.*
import io.ktor.server.config.*

object DatabaseFactory {
    fun init(config: ApplicationConfig) {
        val hikariConfig = HikariConfig().apply {
            driverClassName = config.property("ktor.datasource.driver").getString()
            jdbcUrl = config.property("ktor.datasource.url").getString()
            username = config.property("ktor.datasource.user").getString()
            password = config.property("ktor.datasource.password").getString()
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        }

        val dataSource = HikariDataSource(hikariConfig)
        Database.connect(dataSource)
    }

    suspend fun <T> dbQuery(block: () -> T): T = transaction { block() }
}