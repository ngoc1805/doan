package com.example.dat_lich_kham_fe.ui.screen

import android.app.DatePickerDialog
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dat_lich_kham_fe.ui.component.AppBarView
import com.example.dat_lich_kham_fe.viewmodel.InFoViewModel
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.util.divideAndRound
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import java.util.Calendar
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import com.example.dat_lich_kham_fe.viewmodel.DepartmentViewModelFactory
import com.example.dat_lich_kham_fe.viewmodel.InFoViewModelFactory
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InFoScreen(
    navController: NavController,
    inFoViewModel: InFoViewModel = viewModel(factory = InFoViewModelFactory(LocalContext.current))
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value

    var showDialog by remember { mutableStateOf(false) }
    var showScanner by remember { mutableStateOf(false) }
    var scanResult by remember{ mutableStateOf("Chưa có kết quả") }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _, year, month, dayOfMonth ->
            val selectedDate = "%02d%02d%04d".format(dayOfMonth, month + 1, year)
            inFoViewModel.onBirthDateChanged(selectedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Lưu trạng thái ban đầu của các trường khi vào trang - CHỈ SET MỘT LẦN
    val initialFullNameFilled = remember { mutableStateOf<Boolean?>(null) }
    val initialBirthDateFilled = remember { mutableStateOf<Boolean?>(null) }
    val initialGenderFilled = remember { mutableStateOf<Boolean?>(null) }
    val initialCccdFilled = remember { mutableStateOf<Boolean?>(null) }
    val initialHometownFilled = remember { mutableStateOf<Boolean?>(null) }
    val isInitialized = remember { mutableStateOf(false) }

    LaunchedEffect(inFoViewModel.hasQrScanResult) {
        if (!inFoViewModel.hasQrScanResult) {
            inFoViewModel.reset()
            inFoViewModel.loadUserInfoFromLocal()
        }
    }

    // Cập nhật trạng thái ban đầu CHỈ MỘT LẦN sau khi load xong
    LaunchedEffect(Unit) {
        // Đợi một chút để ViewModel load xong
        kotlinx.coroutines.delay(100)
        if (!isInitialized.value) {
            initialFullNameFilled.value = inFoViewModel.fullName.isNotBlank()
            initialBirthDateFilled.value = inFoViewModel.birthDate != null
            initialGenderFilled.value = inFoViewModel.gender.isNotBlank()
            initialCccdFilled.value = inFoViewModel.cccd.isNotBlank()
            initialHometownFilled.value = inFoViewModel.hometown.isNotBlank()
            isInitialized.value = true
        }
    }

    // Kiểm tra xem các trường có đầy đủ thông tin không (dùng trạng thái ban đầu)
    val isFullNameFilled = initialFullNameFilled.value ?: false
    val isBirthDateFilled = initialBirthDateFilled.value ?: false
    val isGenderFilled = initialGenderFilled.value ?: false
    val isCccdFilled = initialCccdFilled.value ?: false
    val isHometownFilled = initialHometownFilled.value ?: false

    // Tất cả thông tin đã đầy đủ
    val isAllFieldsFilled = isFullNameFilled && isBirthDateFilled && isGenderFilled && isCccdFilled && isHometownFilled

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        AppBarView(
            title = stringResource(id = R.string.personal_information_title) ,
            color = R.color.white  ,
            backgroundColor = R.color.darkblue ,
            alignment = Alignment.Center,
            onDeleteNavClicked = {
                navController.navigate("MainScreen/3")
                inFoViewModel.onHasQrScanResultChanged(false)

            },
            isVisible = true
        )

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = (16 * divideAndRound(screenWidthValue)).dp)
        ) {
            Spacer(modifier = Modifier.height((24 * divideAndRound(screenWidthValue)).dp))

            // QR Scanner Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow((4 * divideAndRound(screenWidthValue)).dp, RoundedCornerShape((12 * divideAndRound(screenWidthValue)).dp))
                    .clickable { navController.navigate("QrScannerScreen") },
                shape = RoundedCornerShape((12 * divideAndRound(screenWidthValue)).dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding((20 * divideAndRound(screenWidthValue)).dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.qrscan),
                        contentDescription = "scan icon",
                        tint = colorResource(id = R.color.darkblue),
                        modifier = Modifier.size((24 * divideAndRound(screenWidthValue)).dp)
                    )
                    Spacer(modifier = Modifier.width((16 * divideAndRound(screenWidthValue)).dp))
                    Text(
                        text = stringResource(id = R.string.scan_the_id),
                        color = Color(0xFF1F2937),
                        fontSize = (16 * divideAndRound(screenWidthValue)).sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height((24 * divideAndRound(screenWidthValue)).dp))

            // Personal Information Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow((4 * divideAndRound(screenWidthValue)).dp, RoundedCornerShape((16 * divideAndRound(screenWidthValue)).dp)),
                shape = RoundedCornerShape((16 * divideAndRound(screenWidthValue)).dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding((20 * divideAndRound(screenWidthValue)).dp)
                ) {
                    Text(
                        text = "Thông tin cá nhân",
                        fontSize = (20 * divideAndRound(screenWidthValue)).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937),
                        modifier = Modifier.padding(bottom = (20 * divideAndRound(screenWidthValue)).dp)
                    )

                    // Họ tên
                    OutlinedTextField(
                        value = inFoViewModel.fullName,
                        onValueChange = {inFoViewModel.onFullNameChanged(it)},
                        label = { Text(text = stringResource(id = R.string.full_name))},
                        supportingText = { inFoViewModel.fullNameError?.let { Text(text = it, color = Color.Red) } },
                        isError = inFoViewModel.fullNameError != null,
                        enabled = !isFullNameFilled,
                        shape = RoundedCornerShape((12 * divideAndRound(screenWidthValue)).dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedTextFieldColors(
                            focusedBorderColor = colorResource(id = R.color.darkblue),
                            unfocusedBorderColor = Color(0xFFE5E7EB),
                            disabledBorderColor = Color(0xFFE5E7EB),
                            focusedLabelColor = colorResource(id = R.color.darkblue),
                            unfocusedLabelColor = Color(0xFF6B7280),
                            disabledLabelColor = Color(0xFF9CA3AF),
                            disabledTextColor = Color(0xFF6B7280),
                            cursorColor = colorResource(id = R.color.darkblue)
                        ),
                    )

                    Spacer(modifier = Modifier.height((16 * divideAndRound(screenWidthValue)).dp))

                    // Ngày sinh & Giới tính
                    Row(verticalAlignment = Alignment.Top) {
                        OutlinedTextField(
                            value = inFoViewModel.birthDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "",
                            onValueChange = {},
                            label = { Text(text = stringResource(id = R.string.date_of_birth))},
                            trailingIcon = {
                                Image(
                                    painter = painterResource(id = R.drawable.baseline_keyboard_arrow_down_24),
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(if (isBirthDateFilled) Color(0xFF9CA3AF) else Color(0xFF6B7280)),
                                    modifier = if (!isBirthDateFilled) {
                                        Modifier.clickable { datePickerDialog.show() }
                                    } else Modifier
                                )
                            },
                            readOnly = true,
                            enabled = !isBirthDateFilled,
                            modifier = Modifier
                                .weight(1f)
                                .then(
                                    if (!isBirthDateFilled) {
                                        Modifier.clickable { datePickerDialog.show() }
                                    } else Modifier
                                ),
                            shape = RoundedCornerShape((12 * divideAndRound(screenWidthValue)).dp),
                            colors = outlinedTextFieldColors(
                                focusedBorderColor = colorResource(id = R.color.darkblue),
                                unfocusedBorderColor = Color(0xFFE5E7EB),
                                disabledBorderColor = Color(0xFFE5E7EB),
                                focusedLabelColor = colorResource(id = R.color.darkblue),
                                unfocusedLabelColor = Color(0xFF6B7280),
                                disabledLabelColor = Color(0xFF9CA3AF),
                                disabledTextColor = Color(0xFF6B7280),
                                cursorColor = colorResource(id = R.color.darkblue)
                            ),
                            isError = inFoViewModel.birthDateError != null
                        )

                        Spacer(modifier = Modifier.width((16 * divideAndRound(screenWidthValue)).dp))

                        // Gender Selection
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape((12 * divideAndRound(screenWidthValue)).dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isGenderFilled) Color(0xFFF3F4F6) else Color(0xFFF9FAFB)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding((16 * divideAndRound(screenWidthValue)).dp)
                            ) {
                                Text(
                                    text = "Giới tính",
                                    fontSize = (14 * divideAndRound(screenWidthValue)).sp,
                                    color = if (isGenderFilled) Color(0xFF9CA3AF) else Color(0xFF6B7280),
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(bottom = (8 * divideAndRound(screenWidthValue)).dp)
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = inFoViewModel.gender == "Nam",
                                        onClick = { if (!isGenderFilled) inFoViewModel.onGenderChanged("Nam") },
                                        enabled = !isGenderFilled,
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = colorResource(id = R.color.darkblue),
                                            unselectedColor = Color(0xFF9CA3AF),
                                            disabledSelectedColor = colorResource(id = R.color.darkblue).copy(alpha = 0.6f),
                                            disabledUnselectedColor = Color(0xFFD1D5DB)
                                        )
                                    )
                                    Text(
                                        text = "Nam",
                                        color = if (isGenderFilled) Color(0xFF9CA3AF) else Color(0xFF1F2937),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = (14 * divideAndRound(screenWidthValue)).sp,
                                        modifier = Modifier.padding(start = (4 * divideAndRound(screenWidthValue)).dp)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = inFoViewModel.gender == "Nữ",
                                        onClick = { if (!isGenderFilled) inFoViewModel.onGenderChanged("Nữ") },
                                        enabled = !isGenderFilled,
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = colorResource(id = R.color.darkblue),
                                            unselectedColor = Color(0xFF9CA3AF),
                                            disabledSelectedColor = colorResource(id = R.color.darkblue).copy(alpha = 0.6f),
                                            disabledUnselectedColor = Color(0xFFD1D5DB)
                                        )
                                    )
                                    Text(
                                        text = "Nữ",
                                        color = if (isGenderFilled) Color(0xFF9CA3AF) else Color(0xFF1F2937),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = (14 * divideAndRound(screenWidthValue)).sp,
                                        modifier = Modifier.padding(start = (4 * divideAndRound(screenWidthValue)).dp)
                                    )
                                }
                            }
                        }
                    }

                    // Error messages
                    inFoViewModel.birthDateError?.let{
                        Text(
                            text = it,
                            color = Color.Red,
                            fontSize = (12 * divideAndRound(screenWidthValue)).sp,
                            modifier = Modifier.padding(start = (4 * divideAndRound(screenWidthValue)).dp, top = (4 * divideAndRound(screenWidthValue)).dp)
                        )
                    }

                    inFoViewModel.genderError?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            fontSize = (12 * divideAndRound(screenWidthValue)).sp,
                            modifier = Modifier.padding(start = (4 * divideAndRound(screenWidthValue)).dp, top = (4 * divideAndRound(screenWidthValue)).dp)
                        )
                    }

                    Spacer(modifier = Modifier.height((16 * divideAndRound(screenWidthValue)).dp))

                    // Số CCCD
                    OutlinedTextField(
                        value = inFoViewModel.cccd,
                        onValueChange = {inFoViewModel.onCccdChanged(it)},
                        label = { Text(text = stringResource(id = R.string.cccd_number))},
                        supportingText = { inFoViewModel.cccdError?.let { Text(text = it, color = Color.Red) } },
                        isError = inFoViewModel.cccdError != null,
                        enabled = !isCccdFilled,
                        shape = RoundedCornerShape((12 * divideAndRound(screenWidthValue)).dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedTextFieldColors(
                            focusedBorderColor = colorResource(id = R.color.darkblue),
                            unfocusedBorderColor = Color(0xFFE5E7EB),
                            disabledBorderColor = Color(0xFFE5E7EB),
                            focusedLabelColor = colorResource(id = R.color.darkblue),
                            unfocusedLabelColor = Color(0xFF6B7280),
                            disabledLabelColor = Color(0xFF9CA3AF),
                            disabledTextColor = Color(0xFF6B7280),
                            cursorColor = colorResource(id = R.color.darkblue)
                        ),
                    )

                    Spacer(modifier = Modifier.height((16 * divideAndRound(screenWidthValue)).dp))

                    // Quê quán
                    OutlinedTextField(
                        value = inFoViewModel.hometown,
                        onValueChange = {inFoViewModel.onHometownChanged(it)},
                        label = { Text(text = stringResource(id = R.string.hometown))},
                        supportingText = { inFoViewModel.hometownError?.let { Text(text = it, color = Color.Red) } },
                        isError = inFoViewModel.hometownError != null,
                        enabled = !isHometownFilled,
                        shape = RoundedCornerShape((12 * divideAndRound(screenWidthValue)).dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedTextFieldColors(
                            focusedBorderColor = colorResource(id = R.color.darkblue),
                            unfocusedBorderColor = Color(0xFFE5E7EB),
                            disabledBorderColor = Color(0xFFE5E7EB),
                            focusedLabelColor = colorResource(id = R.color.darkblue),
                            unfocusedLabelColor = Color(0xFF6B7280),
                            disabledLabelColor = Color(0xFF9CA3AF),
                            disabledTextColor = Color(0xFF6B7280),
                            cursorColor = colorResource(id = R.color.darkblue)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height((32 * divideAndRound(screenWidthValue)).dp))

            // Update Button - Chỉ hiển thị khi chưa đầy đủ thông tin
            if (!isAllFieldsFilled) {
                Button(
                    onClick = {
                        inFoViewModel.validateAndUpdate {
                            showDialog = true
                        }
                        try {
                            showDialog = true
                        } catch (e: Exception) {
                            Log.e("Button", "Error: ${e.message}")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((56 * divideAndRound(screenWidthValue)).dp)
                        .shadow((8 * divideAndRound(screenWidthValue)).dp, RoundedCornerShape((12 * divideAndRound(screenWidthValue)).dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.darkblue)
                    ),
                    shape = RoundedCornerShape((12 * divideAndRound(screenWidthValue)).dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.update),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = (16 * divideAndRound(screenWidthValue)).sp
                    )
                }

                Spacer(modifier = Modifier.height((32 * divideAndRound(screenWidthValue)).dp))
            }
        }

        // Success Dialog
        if (showDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    Button(
                        onClick = { showDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.darkblue)
                        ),
                        shape = RoundedCornerShape((8 * divideAndRound(screenWidthValue)).dp)
                    ) {
                        Text("OK", color = Color.White, fontWeight = FontWeight.Medium)
                    }
                },
                title = {
                    Text(
                        stringResource(id = R.string.success),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                },
                shape = RoundedCornerShape((16 * divideAndRound(screenWidthValue)).dp)
            )
        }
    }
}