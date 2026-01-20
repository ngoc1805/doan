package com.example.nhom1_fe_datnhahang.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nhom1_fe_datnhahang.R
import com.example.nhom1_fe_datnhahang.util.divideAndRound
import com.example.nhom1_fe_datnhahang.viewmodel.QrResultViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableScreen(
    navController: NavController,
    qrResultViewModel: QrResultViewModel = viewModel()
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value

    Column {
        //
        TopAppBar(
            title = {
                Row {
                    //
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "status",
                            tint = Color.Unspecified,
                            modifier = Modifier.size((28 * divideAndRound(screenWidthValue)).dp)
                                .clickable { navController.popBackStack() }
                        )
                    }

                    //
                    Spacer(modifier = Modifier.width((16 * divideAndRound(screenWidthValue)).dp))
                    //
                    Text(
                        text = qrResultViewModel.qrResult,
                        modifier = Modifier.padding(vertical = (16 * divideAndRound(screenWidthValue)).dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = (30 * divideAndRound(screenWidthValue)).sp
                    )

                }
            }
        )
        //
    }
}
