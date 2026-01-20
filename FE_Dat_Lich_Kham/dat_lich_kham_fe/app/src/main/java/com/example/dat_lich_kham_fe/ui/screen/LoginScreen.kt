package com.example.dat_lich_kham_fe.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.util.divideAndRound
import com.example.dat_lich_kham_fe.viewmodel.LoginViewModel
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(navController: NavController) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value

    val viewModel = remember { LoginViewModel(context) }

    val phone = viewModel.numberPhone
    val password = viewModel.passWord
    val phoneError = viewModel.phoneNumberError
    val passwordError = viewModel.passwordError
    val loginError = viewModel.loginError
    val attemptsRemaining = viewModel.attemptsRemaining
    val isAccountLocked = viewModel.isAccountLocked

    var passwordVisible by remember { mutableStateOf(false) }

    // ✅ STATE CHO DIALOG BỊ KHÓA
    var showLockedDialog by remember { mutableStateOf(false) }
    var lockedMessage by remember { mutableStateOf("") }
    var lockedTimeRemaining by remember { mutableStateOf(0L) }

    // ✅ STATE CHO COUNTDOWN BUTTON
    var isButtonEnabled by remember { mutableStateOf(true) }
    var buttonText by remember { mutableStateOf("Đăng nhập") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Phone
            OutlinedTextField(
                value = phone,
                onValueChange = { viewModel.onNumberPhoneChanged(it) },
                label = { Text(text = stringResource(id = R.string.phone_number)) },
                isError = phoneError != null,
                supportingText = { phoneError?.let { Text(text = it, color = Color.Red) } },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        tint = Color.Gray,
                        contentDescription = null,
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding((8 * divideAndRound(screenWidthValue)).dp)
            )

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.onPassWordChanged(it) },
                label = { Text(text = stringResource(id = R.string.password)) },
                isError = passwordError != null,
                supportingText = { passwordError?.let { Text(text = it, color = Color.Red) } },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        tint = Color.Gray,
                        contentDescription = null,
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation =
                    if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible)
                        painterResource(id = R.drawable.ic_visibility)
                    else
                        painterResource(id = R.drawable.ic_visibility_off)

                    IconButton(onClick = {
                        passwordVisible = !passwordVisible
                    }) {
                        Icon(
                            painter = image,
                            contentDescription = if (passwordVisible) "Ẩn mật khẩu" else "Hiển thị mật khẩu"
                        )
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            // Login Button
            Button(
                onClick = {
                    viewModel.validateAndLogin(
                        onSuccess = {
                            navController.navigate("MainScreen")
                        },
                        onFail = { errorMessage ->
                            //  Đăng nhập thất bại
                            // Message đã được hiển thị ở loginError
                        },
                        onLocked = { message, remainingTime ->
                            //  Tài khoản bị khóa
                            lockedMessage = message
                            lockedTimeRemaining = remainingTime
                            showLockedDialog = true
                        }
                    )
                },
                enabled = isButtonEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding((24 * divideAndRound(screenWidthValue)).dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.darkblue)
                )
            ) {
                Text(
                    text = buttonText,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            //  HIỂN THỊ LỖI VÀ SỐ LẦN THỬ CÒN LẠI
            loginError?.let { error ->
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = error,
                        color = if (isAccountLocked) MaterialTheme.colorScheme.error else Color.Red,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )

                    // Hiển thị số lần thử còn lại
                    if (!isAccountLocked && attemptsRemaining != null) {
                        Text(
                            text = "Còn $attemptsRemaining lần thử",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Text(
                text = "${stringResource(id = R.string.forgot_password)}?",
                modifier = Modifier
                    .padding((16 * divideAndRound(screenWidthValue)).dp)
                    .clickable { navController.navigate("ForgotPasswordScreen") },
                color = colorResource(id = R.color.darkblue),
                fontWeight = FontWeight.Bold
            )
        }
    }

    // ✅ DIALOG TÀI KHOẢN BỊ KHÓA
    if (showLockedDialog) {
        AlertDialog(
            onDismissRequest = { },  // Không cho dismiss
            title = {
                Text(
                    text = "Tài khoản bị khóa",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(text = lockedMessage)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Thời gian còn lại: ${viewModel.formatRemainingTime(lockedTimeRemaining)}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLockedDialog = false
                        viewModel.reset()
                    }
                ) {
                    Text("Đóng")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        // Liên hệ hỗ trợ
                        showLockedDialog = false
                    }
                ) {
                    Text("Liên hệ hỗ trợ")
                }
            }
        )
    }
}