package util

import java.util.Base64
import org.json.JSONObject
import data.model.AccountInfo

object JwtUtils {
    fun parseAccountInfo(token: String): AccountInfo? {
        val parts = token.split(".")
        if (parts.size != 3) return null
        val payload = parts[1]
        val decoded = Base64.getUrlDecoder().decode(payload)
        val json = JSONObject(String(decoded))

        val accountId = json.optInt("id", -1)
        val username = json.optString("username", "")
        val role = json.optString("role", "")
        return AccountInfo(accountId, username, role)
    }
}