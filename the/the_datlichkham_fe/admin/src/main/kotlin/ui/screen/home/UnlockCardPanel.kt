package ui.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ui.components.ActionBtn
import ui.screen.ActionBtn
import ui.screen.AdminField
import ui.screen.PanelCard
import ui.screen.ResultMsg
import ui.theme.*

@Composable
fun UnlockCardPanel() {
    val scope = rememberCoroutineScope()
    var secretKey by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var result    by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    PanelCard("Mở khóa thẻ", "Reset bộ đếm sai PIN, mở khóa thẻ bị chặn", Icons.Filled.LockOpen, AccentTeal) {
        Surface(color = WarningLight, shape = RoundedCornerShape(12.dp), elevation = 0.dp) {
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Filled.Warning, null, tint = Warning, modifier = Modifier.size(18.dp))
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("Thao tác không thể hoàn tác", style = MaterialTheme.typography.body2,
                        color = TextDark, fontWeight = FontWeight.SemiBold)
                    Text("Bộ đếm sai PIN sẽ được reset về 0 và thẻ sẽ được mở khóa.",
                        style = MaterialTheme.typography.caption, color = TextGray
                    )
                }
            }
        }

        AdminField("Secret Key quản trị", secretKey, { secretKey = it },
            Icons.Filled.VpnKey, "Nhập secret key để xác nhận", isPassword = true)

        Surface(color = Color(0xFFE0F2F1), shape = RoundedCornerShape(10.dp), elevation = 0.dp) {
            Row(modifier = Modifier.padding(12.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Filled.CreditCard, null, tint = AccentTeal, modifier = Modifier.size(16.dp))
                Column {
                    Text("Thẻ hiện tại", style = MaterialTheme.typography.caption, color = TextGray)
                    Text("AID: 11 22 33 44 55 00", style = MaterialTheme.typography.body2,
                        color = TextDark, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        ActionBtn("Mở khóa thẻ", isLoading, AccentTeal, Icons.Filled.LockOpen,
            enabled = secretKey.isNotBlank()) {
            isLoading = true; result = null
            scope.launch {
                delay(1000) // TODO: APDU INS_RESET_PIN
                result = true to "✓  Mở khóa thành công!\nThẻ đã được mở khóa và sẵn sàng sử dụng."
                isLoading = false
            }
        }
        ResultMsg(result)
    }
}
