package ui.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ui.components.ActionBtn
import ui.components.AdminField
import ui.components.PanelCard
import ui.components.ResultMsg
import ui.theme.*

@Composable
fun CreateCardPanel() {
    val scope = rememberCoroutineScope()
    var patientId by remember { mutableStateOf("") }
    var citizenId by remember { mutableStateOf("") }
    var patientName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var balance by remember { mutableStateOf("0") }
    var isLoading by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    PanelCard("Tạo thẻ mới", "Đổ dữ liệu cá nhân vào thẻ trắng", Icons.Filled.AddCard, AdminPurple) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(Modifier.weight(1f)) {
                AdminField("Mã bệnh nhân", patientId, { patientId = it }, Icons.Filled.Badge, "VD: BN001234")
            }
            Box(Modifier.weight(1f)) {
                AdminField("CCCD", citizenId, { citizenId = it.filter(Char::isDigit) }, Icons.Filled.CreditCard, "Nhập số CCCD", isNumeric = true)
            }
        }

        AdminField("Họ và tên bệnh nhân", patientName, { patientName = it }, Icons.Filled.Person, "VD: Nguyễn Văn A")

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(Modifier.weight(1f)) {
                AdminField("Giới tính", gender, { gender = it }, Icons.Filled.Wc, "VD: Nam")
            }
            Box(Modifier.weight(1f)) {
                AdminField("Ngày sinh", dob, { dob = it }, Icons.Filled.CalendarToday, "VD: 01/01/1990")
            }
        }

        AdminField("Địa chỉ / Quê quán", address, { address = it }, Icons.Filled.LocationOn, "VD: Hà Nội")
        AdminField("Số dư ban đầu (VNĐ)", balance, { balance = it.filter(Char::isDigit) }, Icons.Filled.AccountBalanceWallet, "VD: 0", isNumeric = true)

        Surface(color = Color(0xFFE3F2FD), shape = RoundedCornerShape(10.dp), elevation = 0.dp) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.Info, null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                Text("Hệ thống sẽ tự động gán System Secret Key từ phiên đăng nhập hiện tại và thiết lập PIN mặc định. Bệnh nhân cần đổi PIN ngay lần đầu sử dụng.",
                    style = MaterialTheme.typography.caption, color = TextDark)
            }
        }

        ActionBtn("Ghi dữ liệu thẻ", isLoading, AdminPurple, Icons.Filled.Nfc,
            enabled = patientId.isNotBlank() && patientName.isNotBlank() && citizenId.isNotBlank()) {
            isLoading = true; result = null
            scope.launch {
                delay(1200) // TODO: Thực thi logic APDU INS_CREATE_INFO
                result = true to "✓ Ghi thông tin thẻ thành công!\nMã BN: $patientId — $patientName"
                isLoading = false
            }
        }
        ResultMsg(result)
    }
}