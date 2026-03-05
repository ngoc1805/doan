package ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ui.screen.*
import ui.theme.DividerColor

@Composable
fun ResetPinPanel() {
    val scope = rememberCoroutineScope()
    val orange = Color(0xFFF57F17)
    var secretKey   by remember { mutableStateOf("") }
    var newPin      by remember { mutableStateOf("") }
    var confirmPin  by remember { mutableStateOf("") }
    var isLoading   by remember { mutableStateOf(false) }
    var result      by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    val pinValid = newPin.length in 4..8 && newPin.all(Char::isDigit)
    val pinMatch = newPin == confirmPin && newPin.isNotBlank()

    PanelCard("Cấp lại mã PIN", "Đặt PIN mới cho thẻ bệnh nhân bằng quyền quản trị", Icons.Filled.Key, orange) {
        AdminField("Secret Key quản trị", secretKey, { secretKey = it },
            Icons.Filled.VpnKey, "Nhập secret key để xác nhận", isPassword = true)

        Divider(color = DividerColor, modifier = Modifier.padding(vertical = 4.dp))

        AdminField("PIN mới (4–8 chữ số)", newPin, { newPin = it.filter(Char::isDigit).take(8) },
            Icons.Filled.Pin, "Nhập PIN mới", isPassword = true, isNumeric = true)

        AdminField("Xác nhận PIN mới", confirmPin, { confirmPin = it.filter(Char::isDigit).take(8) },
            Icons.Filled.Pin, "Nhập lại PIN mới", isPassword = true, isNumeric = true)

        AnimatedVisibility(newPin.isNotBlank()) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Hint("PIN từ 4 đến 8 chữ số", pinValid)
                Hint("Hai lần nhập trùng khớp", pinMatch && pinValid)
            }
        }

        ActionBtn("Cấp lại PIN", isLoading, orange, Icons.Filled.Key,
            enabled = secretKey.isNotBlank() && pinMatch && pinValid) {
            isLoading = true; result = null
            scope.launch {
                delay(1000) // TODO: APDU INS_UPDATE_PIN_DIRECT
                result = true to "✓  Cấp lại PIN thành công!\nBệnh nhân có thể dùng PIN mới ngay lập tức."
                isLoading = false
            }
        }
        ResultMsg(result)
    }
}