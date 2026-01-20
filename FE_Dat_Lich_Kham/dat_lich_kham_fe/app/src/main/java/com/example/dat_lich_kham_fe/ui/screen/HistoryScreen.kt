package com.example.dat_lich_kham_fe.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.ui.component.AppBarView
import com.example.dat_lich_kham_fe.ui.component.AppointmentCard
import com.example.dat_lich_kham_fe.ui.component.HistoryCard
import com.example.dat_lich_kham_fe.util.divideAndRound
import com.example.dat_lich_kham_fe.util.userId
import com.example.dat_lich_kham_fe.viewmodel.AppointmentViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryScreen(
    navController: NavController
) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    val scale = divideAndRound(screenWidthValue)

    val appointmentViewModel = remember { AppointmentViewModel(context) }

    LaunchedEffect(Unit) {
        appointmentViewModel.listappontment(userId, listOf())
    }
    val appointments = appointmentViewModel.appointments.reversed()

    Column() {
        AppBarView(
            title = stringResource(id =R.string.history_title) ,
            color = R.color.white ,
            backgroundColor = R.color.darkblue,
            alignment = Alignment.TopCenter,
            onDeleteNavClicked = {navController.navigate("MainScreen/3")},
            isVisible = true
        )
        if (appointments.size == 0) {
            Column {
                Spacer(modifier = Modifier.height((16 * scale).dp))
                Box(modifier = Modifier.fillMaxSize()) {  // Box to center content
                    androidx.compose.material3.Text(
                        text = "Không có dữ liệu",
                        fontSize = (16 * scale).sp,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            }
        }else {
            LazyColumn {
                items(appointments) { appointment ->
                    HistoryCard(
                        id = appointment.id,
                        doctorId = appointment.doctorId,
                        doctorName = appointment.doctorName,
                        doctorCode = appointment.doctorCode,
                        department = appointment.department,
                        examPrice = appointment.examPrice,
                        examDate = appointment.examDate,
                        examTime = appointment.examTime,
                        status = appointment.status,
                    )
                }
            }
        }
    }

}
