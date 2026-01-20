package com.example.dat_lich_kham_fe.ui.screen

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.ui.component.AppBarView
import com.example.dat_lich_kham_fe.ui.component.Day
import com.example.dat_lich_kham_fe.ui.component.DoctorCard
import com.example.dat_lich_kham_fe.ui.component.WeekDaysRow
import com.example.dat_lich_kham_fe.util.divideAndRound
import com.example.dat_lich_kham_fe.viewmodel.DoctorViewModel
import com.example.dat_lich_kham_fe.viewmodel.DoctorViewModelFactory

@Composable
fun DetailDepartmentScreen(
    navController: NavController,
    id: Comparable<*>,
    name: String
) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value

    val selectedDate = remember { mutableStateOf<Day?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    // Doctor paging viewmodel
    val doctorViewModel: DoctorViewModel = viewModel(
        factory = DoctorViewModelFactory(context)
    )
    val doctors = doctorViewModel.doctors
    val isLoading = doctorViewModel.isLoading
    val hasMore = doctorViewModel.hasMore
    val error = doctorViewModel.error

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

    val listState = rememberLazyListState()

    // Lần đầu load danh sách bác sĩ
    LaunchedEffect(id) {
        doctorViewModel.resetPaging()
        doctorViewModel.fetchDoctors((id as? Int) ?: id.toString().toInt())
    }

    // Theo dõi scroll để load thêm bác sĩ (chỉ khi không tìm kiếm)
    LaunchedEffect(listState, doctors, hasMore, isLoading, searchQuery) {
        if (searchQuery.isBlank()) {
            snapshotFlow { listState.layoutInfo.visibleItemsInfo }
                .collect { visibleItems ->
                    if (visibleItems.isNotEmpty() && hasMore && !isLoading) {
                        val lastVisibleItem = visibleItems.last().index
                        if (lastVisibleItem >= doctors.size - 1 && doctors.isNotEmpty()) {
                            doctorViewModel.fetchDoctors((id as? Int) ?: id.toString().toInt(), isLoadMore = true)
                        }
                    }
                }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppBarView(
            title = stringResource(id = R.string.choose_a_doctor_title),
            color = R.color.white,
            backgroundColor = R.color.darkblue,
            alignment = Alignment.TopCenter,
            onDeleteNavClicked = { navController.popBackStack() },
            isVisible = true
        )
        Spacer(modifier = Modifier.height((16 * divideAndRound(screenWidthValue)).dp))
        Text(text = "${stringResource(id = R.string.doctor)} $name")
        WeekDaysRow(onDaySelected = { day ->
            selectedDate.value = day
        })

        Spacer(modifier = Modifier.height(12.dp))

        // Thanh tìm kiếm
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
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
                    tint = MaterialTheme.colorScheme.primary
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
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.LightGray,
                cursorColor = MaterialTheme.colorScheme.primary
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Box(modifier = Modifier
            .fillMaxSize()
            .weight(1f)) {

            // Hiển thị danh sách hoặc thông báo không tìm thấy
            if (filteredDoctors.isEmpty() && searchQuery.isNotEmpty() && !isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
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
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredDoctors) { doctor ->
                        DoctorCard(
                            id = doctor.id,
                            name = doctor.name,
                            code = doctor.code,
                            examPrice = doctor.examPrice,
                            department = doctor.department,
                            onClicked = {
                                val day = selectedDate.value
                                if (day != null) {
                                    navController.navigate(
                                        "BookingScreen/${doctor.id}/${Uri.encode(doctor.name)}/${doctor.code}/${doctor.examPrice}/${Uri.encode(doctor.department)}/${Uri.encode(day.fullDate)}"
                                    )
                                } else {
                                    // Có thể hiện thông báo yêu cầu chọn ngày
                                }
                            }
                        )
                    }
                    // Chỉ hiển thị loading khi không tìm kiếm
                    if (isLoading && hasMore && searchQuery.isBlank()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }

            // Hiển thị lỗi nếu có
            if (error != null) {
                Text(
                    text = error,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
