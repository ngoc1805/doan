package com.example.utils

import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.crypto.spec.IvParameterSpec
import java.security.SecureRandom

object EncryptionUtil {
    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val KEY_ALGORITHM = "AES"

    // Lấy key từ biến môi trường cho user data
    private val encryptionKey: String by lazy {
        System.getProperty("ENCRYPTION_KEY")
            ?: throw IllegalStateException("ENCRYPTION_KEY không được tìm thấy trong biến môi trường")
    }

    // Lấy key riêng cho transaction
    private val encryptionKeyTrans: String by lazy {
        System.getProperty("ENCRYPTION_KEY_TRANS")
            ?: throw IllegalStateException("ENCRYPTION_KEY_TRANS không được tìm thấy trong biến môi trường")
    }


    fun encrypt(plainText: String?): String? {
        if (plainText.isNullOrBlank()) return plainText

        try {
            // Tạo IV ngẫu nhiên (Initialization Vector)
            val iv = ByteArray(16)
            SecureRandom().nextBytes(iv)
            val ivSpec = IvParameterSpec(iv)

            // Tạo key từ encryption key
            val keySpec = SecretKeySpec(encryptionKey.toByteArray().copyOf(32), KEY_ALGORITHM)

            // Khởi tạo cipher
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)

            // Mã hóa
            val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

            // Kết hợp IV + encrypted data, sau đó encode Base64
            val combined = iv + encrypted
            return Base64.getEncoder().encodeToString(combined)
        } catch (e: Exception) {
            println(" Lỗi khi mã hóa: ${e.message}")
            throw e
        }
    }


    fun decrypt(encryptedText: String?): String? {
        if (encryptedText.isNullOrBlank()) return encryptedText

        try {
            // Decode Base64
            val combined = Base64.getDecoder().decode(encryptedText)

            // Tách IV và encrypted data
            val iv = combined.copyOfRange(0, 16)
            val encrypted = combined.copyOfRange(16, combined.size)

            val ivSpec = IvParameterSpec(iv)

            // Tạo key từ encryption key
            val keySpec = SecretKeySpec(encryptionKey.toByteArray().copyOf(32), KEY_ALGORITHM)

            // Khởi tạo cipher
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)

            // Giải mã
            val decrypted = cipher.doFinal(encrypted)
            return String(decrypted, Charsets.UTF_8)
        } catch (e: Exception) {
            println(" Lỗi khi giải mã: ${e.message}")
            throw e
        }
    }

    fun encryptInt(value: Int?): String? {
        return if (value != null) encrypt(value.toString()) else null
    }


    fun decryptInt(encryptedValue: String?): Int? {
        val decrypted = decrypt(encryptedValue)
        return decrypted?.toIntOrNull()
    }

    //----------------------------------------------------

    fun encryptTransaction(plainText: String?): String? {
        if (plainText.isNullOrBlank()) return plainText

        try {
            val iv = ByteArray(16)
            SecureRandom().nextBytes(iv)
            val ivSpec = IvParameterSpec(iv)

            val keySpec = SecretKeySpec(encryptionKeyTrans.toByteArray().copyOf(32), KEY_ALGORITHM)

            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)

            val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

            val combined = iv + encrypted
            return Base64.getEncoder().encodeToString(combined)
        } catch (e: Exception) {
            println(" Lỗi khi mã hóa giao dịch: ${e.message}")
            throw e
        }
    }


    fun decryptTransaction(encryptedText: String?): String? {
        if (encryptedText.isNullOrBlank()) return encryptedText

        try {
            val combined = Base64.getDecoder().decode(encryptedText)

            val iv = combined.copyOfRange(0, 16)
            val encrypted = combined.copyOfRange(16, combined.size)

            val ivSpec = IvParameterSpec(iv)

            val keySpec = SecretKeySpec(encryptionKeyTrans.toByteArray().copyOf(32), KEY_ALGORITHM)

            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)

            val decrypted = cipher.doFinal(encrypted)
            return String(decrypted, Charsets.UTF_8)
        } catch (e: Exception) {
            println(" Lỗi khi giải mã giao dịch: ${e.message}")
            throw e
        }
    }
}