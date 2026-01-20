package com.example.dat_lich_kham_fe.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.dat_lich_kham_fe.data.model.Transaction
import com.example.dat_lich_kham_fe.util.UserLocalStore
import com.example.dat_lich_kham_fe.util.divideAndRound
import com.example.dat_lich_kham_fe.viewmodel.TransactionViewModel
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(navController: NavController) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    val context = LocalContext.current

    val userStore = UserLocalStore(context)
    val viewModel = remember { TransactionViewModel(context) }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        val user = userStore.getUser()
        val userId = user?.Id ?: 0
        if (userId != 0) {
            viewModel.loadTransactionHistory(userId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Lịch sử giao dịch",
                        fontSize = (18 * divideAndRound(screenWidthValue)).sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0B8FAC),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF0B8FAC)
                    )
                }
                uiState.errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding((16 * divideAndRound(screenWidthValue)).dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = uiState.errorMessage ?: "Đã xảy ra lỗi",
                            color = Color.Red,
                            fontSize = (14 * divideAndRound(screenWidthValue)).sp
                        )
                        Spacer(modifier = Modifier.height((16 * divideAndRound(screenWidthValue)).dp))
                        Button(
                            onClick = {
//                                val user = userStore.getUser()
//                                val userId = user?.Id ?: 0
//                                if (userId != 0) {
//                                    viewModel.loadTransactionHistory(userId)
//                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0B8FAC)
                            )
                        ) {
                            Text("Thử lại")
                        }
                    }
                }
                uiState.transactions.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding((16 * divideAndRound(screenWidthValue)).dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Chưa có giao dịch nào",
                            fontSize = (16 * divideAndRound(screenWidthValue)).sp,
                            color = Color.Gray
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding((16 * divideAndRound(screenWidthValue)).dp),
                        verticalArrangement = Arrangement.spacedBy((12 * divideAndRound(screenWidthValue)).dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height((8 * divideAndRound(screenWidthValue)).dp))
                        }
                        items(uiState.transactions) { transaction ->
                            TransactionDetailCard(transaction, screenWidthValue)
                        }
                        item {
                            Spacer(modifier = Modifier.height((16 * divideAndRound(screenWidthValue)).dp))
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionDetailCard(transaction: Transaction, screenWidthValue: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape((12 * divideAndRound(screenWidthValue)).dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation((3 * divideAndRound(screenWidthValue)).dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding((16 * divideAndRound(screenWidthValue)).dp)
        ) {
            // Header với category và số tiền
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = transaction.category,
                    fontSize = (18 * divideAndRound(screenWidthValue)).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C3E50),
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = formatAmount(transaction.amount),
                    fontSize = (18 * divideAndRound(screenWidthValue)).sp,
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.isIncome) Color(0xFF4CAF50) else Color(0xFFE53935)
                )
            }

            Spacer(modifier = Modifier.height((12 * divideAndRound(screenWidthValue)).dp))

            Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)

            Spacer(modifier = Modifier.height((12 * divideAndRound(screenWidthValue)).dp))

            // Thông tin chi tiết
            TransactionInfoRow("ID Giao dịch:", transaction.id.toString(), screenWidthValue)
            Spacer(modifier = Modifier.height((8 * divideAndRound(screenWidthValue)).dp))

            TransactionInfoRow("Loại giao dịch:", transaction.transactionType, screenWidthValue)
            Spacer(modifier = Modifier.height((8 * divideAndRound(screenWidthValue)).dp))

            TransactionInfoRow("Phân loại:", if (transaction.isIncome) "Thu nhập" else "Chi tiêu", screenWidthValue)
            Spacer(modifier = Modifier.height((8 * divideAndRound(screenWidthValue)).dp))

            TransactionInfoRow("Thời gian:", formatDateTime(transaction.transactionTime), screenWidthValue)
            Spacer(modifier = Modifier.height((8 * divideAndRound(screenWidthValue)).dp))

            TransactionInfoRow("Số tiền:", formatAmountDetail(transaction.amount), screenWidthValue)
        }
    }
}

@Composable
fun TransactionInfoRow(label: String, value: String, screenWidthValue: Float) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = (14 * divideAndRound(screenWidthValue)).sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = (14 * divideAndRound(screenWidthValue)).sp,
            color = Color(0xFF2C3E50),
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

fun formatAmount(amount: String): String {
    val numberFormat = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    val cleanAmount = amount.replace("+", "").replace("-", "")
    val sign = if (amount.startsWith("+")) "+" else "-"
    return "$sign${numberFormat.format(cleanAmount.toLongOrNull() ?: 0)} VNĐ"
}

fun formatAmountDetail(amount: String): String {
    val numberFormat = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    val cleanAmount = amount.replace("+", "").replace("-", "")
    return "${numberFormat.format(cleanAmount.toLongOrNull() ?: 0)} VNĐ"
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDateTime(dateTimeString: String): String {
    return try {
        val instant = Instant.parse(dateTimeString)
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss")
            .withZone(ZoneId.of("Asia/Ho_Chi_Minh"))
        formatter.format(instant)
    } catch (e: Exception) {
        dateTimeString
    }
}