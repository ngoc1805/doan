package com.example.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

// Extension cho chuyển đổi kiểu ngày
fun java.time.LocalDate.toKotlinxLocalDate(): kotlinx.datetime.LocalDate =
    kotlinx.datetime.LocalDate(year, monthValue, dayOfMonth)

fun kotlinx.datetime.LocalDate.toJavaLocalDate(): java.time.LocalDate =
    java.time.LocalDate.of(year, monthNumber, dayOfMonth)

fun java.time.Instant.toKotlinxInstant(): Instant =
    Instant.fromEpochMilliseconds(this.toEpochMilli())

// Extension function for java.time.Instant to kotlinx LocalDateTime
fun java.time.Instant.toKotlinxLocalDateTime(): LocalDateTime {
    return this.toKotlinxInstant().toLocalDateTime(TimeZone.currentSystemDefault())
}

// Extension function for kotlinx.datetime.Instant to LocalDateTime
fun Instant.toKotlinxLocalDateTime(): LocalDateTime {
    return this.toLocalDateTime(TimeZone.currentSystemDefault())
}