package com.example.dat_lich_kham_fe.ui.screen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.ui.component.AppBarView
import com.example.dat_lich_kham_fe.ui.component.ClinicCard
import com.example.dat_lich_kham_fe.util.divideAndRound
import com.example.dat_lich_kham_fe.viewmodel.DepartmentViewModel
import com.example.dat_lich_kham_fe.viewmodel.DepartmentViewModelFactory

@Composable
fun DepartmentScreen(
    navController: NavController,
    departmentViewModel: DepartmentViewModel =
        viewModel( factory = DepartmentViewModelFactory(LocalContext.current))
) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value

    var searchQuery by remember { mutableStateOf("") }

    val listState = rememberLazyListState()
    val isHeaderVisible by remember {
        derivedStateOf { listState.firstVisibleItemIndex == 0 }
    }

    LaunchedEffect(Unit) {
        departmentViewModel.fetchDepartments()
    }

    // Lọc danh sách khoa theo từ khóa tìm kiếm
    val filteredDepartments = remember(departmentViewModel.departments, searchQuery) {
        if (searchQuery.isBlank()) {
            departmentViewModel.departments
        } else {
            departmentViewModel.departments.filter { dept ->
                dept.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        AppBarView(
            title = stringResource(R.string.schedule_an_appointment_title),
            color = R.color.white,
            backgroundColor = R.color.darkblue,
            alignment = Alignment.TopCenter,
            onDeleteNavClicked = {navController.popBackStack()},
            isVisible = true
        )

        Column(
            modifier = Modifier.padding(horizontal = (16 * divideAndRound(screenWidthValue)).dp)
        ) {
            Spacer(modifier = Modifier.height((20 * divideAndRound(screenWidthValue)).dp))

            if (isHeaderVisible) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow((4 * divideAndRound(screenWidthValue)).dp, RoundedCornerShape((12 * divideAndRound(screenWidthValue)).dp)),
                    shape = RoundedCornerShape((12 * divideAndRound(screenWidthValue)).dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier.padding((16 * divideAndRound(screenWidthValue)).dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_keyboard_arrow_down_24),
                            contentDescription = null,
                            tint = colorResource(id = R.color.teal_700),
                            modifier = Modifier.size((20 * divideAndRound(screenWidthValue)).dp)
                        )
                        Spacer(modifier = Modifier.width((8 * divideAndRound(screenWidthValue)).dp))
                        Text(
                            text = stringResource(id = R.string.select_a_medical_department),
                            fontSize = (16 * divideAndRound(screenWidthValue)).sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorResource(id = R.color.teal_700)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height((16 * divideAndRound(screenWidthValue)).dp))

            // Thanh tìm kiếm
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Tìm kiếm khoa khám...",
                        color = Color.Gray,
                        fontSize = (14 * divideAndRound(screenWidthValue)).sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = colorResource(id = R.color.darkblue)
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
                shape = RoundedCornerShape((12 * divideAndRound(screenWidthValue)).dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(id = R.color.darkblue),
                    unfocusedBorderColor = Color.LightGray,
                    cursorColor = colorResource(id = R.color.darkblue),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height((12 * divideAndRound(screenWidthValue)).dp))

            // Hiển thị số kết quả
            if (searchQuery.isNotEmpty()) {
                Text(
                    text = "Tìm thấy ${filteredDepartments.size} khoa",
                    fontSize = (14 * divideAndRound(screenWidthValue)).sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = (4 * divideAndRound(screenWidthValue)).dp)
                )
                Spacer(modifier = Modifier.height((8 * divideAndRound(screenWidthValue)).dp))
            }

            if (departmentViewModel.isLoading) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow((4 * divideAndRound(screenWidthValue)).dp, RoundedCornerShape((16 * divideAndRound(screenWidthValue)).dp)),
                    shape = RoundedCornerShape((16 * divideAndRound(screenWidthValue)).dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding((40 * divideAndRound(screenWidthValue)).dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = colorResource(id = R.color.darkblue),
                                modifier = Modifier.size((32 * divideAndRound(screenWidthValue)).dp)
                            )
                            Spacer(modifier = Modifier.height((16 * divideAndRound(screenWidthValue)).dp))
                            Text(
                                text = "Đang tải danh sách khoa...",
                                fontSize = (14 * divideAndRound(screenWidthValue)).sp,
                                color = Color(0xFF6B7280),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            } else if (departmentViewModel.error != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow((4 * divideAndRound(screenWidthValue)).dp, RoundedCornerShape((16 * divideAndRound(screenWidthValue)).dp)),
                    shape = RoundedCornerShape((16 * divideAndRound(screenWidthValue)).dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = departmentViewModel.error ?: "",
                        color = Color(0xFFDC2626),
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding((20 * divideAndRound(screenWidthValue)).dp)
                    )
                }
            } else if (filteredDepartments.isEmpty() && searchQuery.isNotEmpty()) {
                // Hiển thị thông báo không tìm thấy
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow((4 * divideAndRound(screenWidthValue)).dp, RoundedCornerShape((16 * divideAndRound(screenWidthValue)).dp)),
                    shape = RoundedCornerShape((16 * divideAndRound(screenWidthValue)).dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding((40 * divideAndRound(screenWidthValue)).dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size((48 * divideAndRound(screenWidthValue)).dp)
                            )
                            Spacer(modifier = Modifier.height((16 * divideAndRound(screenWidthValue)).dp))
                            Text(
                                text = "Không tìm thấy khoa nào phù hợp",
                                fontSize = (16 * divideAndRound(screenWidthValue)).sp,
                                color = Color(0xFF6B7280),
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy((8 * divideAndRound(screenWidthValue)).dp)
                ) {
                    items(filteredDepartments) { dept ->
                        ClinicCard(
                            id = dept.id,
                            title = dept.name,
                            detail = dept.description,
                            onClick = { navController.navigate("DetailDepartmentScreen/${dept.id}/${dept.name}") }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height((16 * divideAndRound(screenWidthValue)).dp))
                    }
                }
            }
        }
    }
}
