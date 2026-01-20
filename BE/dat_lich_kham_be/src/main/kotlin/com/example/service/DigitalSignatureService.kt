package com.example.service

import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.StampingProperties
import com.itextpdf.signatures.*
import com.itextpdf.kernel.geom.Rectangle
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.*
import java.security.cert.Certificate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.serialization.Serializable
import com.itextpdf.signatures.PdfSignatureAppearance

class DigitalSignatureService(
    private val hospitalKeyService: HospitalKeyService
) {

    init {
        Security.addProvider(BouncyCastleProvider())
    }


    fun signPdfWithVisibleSignature(
        inputPdfPath: String,
        outputPdfPath: String,
        doctorName: String,
        doctorTitle: String = "Bac si",
        reason: String = "Ket qua kham benh"
    ): String {

        if (!hospitalKeyService.isKeysReady()) {
            throw IllegalStateException("Hospital keys chưa sẵn sàng")
        }

        val privateKey = hospitalKeyService.getPrivateKey()
        val certificate = hospitalKeyService.getCertificate()
        val hospitalName = hospitalKeyService.getHospitalName()

        val reader = PdfReader(inputPdfPath)
        val outputStream = FileOutputStream(outputPdfPath)
        val signer = PdfSigner(reader, outputStream, StampingProperties())

        // Cấu hình chữ ký
        val appearance = signer.signatureAppearance
        appearance.setReason(reason)
        appearance.setLocation(hospitalName)

        // VỊ TRÍ: Góc dưới bên trái (dễ test nhất)
        // x, y, width, height
        val rect = Rectangle(36f, 36f, 200f, 80f) // 36 = 0.5 inch lề
        appearance.setPageRect(rect)
        appearance.setPageNumber(1)

        // Nội dung hiển thị
        val currentTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")

                val signText = """
        Digitally signed by: $doctorName
        $doctorTitle - $hospitalName
        Date: ${currentTime.format(formatter)}
        Reason: $reason
            """.trimIndent()

        appearance.setLayer2Text(signText)

        // set Layer2Font
        val font = com.itextpdf.kernel.font.PdfFontFactory.createFont(
            com.itextpdf.io.font.constants.StandardFonts.HELVETICA
        )
        appearance.setLayer2Font(font)

        // Set rendering mode
        appearance.setRenderingMode(PdfSignatureAppearance.RenderingMode.DESCRIPTION)

        // Thực hiện ký
        val chain = arrayOf(certificate)
        val pks = PrivateKeySignature(
            privateKey,
            DigestAlgorithms.SHA256,
            BouncyCastleProvider.PROVIDER_NAME
        )
        val digest = BouncyCastleDigest()

        signer.signDetached(digest, pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CMS)

        println(" Đã ký file: $outputPdfPath")

        return calculateFileHash(outputPdfPath)
    }


    fun verifyPdfSignature(pdfPath: String): SignatureVerificationResult {
        try {
            val reader = PdfReader(pdfPath)
            val document = com.itextpdf.kernel.pdf.PdfDocument(reader)
            val signUtil = SignatureUtil(document)

            val signatureNames = signUtil.signatureNames

            if (signatureNames.isEmpty()) {
                return SignatureVerificationResult(
                    isValid = false,
                    message = "File không có chữ ký số"
                )
            }

            // Lấy chữ ký đầu tiên (trong trường hợp có nhiều chữ ký)
            val signatureName = signatureNames.first()
            val pkcs7 = signUtil.readSignatureData(signatureName)

            // Verify
            val isIntegrityValid = pkcs7.verifySignatureIntegrityAndAuthenticity()

            // Lấy thông tin
            val signerCert = pkcs7.signingCertificate
            val signerName = signerCert.subjectDN.name
            val signDate = pkcs7.signDate?.toString() ?: "Không rõ"

            document.close()

            return SignatureVerificationResult(
                isValid = isIntegrityValid,
                message = if (isIntegrityValid) "Chữ ký hợp lệ - File nguyên vẹn" else "Chữ ký không hợp lệ hoặc file đã bị sửa đổi",
                signerName = signerName,
                signDate = signDate,
                documentModified = !isIntegrityValid
            )

        } catch (e: Exception) {
            return SignatureVerificationResult(
                isValid = false,
                message = "Lỗi khi verify: ${e.message}",
                documentModified = true
            )
        }
    }


    private fun calculateFileHash(filePath: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val file = File(filePath)
        val buffer = ByteArray(8192)
        var count: Int

        FileInputStream(file).use { fis ->
            while (fis.read(buffer).also { count = it } > 0) {
                digest.update(buffer, 0, count)
            }
        }

        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}

@Serializable
data class SignatureVerificationResult(
    val isValid: Boolean,
    val message: String,
    val signerName: String? = null,
    val signDate: String? = null,
    val documentModified: Boolean = false
)