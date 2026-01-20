package ui.compoment

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.Navigator
import ui.screen.doctor.MedicalExaminationScreen

@Composable
fun AppointmentByIdDoctorCard(
    id: Int,
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

    // Animation cho press effect
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.98f else 1f)

    Card(
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                navigator.push(
                    MedicalExaminationScreen(
                        id = id,
                        userId = userId,
                        userName = userName,
                        gender = gender,
                        birthDate = birthDate,
                        homeTown = homeTown,
                        cccd = cccd,
                        examDate = examDate,
                        examTime = examTime,
                        status = status,
                        fmctoken = fmctoken
                    )
                )
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#$id",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = Color(0xFF0856A8),
                modifier = Modifier.weight(1f)
            )
            Text(
                userName,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = Color(0xFF2C3E50),
                modifier = Modifier.weight(2f)
            )
            Text(
                gender,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                color = Color(0xFF7F8C8D),
                modifier = Modifier.weight(1f)
            )
            Text(
                "$age",
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                color = Color(0xFF7F8C8D),
                modifier = Modifier.weight(1f)
            )
            Text(
                examTime,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = Color(0xFF27AE60),
                modifier = Modifier.weight(1f)
            )
        }
    }
}