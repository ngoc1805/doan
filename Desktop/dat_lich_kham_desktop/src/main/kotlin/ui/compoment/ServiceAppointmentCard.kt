package ui.compoment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.Navigator
import ui.screen.doctor.MedicalExaminationScreen
import ui.screen.serviceroom.ServiceExaminationSreen
import ui.screen.serviceroom.nameServices
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun ServiceAppointmentCard(
    id: Int,
    appointmentId: Int,
    userId: Int,
    userName: String,
    gender: String,
    birthDate: String,
    homeTown: String,
    cccd: String,
    examDate: String,
    examTime: String,
    status: String,
    fmctoken: String,
    navigator: Navigator,

){
    val birthYear = birthDate.substring(0, 4).toIntOrNull() ?: 0
    val examYear = examDate.substring(0, 4).toIntOrNull() ?: 0
    val age = examYear - birthYear
    Card(
        elevation = 4.dp,
        backgroundColor = Color(0xFFE3F2FD), // Màu xanh nhạt
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable{
                navigator.push(
                    ServiceExaminationSreen(
                        id = id,
                        appointmentId = appointmentId,
                        userId = userId,
                        userName = userName,
                        gender = gender,
                        birthDate = birthDate,
                        homeTown = homeTown,
                        cccd = cccd,
                        examDate = examDate,
                        examTime =examTime,
                        status = status,
                        fmctoken = fmctoken,
                        nameServiceRoom = nameServices
                    )
                )
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$id",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF0856A8),
                modifier = Modifier.weight(1f)
            )
            Text(
                "$userName",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF0856A8),
                modifier = Modifier.weight(2f)
            )
            Text(
                "$gender",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF0856A8),
                modifier = Modifier.weight(1f)
            )
            Text(
                "$age",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF0856A8),
                modifier = Modifier.weight(1f)
            )
            Text(
                "$homeTown",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF0856A8),
                modifier = Modifier.weight(3f)
            )
        }
    }
}