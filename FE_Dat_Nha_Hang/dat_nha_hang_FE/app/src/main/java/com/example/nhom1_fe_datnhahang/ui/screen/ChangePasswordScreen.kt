package com.example.nhom1_fe_datnhahang.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nhom1_fe_datnhahang.R
import com.example.nhom1_fe_datnhahang.util.PersistentCookieJar
import com.example.nhom1_fe_datnhahang.viewmodel.AccountViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val accountViewModel = remember { AccountViewModel(context) }
    val cookieJar = remember { PersistentCookieJar(context) }

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var oldPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var oldPasswordError by remember { mutableStateOf("") }
    var newPasswordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }

    val uiState by accountViewModel.changePasswordState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Hiển thị thông báo khi có success hoặc error
    LaunchedEffect(uiState) {
        when {
            uiState.isSuccess -> {
                snackbarHostState.showSnackbar(
                    message = uiState.successMessage ?: "Đổi mật khẩu thành công!",
                    duration = SnackbarDuration.Short
                )
                delay(1500)
                accountViewModel.resetChangePasswordState()
                navController.popBackStack()
            }
            uiState.errorMessage != null -> {
                snackbarHostState.showSnackbar(
                    message = uiState.errorMessage ?: "Có lỗi xảy ra",
                    duration = SnackbarDuration.Long
                )
                accountViewModel.resetChangePasswordState()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Đổi mật khẩu",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Cập nhật mật khẩu của bạn",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = if (uiState.isSuccess)
                        Color(0xFF10B981) else Color(0xFFEF4444),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Icon Card
                Card(
                    modifier = Modifier.size(100.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFEEF2FF)
                    )
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFF6366F1),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Form Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        // Security Info Banner
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFEF3C7)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = Color(0xFFF59E0B),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Mật khẩu mới phải có ít nhất 6 ký tự và khác mật khẩu cũ",
                                    fontSize = 13.sp,
                                    color = Color(0xFF92400E),
                                    lineHeight = 18.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Mật khẩu cũ
                        PasswordField(
                            value = oldPassword,
                            onValueChange = {
                                oldPassword = it
                                oldPasswordError = ""
                            },
                            label = "Mật khẩu hiện tại",
                            placeholder = "Nhập mật khẩu hiện tại...",
                            isVisible = oldPasswordVisible,
                            onVisibilityChange = { oldPasswordVisible = it },
                            error = oldPasswordError,
                            enabled = !uiState.isLoading
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Mật khẩu mới
                        PasswordField(
                            value = newPassword,
                            onValueChange = {
                                newPassword = it
                                newPasswordError = ""
                            },
                            label = "Mật khẩu mới",
                            placeholder = "Nhập mật khẩu mới...",
                            isVisible = newPasswordVisible,
                            onVisibilityChange = { newPasswordVisible = it },
                            error = newPasswordError,
                            enabled = !uiState.isLoading
                        )

                        // Password strength indicator
                        if (newPassword.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            PasswordStrengthIndicator(password = newPassword)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Xác nhận mật khẩu mới
                        PasswordField(
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                                confirmPasswordError = ""
                            },
                            label = "Xác nhận mật khẩu mới",
                            placeholder = "Nhập lại mật khẩu mới...",
                            isVisible = confirmPasswordVisible,
                            onVisibilityChange = { confirmPasswordVisible = it },
                            error = confirmPasswordError,
                            enabled = !uiState.isLoading
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                // Validate
                                var hasError = false

                                if (oldPassword.isEmpty()) {
                                    oldPasswordError = "Vui lòng nhập mật khẩu hiện tại"
                                    hasError = true
                                }

                                if (newPassword.isEmpty()) {
                                    newPasswordError = "Vui lòng nhập mật khẩu mới"
                                    hasError = true
                                } else if (newPassword.length < 6) {
                                    newPasswordError = "Mật khẩu mới phải có ít nhất 6 ký tự"
                                    hasError = true
                                } else if (newPassword == oldPassword) {
                                    newPasswordError = "Mật khẩu mới phải khác mật khẩu cũ"
                                    hasError = true
                                }

                                if (confirmPassword.isEmpty()) {
                                    confirmPasswordError = "Vui lòng xác nhận mật khẩu mới"
                                    hasError = true
                                } else if (confirmPassword != newPassword) {
                                    confirmPasswordError = "Mật khẩu xác nhận không khớp"
                                    hasError = true
                                }

                                if (!hasError) {
                                    scope.launch {
                                        val accountId = cookieJar.getaccountId()?.toIntOrNull()
                                        if (accountId != null) {
                                            accountViewModel.changePassword(accountId, oldPassword, newPassword)
                                        }
                                    }
                                }
                            },
                            enabled = !uiState.isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6366F1),
                                disabledContainerColor = Color(0xFFE2E8F0)
                            )
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    "Đang xử lý...",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    "Đổi mật khẩu",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Cancel Button
                        OutlinedButton(
                            onClick = { navController.popBackStack() },
                            enabled = !uiState.isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "Hủy",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Security Tips Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF1F5F9)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFF6366F1),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Mẹo bảo mật",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        SecurityTip("Sử dụng mật khẩu mạnh với chữ hoa, chữ thường và số")
                        SecurityTip("Không chia sẻ mật khẩu với người khác")
                        SecurityTip("Thay đổi mật khẩu định kỳ để tăng cường bảo mật")
                        SecurityTip("Không sử dụng mật khẩu giống nhau cho nhiều tài khoản")
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isVisible: Boolean,
    onVisibilityChange: (Boolean) -> Unit,
    error: String,
    enabled: Boolean
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color(0xFF64748B),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            placeholder = { Text(placeholder, fontSize = 14.sp, color = Color(0xFF94A3B8)) },
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (error.isNotEmpty()) Color(0xFFEF4444) else Color(0xFF6366F1),
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                IconButton(onClick = { onVisibilityChange(!isVisible) }) {
                    Icon(
                        painter = painterResource(
                            id = if (isVisible)
                                R.drawable.ic_visibility
                            else
                                R.drawable.ic_visibility_off
                        ),
                        contentDescription = if (isVisible) "Ẩn mật khẩu" else "Hiện mật khẩu",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            isError = error.isNotEmpty(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF6366F1),
                unfocusedBorderColor = Color(0xFFE2E8F0),
                errorBorderColor = Color(0xFFEF4444),
                cursorColor = Color(0xFF6366F1)
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (error.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = error,
                    fontSize = 12.sp,
                    color = Color(0xFFEF4444)
                )
            }
        }
    }
}

@Composable
private fun PasswordStrengthIndicator(password: String) {
    val strength = calculatePasswordStrength(password)
    val (color, label) = when (strength) {
        in 0..2 -> Color(0xFFEF4444) to "Yếu"
        in 3..4 -> Color(0xFFF59E0B) to "Trung bình"
        else -> Color(0xFF10B981) to "Mạnh"
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(5) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(
                            color = if (index < strength) color else Color(0xFFE2E8F0),
                            shape = RoundedCornerShape(2.dp)
                        )
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Độ mạnh: $label",
            fontSize = 12.sp,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun calculatePasswordStrength(password: String): Int {
    var strength = 0
    if (password.length >= 6) strength++
    if (password.length >= 8) strength++
    if (password.any { it.isUpperCase() }) strength++
    if (password.any { it.isLowerCase() }) strength++
    if (password.any { it.isDigit() }) strength++
    return strength
}

@Composable
private fun SecurityTip(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .padding(top = 6.dp)
                .background(
                    color = Color(0xFF6366F1),
                    shape = RoundedCornerShape(3.dp)
                )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            color = Color(0xFF475569),
            lineHeight = 18.sp
        )
    }
}
