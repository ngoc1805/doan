//package com.example.dat_lich_kham_fe.ui.screen
//
//import android.os.Build
//import androidx.annotation.RequiresApi
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalConfiguration
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.colorResource
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import com.example.dat_lich_kham_fe.R
//import com.example.dat_lich_kham_fe.ui.component.AppBarView
//import com.example.dat_lich_kham_fe.ui.component.Day
//import com.example.dat_lich_kham_fe.ui.component.TimeCard
//import com.example.dat_lich_kham_fe.util.divideAndRound
//import com.example.dat_lich_kham_fe.util.formatNumber
//import java.time.LocalDate
//import java.time.format.DateTimeFormatter
//
//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun BookingScreen(
//    navController: NavController,
//    doctor_id: Int,
//    doctor_name: String,
//    doctor_code: String,
//    examPrice: Int,
//    department: String,
//    date: String, // <-- sửa lại thành String
//) {
//    val configuration = LocalConfiguration.current
//    val context = LocalContext.current
//    val screenWidth = configuration.screenWidthDp.dp
//    val screenWidthValue = screenWidth.value
//    var ngayKham by remember{ mutableStateOf("") }
//    var selectedTime by remember { mutableStateOf<String?>(null) }
//
//    val tienCoc = 100000
//
//    var showDialog by remember { mutableStateOf(false) }
//    var dialogMessage by remember { mutableStateOf("") }
//
//    var showPinScreen by remember { mutableStateOf(false) }
//    var pinError by remember { mutableStateOf("") }
//    var showCreatePinScreen by remember { mutableStateOf(false) }
//    var showConfirmPinScreen by remember { mutableStateOf(false) }
//    var tempPin by remember { mutableStateOf("") }
//
//
//
//    Box() {
//        Column(modifier = Modifier.fillMaxSize()) {
//            AppBarView(
//                title = stringResource(id = R.string.appointment_title) ,
//                color = R.color.black,
//                backgroundColor = R.color.white ,
//                alignment = Alignment.TopCenter,
//                onDeleteNavClicked = { navController.popBackStack()},
//                isVisible = true
//            )
//
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding((16 * divideAndRound(screenWidthValue)).dp),
//                shape = RoundedCornerShape((16 * divideAndRound(screenWidthValue)).dp),
//                elevation = CardDefaults.cardElevation((4 * divideAndRound(screenWidthValue)).dp)
//            ) {
//                Box(
//                    modifier = Modifier
//                        .background(color = Color(0xFF1565C0))
//                        .padding((16 * divideAndRound(screenWidthValue)).dp)
//                ) {
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        modifier = Modifier.padding(top = (40 * divideAndRound(screenWidthValue)).dp)
//                    ) {
//                        Row(
//                            horizontalArrangement = Arrangement.End, // Căn lề sang phải
//                            modifier = Modifier.fillMaxWidth()
//                        ) {
//                            Column( horizontalAlignment = Alignment.End) {
//                                Text(
//                                    text = "${stringResource(id = R.string.doctor)}: $doctor_code - $doctor_name",
//                                    fontSize = (16 * divideAndRound(screenWidthValue)).sp,
//                                    fontWeight = FontWeight.Bold,
//                                    color = Color.White
//                                )
//                                Spacer(modifier = Modifier.height((4 * divideAndRound(screenWidthValue)).dp))
//                                Text(
//                                    text = "${stringResource(id = R.string.department)}: $department",
//                                    fontSize = (12 * divideAndRound(screenWidthValue)).sp,
//                                    fontWeight = FontWeight.Bold,
//                                    color = Color.White
//                                )
//                            }
//                            Spacer(modifier = Modifier.width((16 * divideAndRound(screenWidthValue)).dp))
//                            Image(
//                                painter = painterResource(id = R.drawable.anhtrang),
//                                contentDescription = null,
//                                modifier = Modifier
//                                    .size((80 * divideAndRound(screenWidthValue)).dp)
//                                    .clip(CircleShape)
//                                    .background(Color.White),
//                                contentScale = ContentScale.Crop
//                            )
//                        }
//                    }
//                }
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    modifier = Modifier
//                        //.padding(top = (140 * divideAndRound(screenWidthValue)).dp)
//                        .background(
//                            color = Color.White,
//                            shape = RoundedCornerShape(topStart = (16 * divideAndRound(screenWidthValue)).dp, topEnd = (16 * divideAndRound(screenWidthValue)).dp)
//                        )
//                        .fillMaxWidth()
//                        .padding((16 * divideAndRound(screenWidthValue)).dp)
//                ) {
//                    Text(
//                        text = "${stringResource(id = R.string.exam_price)}: ${formatNumber(examPrice)} VNĐ",
//                        color = Color(0xFF1565C0),
//                        fontWeight = FontWeight.Bold,
//                        fontSize = (16 * divideAndRound(screenWidthValue)).sp
//                    )
//                    Text(
//                        text = "${stringResource(id = R.string.deposit)}: ${formatNumber(tienCoc  )} VNĐ",
//                        color = Color(0xFF1565C0),
//                        fontWeight = FontWeight.Bold,
//                        fontSize = (16 * divideAndRound(screenWidthValue)).sp
//                    )
//                }
//            }
//            Spacer(modifier = Modifier.height((24 * divideAndRound(screenWidthValue)).dp))
//            ngayKham = date
//            Text(
//                text = "${stringResource(id = R.string.appointment_date)}: $date",
//                fontWeight = FontWeight.Bold,
//                fontSize = (18 * divideAndRound(screenWidthValue)).sp,
//                color = colorResource(id = R.color.teal_700),
//                modifier = Modifier.align(Alignment.CenterHorizontally)
//            )
//            Spacer(modifier = Modifier.height((8 * divideAndRound(screenWidthValue)).dp))
//            TimeCard(
//                doctorId = doctor_id,
//                selectedDate = date, // Nếu TimeCard nhận String, nếu không thì truyền Day(fullDate = date)
//                onTimeSelected = { time ->
//                    selectedTime = time
//                }
//            )
//            Spacer(modifier = Modifier.height((8 * divideAndRound(screenWidthValue)).dp))
//            Spacer(modifier = Modifier.height((16 * divideAndRound(screenWidthValue)).dp))
//            Button(
//                onClick = {
//                    val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
//                    val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//                    val datein = LocalDate.parse(date, inputFormatter)
//                    val dateout: String = datein.format(outputFormatter)
//                    navController.navigate("PaymentScreen/$doctor_id/$dateout/$selectedTime")
//                },
//                modifier = Modifier
//                    .width((200 * divideAndRound(screenWidthValue)).dp)
//                    .align(Alignment.CenterHorizontally),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = colorResource(id = R.color.darkblue),
//                    contentColor = Color.White
//                ),
//                shape = RoundedCornerShape((50 * divideAndRound(screenWidthValue))),
//                enabled = selectedTime != null
//            ) {
//                Text(text = stringResource(id = R.string.confirm))
//            }
//        }
//    }
//}


