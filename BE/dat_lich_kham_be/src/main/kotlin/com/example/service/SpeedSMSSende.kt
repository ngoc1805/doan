package com.example.service

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class SpeedSMSRequest(
    val to: List<String>,
    val content: String,
    val type: Int,
    val sender: String = ""
)

object SpeedSMSSender {
    private const val ACCESS_TOKEN = "8RPms5dl6136i0Bz0ZXJldh0_CkkwRUW"
    private const val API_URL = "https://api.speedsms.vn/index.php/sms/send"
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun sendOtpSMS(phone: String, otp: String): Boolean {
        return try {
            val request = SpeedSMSRequest(
                to = listOf(phone),
                content = "Ma OTP cua ban la: $otp",
                type = 2
            )
            val response: HttpResponse = client.post(API_URL) {
                header("Authorization", "Basic $ACCESS_TOKEN")
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            val text = response.bodyAsText()
            println("SpeedSMS response: $text")
            response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Accepted
        } catch (e: Exception) {
            println("Error sending SpeedSMS: ${e.message}")
            false
        }
    }
}