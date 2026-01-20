package com.example.service

import com.example.dao.ResultDAO
import com.example.dto.Request.ResultRequest
import com.example.dto.Response.ResultItem
import com.example.repository.ResultRepository

class ResultService(private val repo: ResultRepository = ResultRepository()) {
    fun getResultsByUserIdAndStatus(userId: Int, status: String): List<ResultItem> {
        return repo.getResultsByUserIdAndStatus(userId, status)
    }
    fun createResult(request: ResultRequest): ResultDAO {
        return repo.createResult(request)
    }
}