//--------------------------------------------------------
package com.example.dat_lich_kham_fe.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.ui.component.AppBarView
import com.example.dat_lich_kham_fe.ui.component.TimeCard
import com.example.dat_lich_kham_fe.util.divideAndRound
import com.example.dat_lich_kham_fe.util.formatNumber
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BookingScreen(
    navController: NavController,
    doctor_id: Int,
    doctor_name: String,
    doctor_code: String,
    examPrice: Int,
    department: String,
    date: String,
) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    var ngayKham by remember{ mutableStateOf("") }
    var selectedTime by remember { mutableStateOf<String?>(null) }

    val tienCoc = 100000

    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    var showPinScreen by remember { mutableStateOf(false) }
    var pinError by remember { mutableStateOf("") }
    var showCreatePinScreen by remember { mutableStateOf(false) }
    var showConfirmPinScreen by remember { mutableStateOf(false) }
    var tempPin by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()


    Box {
        Column(modifier = Modifier.fillMaxSize()) {
            AppBarView(
                title = stringResource(id = R.string.appointment_title),
                color = R.color.black,
                backgroundColor = R.color.white,
                alignment = Alignment.TopCenter,
                onDeleteNavClicked = { navController.popBackStack()},
                isVisible = true
            )

            // Doctor Info Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding((16 * divideAndRound(screenWidthValue)).dp)
                    .shadow(
                        elevation = (12 * divideAndRound(screenWidthValue)).dp,
                        shape = RoundedCornerShape((20 * divideAndRound(screenWidthValue)).dp)
                    ),
                shape = RoundedCornerShape((20 * divideAndRound(screenWidthValue)).dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF1565C0),
                                    Color(0xFF1976D2),
                                    Color(0xFF1E88E5)
                                )
                            )
                        )
                ) {
                    // Decorative circles
                    Box(
                        modifier = Modifier
                            .size((120 * divideAndRound(screenWidthValue)).dp)
                            .align(Alignment.TopStart)
                            .offset(x = (-30 * divideAndRound(screenWidthValue)).dp, y = (-30 * divideAndRound(screenWidthValue)).dp)
                            .background(Color.White.copy(alpha = 0.1f), CircleShape)
                    )

                    Column(
                        modifier = Modifier.padding((20 * divideAndRound(screenWidthValue)).dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.doctor),
                                    fontSize = (12 * divideAndRound(screenWidthValue)).sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height((4 * divideAndRound(screenWidthValue)).dp))
                                Text(
                                    text = "$doctor_code - $doctor_name",
                                    fontSize = (16 * divideAndRound(screenWidthValue)).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height((8 * divideAndRound(screenWidthValue)).dp))
                                Box(
                                    modifier = Modifier
                                        .background(
                                            Color.White.copy(alpha = 0.2f),
                                            RoundedCornerShape((8 * divideAndRound(screenWidthValue)).dp)
                                        )
                                        .padding(
                                            horizontal = (10 * divideAndRound(screenWidthValue)).dp,
                                            vertical = (4 * divideAndRound(screenWidthValue)).dp
                                        )
                                ) {
                                    Text(
                                        text = department,
                                        fontSize = (12 * divideAndRound(screenWidthValue)).sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size((80 * divideAndRound(screenWidthValue)).dp)
                                    .border(
                                        width = (3 * divideAndRound(screenWidthValue)).dp,
                                        color = Color.White,
                                        shape = CircleShape
                                    )
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.anhtrang),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height((16 * divideAndRound(screenWidthValue)).dp))

                        // Price info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            PriceInfoItem(
                                label = stringResource(id = R.string.exam_price),
                                amount = formatNumber(examPrice),
                                scale = divideAndRound(screenWidthValue)
                            )

                            Spacer(modifier = Modifier.width((12 * divideAndRound(screenWidthValue)).dp))

                            PriceInfoItem(
                                label = stringResource(id = R.string.deposit),
                                amount = formatNumber(tienCoc),
                                scale = divideAndRound(screenWidthValue)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height((20 * divideAndRound(screenWidthValue)).dp))

            // Date display
            ngayKham = date
            Card(
                modifier = Modifier
                    .padding(horizontal = (16 * divideAndRound(screenWidthValue)).dp)
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape((12 * divideAndRound(screenWidthValue)).dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(id = R.color.teal_700).copy(alpha = 0.1f)
                ),
                elevation = CardDefaults.cardElevation((2 * divideAndRound(screenWidthValue)).dp)
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = (16 * divideAndRound(screenWidthValue)).dp,
                        vertical = (12 * divideAndRound(screenWidthValue)).dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.baseline_calendar_today_24),
