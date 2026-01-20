package com.example.service

import java.io.File
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.util.Date

class HospitalKeyService {

    private var privateKey: PrivateKey? = null
    private var publicKey: PublicKey? = null
    private var certificate: java.security.cert.X509Certificate? = null

    private val hospitalName = "Bệnh viện ABC"
    private val keysDirectory = "keys"
    private val privateKeyFile = File("$keysDirectory/hospital_private.pem")
    private val publicKeyFile = File("$keysDirectory/hospital_public.pem")
    private val certificateFile = File("$keysDirectory/hospital_cert.pem")

    init {
        Security.addProvider(BouncyCastleProvider())
        initializeKeys()
    }

    private fun initializeKeys() {
        // Tạo thư mục keys nếu chưa có
        val keysDir = File(keysDirectory)
        if (!keysDir.exists()) {
            keysDir.mkdirs()
            println(" Đã tạo thư mục: $keysDirectory")
        }

        // Kiểm tra xem đã có keys chưa
        if (privateKeyFile.exists() && publicKeyFile.exists() && certificateFile.exists()) {
            // Load keys có sẵn
            loadExistingKeys()
        } else {
            // Tạo keys mới
            generateNewKeys()
        }
    }

    private fun loadExistingKeys() {
        try {
            println(" Đang load keys từ file...")

            privateKey = loadPrivateKeyFromFile(privateKeyFile)
            publicKey = loadPublicKeyFromFile(publicKeyFile)
            certificate = loadCertificateFromFile(certificateFile)

            println(" Đã load keys thành công!")
            println("   - Private key: ${privateKeyFile.absolutePath}")
            println("   - Public key: ${publicKeyFile.absolutePath}")
            println("   - Certificate: ${certificateFile.absolutePath}")

        } catch (e: Exception) {
            println(" Lỗi khi load keys: ${e.message}")
            println("️  Sẽ tạo keys mới...")
            generateNewKeys()
        }
    }

    private fun generateNewKeys() {
        try {
            println(" Đang tạo cặp khóa mới cho bệnh viện...")

            // Tạo cặp khóa RSA 2048-bit
            val keyGen = KeyPairGenerator.getInstance("RSA")
            keyGen.initialize(2048, SecureRandom())
            val keyPair = keyGen.generateKeyPair()

            privateKey = keyPair.private
            publicKey = keyPair.public

            // Tạo self-signed certificate
            certificate = generateSelfSignedCertificate(keyPair)

            // Lưu vào file
            savePrivateKeyToFile(privateKey!!, privateKeyFile)
            savePublicKeyToFile(publicKey!!, publicKeyFile)
            saveCertificateToFile(certificate!!, certificateFile)

            println(" Đã tạo và lưu keys thành công!")
            println("   - Private key: ${privateKeyFile.absolutePath}")
            println("   - Public key: ${publicKeyFile.absolutePath}")
            println("   - Certificate: ${certificateFile.absolutePath}")
            println("")
            println("  QUAN TRỌNG:")
            println("   1. BACKUP thư mục keys/ ngay!")
            println("   2. KHÔNG commit keys/ vào git!")
            println("   3. Giữ private key tuyệt đối bảo mật!")

        } catch (e: Exception) {
            println(" Lỗi khi tạo keys: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun loadPrivateKeyFromFile(file: File): PrivateKey {
        val keyContent = file.readText()
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\s".toRegex(), "")

        val keyBytes = Base64.getDecoder().decode(keyContent)
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePrivate(keySpec)
    }

    private fun loadPublicKeyFromFile(file: File): PublicKey {
        val keyContent = file.readText()
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\\s".toRegex(), "")

        val keyBytes = Base64.getDecoder().decode(keyContent)
        val keySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(keySpec)
    }

    private fun loadCertificateFromFile(file: File): java.security.cert.X509Certificate {
        val certContent = file.readText()
            .replace("-----BEGIN CERTIFICATE-----", "")
            .replace("-----END CERTIFICATE-----", "")
            .replace("\\s".toRegex(), "")

        val certBytes = Base64.getDecoder().decode(certContent)
        val certFactory = java.security.cert.CertificateFactory.getInstance("X.509")
        return certFactory.generateCertificate(
            java.io.ByteArrayInputStream(certBytes)
        ) as java.security.cert.X509Certificate
    }

    private fun savePrivateKeyToFile(key: PrivateKey, file: File) {
        val encoded = Base64.getEncoder().encodeToString(key.encoded)
        val pem = buildString {
            appendLine("-----BEGIN PRIVATE KEY-----")
            encoded.chunked(64).forEach { appendLine(it) }
            append("-----END PRIVATE KEY-----")
        }
        file.writeText(pem)
    }

    private fun savePublicKeyToFile(key: PublicKey, file: File) {
        val encoded = Base64.getEncoder().encodeToString(key.encoded)
        val pem = buildString {
            appendLine("-----BEGIN PUBLIC KEY-----")
            encoded.chunked(64).forEach { appendLine(it) }
            append("-----END PUBLIC KEY-----")
        }
        file.writeText(pem)
    }

    private fun saveCertificateToFile(cert: java.security.cert.Certificate, file: File) {
        val encoded = Base64.getEncoder().encodeToString(cert.encoded)
        val pem = buildString {
            appendLine("-----BEGIN CERTIFICATE-----")
            encoded.chunked(64).forEach { appendLine(it) }
            append("-----END CERTIFICATE-----")
        }
        file.writeText(pem)
    }

    private fun generateSelfSignedCertificate(
        keyPair: KeyPair
    ): java.security.cert.X509Certificate {
        val now = System.currentTimeMillis()
        val startDate = Date(now)
        val endDate = Date(now + 365L * 24 * 60 * 60 * 1000 * 5) // 5 năm

        val certGen = org.bouncycastle.x509.X509V3CertificateGenerator()
        val dnName = org.bouncycastle.asn1.x509.X509Name(
            "CN=$hospitalName, O=Healthcare System, C=VN"
        )

        certGen.setSerialNumber(java.math.BigInteger.valueOf(now))
        certGen.setSubjectDN(dnName)
        certGen.setIssuerDN(dnName) // Self-signed
        certGen.setNotBefore(startDate)
        certGen.setNotAfter(endDate)
        certGen.setPublicKey(keyPair.public)
        certGen.setSignatureAlgorithm("SHA256WithRSA")

        return certGen.generate(keyPair.private, "BC")
    }

    fun getPrivateKey(): PrivateKey {
        return privateKey ?: throw IllegalStateException("Private key chưa được khởi tạo")
    }

    fun getPublicKey(): PublicKey {
        return publicKey ?: throw IllegalStateException("Public key chưa được khởi tạo")
    }

    fun getCertificate(): java.security.cert.Certificate {
        return certificate ?: throw IllegalStateException("Certificate chưa được khởi tạo")
    }

    fun getHospitalName(): String = hospitalName

    fun isKeysReady(): Boolean {
        return privateKey != null && publicKey != null && certificate != null
    }
}