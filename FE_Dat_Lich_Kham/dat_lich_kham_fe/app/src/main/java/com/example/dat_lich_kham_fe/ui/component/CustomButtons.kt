package com.example.dat_lich_kham_fe.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.util.PersistentCookieJar
import com.example.dat_lich_kham_fe.util.divideAndRound

@Composable
fun CustomButtons(
    icon: Painter,
    text: String,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier,
    onNavClicked: () -> Unit = {},
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value

    Button(
        onClick = {
            onNavClicked()
            if(text == "Làm mới tin"){

            }
        },
        modifier = modifier
            .clip(RoundedCornerShape((50 * divideAndRound(screenWidthValue)).dp))
            .wrapContentHeight() // Cho phép button tự động điều chỉnh chiều cao
            .height((60 * divideAndRound(screenWidthValue)).dp), // Tăng chiều cao để có thể chứa 2 dòng

        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF005BAC) else Color.White,
            contentColor = if (isSelected) Color.White else Color(0xFF005BAC)
        ),
        border = if (!isSelected) BorderStroke((1 * divideAndRound(screenWidthValue)).dp, Color(0xFF005BAC)) else null
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = (4 * divideAndRound(screenWidthValue)).dp)
        ) {
            Icon(
                painter = icon,
                contentDescription = text,
                modifier = Modifier.size((24 * divideAndRound(screenWidthValue)).dp),
                tint = if(isSelected) Color.White else Color(0xFF0856A8)
            )
            Spacer(modifier = Modifier.width((8 * divideAndRound(screenWidthValue)).dp))
            Text(
                text = text,
                fontSize = (14 * divideAndRound(screenWidthValue)).sp, // Giảm font size một chút
                fontWeight = FontWeight.Bold,
                color = if(isSelected) Color.White else Color(0xFF0856A8),
                textAlign = TextAlign.Center,
                maxLines = 2, // Cho phép tối đa 2 dòng
                overflow = TextOverflow.Ellipsis, // Thêm "..." nếu vẫn quá dài
                modifier = Modifier.weight(1f) // Cho text chiếm hết không gian còn lại
            )
        }
    }
}

@Composable
fun ButtonGrid(navController: NavController){
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value

    val context = LocalContext.current
    val cookieJar = remember { PersistentCookieJar(context) }

    var tokenExists by remember { mutableStateOf<Boolean?>(null) }
    var isUpdated by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(Unit) {
        tokenExists = cookieJar.hasToken()
        isUpdated = cookieJar.isInfoUpdated()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = (16 * divideAndRound(screenWidthValue)).dp),
        horizontalArrangement = Arrangement.spacedBy((8 * divideAndRound(screenWidthValue)).dp)
    ) {
        CustomButtons(
            icon = painterResource(id = R.drawable.datlichkham),
            text = stringResource(id = R.string.schedule_an_appointment_title),
            isSelected = true,
            modifier = Modifier.weight(1f),
            onNavClicked = {
                if(tokenExists == true){
                    if(isUpdated == true)
                        navController.navigate("DepartmentScreen")
                    else
                        navController.navigate("InFoScreen")
                }
                else navController.navigate("Login_RegisterScreen")
            }
        )
        CustomButtons(
            icon = painterResource(id = R.drawable.hososuckhoe),
            text = stringResource(id = R.string.health_record_title),
            modifier = Modifier.weight(1f),
            onNavClicked = {
                if(tokenExists == true){
                    if(isUpdated == true)
                        navController.navigate("HealthRecordScreen")
                    else
                        navController.navigate("InFoScreen")
                }
                else navController.navigate("Login_RegisterScreen")
            }
        )
    }

    Spacer(modifier = Modifier.height((12* divideAndRound(screenWidthValue)).dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = (16 * divideAndRound(screenWidthValue)).dp),
        horizontalArrangement = Arrangement.spacedBy((8 * divideAndRound(screenWidthValue)).dp)
    ) {
//        CustomButtons(
//            icon = painterResource(id = R.drawable.meal),
//            text = stringResource(id = R.string.order_a_meal),
//            modifier = Modifier.weight(1f),
//            onNavClicked = {navController.navigate("OrderMealScreen")}
//        )
        CustomButtons(
            icon = painterResource(id = R.drawable.meal),
            text = "Nội trú",
            modifier = Modifier.weight(1f),
            onNavClicked = {navController.navigate("InpatientMealScreen")}
        )
        CustomButtons(
            icon = painterResource(id = R.drawable.bot),
            text = stringResource(id = R.string.chatting_with_ai),
            modifier = Modifier.weight(1f),
            onNavClicked = {navController.navigate("ChatPage")}
        )
    }
}