//                        contentDescription = null,
//                        tint = colorResource(id = R.color.teal_700),
//                        modifier = Modifier.size((20 * divideAndRound(screenWidthValue)).dp)
//                    )
                    Spacer(modifier = Modifier.width((8 * divideAndRound(screenWidthValue)).dp))
                    Text(
                        text = "${stringResource(id = R.string.appointment_date)}: $date",
                        fontWeight = FontWeight.Bold,
                        fontSize = (16 * divideAndRound(screenWidthValue)).sp,
                        color = colorResource(id = R.color.teal_700)
                    )
                }
            }

            Spacer(modifier = Modifier.height((16 * divideAndRound(screenWidthValue)).dp))

            TimeCard(
                doctorId = doctor_id,
                selectedDate = date,
                onTimeSelected = { time ->
                    selectedTime = time
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val datein = LocalDate.parse(date, inputFormatter)
                    val dateout: String = datein.format(outputFormatter)
                    navController.navigate("PaymentScreen/$doctor_id/$dateout/$selectedTime")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = (16 * divideAndRound(screenWidthValue)).dp)
                    .height((56 * divideAndRound(screenWidthValue)).dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.darkblue),
                    disabledContainerColor = Color(0xFFE0E0E0)
                ),
                shape = RoundedCornerShape((16 * divideAndRound(screenWidthValue)).dp),
                enabled = selectedTime != null,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = (6 * divideAndRound(screenWidthValue)).dp
                )
            ) {
                Text(
                    text = stringResource(id = R.string.confirm),
                    fontSize = (16 * divideAndRound(screenWidthValue)).sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height((16 * divideAndRound(screenWidthValue)).dp))
        }
    }
}

@Composable
fun PriceInfoItem(label: String, amount: String, scale: Float) {
    Column(
        modifier = Modifier
            .background(
                Color.White.copy(alpha = 0.15f),
                RoundedCornerShape((10 * scale).dp)
            )
            .padding(horizontal = (12 * scale).dp, vertical = (8 * scale).dp)
    ) {
        Text(
            text = label,
            fontSize = (11 * scale).sp,
            color = Color.White.copy(alpha = 0.8f),
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height((4 * scale).dp))
        Text(
            text = "$amount VNĐ",
            fontSize = (14 * scale).sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
