package com.example.service

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*

object OtpGatewayService {
    private val client = HttpClient(CIO)
    //private const val ANDROID_GATEWAY_URL

    private val  ANDROID_GATEWAY_URL: String by lazy {
        System.getProperty("ANDROID_GATEWAY_URL")
            ?: throw IllegalStateException(" ANDROID_GATEWAY_URL không được tìm thấy trong biến môi trường")
    }

    suspend fun sendOtpViaGateway(phone: String, otp: String): Boolean {
        // Dung string truc tiep de tranh loi serialization
        val payload = """{"phone_number":"$phone","message":"$otp"}"""

        println("Sending to Android: $payload")

        return try {
            val response = client.post(ANDROID_GATEWAY_URL) {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }

            val success = response.status.value in 200..299
            println("Response status: ${response.status.value}")
            success
        } catch (e: Exception) {
            println("Loi gui otp qua gateway: ${e.message}")
            e.printStackTrace()
            false
        }
    }
}