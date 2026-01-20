package data.model

import kotlinx.serialization.Serializable

@Serializable
data class ResultFileItem(
    val id: Int,
    val fileName: String,
    val filePath: String,

    // Thêm các field cho chữ ký
    val isSigned: Boolean = false,
    val signedFilePath: String? = null,
    val signatureHash: String? = null,
    val signedByDoctorId: Int? = null,
    val signedByDoctorName: String? = null,
    val signedAt: String? = null
)

data class ListResultFileResponse(
    val resultfiles : List<ResultFileItem>
)


@Serializable
data class SignatureVerificationResult(
    val isValid: Boolean,
    val message: String,
    val signerName: String? = null,
    val signDate: String? = null,
    val documentModified: Boolean = false
)