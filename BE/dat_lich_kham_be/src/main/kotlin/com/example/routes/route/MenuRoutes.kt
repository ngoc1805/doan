import com.example.dto.Request.UpdateDisplayRequest
import com.example.dto.Response.BaseResponse
import com.example.service.MenuService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import java.io.File
import java.text.Normalizer

// Hàm chuyển tên thành slug
fun slugify(input: String): String {
    val normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
        .lowercase()
        .replace(Regex("[^a-z0-9\\s-]"), "") // chỉ giữ chữ, số, khoảng trắng, dấu -
        .replace(Regex("\\s+"), "-")         // thay khoảng trắng thành -
        .replace(Regex("-+"), "-")           // nhiều dấu - thành 1
        .trim('-')
    return normalized
}

fun Route.menuRoutes(menuService: MenuService) {
    route("/api/nhaan") {
        post("/menus") {
            val multipart = call.receiveMultipart()
            var imageUrl: String? = null
            var name: String? = null
            var description: String? = null
            var examPrice: Int? = null
            var category: String? = null
            var fileExtension = "jpg"
            var imagePart: PartData.FileItem? = null

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        when (part.name) {
                            "name" -> name = part.value
                            "description" -> description = part.value
                            "examPrice" -> examPrice = part.value.toIntOrNull()
                            "category" -> category = part.value
                        }
                    }
                    is PartData.FileItem -> {
                        if (part.name == "image") {
                            val ext = File(part.originalFileName ?: "image.jpg").extension.ifBlank { "jpg" }
                            fileExtension = ext
                            imagePart = part
                        }
                    }
                    else -> {}
                }
                if (part !is PartData.FileItem) {
                    part.dispose()
                }
            }

            if (name == null || description == null || examPrice == null || category == null || imagePart == null) {
                imagePart?.dispose?.let { it() }
                call.respond(HttpStatusCode.BadRequest, BaseResponse(false, "Thiếu dữ liệu", null))
                return@post
            }

            val slug = slugify(name!!)
            val fileName = "$slug.$fileExtension"
            val uploadDir = File("uploads")
            if (!uploadDir.exists()) uploadDir.mkdirs()
            val file = File(uploadDir, fileName)
            imagePart!!.streamProvider().use { input ->
                file.outputStream().buffered().use { output ->
                    input.copyTo(output)
                }
            }
            imagePart!!.dispose()
            imageUrl = "/uploads/$fileName"

            val menu = menuService.createMenu(name!!, description!!, examPrice!!, category!!, imageUrl!!)
            call.respond(
                BaseResponse(
                    success = true,
                    message = "Tạo món ăn thành công",
                    data = Json.encodeToJsonElement(menu)
                )
            )
        }
        //

        //
        put("/menus/is-display") {
            val request = call.receive<UpdateDisplayRequest>()
            val ok = menuService.updateIsDisplay(request.id, request.isDisplay)
            if (ok) {
                call.respond(
                    BaseResponse(
                        success = true,
                        message = "Cập nhật trạng thái hiển thị thành công"
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    BaseResponse(
                        success = false,
                        message = "Không tìm thấy menu với id = ${request.id}"
                    )
                )
            }
        }
    }
}