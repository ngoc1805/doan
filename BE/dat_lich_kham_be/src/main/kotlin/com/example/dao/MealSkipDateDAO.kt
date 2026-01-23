package com.example.dao

import com.example.Tables.MealSkipDates
import com.example.utils.toKotlinxInstant
import com.example.utils.toKotlinxLocalDate
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class MealSkipDateDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MealSkipDateDAO>(MealSkipDates)

    var cycleId by MealSkipDates.cycleId
    var inpatientId by MealSkipDates.inpatientId
    var skipDate by MealSkipDates.skipDate
    var createdAt by MealSkipDates.createdAt

    fun toModel(): com.example.models.MealSkipDate {
        return com.example.models.MealSkipDate(
            id = id.value,
            cycleId = cycleId.value,
            inpatientId = inpatientId.value,
            skipDate = skipDate.toKotlinxLocalDate(),
            createdAt = createdAt.toKotlinxInstant()
        )
    }
}