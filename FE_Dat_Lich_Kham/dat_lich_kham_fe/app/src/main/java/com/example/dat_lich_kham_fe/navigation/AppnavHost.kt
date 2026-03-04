package com.example.dat_lich_kham_fe.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.glance.appwidget.compose
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.dat_lich_kham_fe.chatbot.ChatPageWithMemory
import com.example.dat_lich_kham_fe.data.model.OrderItemDetail
import com.example.dat_lich_kham_fe.data.model.ResultFileItem
import com.example.dat_lich_kham_fe.ui.components.GlobalSessionExpiredDialog
import com.example.dat_lich_kham_fe.ui.screen.BookingScreen
import com.example.dat_lich_kham_fe.ui.screen.ChangePasswordScreen
import com.example.dat_lich_kham_fe.ui.screen.ConfirmPinScreen
import com.example.dat_lich_kham_fe.ui.screen.CreatePinScreen
import com.example.dat_lich_kham_fe.ui.screen.DepartmentScreen
import com.example.dat_lich_kham_fe.ui.screen.DepositScreen
import com.example.dat_lich_kham_fe.ui.screen.DetailDepartmentScreen
import com.example.dat_lich_kham_fe.ui.screen.DetailNewsScreen
import com.example.dat_lich_kham_fe.ui.screen.ExaminationResultScreen
import com.example.dat_lich_kham_fe.ui.screen.ForgotPasswordScreen
import com.example.dat_lich_kham_fe.ui.screen.ForgotPinScreen
import com.example.dat_lich_kham_fe.ui.screen.HealthIndex
import com.example.dat_lich_kham_fe.ui.screen.HealthRecordScreen
import com.example.dat_lich_kham_fe.ui.screen.HistoryScreen
import com.example.dat_lich_kham_fe.ui.screen.InFoScreen
import com.example.dat_lich_kham_fe.ui.screen.InpatientMealScreen
import com.example.dat_lich_kham_fe.ui.screen.Login_RegisterScreen
import com.example.dat_lich_kham_fe.ui.screen.MainScreen
import com.example.dat_lich_kham_fe.ui.screen.MealPaymentScreen
import com.example.dat_lich_kham_fe.ui.screen.MedicalExaminationScreen
import com.example.dat_lich_kham_fe.ui.screen.NotificationScreen
import com.example.dat_lich_kham_fe.ui.screen.OrderConfirmationScreen
import com.example.dat_lich_kham_fe.ui.screen.OrderDetailScreen
import com.example.dat_lich_kham_fe.ui.screen.OrderHistoryDetailScreen
import com.example.dat_lich_kham_fe.ui.screen.OrderHistoryScreen
import com.example.dat_lich_kham_fe.ui.screen.OrderMealScreen
import com.example.dat_lich_kham_fe.ui.screen.OrderScreen
import com.example.dat_lich_kham_fe.ui.screen.PDFViewerScreen
import com.example.dat_lich_kham_fe.ui.screen.PaymentScreen
import com.example.dat_lich_kham_fe.ui.screen.PinCodeScreenV2
import com.example.dat_lich_kham_fe.ui.screen.QrScannerScreen
import com.example.dat_lich_kham_fe.ui.screen.ResetPasswordScreen
import com.example.dat_lich_kham_fe.ui.screen.ResetPinScreen
import com.example.dat_lich_kham_fe.ui.screen.TransactionScreen
import com.example.dat_lich_kham_fe.viewmodel.InFoViewModel
//import com.google.common.reflect.TypeToken
import com.google.gson.reflect.TypeToken
import com.google.gson.Gson
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppnavHost(
    navController: NavHostController,
    inFoViewModel: InFoViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "MainScreen"
    ) {
        composable("Login_RegisterScreen") {
            Login_RegisterScreen(navController)
        }
        composable("MainScreen") {
            MainScreen(navController, 0)
        }
        composable("MainScreen/{initialTab}"){backStackEntry ->
            val initialTab = backStackEntry.arguments?.getString("initialTab")?.toIntOrNull() ?: 0
            MainScreen(
                navController,
                initialTab = initialTab
            )
        }
        composable("DepartmentScreen") {
            DepartmentScreen(navController)
        }
        composable("DetailDepartmentScreen/{id}/{name}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            val name = backStackEntry.arguments?.getString("name") ?: ""
            DetailDepartmentScreen(navController, id, name)
        }
        composable("HealthIndex") {
            HealthIndex(navController)
        }
        composable("InFoScreen") {
            InFoScreen(navController, inFoViewModel)
        }
        composable("QrScannerScreen") {
            QrScannerScreen(navController, inFoViewModel)
        }
        composable(
            "BookingScreen/{doctor_id}/{doctor_name}/{doctor_code}/{examPrice}/{department}/{date}",
        ) { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctor_id")?.toInt() ?: 0
            val doctorName = backStackEntry.arguments?.getString("doctor_name") ?: ""
            val doctorCode = backStackEntry.arguments?.getString("doctor_code") ?: ""
            val examPrice = backStackEntry.arguments?.getString("examPrice")?.toInt() ?: 0
            val department = backStackEntry.arguments?.getString("department") ?: ""
            val date = backStackEntry.arguments?.getString("date") ?: "" // có thể truyền fullDate

            BookingScreen(
                navController = navController,
                doctor_id = doctorId,
                doctor_name = doctorName,
                doctor_code = doctorCode,
                examPrice = examPrice,
                department = department,
                date = date
            )
        }
        composable("PaymentScreen/{doctor_id}/{examDate}/{examTime}") {
                backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctor_id")?.toInt() ?: 0
            val examDate = backStackEntry.arguments?.getString("examDate") ?: ""
            val examTime = backStackEntry.arguments?.getString("examTime") ?: ""
            PaymentScreen(
                navController = navController,
                doctorId = doctorId,
                examDate = examDate,
                examTime = examTime
            )
        }
        composable("DepositScreen/{momoSuccess}") { backStackEntry ->
            val momoSuccess = backStackEntry.arguments?.getString("momoSuccess")?.toBoolean() ?: false
            DepositScreen(navController,momoSuccess = momoSuccess)
        }
        // Route mới có cả momoSuccess và amount
        composable("DepositScreen/{momoSuccess}/{amount}") { backStackEntry ->
            val momoSuccess = backStackEntry.arguments?.getString("momoSuccess")?.toBoolean() ?: false
            val amount = backStackEntry.arguments?.getString("amount")
            DepositScreen(navController, momoSuccess = momoSuccess, amount = amount)
        }
        //
        composable("CreatePinScreen") {
            // Truyền các tham số cần thiết (navController, callback, ...)
            CreatePinScreen(
                onPinCreated = { pin ->
                    navController.navigate("ConfirmPinScreen/$pin")
                },
                onClose = { navController.popBackStack() }
            )
        }
        composable("ConfirmPinScreen/{originalPin}") { backStackEntry ->
            val originalPin = backStackEntry.arguments?.getString("originalPin") ?: ""
            ConfirmPinScreen(
                originalPin = originalPin,
                onPinConfirmed = { confirmedPin ->
                    // Gọi API tạo PIN tại đây, xong chuyển hướng tiếp
                    // userViewModel.createOrUpdatePin(...)
                    navController.navigate("PinCodeScreenV2")
                },
                onPinMismatch = {
                    navController.navigate("CreatePinScreen") {
                        popUpTo("ConfirmPinScreen") { inclusive = true }
                    }
                },
                onClose = { navController.popBackStack() }
            )
        }
        composable("PinCodeScreenV2") {
            PinCodeScreenV2(
                onPinEntered = { pin ->
                    // Gọi API checkPin, nếu đúng thì chuyển tiếp, sai thì báo lỗi
                    // userViewModel.checkPin(...)
                },
                onClose = { navController.popBackStack() },
                errorMessage = "" ,
                navController = navController
            )
        }
        composable("NotificationScreen"){
            NotificationScreen(navController)
        }
        composable("HistoryScreen") {
            HistoryScreen(navController)
        }
        composable(
            "MedicalExaminationScreen/{id}/{doctorId}/{doctorName}/{doctorCode}/{department}/{examPrice}/{examDate}/{examTime}/{status}"
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toInt() ?: 0
            val doctorId = backStackEntry.arguments?.getString("doctorId")?.toInt() ?: 0
            val doctorName = backStackEntry.arguments?.getString("doctorName") ?: ""
            val doctorCode = backStackEntry.arguments?.getString("doctorCode") ?: ""
            val department = backStackEntry.arguments?.getString("department") ?: ""
            val examPrice = backStackEntry.arguments?.getString("examPrice")?.toInt() ?: 0
            val examDate = backStackEntry.arguments?.getString("examDate") ?: ""
            val examTime = backStackEntry.arguments?.getString("examTime") ?: ""
            val status = backStackEntry.arguments?.getString("status") ?: ""
            MedicalExaminationScreen(
                navController = navController,
                id = id,
                doctorId = doctorId,
                doctorName = doctorName,
                doctorCode = doctorCode,
                department = department,
                examPrice = examPrice,
                examDate = examDate,
                examTime = examTime,
                status = status
            )
        }
        composable("HealthRecordScreen") {
            HealthRecordScreen(navController)
        }
        //
        composable("ExaminationResultScreen/{appointmentId}/{fullName}/{comment}/{dietRecommendation}/{resultFiles}/{examDate}") { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getString("appointmentId")?.toInt() ?: 0
            val encodedFullName = backStackEntry.arguments?.getString("fullName") ?: ""
            val encodedComment = backStackEntry.arguments?.getString("comment") ?: ""
            val encodedDietRecommendation = backStackEntry.arguments?.getString("dietRecommendation") ?: ""
            val encodedResultFiles = backStackEntry.arguments?.getString("resultFiles") ?: ""
            val encodedExamDate = backStackEntry.arguments?.getString("examDate") ?: ""

            // Decode URL encoded strings
            val fullName = URLDecoder.decode(encodedFullName, StandardCharsets.UTF_8.toString())
            val comment = URLDecoder.decode(encodedComment, StandardCharsets.UTF_8.toString())
            val dietRecommendation = URLDecoder.decode(encodedDietRecommendation, StandardCharsets.UTF_8.toString()).takeIf { it.isNotEmpty() }
            val examDate = URLDecoder.decode(encodedExamDate, StandardCharsets.UTF_8.toString())
            val resultFilesJson = URLDecoder.decode(encodedResultFiles, StandardCharsets.UTF_8.toString())

            // Parse JSON back to List<ResultFileItem>
            val gson = Gson()
            val listType = object : TypeToken<List<ResultFileItem>>() {}.type
            val resultFiles: List<com.example.dat_lich_kham_fe.data.model.ResultFileItem> = try {
                gson.fromJson(resultFilesJson, listType) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }

            ExaminationResultScreen(
                navController = navController,
                appointmentId = appointmentId,
                fullName = fullName,
                comment = comment,
                dietRecommendation = dietRecommendation,
                resultFiles = resultFiles,
                examDate = examDate,
            )
        }
        //
        // Thêm vào NavHost của bạn:
        composable(
            "pdf_viewer/{fileName}/{fileUrl}",
            arguments = listOf(
                navArgument("fileName") { type = NavType.StringType },
                navArgument("fileUrl") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val fileName = backStackEntry.arguments?.getString("fileName") ?: ""
            val fileUrl = backStackEntry.arguments?.getString("fileUrl") ?: ""

            // Decode URL-encoded parameters
            val decodedFileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8.toString())
            val decodedFileUrl = URLDecoder.decode(fileUrl, StandardCharsets.UTF_8.toString())

            PDFViewerScreen(
                navController = navController,
                fileName = decodedFileName,
                fileUrl = decodedFileUrl
            )
        }
        //
        composable("ChatPage") {
            ChatPageWithMemory(navController)
        }
        composable("InpatientMealScreen") {
            InpatientMealScreen(navController)
        }
        composable("MealPaymentScreen/{cycleId}/{inpatientId}") { backStackEntry ->
            val cycleId = backStackEntry.arguments?.getString("cycleId")?.toIntOrNull() ?: 0
            val inpatientId = backStackEntry.arguments?.getString("inpatientId")?.toIntOrNull() ?: 0
            MealPaymentScreen(navController, cycleId, inpatientId)
        }
        composable("OrderMealScreen") {
            OrderMealScreen(navController)
        }
        //
        composable("detailNews?link={link}") { backStackEntry ->
            DetailNewsScreen(backStackEntry)
        }

        composable(
            route = "orderConfirmation/{cartData}",
            arguments = listOf(navArgument("cartData") { type = NavType.StringType })
        ) { backStackEntry ->
            val cartData = backStackEntry.arguments?.getString("cartData") ?: ""
            OrderConfirmationScreen(navController, cartData)
        }
        //
        composable("OrderScreen"){
            OrderScreen(navController)
        }
        //
        composable(
            route = "OrderDetailScreen/{id}/{userId}/{userName}/{fmctoken}/{phone}/{address}/{note}/{status}/{items}/{totalPrice}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType },
                navArgument("userId") { type = NavType.IntType },
                navArgument("userName") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                },
                navArgument("fmctoken") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                },
                navArgument("phone") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                },
                navArgument("address") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                },
                navArgument("note") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = "Không có ghi chú"
                },
                navArgument("status") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                },
                navArgument("items") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                },
                navArgument("totalPrice") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0

            val userName = URLDecoder.decode(
                backStackEntry.arguments?.getString("userName") ?: "",
                StandardCharsets.UTF_8.toString()
            )
            val fmctoken = URLDecoder.decode(
                backStackEntry.arguments?.getString("fmctoken") ?: "",
                StandardCharsets.UTF_8.toString()
            )
            val phone = URLDecoder.decode(
                backStackEntry.arguments?.getString("phone") ?: "",
                StandardCharsets.UTF_8.toString()
            )
            val address = URLDecoder.decode(
                backStackEntry.arguments?.getString("address") ?: "",
                StandardCharsets.UTF_8.toString()
            )
            val note = URLDecoder.decode(
                backStackEntry.arguments?.getString("note") ?: "",
                StandardCharsets.UTF_8.toString()
            )
            val status = URLDecoder.decode(
                backStackEntry.arguments?.getString("status") ?: "",
                StandardCharsets.UTF_8.toString()
            )
            val encodedItems = backStackEntry.arguments?.getString("items") ?: ""
            val totalPrice = backStackEntry.arguments?.getInt("totalPrice") ?: 0

            val itemsJson = URLDecoder.decode(encodedItems, StandardCharsets.UTF_8.toString())
            val gson = Gson()
            val listType = object : com.google.gson.reflect.TypeToken<List<OrderItemDetail>>() {}.type
            val items: List<OrderItemDetail> = try {
                gson.fromJson(itemsJson, listType) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }

            OrderDetailScreen(
                navController = navController,
                id = id,
                userId = userId,
                userName = userName,
                fmctoken = fmctoken,
                phone = phone,
                address = address,
                note = note,
                status = status,
                items = items,
                totalPrice = totalPrice
            )
        }
        //
        composable("OrderHistoryScreen"){
            OrderHistoryScreen(navController)
        }
        //
        composable(
            route = "OrderHistoryDetailScreen/{id}/{userId}/{userName}/{fmctoken}/{phone}/{address}/{note}/{status}/{items}/{totalPrice}/{imageUrl}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType },
                navArgument("userId") { type = NavType.IntType },
                navArgument("userName") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                },
                navArgument("fmctoken") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                },
                navArgument("phone") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                },
                navArgument("address") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                },
                navArgument("note") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = "Không có ghi chú"
                },
                navArgument("status") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                },
                navArgument("items") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                },
                navArgument("totalPrice") { type = NavType.IntType },
                navArgument("imageUrl") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0

            val userName = URLDecoder.decode(
                backStackEntry.arguments?.getString("userName") ?: "",
                StandardCharsets.UTF_8.toString()
            )
            val fmctoken = URLDecoder.decode(
                backStackEntry.arguments?.getString("fmctoken") ?: "",
                StandardCharsets.UTF_8.toString()
            )
            val phone = URLDecoder.decode(
                backStackEntry.arguments?.getString("phone") ?: "",
                StandardCharsets.UTF_8.toString()
            )
            val address = URLDecoder.decode(
                backStackEntry.arguments?.getString("address") ?: "",
                StandardCharsets.UTF_8.toString()
            )
            val note = URLDecoder.decode(
                backStackEntry.arguments?.getString("note") ?: "",
                StandardCharsets.UTF_8.toString()
            )
            val status = URLDecoder.decode(
                backStackEntry.arguments?.getString("status") ?: "",
                StandardCharsets.UTF_8.toString()
            )
            val encodedItems = backStackEntry.arguments?.getString("items") ?: ""
            val totalPrice = backStackEntry.arguments?.getInt("totalPrice") ?: 0
            val imageUrl = URLDecoder.decode(
                backStackEntry.arguments?.getString("imageUrl") ?: "",
                StandardCharsets.UTF_8.toString()
            )

            val itemsJson = URLDecoder.decode(encodedItems, StandardCharsets.UTF_8.toString())
            val gson = Gson()
            val listType = object : com.google.gson.reflect.TypeToken<List<OrderItemDetail>>() {}.type
            val items: List<OrderItemDetail> = try {
                gson.fromJson(itemsJson, listType) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }

            OrderHistoryDetailScreen(
                navController = navController,
                id = id,
                userId = userId,
                userName = userName,
                fmctoken = fmctoken,
                phone = phone,
                address = address,
                note = note,
                status = status,
                items = items,
                totalPrice = totalPrice,
                imageUrl = imageUrl
            )
        }
        //
        composable("ChangePasswordScreen"){
            ChangePasswordScreen(navController)
        }

        composable("ForgotPasswordScreen") {
            ForgotPasswordScreen(navController)
        }

        //  Route: Đặt mật khẩu mới (Màn 2 - Reset Password)
        composable(
            route = "ResetPasswordScreen/{resetToken}",
            arguments = listOf(navArgument("resetToken") { type = NavType.StringType })
        ) { backStackEntry ->
            val resetToken = backStackEntry.arguments?.getString("resetToken") ?: ""
            ResetPasswordScreen(navController, resetToken)
        }

        composable("ChangePasswordScreen"){
            ChangePasswordScreen(navController)
        }

        //
        composable("ForgotPinScreen") {
            ForgotPinScreen(navController)
        }

        // ✅ Đặt mã PIN mới (Màn 2 - Reset PIN)
        composable(
            route = "ResetPinScreen/{resetToken}",
            arguments = listOf(navArgument("resetToken") { type = NavType.StringType })
        ) { backStackEntry ->
            val resetToken = backStackEntry.arguments?.getString("resetToken") ?: ""
            ResetPinScreen(navController, resetToken)
        }

        composable("TransactionScreen") {
            TransactionScreen(navController = navController)
        }

    }
//    GlobalSessionExpiredDialog(
//        navController = navController
//    )
}
