package ui.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import ui.compoment.DepartmentTab
import ui.compoment.DoctorTab
import viewmodel.DerpartmentViewModel
import viewmodel.AccountViewModel
import viewmodel.CreateDoctorState
import viewmodel.DoctorViewModel
import viewmodel.CreateDepartmentState

class DepartmentManagerAndDoctorScreen : Screen {
    @Composable
    override fun Content() {
        var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
        var showAddDepartmentDialog by remember { mutableStateOf(false) }
        var showAddDoctorDialog by remember { mutableStateOf(false) }

        val departmentViewModel = remember { DerpartmentViewModel() }
        val accountViewModel = remember { AccountViewModel() }
        val doctorViewModel = remember { DoctorViewModel() }

        LaunchedEffect(Unit) {
            departmentViewModel.fetchDepartment()
        }

        val departments by departmentViewModel.departments.collectAsState()
        val createDoctorState by accountViewModel.createDoctorState.collectAsState()
        // Thêm state cho việc tạo khoa
        val createDepartmentState by departmentViewModel.createDepartmentState.collectAsState()

        // Xử lý trạng thái tạo bác sĩ
        LaunchedEffect(createDoctorState) {
            when (val state = createDoctorState) {
                is CreateDoctorState.Success -> {
                    // Refresh danh sách bác sĩ sau khi thêm thành công
                    doctorViewModel.fetchDoctor()
                    accountViewModel.resetState()
                }
                is CreateDoctorState.Error -> {
                    // TODO: Có thể hiển thị Snackbar với thông báo lỗi
                    accountViewModel.resetState()
                }
                else -> {}
            }
        }

        // Thêm xử lý trạng thái tạo khoa
        LaunchedEffect(createDepartmentState) {
            when (val state = createDepartmentState) {
                is CreateDepartmentState.Success -> {
                    // Refresh danh sách khoa sau khi thêm thành công
                    departmentViewModel.fetchDepartment()
                    departmentViewModel.resetCreateDepartmentState()
                }
                is CreateDepartmentState.Error -> {
                    // TODO: Có thể hiển thị Snackbar với thông báo lỗi
                    departmentViewModel.resetCreateDepartmentState()
                }
                else -> {}
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
        ) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                elevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Quản lý Khoa & Bác sĩ",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Quản lý danh sách các khoa và bác sĩ trong bệnh viện",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Tab Row
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                elevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ModernTab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = "Khoa",
                        icon = Icons.Default.Business,
                        modifier = Modifier.weight(1f)
                    )
                    ModernTab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = "Bác sĩ",
                        icon = Icons.Default.LocalHospital,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Tab Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8FAFC))
            ) {
                when (selectedTabIndex) {
                    0 -> DepartmentTab(onAddClick = { showAddDepartmentDialog = true })
                    1 -> DoctorTab(onAddClick = { showAddDoctorDialog = true })
                }
            }

            // Dialogs
            if (showAddDepartmentDialog) {
                ModernAddDepartmentDialog(
                    onDismiss = { showAddDepartmentDialog = false },
                    onAdd = { name, description ->
                        // Gọi function createDepartment từ viewModel
                        departmentViewModel.createDepartment(name, description)
                        showAddDepartmentDialog = false
                    },
                    isLoading = createDepartmentState is CreateDepartmentState.Loading
                )
            }

            if (showAddDoctorDialog) {
                ModernAddDoctorDialog(
                    departments = departments ?: emptyList(),
                    onDismiss = { showAddDoctorDialog = false },
                    onAdd = { name, code, departmentId, examPrice ->
                        accountViewModel.createDoctor(
                            name = name,
                            code = code,
                            departmentId = departmentId,
                            examPrice = examPrice.toInt()
                        )
                        showAddDoctorDialog = false
                    },
                    isLoading = createDoctorState is CreateDoctorState.Loading
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ModernTab(
    selected: Boolean,
    onClick: () -> Unit,
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) Color(0xFF1E88E5) else Color.Transparent,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) Color.White else Color(0xFF64748B),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = if (selected) Color.White else Color(0xFF64748B),
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ModernAddDepartmentDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, description: String) -> Unit,
    isLoading: Boolean = false
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { if (!isLoading) onDismiss() }) {
        Card(
            shape = RoundedCornerShape(20.dp),
            backgroundColor = Color.White,
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .width(450.dp)
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
                                    color = Color(0xFFE3F2FD),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Business,
                                contentDescription = null,
                                tint = Color(0xFF1E88E5),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Thêm khoa mới",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "Nhập thông tin khoa",
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
                    label = "Tên khoa",
                    placeholder = "Nhập tên khoa...",
                    icon = Icons.Default.Business,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                ModernTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Mô tả",
                    placeholder = "Nhập mô tả về khoa...",
                    icon = Icons.Default.Description,
                    singleLine = false,
                    minLines = 3,
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
                        onClick = { onAdd(name, description) },
                        enabled = !isLoading && name.isNotBlank() && description.isNotBlank(),
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF1E88E5),
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ModernAddDoctorDialog(
    departments: List<data.model.DepartmentResponse>,
    onDismiss: () -> Unit,
    onAdd: (name: String, code: String, departmentId: Int, examPrice: Double) -> Unit,
    isLoading: Boolean = false
) {
    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var examPrice by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedDepartment by remember { mutableStateOf<data.model.DepartmentResponse?>(null) }

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
                                imageVector = Icons.Default.LocalHospital,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Thêm bác sĩ mới",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "Nhập thông tin bác sĩ",
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
                    label = "Họ và tên",
                    placeholder = "Nhập họ tên bác sĩ...",
                    icon = Icons.Default.Person,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                ModernTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = "Mã bác sĩ",
                    placeholder = "Nhập mã bác sĩ...",
                    icon = Icons.Default.Badge,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Department dropdown
                Column {
                    Text(
                        text = "Khoa",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { if (!isLoading) expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedDepartment?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            enabled = !isLoading,
                            placeholder = { Text("Chọn khoa...") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Business,
                                    contentDescription = null,
                                    tint = Color(0xFF1E88E5),
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF1E88E5),
                                unfocusedBorderColor = Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            departments.forEach { department ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedDepartment = department
                                        expanded = false
                                    }
                                ) {
                                    Text(
                                        text = department.name,
                                        fontSize = 14.sp,
                                        color = Color(0xFF1E293B)
                                    )
                                }
                            }
                        }
                    }
                }

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
                            selectedDepartment?.let { dept ->
                                onAdd(name, code, dept.id, price)
                            }
                        },
                        enabled = !isLoading &&
                                name.isNotBlank() &&
                                code.isNotBlank() &&
                                selectedDepartment != null &&
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
    minLines: Int = 1,
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
                    tint = Color(0xFF1E88E5),
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
            minLines = minLines,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF1E88E5),
                unfocusedBorderColor = Color(0xFFE2E8F0),
                cursorColor = Color(0xFF1E88E5)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}