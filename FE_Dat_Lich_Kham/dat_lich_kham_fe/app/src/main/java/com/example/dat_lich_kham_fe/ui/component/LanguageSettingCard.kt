package com.example.dat_lich_kham_fe.ui.component

import android.app.Activity
import android.content.ContextWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dat_lich_kham_fe.util.LanguageLocalStore
import com.example.dat_lich_kham_fe.util.divideAndRound

@Composable
fun LanguageSettingCard(
    navController: NavController,
    onLanguageChanged: (String) -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    val context = LocalContext.current

    val languageStore = LanguageLocalStore(context)
    var isEnglish by remember { mutableStateOf(false) }
    var currentLanguage by remember { mutableStateOf("vi") }
    var showDialog by remember { mutableStateOf(false) }
    var pendingLanguage by remember { mutableStateOf("") }

    // Load saved language preference
    LaunchedEffect(Unit) {
        currentLanguage = languageStore.getLanguage()
        isEnglish = currentLanguage == "en"
    }


    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                isEnglish = currentLanguage == "en"
            },
            title = {
                Text(
                    text = if (currentLanguage == "en") "Language Changed" else "Ngôn ngữ đã được thay đổi",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (currentLanguage == "en")
                        "Language has been saved. Please exit and restart the app to apply the new language."
                    else
                        "Ngôn ngữ đã được lưu. Vui lòng thoát và khởi động lại ứng dụng để áp dụng ngôn ngữ mới."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        (context as? Activity)?.finishAffinity()
                    }
                ) {
                    Text(
                        text = if (currentLanguage == "en") "Exit App" else "Thoát ứng dụng",
                        color = Color(0xFF2196F3)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        isEnglish = currentLanguage == "en"
                    }
                ) {
                    Text(
                        text = if (currentLanguage == "en") "Cancel" else "Hủy",
                        color = Color.Gray
                    )
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp, vertical = 0.dp),
        elevation = CardDefaults.cardElevation((4 * divideAndRound(screenWidthValue)).dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding((16 * divideAndRound(screenWidthValue)).dp)
                .fillMaxWidth()
        ) {
            // Language icon
            Box(
                modifier = Modifier
                    .size((32 * divideAndRound(screenWidthValue)).dp)
                    .background(
                        color = Color(0xFFE3F2FD),
                        shape = RoundedCornerShape((8 * divideAndRound(screenWidthValue)).dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🌐",
                    fontSize = (16 * divideAndRound(screenWidthValue)).sp
                )
            }

            Spacer(modifier = Modifier.width((16 * divideAndRound(screenWidthValue)).dp))

            // Language title
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isEnglish) "Language" else "Ngôn ngữ",
                    fontSize = (16 * divideAndRound(screenWidthValue)).sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height((2 * divideAndRound(screenWidthValue)).dp))
                Text(
                    text = if (isEnglish) "English" else "Tiếng Việt",
                    fontSize = (12 * divideAndRound(screenWidthValue)).sp,
                    color = Color.Gray
                )
            }

            // Custom toggle switch
            Box(
                modifier = Modifier
                    .width((50 * divideAndRound(screenWidthValue)).dp)
                    .height((26 * divideAndRound(screenWidthValue)).dp)
                    .background(
                        color = if (isEnglish) Color(0xFF2196F3) else Color(0xFFBDBDBD),
                        shape = CircleShape
                    )
                    .clickable {
                        val newLanguage = if (isEnglish) "vi" else "en"

                        if (newLanguage != currentLanguage) {
                            isEnglish = !isEnglish
                            pendingLanguage = newLanguage

                            // Save language preference ngay láº­p tá»©c
                            languageStore.saveLanguage(newLanguage)


                            showDialog = true

                            // Call callback
                            onLanguageChanged(newLanguage)
                        }
                    }
                    .padding((2 * divideAndRound(screenWidthValue)).dp),
                contentAlignment = if (isEnglish) Alignment.CenterEnd else Alignment.CenterStart
            ) {
                Box(
                    modifier = Modifier
                        .size((22 * divideAndRound(screenWidthValue)).dp)
                        .background(Color.White, CircleShape)
                )
            }
        }
    }
}


private tailrec fun android.content.Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
