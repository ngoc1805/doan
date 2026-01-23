package com.example.dao

import com.example.Tables.MealSubscriptionCycles
import com.example.utils.toKotlinxInstant
import com.example.utils.toKotlinxLocalDate
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class MealSubscriptionCycleDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MealSubscriptionCycleDAO>(MealSubscriptionCycles)

    var inpatientId by MealSubscriptionCycles.inpatientId
    var cycleNumber by MealSubscriptionCycles.cycleNumber
    var weekStartDate by MealSubscriptionCycles.weekStartDate
    var weekEndDate by MealSubscriptionCycles.weekEndDate
    var registrationDate by MealSubscriptionCycles.registrationDate
    var status by MealSubscriptionCycles.status
    var daysInCycle by MealSubscriptionCycles.daysInCycle
    var expectedMealDays by MealSubscriptionCycles.expectedMealDays
    var actualMealDays by MealSubscriptionCycles.actualMealDays
    var actualSkipDays by MealSubscriptionCycles.actualSkipDays
    var createdAt by MealSubscriptionCycles.createdAt
    var updatedAt by MealSubscriptionCycles.updatedAt

    fun toModel(): com.example.models.MealSubscriptionCycle {
        return com.example.models.MealSubscriptionCycle(
            id = id.value,
            inpatientId = inpatientId.value,
            cycleNumber = cycleNumber,
            weekStartDate = weekStartDate.toKotlinxLocalDate(),
            weekEndDate = weekEndDate.toKotlinxLocalDate(),
            registrationDate = registrationDate.toKotlinxLocalDate(),
            status = status,
            daysInCycle = daysInCycle,
            expectedMealDays = expectedMealDays,
            actualMealDays = actualMealDays,
            actualSkipDays = actualSkipDays,
            createdAt = createdAt.toKotlinxInstant(),
            updatedAt = updatedAt.toKotlinxInstant()
        )
    }
}