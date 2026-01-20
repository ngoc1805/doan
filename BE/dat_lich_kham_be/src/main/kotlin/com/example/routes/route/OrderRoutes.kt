package com.example.routes

import com.example.dto.Request.OrderRequest
import com.example.dto.Response.BaseResponse
import com.example.service.OrderService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import java.io.File
import java.text.Normalizer
import java.util.*

// Thêm hàm slugify tại đây
fun slugify(input: String): String {
    return Normalizer.normalize(input, Normalizer.Form.NFD)
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
        .replace("[^\\w\\s-]".toRegex(), "")
        .replace("\\s+".toRegex(), "-")
        .lowercase(Locale.getDefault())
        .trim('-')
}

fun Route.orderRoutes(orderService: OrderService) {
    route("/api/benhnhan") {
        post("/orders") {
            val request = call.receive<OrderRequest>()
            val order = orderService.createOrder(
                userId = request.userId,
                phone = request.phone,
                address = request.address,
                note = request.note ?: "",
                status = request.status
            )
            call.respond(
                BaseResponse(
                    success = true,
                    message = "Tạo đơn thành công",
                    data = Json.encodeToJsonElement(order)
                )
            )
        }

        get("/orders/by-user") {
            val userId = call.request.queryParameters["userId"]?.toIntOrNull()
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, "Thiếu hoặc sai userId")
                return@get
            }
            // Lấy list status từ query param, ví dụ: ?status=Đã đặt hàng&status=Đã thanh toán
            val statuses = call.request.queryParameters.getAll("status")
                ?.filter { it.isNotBlank() }
                ?: listOf("Đã đặt hàng", "Đã thanh toán") // mặc định nếu không truyền

            val orders = orderService.getOrdersWithItemsByStatusesAndUserId(statuses, userId)
            call.respond(orders)
        }
    }

    route("api/nhaan") {
        get("/orders") {
            val statuses = call.request.queryParameters.getAll("status")
                ?.filter { it.isNotBlank() }
                ?: listOf("Đã đặt hàng", "Đã thanh toán")

            val orders = orderService.getOrdersWithItemsByStatuses(statuses)
            call.respond(orders)
        }

        put("/orders/update-status-and-image") {
            try {
                val multipart = call.receiveMultipart()
                var orderId: Int? = null
                var imageBytes: ByteArray? = null
                var originalFileName: String? = null

                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            if (part.name == "orderId") {
                                orderId = part.value.toIntOrNull()
                            }
                        }

                        is PartData.FileItem -> {
                            if (part.name == "image") {
                                originalFileName = part.originalFileName
                                // Đọc toàn bộ bytes trước khi xử lý
                                imageBytes = part.streamProvider().readBytes()
                            }
                        }

                        else -> {}
                    }
                    part.dispose()
                }

                if (orderId == null || imageBytes == null || imageBytes!!.isEmpty()) {
                    call.respond(HttpStatusCode.BadRequest, BaseResponse(false, "Thiếu orderId hoặc ảnh", null))
                    return@put
                }

                // Kiểm tra kích thước file (tối đa 5MB)
                if (imageBytes!!.size > 5 * 1024 * 1024) {
                    call.respond(HttpStatusCode.BadRequest, BaseResponse(false, "File ảnh quá lớn (tối đa 5MB)", null))
                    return@put
                }

                // Lấy order, user
                val order = orderService.getOrderById(orderId!!)
                if (order == null) {
                    call.respond(HttpStatusCode.NotFound, BaseResponse(false, "Không tìm thấy đơn hàng", null))
                    return@put
                }

                val user = orderService.getUserByOrderId(orderId!!)
                if (user == null) {
                    call.respond(HttpStatusCode.NotFound, BaseResponse(false, "Không tìm thấy user", null))
                    return@put
                }

                // Xử lý extension file an toàn hơn
                val fileExtension = when {
                    originalFileName?.lowercase()?.endsWith(".jpg") == true -> "jpg"
                    originalFileName?.lowercase()?.endsWith(".jpeg") == true -> "jpg"
                    originalFileName?.lowercase()?.endsWith(".png") == true -> "png"
                    originalFileName?.lowercase()?.endsWith(".webp") == true -> "webp"
                    else -> "jpg" // mặc định
                }

                // Tạo tên file an toàn (loại bỏ ký tự đặc biệt)
                val safeName = user.fullName
                    .replace(Regex("[^a-zA-Z0-9\\s]"), "")
                    .replace(Regex("\\s+"), "_")
                    .lowercase()

                val fileName = "${orderId}_${safeName}.${fileExtension}"

                // Tạo thư mục uploads
                val uploadDir = File("uploads")
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs()
                }

                val file = File(uploadDir, fileName)

                // Ghi file một cách an toàn
                try {
                    file.writeBytes(imageBytes!!)
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        BaseResponse(false, "Lỗi khi lưu file: ${e.message}", null)
                    )
                    return@put
                }

                val imageUrl = "/uploads/$fileName"

                // Cập nhật trạng thái và ảnh
                val ok = orderService.updateOrderStatusAndImage(orderId!!, "Đã giao hàng", imageUrl)
                if (!ok) {
                    // Xóa file nếu cập nhật database thất bại
                    file.delete()
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        BaseResponse(false, "Cập nhật trạng thái/ảnh lỗi", null)
                    )
                    return@put
                }

                // Chỉ cộng tiền nếu status ban đầu là "Đã thanh toán"
                if (order.status == "Đã thanh toán") {
                    val total = orderService.getOrderTotalPrice(orderId!!)
                    orderService.increaseUserBalance(2, total)
                }

                call.respond(BaseResponse(true, "Cập nhật đơn hàng thành công", null))

            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, BaseResponse(false, "Lỗi server: ${e.message}", null))
            }
        }
    }
}