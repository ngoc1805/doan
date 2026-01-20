package ui.compoment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import util.formatDate

@Composable
fun HistoryOfServiceRoomCard(
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
) {
    Card(
        elevation = 6.dp,
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color(0xFFF1F8E9),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ){
        Column(
            modifier = Modifier
                .background(Color(0xFFF1F8E9))
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Text(
                    text = "Mã lịch hẹn: ",
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
                Text(
                    text = appointmentId.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1976D2)
                )
            }
            Text(
                text = userName,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF1976D2)
            )
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Text(
                    text = "Giới tính: ",
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
                Text(
                    text = gender,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Ngày sinh: ",
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
                Text(
                    text = formatDate(birthDate),
                    fontWeight = FontWeight.Normal
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Text(
                    text = "Quê quán: ",
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
                Text(
                    text = homeTown,
                    fontWeight = FontWeight.Normal
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Text(
                    text = "CCCD: ",
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
                Text(
                    text = cccd,
                    fontWeight = FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ngày khám: ",
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF388E3C)
                )
                Text(
                    text = formatDate(examDate.toString()),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF388E3C)
                )
            }
        }
    }
}