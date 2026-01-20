package com.example.dat_lich_kham_fe.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.util.divideAndRound
import com.example.dat_lich_kham_fe.viewmodel.LoginViewModel
import com.example.dat_lich_kham_fe.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    selectedTab: Int,
    setSelectedTab: (Int) -> Unit
) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val registerViewModel = remember { RegisterViewModel(context) }
    var dialogSuccess by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var cfpasswordVisible by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = registerViewModel.phoneNumber,
                onValueChange = { registerViewModel.onPhoneNumberChanged(it) },
                label = { Text(text = stringResource(id = R.string.phone_number)) },
                supportingText = { registerViewModel.numberPhoneErr?.let { Text(text = it, color = Color.Red) } },
                isError = registerViewModel.numberPhoneErr != null,
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
            // password
            OutlinedTextField(
                value = registerViewModel.passWord,
                onValueChange = { registerViewModel.onPassWordChanged(it) },
                label = { Text(text = stringResource(id = R.string.password)) },
                supportingText = { registerViewModel.passWordErr?.let { Text(text = it, color = Color.Red) } },
                isError = registerViewModel.passWordErr != null,
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
                    else
                        PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible)
                        painterResource(id = R.drawable.ic_visibility) // Icon hiện mật khẩu
                    else
                        painterResource(id = R.drawable.ic_visibility_off) // Icon ẩn mật khẩu

                    IconButton(onClick = {
                        passwordVisible = !passwordVisible
                    }) {
                        Icon(painter = image, contentDescription = if (passwordVisible) "Ẩn mật khẩu" else "Hiển thị mật khẩu")
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            // Confirm password
            OutlinedTextField(
                value = registerViewModel.confirmPassWord,
                onValueChange = { registerViewModel.onConfirmPassWordChanged(it) },
                label = { Text(text = "Nhập lại mật khẩu") },
                supportingText = { registerViewModel.confirmPassWordErr?.let { Text(text = it, color = Color.Red) } },
                isError = registerViewModel.confirmPassWordErr != null,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        tint = Color.Gray,
                        contentDescription = null,
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation =
                    if (cfpasswordVisible) VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (cfpasswordVisible)
                        painterResource(id = R.drawable.ic_visibility) // Icon hiện mật khẩu
                    else
                        painterResource(id = R.drawable.ic_visibility_off) // Icon ẩn mật khẩu

                    IconButton(onClick = {
                        cfpasswordVisible = !cfpasswordVisible
                    }) {
                        Icon(painter = image, contentDescription = if (cfpasswordVisible) "Ẩn mật khẩu" else "Hiển thị mật khẩu")
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            // ButtonRegister
            Button(
                onClick = {
                    registerViewModel.validateAndRegister(
                        onSuccess = {dialogSuccess =  true},
                        onFail = {}
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding((24 * divideAndRound(screenWidthValue)).dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.darkblue))
            ) {
                Text(stringResource(id = R.string.register), color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
        //
        if (dialogSuccess) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { dialogSuccess = false },
                title = { Text("Thành công") },
                text = { Text("Đăng ký tài khoản thành công!") },
                confirmButton = {
                    Button(
                        onClick = {
                            dialogSuccess = false
                            setSelectedTab(0)
                        }
                    ) {
                        Text("Đóng")
                    }
                }
            )
        }
    }
}
