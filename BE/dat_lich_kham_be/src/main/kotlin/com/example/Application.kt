package com.example

import com.example.Tables.*
import com.example.config.*
import com.example.repository.AccountRepository
import com.example.repository.RedisTokenRepository
import com.example.repository.ResultFileRepository
import com.example.routes.configureRouting
import com.example.service.*
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import io.ktor.server.websocket.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import kotlin.time.Duration.Companion.seconds

fun main(args: Array<String>) {
    val dotenv = dotenv {
        directory = "./"
        ignoreIfMalformed = true
        ignoreIfMissing = true
    }

    dotenv.entries().forEach { entry ->
        System.setProperty(entry.key, entry.value)
    }
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    DatabaseFactory.init(environment.config)
    transaction {
        SchemaUtils.create(
            Roles,
            Departments,
            Accounts,
            Doctors,
            Users,
            Appointments,
            Notifications,
            ServiceRooms,
            ServiceAppointments,
            ResultFiles,
            Results,
            Inpatients,
            Menus,
            Orders,
            OrderItem,
            Transactions,
            MealSubscriptionCycles,
            MealSkipDates,
            ChatSessions,
            ChatMessages,
            TempMemories,
            ChatMemories,
            ChatMemoriesIndex
        )
    }

    val redisConfig = RedisConfig(
        host = environment.config.propertyOrNull("redis.host")?.getString() ?: "localhost",
        port = environment.config.propertyOrNull("redis.port")?.getString()?.toInt() ?: 6379,
        password = environment.config.propertyOrNull("redis.password")?.getString(),
        database = environment.config.propertyOrNull("redis.database")?.getString()?.toInt() ?: 0
    )
    val redisTokenRepository = RedisTokenRepository(redisConfig)

    // Test Redis connection
    println("Testing Redis: ${redisConfig.ping()}")

    val accountRepository = AccountRepository()

    // Khởi tạo Hospital Key Service
    val hospitalKeyService = HospitalKeyService()

    if (!hospitalKeyService.isKeysReady()) {
        println(" CẢNH BÁO: Keys chưa sẵn sàng. Chức năng ký file sẽ không hoạt động!")
    } else {
        println(" Hospital keys đã sẵn sàng")
        println(" Bệnh viện: ${hospitalKeyService.getHospitalName()}")
    }

    val loginSecurityService = LoginSecurityService()

    // Khởi tạo các service cho chữ ký số
    val signatureService = DigitalSignatureService(hospitalKeyService)
    val resultFileRepo = ResultFileRepository()
    val resultFileService = ResultFileService(resultFileRepo, signatureService)





    val jwtConfig = com.example.config.JWTConfig(
        domain = environment.config.property("jwt.domain").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        realm = environment.config.property("jwt.realm").getString(),
        secret = "your-secret-key-here",
        accessTokenExpiry = 1000L * 60 * 5,
        refreshTokenExpiry = 1000L * 60 * 60 * 24 * 30
    )
//    val authService = AuthService(accountRepository, jwtConfig, loginSecurityService, jwtConfig)
    val authService = AuthService(
        accountRepository = accountRepository,
        jwtConfig = jwtConfig,
        securityService = loginSecurityService,
        redisTokenRepository = redisTokenRepository
    )



    install(WebSockets) {
        pingPeriod = 15.seconds      // Server gửi ping mỗi 15s
        timeout = 120.seconds          // FIX: Tăng lên 60s (từ 30s)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    val uploadDir = File("uploads")
    if (!uploadDir.exists()) uploadDir.mkdirs()
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureSecurity(jwtConfig, redisTokenRepository)
    configureRouting(authService, jwtConfig, resultFileService, hospitalKeyService)
    //
    val serviceAccountStream = this::class.java.classLoader.getResourceAsStream("datlichkham-f0ca9-firebase-adminsdk-fbsvc-ee4161db61.json")
    val options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
        .build()

    FirebaseApp.initializeApp(options)
}
