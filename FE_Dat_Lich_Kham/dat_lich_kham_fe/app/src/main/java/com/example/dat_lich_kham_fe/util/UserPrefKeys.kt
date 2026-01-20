package com.example.dat_lich_kham_fe.util

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import com.example.dat_lich_kham_fe.data.model.UserResponse

private val Context.dataStore by preferencesDataStore("user_prefs")

object UserPrefKeys {
    val ID = intPreferencesKey("id")
    val FULL_NAME = stringPreferencesKey("full_name")
    val GENDER = stringPreferencesKey("gender")
    val BIRTH_DATE = stringPreferencesKey("birth_date")
    val CCCD = stringPreferencesKey("cccd")
    val HOMETOWN = stringPreferencesKey("hometown")
    val BALANCE = intPreferencesKey("balance")
    val IMAGE_URL = stringPreferencesKey("image_url")
}

class UserLocalStore(private val context: Context) {

    // Hàm lưu tất cả thông tin user vào DataStore
    suspend fun saveUser(user: UserResponse) {
        context.dataStore.edit { prefs ->
            prefs[UserPrefKeys.ID] = user.Id
            prefs[UserPrefKeys.FULL_NAME] = user.fullName
            prefs[UserPrefKeys.GENDER] = user.gender
            prefs[UserPrefKeys.BIRTH_DATE] = user.birthDate.toString()
            prefs[UserPrefKeys.CCCD] = user.cccd
            prefs[UserPrefKeys.HOMETOWN] = user.hometown
            prefs[UserPrefKeys.BALANCE] = user.balance
            prefs[UserPrefKeys.IMAGE_URL] = user.imageUrl ?: "" // Đúng tên trường
        }
    }

    suspend fun saveBalance(balance: Int) {
        context.dataStore.edit { prefs ->
            prefs[UserPrefKeys.BALANCE] = balance
        }
    }

    // Hàm lấy toàn bộ thông tin user
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getUser(): UserResponse? {
        val prefs = context.dataStore.data.first()
        val id = prefs[UserPrefKeys.ID] ?: return null
        val fullName = prefs[UserPrefKeys.FULL_NAME] ?: ""
        val gender = prefs[UserPrefKeys.GENDER] ?: ""
        val birthDateStr = prefs[UserPrefKeys.BIRTH_DATE] ?: ""
        val cccd = prefs[UserPrefKeys.CCCD] ?: ""
        val hometown = prefs[UserPrefKeys.HOMETOWN] ?: ""
        val balance = prefs[UserPrefKeys.BALANCE] ?: 0
        val imageUrl = prefs[UserPrefKeys.IMAGE_URL] ?: "" // Đúng tên trường

        // Convert birthDate từ String sang LocalDate, nếu có lỗi thì trả về null
        val birthDate = try {
            LocalDate.parse(birthDateStr)
        } catch (e: Exception) {
            return null
        }

        return UserResponse(
            Id = id,
            fullName = fullName,
            gender = gender,
            birthDate = birthDate.toString(),
            cccd = cccd,
            hometown = hometown,
            balance = balance,
            imageUrl = if (imageUrl.isNotEmpty()) imageUrl else null // Đúng tên trường
        )
    }

    // Các hàm get từng dữ liệu
    suspend fun getId(): Int? =
        context.dataStore.data.first()[UserPrefKeys.ID]

    suspend fun getFullName(): String? =
        context.dataStore.data.first()[UserPrefKeys.FULL_NAME]

    suspend fun getGender(): String? =
        context.dataStore.data.first()[UserPrefKeys.GENDER]

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getBirthDate(): LocalDate? =
        context.dataStore.data.first()[UserPrefKeys.BIRTH_DATE]?.let {
            try { LocalDate.parse(it) } catch (_: Exception) { null }
        }

    suspend fun getCCCD(): String? =
        context.dataStore.data.first()[UserPrefKeys.CCCD]

    suspend fun getHometown(): String? =
        context.dataStore.data.first()[UserPrefKeys.HOMETOWN]

    suspend fun getBalance(): Int? =
        context.dataStore.data.first()[UserPrefKeys.BALANCE]

    // Hàm xóa toàn bộ thông tin user (nên gọi khi logout)
    suspend fun clearUser() {
        context.dataStore.edit { it.clear() }
    }
}
