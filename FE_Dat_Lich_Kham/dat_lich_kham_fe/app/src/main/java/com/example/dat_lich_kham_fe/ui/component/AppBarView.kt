package com.example.dat_lich_kham_fe.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.util.divideAndRound

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarView(
    title: String,
    color: Int,
    backgroundColor: Int,
    alignment: Alignment,
    onDeleteNavClicked: () -> Unit = {},
    isVisible: Boolean


) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    if (isVisible) {
        TopAppBar(
            colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                containerColor = colorResource(id = backgroundColor)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height((110 * divideAndRound(screenWidthValue)).dp), // Tăng chiều cao của AppBar

            title = {
                // Dùng Box để tiêu đề ở giữa và biểu tượng thùng rác ở góc phải
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Tiêu đề căn giữa
                    Text(
                        text = title,
                        color = colorResource(id = color),
                        fontSize = (20 * divideAndRound(screenWidthValue)).sp, // Tăng kích thước chữ
                        modifier = Modifier.align(alignment).offset(y = (16 * divideAndRound(screenWidthValue)).dp)
                    )

                    // Biểu tượng thùng rác ở góc phải nếu tiêu đề là "Thông báo"
//                    if (title.contains("Thông báo")) {
//                        IconButton(
//                            onClick = onDeleteNavClicked,
//                            modifier = Modifier.align(Alignment.BottomEnd) // Đặt biểu tượng về góc phải
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Delete,
//                                tint = Color.White,
//                                contentDescription = "Delete Icon"
//                            )
//                        }
//                    }
                    // Biểu tượng ArrowBack nếu tiêu đề là "Thông tin cá nhân"
                    if (title.contains(stringResource(id = R.string.personal_information_title))) {
                        IconButton(
                            onClick = onDeleteNavClicked,
                            modifier = Modifier.align(Alignment.BottomStart).offset(y = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                tint = Color.White,
                                contentDescription = "ArrowBack Icon"
                            )

                        }
                    }
                    //
                    if (title.contains(stringResource(id = R.string.healthindex_title))) {
                        IconButton(
                            onClick = onDeleteNavClicked,
                            modifier = Modifier
                                .align(Alignment.BottomStart).offset(y = 8.dp)
                                .padding((16 * divideAndRound(screenWidthValue)).dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                tint = Color.White,
                                contentDescription = "ArrowBack Icon"
                            )

                        }
                    }
                    //
                    if (title.contains(stringResource(id = R.string.health_record_title))) {
                        IconButton(
                            onClick = onDeleteNavClicked,
                            modifier = Modifier.align(Alignment.BottomStart).offset(y = (8 * divideAndRound(screenWidthValue)).dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                tint = Color.White,
                                contentDescription = "ArrowBack Icon"
                            )

                        }
                    }
                    //
                    if (title.contains(stringResource(id = R.string.deposit_money_title))) {
                        IconButton(
                            onClick = onDeleteNavClicked,
                            modifier = Modifier.align(Alignment.BottomStart).offset(y = (8 * divideAndRound(screenWidthValue)).dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                tint = Color.White,
                                contentDescription = "ArrowBack Icon"
                            )

                        }
                    }
                    //
                    if (title.contains(stringResource(R.string.schedule_an_appointment_title))) {
                        IconButton(
                            onClick = onDeleteNavClicked,
                            modifier = Modifier.align(Alignment.BottomStart).offset(y = (8 * divideAndRound(screenWidthValue)).dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                tint = Color.White,
                                contentDescription = "ArrowBack Icon"
                            )

                        }
                    }
                    //
                    if (title.contains(stringResource(id = R.string.choose_a_doctor_title))) {
                        IconButton(
                            onClick = onDeleteNavClicked,
                            modifier = Modifier.align(Alignment.BottomStart).offset(y = (8 * divideAndRound(screenWidthValue)).dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                tint = Color.White,
                                contentDescription = "ArrowBack Icon"
                            )

                        }
                    }

                    if (title.contains(stringResource(id = R.string.appointment_title))) {
                        IconButton(
                            onClick = onDeleteNavClicked,
                            modifier = Modifier.align(Alignment.BottomStart).offset(y = (8 * divideAndRound(screenWidthValue)).dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                tint = Color.Black,
                                contentDescription = "ArrowBack Icon"
                            )

                        }
                    }

                    if (title.contains("Nội dung chi tiết")) {
                        IconButton(
                            onClick = onDeleteNavClicked,
                            modifier = Modifier.align(Alignment.BottomStart).offset(y = (8 * divideAndRound(screenWidthValue)).dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                tint = Color.White,
                                contentDescription = "ArrowBack Icon"
                            )

                        }
                    }
                    //
                    if (title.contains(stringResource(id = R.string.payment_tile))) {
                        IconButton(
                            onClick = onDeleteNavClicked,
                            modifier = Modifier.align(Alignment.BottomStart).offset(y = (8 * divideAndRound(screenWidthValue)).dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                tint = Color.White,
                                contentDescription = "ArrowBack Icon"
                            )

                        }
                    }
                    //
                    if (title.contains(stringResource(id =R.string.history_title))) {
                        IconButton(
                            onClick = onDeleteNavClicked,
                            modifier = Modifier.align(Alignment.BottomStart).offset(y = (8 * divideAndRound(screenWidthValue)).dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                tint = Color.White,
                                contentDescription = "ArrowBack Icon"
                            )

                        }
                    }
                    //
                    if (title.contains(stringResource(id = R.string.medical_examination_title))) {
                        IconButton(
                            onClick = onDeleteNavClicked,
                            modifier = Modifier.align(Alignment.BottomStart).offset(y = (8 * divideAndRound(screenWidthValue)).dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                tint = Color.Black,
                                contentDescription = "ArrowBack Icon"
                            )

                        }
                    }
                    //
                    if (title.contains(stringResource(id = R.string.examination_result_title))) {
                        IconButton(
                            onClick = onDeleteNavClicked,
                            modifier = Modifier.align(Alignment.BottomStart).offset(y = (8 * divideAndRound(screenWidthValue)).dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                tint = Color.White,
                                contentDescription = "ArrowBack Icon"
                            )

                        }
                    }
                    //
                    if (title.contains("Trò chuyện cùng AI")) {
                        IconButton(
                            onClick = onDeleteNavClicked,
                            modifier = Modifier.align(Alignment.BottomStart).offset(y = (8 * divideAndRound(screenWidthValue)).dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                tint = Color.White,
                                contentDescription = "ArrowBack Icon"
                            )

                        }
                    }
                    //
                    if (title.contains("Đổi mật khẩu")) {
                        IconButton(
                            onClick = onDeleteNavClicked,
                            modifier = Modifier.align(Alignment.BottomStart).offset(y = (8 * divideAndRound(screenWidthValue)).dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                tint = Color.White,
                                contentDescription = "ArrowBack Icon"
                            )

                        }
                    }
                    //
                    if (title.contains("Thông tin người dùng")) {
                        IconButton(
                            onClick = onDeleteNavClicked,
                            modifier = Modifier.align(Alignment.BottomStart).offset(y = (8 * divideAndRound(screenWidthValue)).dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                tint = Color.White,
                                contentDescription = "ArrowBack Icon"
                            )

                        }
                    }
                    //
                }
            }
        )
    }
}
