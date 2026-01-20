package com.example.utils

import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object FileEncryptionUtil {
    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val KEY_ALGORITHM = "AES"
    private const val IV_SIZE = 16

    private val fileEncryptionKey: String by lazy {
        System.getProperty("FILE_ENCRYPTION_KEY")
            ?: System.getProperty("ENCRYPTION_KEY")
            ?: throw IllegalStateException("FILE_ENCRYPTION_KEY không được tìm thấy")
    }

    fun encryptFile(inputFile: File, outputFile: File) {
        try {
            // Tạo IV ngẫu nhiên
            val iv = ByteArray(IV_SIZE)
            SecureRandom().nextBytes(iv)
            val ivSpec = IvParameterSpec(iv)

            // Tạo key
            val keySpec = SecretKeySpec(fileEncryptionKey.toByteArray().copyOf(32), KEY_ALGORITHM)

            // Khởi tạo cipher
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)

            // Đảm bảo thư mục đích tồn tại
            outputFile.parentFile?.mkdirs()

            // Ghi IV vào đầu file, sau đó ghi nội dung đã mã hóa
            outputFile.outputStream().use { fos ->
                fos.write(iv) // Ghi IV

                CipherOutputStream(fos, cipher).use { cos ->
                    inputFile.inputStream().use { fis ->
                        fis.copyTo(cos)
                    }
                }
            }
        } catch (e: Exception) {
            println(" Lỗi khi mã hóa file: ${e.message}")
            throw e
        }
    }

    fun encryptFile(inputBytes: ByteArray, outputFile: File) {
        try {
            val iv = ByteArray(IV_SIZE)
            SecureRandom().nextBytes(iv)
            val ivSpec = IvParameterSpec(iv)

            val keySpec = SecretKeySpec(fileEncryptionKey.toByteArray().copyOf(32), KEY_ALGORITHM)

            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)

            outputFile.parentFile?.mkdirs()

            outputFile.outputStream().use { fos ->
                fos.write(iv)

                CipherOutputStream(fos, cipher).use { cos ->
                    cos.write(inputBytes)
                }
            }
        } catch (e: Exception) {
            println(" Lỗi khi mã hóa file từ bytes: ${e.message}")
            throw e
        }
    }

    fun decryptFileToStream(encryptedFile: File): InputStream {
        try {
            val inputStream = encryptedFile.inputStream()

            // Đọc IV từ 16 bytes đầu
            val iv = ByteArray(IV_SIZE)
            inputStream.read(iv)
            val ivSpec = IvParameterSpec(iv)

            // Tạo key
            val keySpec = SecretKeySpec(fileEncryptionKey.toByteArray().copyOf(32), KEY_ALGORITHM)

            // Khởi tạo cipher
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)

            // Trả về CipherInputStream để giải mã on-the-fly
            return CipherInputStream(inputStream, cipher)
        } catch (e: Exception) {
            println(" Lỗi khi giải mã file: ${e.message}")
            throw e
        }
    }


    fun decryptFileToOutputStream(encryptedFile: File, outputStream: OutputStream) {
        try {
            encryptedFile.inputStream().use { fis ->
                // Đọc IV
                val iv = ByteArray(IV_SIZE)
                fis.read(iv)
                val ivSpec = IvParameterSpec(iv)

                // Tạo key
                val keySpec = SecretKeySpec(fileEncryptionKey.toByteArray().copyOf(32), KEY_ALGORITHM)

                // Khởi tạo cipher
                val cipher = Cipher.getInstance(ALGORITHM)
                cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)

                // Giải mã và ghi vào output stream
                CipherInputStream(fis, cipher).use { cis ->
                    cis.copyTo(outputStream)
                }
            }
        } catch (e: Exception) {
            println(" Lỗi khi giải mã file to OutputStream: ${e.message}")
            throw e
        }
    }

    fun decryptFileToBytes(encryptedFile: File): ByteArray {
        return decryptFileToStream(encryptedFile).use { it.readBytes() }
    }
}