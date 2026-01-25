package ui.compoment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import viewmodel.InpatientViewModel

@Composable
fun  ActiveInpatientTab() {
    val inpatientViewModel = remember { InpatientViewModel() }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        inpatientViewModel.fetchInpatient("Đã nhập viện")
    }

    val inpatients = inpatientViewModel.inpatients

    // Lọc danh sách bệnh nhân theo tìm kiếm
    val filteredInpatients = remember(inpatients, searchQuery) {
        if (searchQuery.isBlank()) {
            inpatients
        } else {
            inpatients.filter { inpatient ->
                inpatient.fullname.contains(searchQuery, ignoreCase = true) ||
                        inpatient.cccd.contains(searchQuery, ignoreCase = true) ||
                        inpatient.hometown.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Tiêu đề
        Text(
            text = "Danh sách đang chờ",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E88E5),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Thanh tìm kiếm
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            placeholder = {
                Text(
                    text = "Tìm kiếm theo tên, CCCD hoặc quê quán...",
                    color = Color.Gray
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color(0xFF1E88E5)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = Color.Gray
                        )
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF1E88E5),
                unfocusedBorderColor = Color.LightGray,
                cursorColor = Color(0xFF1E88E5)
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Hiển thị số kết quả
        if (searchQuery.isNotEmpty()) {
            Text(
                text = "Tìm thấy ${filteredInpatients.size} bệnh nhân",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Danh sách bệnh nhân
        if (filteredInpatients.isEmpty() && searchQuery.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Không tìm thấy bệnh nhân nào phù hợp",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredInpatients) { inpatient ->
                    InpatientCard(
                        id = inpatient.id,
                        userId = inpatient.userId,
                        fullname = inpatient.fullname,
                        gender = inpatient.gender,
                        birthDate = inpatient.birthDate,
                        cccd = inpatient.cccd,
                        hometown = inpatient.hometown,
                        address = inpatient.address,
                        admissionDate = inpatient.admissionDate,
                        dischargeDate = inpatient.dischargeDate,
                        status = inpatient.status,
                        createAt = inpatient.createAt,
                        displayApproveButton = false,
                        displayDischargeButton = true,
                        onApprove = null,
                        onDischarge = { id, fullname ->
                            // Xử lý khi xuất viện
                            println("Xuất viện: ID=$id, Name=$fullname")
                            inpatientViewModel.updateStatus(id)
                        }
                    )
                }
            }
        }
    }
}