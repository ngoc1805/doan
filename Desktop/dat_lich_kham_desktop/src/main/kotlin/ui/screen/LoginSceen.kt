package ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ui.screen.admin.AdminMainScreen
import ui.screen.doctor.DoctorMainScreen
import ui.screen.serviceroom.ServicrRoomMainScreen
import viewmodel.LoginViewModel
import viewmodel.LoginUiState

class LoginScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = remember { LoginViewModel() }

        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }
        val userTypes = listOf("Admin", "Bác sĩ", "Phòng chức năng")
        var selectedUserType by remember { mutableStateOf(userTypes[0]) }
        var expanded by remember { mutableStateOf(false) }
        var errorMsg by remember { mutableStateOf<String?>(null) }

        val loginState by viewModel.loginState.collectAsState()

        LaunchedEffect(loginState) {
            when (loginState) {
                is LoginUiState.Success -> {
                    errorMsg = null
                    val role = (loginState as LoginUiState.Success).role // ép kiểu ở đây
                    when (role) {
                        "bacsi" -> navigator.push(DoctorMainScreen())
                        "chucnang" -> navigator.push(ServicrRoomMainScreen())
                        "admin" -> navigator.push(AdminMainScreen())
                        else -> navigator.push(LoginScreen())
                    }
                    viewModel.resetState()
                }
                is LoginUiState.Error -> {
                    errorMsg = (loginState as LoginUiState.Error).message
                }
                else -> {}
            }
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Card(elevation = 8.dp, modifier = Modifier.width(350.dp).padding(32.dp)) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Đăng nhập", style = MaterialTheme.typography.h5)

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu"
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions.Default
                    )

                    Box {
                        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                            Text(selectedUserType)
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            userTypes.forEach { type ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedUserType = type
                                        expanded = false
                                    }
                                ) {
                                    Text(type)
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (username.isBlank() || password.isBlank()) {
                                errorMsg = "Vui lòng nhập đầy đủ tài khoản và mật khẩu"
                            } else {
                                errorMsg = null // reset lỗi cũ
                                viewModel.login(username, password, selectedUserType)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Đăng nhập")
                    }

                    errorMsg?.let {
                        Text(it, color = MaterialTheme.colors.error)
                    }
                }
            }
        }
    }
}