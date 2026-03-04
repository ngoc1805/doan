package com.example.routes

import com.example.controller.appointmentRoutes
import com.example.controller.userRoutes
import com.example.plugins.withRoleAuthorization
import com.example.repository.*
import com.example.routes.route.*
import com.example.service.*
import com.example.websocket.configureWebSocketRoutes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.example.config.JWTConfig
import menuRoutes

fun Application.configureRouting(
    authService: AuthService,
    jwtConfig: JWTConfig? = null,
    resultFileService : ResultFileService,
    hospitalKeyService: HospitalKeyService
) {
    val accountRepository = AccountRepository()
    val userRepository = UserRepository()
    val userService = UserService(userRepository)
    val mealRepository = MealRepository()
    val mealService = MealService(mealRepository)
    val chatService = ChatService()
    val geminiService = GeminiService("AIzaSyDuvJdihXpY3GxVLMase6j60mgoTs5re2Q")

    // AI Chat với Function Calling
    val aiGeminiService = GeminiServiceWithFunctions("AIzaSyDuvJdihXpY3GxVLMase6j60mgoTs5re2Q")
    val departmentRepo = DepartmentRepository()
    val doctorRepo = DoctorRepository()
    val appointmentRepo = AppointmentRepository()
    val bookingExecutor = BookingFunctionExecutor(
        departmentRepo = departmentRepo,
        doctorRepo = doctorRepo,
        appointmentRepo = appointmentRepo
    )

    routing {
        // Health check
        get("/") {
            call.respondText("Server is running! 🚀", ContentType.Text.Plain)
        }

        get("/health") {
            call.respondText("OK", ContentType.Text.Plain, HttpStatusCode.OK)
        }

        // Test JSON parsing
        post("/test-json") {
            try {
                val body = call.receiveText()
                call.respondText("Received: $body", ContentType.Text.Plain, HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respondText("Error: ${e.message}", ContentType.Text.Plain, HttpStatusCode.BadRequest)
            }
        }


        authRoutes(authService)
        departmentRoutes(DepartmentService(DepartmentRepository()))
        momoRoutes()
        sendNotification()
        accountRoutes(AccountService(AccountRepository()))
        allowallRoutets()
        menuRoutes(MenuService(MenuRepository()))
        configureWebSocketRoutes(jwtConfig)
        otpRoutes(accountRepository)
        pinRoutes(accountRepository, userService)
        forgotPasswordRoutes(accountRepository)
        publicFileRoute()
        chatRoutes(chatService, geminiService)
        aiChatRoutes(chatService, aiGeminiService, bookingExecutor) // ← AI Chat mới với Function Calling
        authenticate("auth-jwt"){
            withRoleAuthorization {
                adminRoutes()
                doctorRoutes(DoctorService(DoctorRepository()))
                userRoutes(UserService(UserRepository()))
                appointmentRoutes(AppointmentService(AppointmentRepository()))
                serviceAppointmentRoutes(ServiceAppointmentService(ServiceAppointmentRepository()))
                serviceRoomRoutes(ServiceRoomService(ServiceRoomRepository()))
                notificationRoute()
                FileRoute(resultFileService, hospitalKeyService)
                resultRoute()
                inpatientRoutes(InpatientService())
                orderRoutes(OrderService())
                orderItemRoutes(OrderItemService())
                appointmentPaymentRoute()
                transactionRoutes()
                depositPaymentRoute()
                mealPaymentRoute() // Added this line
                mealRoutes(mealService)
                adminMealStatisticsRoutes(mealService)
            }
        }

    }
}