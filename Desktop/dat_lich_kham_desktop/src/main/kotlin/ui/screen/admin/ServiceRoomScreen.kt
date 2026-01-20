package ui.screen.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import ui.compoment.RoomCard
import viewmodel.ServiceRoomViewModel
import viewmodel.AccountViewModel
import viewmodel.CreateServiceRoomState

class ServiceRoomScreen : Screen {
    @Composable
    override fun Content() {
        val serviceRoomViewModel = remember { ServiceRoomViewModel() }
        val accountViewModel = remember { AccountViewModel() }

        var showAddDialog by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            serviceRoomViewModel.listServiceRoom()
        }

        val servicerooms = serviceRoomViewModel.servicerooms
        val createState by accountViewModel.createServiceRoomState.collectAsState()

        // Xử lý trạng thái tạo phòng dịch vụ
        LaunchedEffect(createState) {
            when (val state = createState) {
                is CreateServiceRoomState.Success -> {
                    serviceRoomViewModel.listServiceRoom()
                    accountViewModel.resetServiceRoomState()
                }
                is CreateServiceRoomState.Error -> {
                    accountViewModel.resetServiceRoomState()
                }
                else -> {}
            }
        }

        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Danh sách phòng dịch vụ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF1E88E5),
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = { serviceRoomViewModel.listServiceRoom() },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Image(
                        painter = painterResource("Icons/refresh.png"),
                        contentDescription = "Refresh",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Button(
                    onClick = { showAddDialog = true },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1E88E5)),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.size(36.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(servicerooms) { serviceroom ->
                    RoomCard(
                        id = serviceroom.id,
                        name = serviceroom.name,
                        code = serviceroom.code,
                        address = serviceroom.address,
                        examPrice = serviceroom.examPrice
                    )
                }
            }
        }

        // Add Dialog
        if (showAddDialog) {
            AddServiceRoomDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { name, code, address, examPrice ->
                    accountViewModel.createServiceRoom(name, code, address, examPrice.toInt())
                    showAddDialog = false
                },
                isLoading = createState is CreateServiceRoomState.Loading
            )
        }
    }
}

@Composable
fun AddServiceRoomDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, code: String, address: String, examPrice: Double) -> Unit,
    isLoading: Boolean = false
) {
    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var examPrice by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { if (!isLoading) onDismiss() }) {
        Card(
            shape = RoundedCornerShape(20.dp),
            backgroundColor = Color.White,
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .width(480.dp)
                    .padding(28.dp)
            ) {
                // Header với icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = Color(0xFFDCFCE7),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MeetingRoom,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Thêm phòng dịch vụ mới",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "Nhập thông tin phòng dịch vụ",
                                fontSize = 13.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Form fields
                ModernTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Tên phòng",
                    placeholder = "Nhập tên phòng dịch vụ...",
                    icon = Icons.Default.MeetingRoom,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                ModernTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = "Mã phòng",
                    placeholder = "Nhập mã phòng...",
                    icon = Icons.Default.QrCode,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                ModernTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = "Địa chỉ",
                    placeholder = "Nhập địa chỉ phòng...",
                    icon = Icons.Default.LocationOn,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                ModernTextField(
                    value = examPrice,
                    onValueChange = {
                        if (it.isEmpty() || it.toDoubleOrNull() != null) {
                            examPrice = it
                        }
                    },
                    label = "Giá khám",
                    placeholder = "Nhập giá khám...",
                    icon = Icons.Default.AttachMoney,
                    suffix = "VNĐ",
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        enabled = !isLoading,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF64748B)
                        )
                    ) {
                        Text(
                            "Hủy",
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp
                        )
                    }
                    Button(
                        onClick = {
                            val price = examPrice.toDoubleOrNull() ?: 0.0
                            onAdd(name, code, address, price)
                        },
                        enabled = !isLoading &&
                                name.isNotBlank() &&
                                code.isNotBlank() &&
                                address.isNotBlank() &&
                                examPrice.isNotBlank() &&
                                examPrice.toDoubleOrNull() != null,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF10B981),
                            disabledBackgroundColor = Color(0xFFE2E8F0)
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (isLoading) "Đang xử lý..." else "Thêm mới",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    singleLine: Boolean = true,
    suffix: String? = null,
    enabled: Boolean = true
) {
    Column {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF64748B),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            placeholder = { Text(placeholder, fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = if (suffix != null) {
                {
                    Text(
                        text = suffix,
                        color = Color(0xFF64748B),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            } else null,
            singleLine = singleLine,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF10B981),
                unfocusedBorderColor = Color(0xFFE2E8F0),
                cursorColor = Color(0xFF10B981)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}