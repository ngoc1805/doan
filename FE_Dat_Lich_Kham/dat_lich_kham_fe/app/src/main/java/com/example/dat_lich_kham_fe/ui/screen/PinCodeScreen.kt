package com.example.dat_lich_kham_fe.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.util.divideAndRound

// Enum quản lý state luồng PIN
enum class PinFlowState { Loading, Create, Confirm, Enter }

// Màn hình nhập PIN mới
@Composable
fun CreatePinScreen(
    onPinCreated: (String) -> Unit,
    onClose: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    var pin by remember { mutableStateOf("") }
    val maxPinLength = 6

    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding((24 * divideAndRound(screenWidthValue)).dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Đóng"
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding((16 * divideAndRound(screenWidthValue)).dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tạo mã PIN",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = (16 * divideAndRound(screenWidthValue)).dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy((8 * divideAndRound(screenWidthValue)).dp),
                modifier = Modifier.padding(vertical = (16 * divideAndRound(screenWidthValue)).dp)
            ) {
                repeat(maxPinLength) { index ->
                    Circle(index < pin.length)
                }
            }
            NumberPad(
                pin = pin,
                maxPinLength = maxPinLength,
                onPinChange = { newPin ->
                    pin = newPin
                    if (newPin.length == maxPinLength) {
                        onPinCreated(newPin)
                        pin = ""
                    }
                }
            )
        }
    }
}

// Màn hình nhập lại PIN để xác nhận
@Composable
fun ConfirmPinScreen(
    originalPin: String,
    onPinConfirmed: (String) -> Unit,
    onPinMismatch: () -> Unit,
    onClose: () -> Unit,
    errorMessage: String = ""
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    var pin by remember { mutableStateOf("") }
    val maxPinLength = 6
    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding((24 * divideAndRound(screenWidthValue)).dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Đóng"
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding((16 * divideAndRound(screenWidthValue)).dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Nhập lại mã PIN",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = (16 * divideAndRound(screenWidthValue)).dp)
            )
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = (16 * divideAndRound(screenWidthValue)).dp)
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy((8 * divideAndRound(screenWidthValue)).dp),
                modifier = Modifier.padding(vertical = (16 * divideAndRound(screenWidthValue)).dp)
            ) {
                repeat(maxPinLength) { index ->
                    Circle(index < pin.length)
                }
            }
            NumberPad(
                pin = pin,
                maxPinLength = maxPinLength,
                onPinChange = { newPin ->
                    pin = newPin
                    if (newPin.length == maxPinLength) {
                        if (pin == originalPin) {
                            onPinConfirmed(pin)
                            pin = ""
                        } else {
                            onPinMismatch()
                            pin = ""
                        }
                    }
                }
            )
        }
    }
}

// Màn hình xác thực mã PIN
@Composable
fun PinCodeScreenV2(
    onPinEntered: (String) -> Unit,
    onClose: () -> Unit,
    errorMessage: String = "",
    navController: NavController
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    var pin by remember { mutableStateOf("") }
    val maxPinLength = 6
    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding((24* divideAndRound(screenWidthValue)).dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Đóng"
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding((16 * divideAndRound(screenWidthValue)).dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Nhập mã PIN",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = (16 * divideAndRound(screenWidthValue)).dp)
            )
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = (16 * divideAndRound(screenWidthValue)).dp)
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy((8 * divideAndRound(screenWidthValue)).dp),
                modifier = Modifier.padding(vertical = (16 * divideAndRound(screenWidthValue)).dp)
            ) {
                repeat(maxPinLength) { index ->
                    Circle(index < pin.length)
                }
            }
            NumberPad(
                pin = pin,
                maxPinLength = maxPinLength,
                onPinChange = { newPin ->
                    pin = newPin
                    if (newPin.length == maxPinLength) {
                        onPinEntered(newPin)
                        pin = ""
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = {
                    navController.navigate("ForgotPinScreen")
                }
            ) {
                Text(
                    text = "Quên mã PIN?",
                    color = colorResource(id = R.color.darkblue),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Số tròn thể hiện số ký tự đã nhập
@Composable
fun Circle(isFilled: Boolean) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    Box(
        modifier = Modifier
            .size((16 * divideAndRound(screenWidthValue)).dp)
            .clip(CircleShape)
            .background(if (isFilled) Color.Black else Color.Gray)
    )
}

// Bàn phím số cho nhập PIN
@Composable
fun NumberPad(
    pin: String,
    maxPinLength: Int,
    onPinChange: (String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy((16 * divideAndRound(screenWidthValue)).dp),
        verticalArrangement = Arrangement.spacedBy((16 * divideAndRound(screenWidthValue)).dp),
        modifier = Modifier.padding((16 * divideAndRound(screenWidthValue)).dp)
    ) {
        (1..9).forEach { number ->
            item {
                NumberButton(
                    number = number.toString(),
                    onClick = {
                        if (pin.length < maxPinLength) {
                            onPinChange(pin + number.toString())
                        }
                    }
                )
            }
        }
        item { Spacer(modifier = Modifier.size((64 * divideAndRound(screenWidthValue)).dp)) }
        item {
            NumberButton(
                number = "0",
                onClick = {
                    if (pin.length < maxPinLength) {
                        onPinChange(pin + "0")
                    }
                }
            )
        }
        item {
            IconButton(
                onClick = {
                    if (pin.isNotEmpty()) {
                        onPinChange(pin.dropLast(1))
                    }
                },
                modifier = Modifier.size((64 * divideAndRound(screenWidthValue)).dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close, // Đổi thành icon xóa nếu có
                    contentDescription = "Xóa",
                    tint = Color.Black
                )
            }
        }
    }
}

// Nút số tròn
@Composable
fun NumberButton(number: String, onClick: () -> Unit) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    Button(
        onClick = onClick,
        modifier = Modifier.size((64 * divideAndRound(screenWidthValue)).dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
    ) {
        Text(
            text = number,
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black
        )
    }
}
