package com.example.nhom1_fe_datnhahang.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nhom1_fe_datnhahang.R
import com.example.nhom1_fe_datnhahang.util.divideAndRound
import com.example.nhom1_fe_datnhahang.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = viewModel()
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp.dp
            val screenWidthValue = screenWidth.value

            Text(
                "Đăng nhập",
                fontWeight = FontWeight.Bold,
                fontSize = (40 * divideAndRound(screenWidthValue)).sp
            )
            Spacer(modifier = Modifier.height((4 * divideAndRound(screenWidthValue)).dp))

            // Logo
            Icon(
                painter = painterResource(id = R.drawable.login),
                contentDescription = "logoLoginPage",
                tint = Color.Unspecified,
                modifier = Modifier.size((360 * divideAndRound(screenWidthValue)).dp)
            )

            // Username field
            OutlinedTextField(
                value = viewModel.userName,
                onValueChange = { viewModel.onUserNameChanged(it) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                },
                label = { Text("Tên đăng nhập") },
                modifier = Modifier
                    .fillMaxWidth(0.77f)
                    .clip(RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(24.dp),
                isError = viewModel.userNameError != null,
                supportingText = {
                    viewModel.userNameError?.let {
                        Text(text = it, color = Color.Red)
                    }
                },
                enabled = !viewModel.isLoading
            )

            // Password field
            Spacer(modifier = Modifier.height((8 * divideAndRound(screenWidthValue)).dp))

            OutlinedTextField(
                value = viewModel.passWord,
                onValueChange = { viewModel.onPassWordChanged(it) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                },
                label = { Text("Mật khẩu") },
                modifier = Modifier
                    .fillMaxWidth(0.77f)
                    .clip(RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(24.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible)
                        painterResource(id = R.drawable.outline_visibility_24)
                    else
                        painterResource(id = R.drawable.baseline_visibility_off_24)

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = image,
                            contentDescription = if (passwordVisible) "Ẩn mật khẩu" else "Hiển thị mật khẩu",
                            tint = Color.Gray
                        )
                    }
                },
                isError = viewModel.passwordError != null,
                supportingText = {
                    viewModel.passwordError?.let {
                        Text(text = it, color = Color.Red)
                    }
                },
                enabled = !viewModel.isLoading
            )

            // Error message
            if (viewModel.loginError != null) {
                Spacer(modifier = Modifier.height((8 * divideAndRound(screenWidthValue)).dp))
                Text(
                    text = viewModel.loginError ?: "",
                    color = Color.Red,
                    fontSize = (14 * divideAndRound(screenWidthValue)).sp
                )
            }

            // Login button
            Spacer(modifier = Modifier.height((16 * divideAndRound(screenWidthValue)).dp))
            Button(
                onClick = {
                    viewModel.validateAndLogin(
                        onSuccess = {
                            navController.navigate("MainScreen") {
                                popUpTo("LoginScreen") { inclusive = true }
                            }
                        },
                        onFail = { error ->
                            // Error đã được xử lý trong ViewModel
                        }
                    )
                },
                modifier = Modifier
                    .height((50 * divideAndRound(screenWidthValue)).dp)
                    .fillMaxWidth(0.77f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.darkblue),
                    contentColor = Color.White
                ),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Đăng nhập",
                        fontSize = (20 * divideAndRound(screenWidthValue)).sp,
                        color = Color.White
                    )
                }
            }

            // Forgot password
            TextButton(
                onClick = {},
                enabled = !viewModel.isLoading
            ) {
                Text(
                    text = "Quên mật khẩu?",
                    fontSize = (16 * divideAndRound(screenWidthValue)).sp,
                    color = Color.Blue
                )
            }
        }
    }
}
