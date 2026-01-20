package ui.compoment

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import viewmodel.DoctorViewModel

@Composable
fun DoctorTab(onAddClick: () -> Unit) {
    val doctorViewModel = remember { DoctorViewModel() }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit){
        doctorViewModel.fetchDoctor()
    }

    val doctors = doctorViewModel.doctors

    // Lọc danh sách bác sĩ theo từ khóa tìm kiếm
    val filteredDoctors = remember(doctors, searchQuery) {
        if (searchQuery.isBlank()) {
            doctors
        } else {
            doctors.filter { doctor ->
                doctor.name.contains(searchQuery, ignoreCase = true) ||
                        doctor.code.contains(searchQuery, ignoreCase = true) ||
                        doctor.department.contains(searchQuery, ignoreCase = true)
            }
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
                text = "Danh sách bác sĩ",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF1E88E5),
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = { doctorViewModel.fetchDoctor() },
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
                onClick = onAddClick,
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

        // Thanh tìm kiếm
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            placeholder = {
                Text(
                    text = "Tìm kiếm theo tên, mã hoặc khoa...",
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
                text = "Tìm thấy ${filteredDoctors.size} kết quả",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Hiển thị danh sách hoặc thông báo không tìm thấy
        if (filteredDoctors.isEmpty() && searchQuery.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Không tìm thấy bác sĩ nào phù hợp",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn {
                items(filteredDoctors) { doctor ->
                    DoctorCard(
                        id = doctor.id,
                        name = doctor.name,
                        code = doctor.code,
                        examPrice = doctor.examPrice,
                        department = doctor.department,
                    )
                }
            }
        }
    }
}