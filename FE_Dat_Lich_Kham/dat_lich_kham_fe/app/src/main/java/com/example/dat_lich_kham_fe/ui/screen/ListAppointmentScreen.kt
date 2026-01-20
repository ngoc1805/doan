package com.example.dat_lich_kham_fe.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.ui.component.AppointmentCard
import com.example.dat_lich_kham_fe.util.divideAndRound
import com.example.dat_lich_kham_fe.util.userId
import com.example.dat_lich_kham_fe.viewmodel.AppointmentViewModel
import org.w3c.dom.Text

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListAppointmentScreen(
    navController: NavController
) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    val scale = divideAndRound(screenWidthValue)

    val appointmentViewModel = remember { AppointmentViewModel(context) }

    LaunchedEffect(Unit) {
        appointmentViewModel.listappontment(userId, listOf("Đã lên lịch","Đang thanh toán", "Đã thanh toán"))
    }
    val appointments = appointmentViewModel.appointments

    if(appointments.isEmpty()){
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.there_is_no_appointment_yet),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    } else {
        LazyColumn {
            items(appointments) { appointment ->
                AppointmentCard(
                    navController = navController,
                    id = appointment.id,
                    doctorId = appointment.doctorId,
                    doctorName = appointment.doctorName,
                    doctorCode = appointment.doctorCode,
                    department = appointment.department,
                    examPrice = appointment.examPrice,
                    examDate = appointment.examDate,
                    examTime = appointment.examTime,
                    status = appointment.status,
                    onClicked = {
                        navController.navigate(
                            "MedicalExaminationScreen/${appointment.id}/${appointment.doctorId}/${appointment.doctorName}/${appointment.doctorCode}/${appointment.department}/${appointment.examPrice}/${appointment.examDate}/${appointment.examTime}/${appointment.status}"
                        )
                    }
                )
            }
        }
    }

    }

