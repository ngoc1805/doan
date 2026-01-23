package com.example.dao

import com.example.Tables.Results
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ResultDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ResultDAO>(Results)

    var appointmentId by Results.appointmentId
    var comment by Results.comment
    var dietRecommendation by Results.dietRecommendation

    fun toModel(): com.example.models.Result {
        return com.example.models.Result(
            id = id.value,
            appointmentId = appointmentId.value,
            comment = comment,
            dietRecommendation = dietRecommendation
        )
    }
}