package ui.screen.serviceroom

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import viewmodel.FileViewModel
import viewmodel.FileUploadState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.io.File
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import viewmodel.ServiceAppointmentViewModel

class ServiceExaminationSreen(
    val id: Int,
    val appointmentId: Int,
    val userId: Int,
    val userName: String,
    val gender: String,
    val birthDate: String,
    val homeTown: String,
    val cccd: String,
    val examDate: String,
    val examTime: String,
    val status: String,
    val fmctoken: String,
    val nameServiceRoom: String
) : Screen {
    @Composable
    override fun Content() {
        val fileViewModel = getScreenModel<FileViewModel>()
        val serviceAppointmentViewModel = remember { ServiceAppointmentViewModel() }
        val uploadState by fileViewModel.uploadState.collectAsState()
        val scope = rememberCoroutineScope()

        val currentDate = LocalDate.now().toString()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val formattedBirthDate = try {
            LocalDate.parse(birthDate).format(formatter)
        } catch (e: Exception) {
            birthDate
        }
        val navigator = LocalNavigator.currentOrThrow

        var resultText by remember { mutableStateOf("") }
        var selectedFile by remember { mutableStateOf<File?>(null) }
        var selectedFileName by remember { mutableStateOf<String?>(null) }

        // Thêm state để quản lý hiển thị thông báo
        var showSuccessMessage by remember { mutableStateOf(false) }
        var showErrorMessage by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf("") }

        // Tạo tên file mới theo format
        val generateFileName = { originalFileName: String ->
            val extension = originalFileName.substringAfterLast(".")
            "${nameServiceRoom} - Bệnh nhân $userName - $appointmentId.$extension"
        }

        // Function để chọn file - sử dụng FileDialog đẹp hơn
        val selectFile = {
            val fileDialog = java.awt.FileDialog(null as java.awt.Frame?, "Chọn file kết quả khám", java.awt.FileDialog.LOAD)

            // Thiết lập filter cho các file y tế
            fileDialog.setFilenameFilter { _, name ->
                val lowercaseName = name.lowercase()
                lowercaseName.endsWith(".pdf") ||
                        lowercaseName.endsWith(".jpg") ||
                        lowercaseName.endsWith(".jpeg") ||
                        lowercaseName.endsWith(".png") ||
                        lowercaseName.endsWith(".doc") ||
                        lowercaseName.endsWith(".docx")
            }

            fileDialog.isVisible = true

            if (fileDialog.file != null) {
                val selectedFileTemp = File(fileDialog.directory + fileDialog.file)
                selectedFile = selectedFileTemp
                selectedFileName = generateFileName(selectedFileTemp.name)
                // Cập nhật tên file vào ô kết quả khám
                resultText = "File đã chọn: ${selectedFileName}"

                // Reset thông báo khi chọn file mới
                showSuccessMessage = false
                showErrorMessage = false
            }
        }

        // Function để gửi kết quả
        fun sendResult() {
            selectedFile?.let { file ->
                scope.launch {
                    try {
                        showSuccessMessage = false
                        showErrorMessage = false

                        val requestFile = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
                        val body = MultipartBody.Part.createFormData(
                            "file",
                            selectedFileName ?: file.name,
                            requestFile
                        )

                        // Gửi kèm file gốc để tạo signature
                        fileViewModel.uploadResultFile(
                            appointmentId = appointmentId,
                            doctorId = appointmentId,
                            doctorName = "BS. Nguyễn Văn A",
                            doctorTitle = "Bác sĩ Khoa ${nameServiceRoom}",
                            file = body,
                            originalFile = file // Thêm tham số này
                        )

                        serviceAppointmentViewModel.updateStatusServiceAppointment(id, "Đã khám xong")

                    } catch (e: Exception) {
                        println("Error uploading file: ${e.message}")
                        showErrorMessage = true
                        errorMessage = e.message ?: "Có lỗi xảy ra"
                    }
                }
            }
        }

        // Xử lý state của upload với delay để hiển thị thông báo
        LaunchedEffect(uploadState) {
            when (uploadState) {
                is FileUploadState.Success -> {
                    println("File uploaded successfully: ${(uploadState as FileUploadState.Success).response}")
                    showSuccessMessage = true
                    showErrorMessage = false

                    // Delay 3 giây rồi mới reset state
                    delay(3000)
                    fileViewModel.resetState()

                    // Sau 5 giây thì ẩn thông báo
                    delay(2000)
                    showSuccessMessage = false
                }
                is FileUploadState.Error -> {
                    println("Upload failed: ${(uploadState as FileUploadState.Error).message}")
                    showErrorMessage = true
                    showSuccessMessage = false
                    errorMessage = (uploadState as FileUploadState.Error).message

                    // Tự động ẩn thông báo lỗi sau 5 giây
                    delay(5000)
                    showErrorMessage = false
                }
                else -> {}
            }
        }

        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xFFEAF1FB))
        ) {
            Box(
                Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(vertical = 32.dp)
            ) {
                Column(
                    Modifier
                        .align(Alignment.Center)
                        .widthIn(min = 400.dp, max = 900.dp)
                        .shadow(12.dp, RoundedCornerShape(24.dp))
                        .background(Color.White, RoundedCornerShape(24.dp))
                        .padding(36.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // AppBar đẹp
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(Color(0xFF1976D2), Color(0xFF62B3FF))
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Quay lại",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(36.dp)
                                    .padding(start = 12.dp)
                                    .clickable { navigator.push(ServicrRoomMainScreen(1)) }
                            )
                            Box(
                                Modifier.weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${nameServiceRoom} - Bệnh nhân $userName",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp,
                                    letterSpacing = 1.sp,
                                )
                            }
                        }
                    }

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        Column(
                            Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedTextField("$id", {}, label = { Text("Id") }, enabled = false, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(userName, {}, label = { Text("Họ tên") }, enabled = false, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField("$formattedBirthDate", {}, label = { Text("Ngày sinh") }, enabled = false, modifier = Modifier.fillMaxWidth())
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                OutlinedTextField(gender, {}, label = { Text("Giới tính") }, enabled = false, modifier = Modifier.weight(1f))
                                OutlinedTextField(cccd, {}, label = { Text("CCCD") }, enabled = false, modifier = Modifier.weight(1f))
                            }
                        }
                        Column(
                            Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedTextField("$userId", {}, label = { Text("Mã bệnh nhân") }, enabled = false, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(homeTown, {}, label = { Text("Quê quán") }, enabled = false, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(examTime, {}, label = { Text("Giờ khám") }, enabled = false, modifier = Modifier.fillMaxWidth())
                        }
                    }

                    // Trường kết quả khám
                    OutlinedTextField(
                        value = resultText,
                        onValueChange = { resultText = it },
                        label = { Text("Kết quả khám") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(96.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = if (selectedFile != null) Color(0xFF1976D2) else Color.Black
                        )
                    )

                    // Chọn file và gửi kết quả
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = selectFile,
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = if (selectedFile != null) Color(0xFF4CAF50) else Color(0xFF1976D2)
                            ),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (selectedFile != null) "File đã chọn ✓" else "Chọn file",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Button(
                            onClick = {
                                sendResult()
                            },
                            enabled = selectedFile != null && uploadState !is FileUploadState.Loading,
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF1976D2),
                                disabledBackgroundColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            if (uploadState is FileUploadState.Loading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Gửi kết quả", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Hiển thị trạng thái upload với state riêng
                    if (showSuccessMessage) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = Color(0xFFE8F5E8),
                            shape = RoundedCornerShape(12.dp),
                            elevation = 4.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "✅ Upload thành công! Kết quả đã được gửi.",
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    if (showErrorMessage) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(12.dp),
                            elevation = 4.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "❌ Lỗi: $errorMessage",
                                    color = Color(0xFFC62828),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}