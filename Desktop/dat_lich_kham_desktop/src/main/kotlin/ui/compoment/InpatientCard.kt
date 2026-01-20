package ui.compoment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import util.formatDate
import util.formatDateTime

@Composable
fun InpatientCard(
    id: Int,
    userId: Int,
    fullname: String,
    gender: String,
    birthDate: String,
    cccd: String,
    hometown: String,
    address: String,
    status: String,
    createAt: String,
    displayApproveButton: Boolean = false,
    displayDischargeButton: Boolean = false,
    onApprove: ((Int, String, String, String, String, String, String) -> Unit)? = null,
    onDischarge: ((Int, String) -> Unit)? = null
) {
    var showApproveDialog by remember { mutableStateOf(false) }
    var showDischargeDialog by remember { mutableStateOf(false) }

    val formattedBirthDate = formatDate(birthDate)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp,
        backgroundColor = Color.White
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header với tên bệnh nhân
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = Color(0xFFE3F2FD),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = Color(0xFF1E88E5),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = fullname,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "ID: #$id",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Divider(color = Color(0xFFE2E8F0), thickness = 1.dp)

            Spacer(modifier = Modifier.height(16.dp))

            // Thông tin chi tiết
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoRow(
                    icon = Icons.Default.Person,
                    label = "Giới tính",
                    value = gender,
                    iconColor = Color(0xFFEC4899)
                )

                InfoRow(
                    icon = Icons.Default.CalendarToday,
                    label = "Ngày sinh",
                    value = formattedBirthDate,
                    iconColor = Color(0xFF8B5CF6)
                )

                InfoRow(
                    icon = Icons.Default.AccountCircle,
                    label = "CCCD",
                    value = cccd,
                    iconColor = Color(0xFF10B981)
                )

                InfoRow(
                    icon = Icons.Default.Home,
                    label = "Quê quán",
                    value = hometown,
                    iconColor = Color(0xFFF59E0B)
                )

                InfoRow(
                    icon = Icons.Default.LocationOn,
                    label = "Nơi điều trị",
                    value = address.ifBlank { "Chưa phân phòng" },
                    iconColor = Color(0xFF06B6D4)
                )

                InfoRow(
                    icon = Icons.Default.DateRange,
                    label = "Ngày nhập viện",
                    value = formatDateTime(createAt),
                    iconColor = Color(0xFF6366F1)
                )
            }

            // Buttons
            if (displayApproveButton || displayDischargeButton) {
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color(0xFFE2E8F0), thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (displayApproveButton) {
                        Button(
                            onClick = { showApproveDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF10B981)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Duyệt",
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    if (displayDischargeButton) {
                        Button(
                            onClick = { showDischargeDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFFEF4444)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Xuất viện",
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog Duyệt
    if (showApproveDialog) {
        ApproveDialog(
            id = id,
            fullname = fullname,
            gender = gender,
            birthDate = birthDate,
            cccd = cccd,
            hometown = hometown,
            address = address,
            onDismiss = { showApproveDialog = false },
            onConfirm = { updatedGender, updatedBirthDate, updatedCccd, updatedHometown, updatedAddress ->
                onApprove?.invoke(id, fullname, updatedGender, updatedBirthDate, updatedCccd, updatedHometown, updatedAddress)
                showApproveDialog = false
            }
        )
    }

    // Dialog Xuất viện
    if (showDischargeDialog) {
        DischargeDialog(
            id = id,
            fullname = fullname,
            onDismiss = { showDischargeDialog = false },
            onConfirm = {
                onDischarge?.invoke(id, fullname)
                showDischargeDialog = false
            }
        )
    }
}

@Composable
private fun ApproveDialog(
    id: Int,
    fullname: String,
    gender: String,
    birthDate: String,
    cccd: String,
    hometown: String,
    address: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String) -> Unit
) {
    var editableGender by remember { mutableStateOf(gender) }
    var editableBirthDate by remember { mutableStateOf(birthDate) }
    var editableCccd by remember { mutableStateOf(cccd) }
    var editableHometown by remember { mutableStateOf(hometown) }
    var editableAddress by remember { mutableStateOf(address) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            backgroundColor = Color.White,
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .width(500.dp)
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Duyệt bệnh nhân",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Thông tin bệnh nhân
                DialogTextField(
                    label = "Họ và tên",
                    value = fullname,
                    enabled = false,
                    onValueChange = {}
                )

                Spacer(modifier = Modifier.height(12.dp))

                DialogTextField(
                    label = "Giới tính",
                    value = editableGender,
                    enabled = gender.isBlank(),
                    onValueChange = { editableGender = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                DialogTextField(
                    label = "Ngày sinh",
                    value = editableBirthDate,
                    enabled = birthDate.isBlank(),
                    onValueChange = { editableBirthDate = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                DialogTextField(
                    label = "CCCD",
                    value = editableCccd,
                    enabled = cccd.isBlank(),
                    onValueChange = { editableCccd = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                DialogTextField(
                    label = "Quê quán",
                    value = editableHometown,
                    enabled = hometown.isBlank(),
                    onValueChange = { editableHometown = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                DialogTextField(
                    label = "Phòng ",
                    value = editableAddress,
                    enabled = address.isBlank(),
                    onValueChange = { editableAddress = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Hủy", color = Color(0xFF64748B))
                    }

                    Button(
                        onClick = {
                            onConfirm(
                                editableGender,
                                editableBirthDate,
                                editableCccd,
                                editableHometown,
                                editableAddress
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF10B981)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Thêm vào phòng", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun DischargeDialog(
    id: Int,
    fullname: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            backgroundColor = Color.White,
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .width(400.dp)
                    .padding(24.dp)
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = Color(0xFFFEE2E2),
                            shape = RoundedCornerShape(28.dp)
                        )
                        .align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Xác nhận xuất viện",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Bạn có chắc chắn muốn xuất viện cho bệnh nhân:",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Thông tin bệnh nhân
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(8.dp),
                    elevation = 0.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "ID: #$id",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = fullname,
                            fontSize = 16.sp,
                            color = Color(0xFF1E293B),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Hủy", color = Color(0xFF64748B))
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFEF4444)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Xuất viện", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogTextField(
    label: String,
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF64748B),
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = if (enabled) Color.White else Color(0xFFF1F5F9),
                focusedBorderColor = Color(0xFF1E88E5),
                unfocusedBorderColor = Color(0xFFE2E8F0),
                disabledBorderColor = Color(0xFFE2E8F0),
                disabledTextColor = Color(0xFF64748B)
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    iconColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    color = iconColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF94A3B8),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 15.sp,
                color = Color(0xFF1E293B),
                fontWeight = FontWeight.Medium
            )
        }
    }
}