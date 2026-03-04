package com.example.dat_lich_kham_fe.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.ui.component.AppBarView
import com.example.dat_lich_kham_fe.util.UserLocalStore
import com.example.dat_lich_kham_fe.util.divideAndRound
import com.example.dat_lich_kham_fe.viewmodel.InpatientViewModel
import com.example.dat_lich_kham_fe.viewmodel.MealViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalAnimationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InpatientMealScreen(navController: NavController) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val scale = divideAndRound(screenWidth.value)

    val userStore = remember { UserLocalStore(context) }
    var userId by remember { mutableIntStateOf(0) }

    val inpatientViewModel = remember { InpatientViewModel(context) }
    val mealViewModel = remember { MealViewModel(context) }

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Nội trú hiện tại", "Lịch sử nội trú")

    // Get current inpatient info
    val currentInpatient by inpatientViewModel.currentInpatient.collectAsState()

    LaunchedEffect(Unit) {
        val user = userStore.getUser()
        userId = user?.Id ?: 0
        if (userId > 0) {
            inpatientViewModel.getCurrentInpatient(userId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
        // App Bar with gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1976D2),
                            Color(0xFF2196F3)
                        )
                    )
                )
        ) {
            AppBarView(
                title = "Quản lý nội trú",
                color = R.color.white,
                backgroundColor = android.R.color.transparent,
                alignment = Alignment.Center,
                isVisible = true,
                onDeleteNavClicked = { navController.popBackStack() }
            )
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.White,
            contentColor = Color(0xFF2196F3),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = (2 * scale).dp),
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTabIndex])
                        .height((3 * scale).dp),
                    color = Color(0xFF2196F3)
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    modifier = Modifier.height((56 * scale).dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (index == 0) R.drawable.baseline_person_24
                                else R.drawable.healthrecord
                            ),
                            contentDescription = null,
                            modifier = Modifier.size((20 * scale).dp),
                            tint = if (selectedTabIndex == index)
                                Color(0xFF2196F3) else Color(0xFF9E9E9E)
                        )
                        Spacer(modifier = Modifier.width((8 * scale).dp))
                        Text(
                            text = title,
                            fontSize = (15 * scale).sp,
                            fontWeight = if (selectedTabIndex == index)
                                FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == index)
                                Color(0xFF2196F3) else Color(0xFF757575)
                        )
                    }
                }
            }
        }

        // Tab Content with animation
        AnimatedContent(
            targetState = selectedTabIndex,
            transitionSpec = {
                fadeIn() + slideInHorizontally() with
                        fadeOut() + slideOutHorizontally()
            }
        ) { targetTab ->
            when (targetTab) {
                0 -> CurrentInpatientTab(
                    currentInpatient = currentInpatient,
                    userId = userId,
                    mealViewModel = mealViewModel,
                    inpatientViewModel = inpatientViewModel,
                    scale = scale,
                    navController = navController
                )
                1 -> InpatientHistoryTab(
                    userId = userId,
                    inpatientViewModel = inpatientViewModel,
                    scale = scale
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CurrentInpatientTab(
    currentInpatient: com.example.dat_lich_kham_fe.data.model.InpatientItem?,
    userId: Int,
    mealViewModel: MealViewModel,
    inpatientViewModel: InpatientViewModel,
    scale: Float,
    navController: NavController
) {
    LaunchedEffect(currentInpatient) {
        currentInpatient?.let {
            mealViewModel.fetchMealStatus(it.id)
        }
    }

    when {
        currentInpatient == null -> {
            // Not admitted - Empty state with beautiful design
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding((20 * scale).dp),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateCard(
                    icon = R.drawable.baseline_person_24,
                    title = "Bạn chưa nội trú",
                    description = "Thông tin nội trú và suất ăn sẽ hiển thị tại đây khi bạn được nhập viện",
                    scale = scale
                )
            }
        }
        else -> {
            // Admitted - Show meal management with beautiful design
            InpatientMealManagement(
                inpatient = currentInpatient,
                mealViewModel = mealViewModel,
                scale = scale,
                navController = navController
            )
        }
    }
}

@Composable
fun EmptyStateCard(
    icon: Int,
    title: String,
    description: String,
    scale: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = (8 * scale).dp,
                shape = RoundedCornerShape((20 * scale).dp)
            ),
        shape = RoundedCornerShape((20 * scale).dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding((32 * scale).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size((100 * scale).dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size((50 * scale).dp)
                )
            }

            Spacer(modifier = Modifier.height((24 * scale).dp))

            Text(
                text = title,
                fontSize = (20 * scale).sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height((12 * scale).dp))

            Text(
                text = description,
                fontSize = (14 * scale).sp,
                color = Color(0xFF757575),
                textAlign = TextAlign.Center,
                lineHeight = (20 * scale).sp
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InpatientMealManagement(
    inpatient: com.example.dat_lich_kham_fe.data.model.InpatientItem,
    mealViewModel: MealViewModel,
    scale: Float,
    navController: NavController
) {
    val mealStatus by mealViewModel.mealStatus.collectAsState()
    val mealHistory by mealViewModel.mealHistory.collectAsState()
    val isLoading by mealViewModel.isLoading.collectAsState()
    val error by mealViewModel.error.collectAsState()
    val successMessage by mealViewModel.successMessage.collectAsState()

    // Fetch data
    LaunchedEffect(inpatient.id) {
        mealViewModel.fetchMealStatus(inpatient.id)
        mealViewModel.fetchMealHistory(inpatient.id)
    }

    // Show messages
    LaunchedEffect(error) {
        error?.let {
            // TODO: Show snackbar or toast
            mealViewModel.clearMessages()
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            // TODO: Show snackbar or toast
            mealViewModel.clearMessages()
        }
    }

    if (isLoading && mealStatus == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF2196F3))
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding((16 * scale).dp),
            verticalArrangement = Arrangement.spacedBy((16 * scale).dp)
        ) {
            // Inpatient Info Card
            item {
                InpatientInfoCard(
                    inpatient = inpatient,
                    scale = scale
                )
            }

            // Current Meal Status Card
            item {
                CurrentMealStatusCard(
                    mealStatus = mealStatus,
                    inpatientId = inpatient.id,
                    mealViewModel = mealViewModel,
                    scale = scale,
                    navController = navController
                )
            }

            // Week Timeline (if active cycle)
            mealStatus?.currentCycle?.let { cycle ->
                item {
                    WeekTimelineCard(
                        cycle = cycle,
                        scale = scale
                    )
                }
            }

            // Action Buttons
            item {
                MealActionButtons(
                    mealStatus = mealStatus,
                    inpatientId = inpatient.id,
                    mealViewModel = mealViewModel,
                    scale = scale,
                    navController = navController
                )
            }

            // Meal History
            if (mealHistory.isNotEmpty()) {
                item {
                    Text(
                        text = "Lịch sử chu kỳ",
                        fontSize = (18 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121),
                        modifier = Modifier.padding(vertical = (8 * scale).dp)
                    )
                }

                items(mealHistory) { cycle ->
                    MealCycleHistoryCard(
                        cycle = cycle,
                        scale = scale
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InpatientInfoCard(
    inpatient: com.example.dat_lich_kham_fe.data.model.InpatientItem,
    scale: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = (4 * scale).dp,
                shape = RoundedCornerShape((16 * scale).dp)
            ),
        shape = RoundedCornerShape((16 * scale).dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding((20 * scale).dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_person_24),
                    contentDescription = null,
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size((24 * scale).dp)
                )
                Spacer(modifier = Modifier.width((12 * scale).dp))
                Text(
                    text = "Thông tin nội trú",
                    fontSize = (18 * scale).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
            }

            Spacer(modifier = Modifier.height((16 * scale).dp))
            Divider(color = Color(0xFFE0E0E0))
            Spacer(modifier = Modifier.height((16 * scale).dp))

            InfoRow("Họ tên", inpatient.fullname, scale)
            Spacer(modifier = Modifier.height((12 * scale).dp))
            InfoRow("Địa chỉ", inpatient.address, scale)
            Spacer(modifier = Modifier.height((12 * scale).dp))

            inpatient.admissionDate?.let {
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                InfoRow("Ngày nhập viện", it.toString(), scale)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CurrentMealStatusCard(
    mealStatus: com.example.dat_lich_kham_fe.data.model.MealStatusResponse?,
    inpatientId: Int,
    mealViewModel: MealViewModel,
    scale: Float,
    navController: NavController
) {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", Locale("vi", "VN"))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = (6 * scale).dp,
                shape = RoundedCornerShape((16 * scale).dp)
            ),
        shape = RoundedCornerShape((16 * scale).dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                mealStatus == null || !mealStatus.canEat -> Color(0xFFFFF3E0)
                mealStatus.isSkippedToday -> Color(0xFFFFF59D)
                else -> Color(0xFFE8F5E9)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding((20 * scale).dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Suất ăn hôm nay",
                        fontSize = (14 * scale).sp,
                        color = Color(0xFF757575)
                    )
                    Text(
                        text = today.format(formatter),
                        fontSize = (16 * scale).sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF212121)
                    )
                }

                Box(
                    modifier = Modifier
                        .size((50 * scale).dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                mealStatus == null || !mealStatus.canEat -> Color(0xFFFF9800)
                                mealStatus.isSkippedToday -> Color(0xFFFFC107)
                                else -> Color(0xFF4CAF50)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(
                            id = when {
                                mealStatus == null || !mealStatus.canEat -> R.drawable.baseline_restaurant_menu_24
                                mealStatus.isSkippedToday -> R.drawable.meal
                                else -> R.drawable.refresh
                            }
                        ),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size((28 * scale).dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height((16 * scale).dp))

            // Status
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape((12 * scale).dp))
                    .background(Color.White.copy(alpha = 0.7f))
                    .padding((16 * scale).dp)
            ) {
                Column {
                    Text(
                        text = "Trạng thái",
                        fontSize = (12 * scale).sp,
                        color = Color(0xFF757575)
                    )
                    Spacer(modifier = Modifier.height((4 * scale).dp))
                    Text(
                        text = when {
                            mealStatus == null -> "Chưa đăng ký suất ăn"
                            mealStatus.isSkippedToday -> "Cắt cơm - Nhận ngũ cốc & sữa"
                            mealStatus.canEat -> "Được phục vụ cơm"
                            else -> mealStatus.status
                        },
                        fontSize = (16 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            mealStatus == null || !mealStatus.canEat -> Color(0xFFE65100)
                            mealStatus.isSkippedToday -> Color(0xFFF57C00)
                            else -> Color(0xFF2E7D32)
                        }
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeekTimelineCard(
    cycle: com.example.dat_lich_kham_fe.data.model.MealSubscriptionCycle,
    scale: Float
) {
    val today = LocalDate.now()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = (4 * scale).dp,
                shape = RoundedCornerShape((16 * scale).dp)
            ),
        shape = RoundedCornerShape((16 * scale).dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding((20 * scale).dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.lichsudatkham),
                    contentDescription = null,
                    tint = Color(0xFF9C27B0),
                    modifier = Modifier.size((24 * scale).dp)
                )
                Spacer(modifier = Modifier.width((12 * scale).dp))
                Text(
                    text = "Chu kỳ tuần này (T2-T6)",
                    fontSize = (16 * scale).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
            }

            Spacer(modifier = Modifier.height((20 * scale).dp))

            // Timeline
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(
                    DayOfWeek.MONDAY to "T2",
                    DayOfWeek.TUESDAY to "T3",
                    DayOfWeek.WEDNESDAY to "T4",
                    DayOfWeek.THURSDAY to "T5",
                    DayOfWeek.FRIDAY to "T6"
                ).forEach { (dayOfWeek, label) ->
                    DayIndicator(
                        label = label,
                        isToday = today.dayOfWeek == dayOfWeek,
                        isPast = today.dayOfWeek.value > dayOfWeek.value,
                        scale = scale
                    )
                }
            }

            Spacer(modifier = Modifier.height((16 * scale).dp))

            // Cycle info
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape((8 * scale).dp))
                    .background(Color(0xFFF3E5F5))
                    .padding((12 * scale).dp)
            ) {
                Text(
                    text = "Chu kỳ ${cycle.cycleNumber} • Đã dùng: ${cycle.actualMealDays}/${cycle.expectedMealDays} ngày",
                    fontSize = (13 * scale).sp,
                    color = Color(0xFF6A1B9A),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun DayIndicator(
    label: String,
    isToday: Boolean,
    isPast: Boolean,
    scale: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size((40 * scale).dp)
                .clip(CircleShape)
                .background(
                    when {
                        isToday -> Color(0xFF2196F3)
                        isPast -> Color(0xFF4CAF50)
                        else -> Color(0xFFE0E0E0)
                    }
                )
                .border(
                    width = if (isToday) (2 * scale).dp else 0.dp,
                    color = if (isToday) Color(0xFF1976D2) else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = (14 * scale).sp,
                fontWeight = FontWeight.Bold,
                color = when {
                    isToday || isPast -> Color.White
                    else -> Color(0xFF9E9E9E)
                }
            )
        }

        if (isToday) {
            Spacer(modifier = Modifier.height((4 * scale).dp))
            Box(
                modifier = Modifier
                    .size((6 * scale).dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2196F3))
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MealActionButtons(
    mealStatus: com.example.dat_lich_kham_fe.data.model.MealStatusResponse?,
    inpatientId: Int,
    mealViewModel: MealViewModel,
    scale: Float,
    navController: NavController
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy((12 * scale).dp)
    ) {
        if (mealStatus?.currentCycle == null) {
            // Register button
            Button(
                onClick = {
                    mealViewModel.registerMeal(inpatientId) { cycleId ->
                        navController.navigate("MealPaymentScreen/$cycleId/$inpatientId")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height((56 * scale).dp)
                    .shadow(
                        elevation = (4 * scale).dp,
                        shape = RoundedCornerShape((14 * scale).dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape((14 * scale).dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_restaurant_menu_24),
                    contentDescription = null,
                    modifier = Modifier.size((22 * scale).dp)
                )
                Spacer(modifier = Modifier.width((12 * scale).dp))
                Text(
                    text = "Đăng ký suất ăn",
                    fontSize = (16 * scale).sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            val cycle = mealStatus.currentCycle
            val today = LocalDate.now()

            // Renew button (show from Friday)
            if (today.dayOfWeek == DayOfWeek.FRIDAY ||
                today.dayOfWeek == DayOfWeek.SATURDAY ||
                today.dayOfWeek == DayOfWeek.SUNDAY) {
                Button(
                    onClick = {
                        mealViewModel.renewMeal(inpatientId)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((56 * scale).dp)
                        .shadow(
                            elevation = (4 * scale).dp,
                            shape = RoundedCornerShape((14 * scale).dp)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    ),
                    shape = RoundedCornerShape((14 * scale).dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.refresh),
                        contentDescription = null,
                        modifier = Modifier.size((22 * scale).dp)
                    )
                    Spacer(modifier = Modifier.width((12 * scale).dp))
                    Text(
                        text = "Gia hạn tuần sau",
                        fontSize = (16 * scale).sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Skip meal button
            if (mealStatus.canEat && !mealStatus.isSkippedToday &&
                today.dayOfWeek != DayOfWeek.SATURDAY &&
                today.dayOfWeek != DayOfWeek.SUNDAY) {
                OutlinedButton(
                    onClick = { mealViewModel.skipMeal(inpatientId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((56 * scale).dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFFF9800)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        (2 * scale).dp,
                        Color(0xFFFF9800)
                    ),
                    shape = RoundedCornerShape((14 * scale).dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_person_24),
                        contentDescription = null,
                        modifier = Modifier.size((22 * scale).dp)
                    )
                    Spacer(modifier = Modifier.width((12 * scale).dp))
                    Text(
                        text = "Cắt cơm hôm nay",
                        fontSize = (16 * scale).sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = "💡 Bạn sẽ nhận ngũ cốc và sữa thay thế",
                    fontSize = (12 * scale).sp,
                    color = Color(0xFF757575),
                    modifier = Modifier.padding(start = (8 * scale).dp)
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MealCycleHistoryCard(
    cycle: com.example.dat_lich_kham_fe.data.model.MealSubscriptionCycle,
    scale: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = (2 * scale).dp,
                shape = RoundedCornerShape((12 * scale).dp)
            ),
        shape = RoundedCornerShape((12 * scale).dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding((16 * scale).dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size((36 * scale).dp)
                            .clip(CircleShape)
                            .background(
                                when (cycle.status) {
                                    "active" -> Color(0xFF4CAF50)
                                    "completed" -> Color(0xFF2196F3)
                                    "pending_payment" -> Color(0xFFFF9800)
                                    else -> Color(0xFF9E9E9E)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${cycle.cycleNumber}",
                            fontSize = (16 * scale).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width((12 * scale).dp))

                    Column {
                        Text(
                            text = "Chu kỳ ${cycle.cycleNumber}",
                            fontSize = (15 * scale).sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF212121)
                        )
                        Text(
                            text = cycle.weekStartDate.toString(),
                            fontSize = (12 * scale).sp,
                            color = Color(0xFF757575)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape((6 * scale).dp))
                        .background(
                            when (cycle.status) {
                                "active" -> Color(0xFFE8F5E9)
                                "completed" -> Color(0xFFE3F2FD)
                                "pending_payment" -> Color(0xFFFFF3E0)
                                else -> Color(0xFFE0E0E0)
                            }
                        )
                        .padding(
                            horizontal = (10 * scale).dp,
                            vertical = (4 * scale).dp
                        )
                ) {
                    Text(
                        text = when (cycle.status) {
                            "active" -> "Đang dùng"
                            "completed" -> "Hoàn thành"
                            "pending_payment" -> "Chờ TT"
                            else -> cycle.status
                        },
                        fontSize = (11 * scale).sp,
                        fontWeight = FontWeight.Medium,
                        color = when (cycle.status) {
                            "active" -> Color(0xFF2E7D32)
                            "completed" -> Color(0xFF1976D2)
                            "pending_payment" -> Color(0xFFE65100)
                            else -> Color(0xFF616161)
                        }
                    )
                }
            }

            if (cycle.status == "completed") {
                Spacer(modifier = Modifier.height((12 * scale).dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatChip(
                        label = "Đã ăn",
                        value = "${cycle.actualMealDays}",
                        color = Color(0xFF4CAF50),
                        scale = scale
                    )
                    StatChip(
                        label = "Đã cắt",
                        value = "${cycle.actualSkipDays}",
                        color = Color(0xFFFF9800),
                        scale = scale
                    )
                }
            }
        }
    }
}

@Composable
fun StatChip(
    label: String,
    value: String,
    color: Color,
    scale: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = (18 * scale).sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = (11 * scale).sp,
            color = Color(0xFF757575)
        )
    }
}

@Composable
fun InfoRow(label: String, value: String, scale: Float) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = (14 * scale).sp,
            color = Color(0xFF757575)
        )
        Text(
            text = value,
            fontSize = (14 * scale).sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF212121),
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InpatientHistoryTab(
    userId: Int,
    inpatientViewModel: InpatientViewModel,
    scale: Float
) {
    val history by inpatientViewModel.inpatientHistory.collectAsState()

    LaunchedEffect(userId) {
        if (userId > 0) {
            inpatientViewModel.getInpatientHistory(userId)
        }
    }

    if (history.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding((20 * scale).dp),
            contentAlignment = Alignment.Center
        ) {
            EmptyStateCard(
                icon = R.drawable.lichsudatkham,
                title = "Chưa có lịch sử",
                description = "Lịch sử các lần nội trú của bạn sẽ được lưu tại đây",
                scale = scale
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding((16 * scale).dp),
            verticalArrangement = Arrangement.spacedBy((12 * scale).dp)
        ) {
            items(history) { inpatient ->
                InpatientHistoryCard(
                    inpatient = inpatient,
                    scale = scale
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InpatientHistoryCard(
    inpatient: com.example.dat_lich_kham_fe.data.model.InpatientItem,
    scale: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = (2 * scale).dp,
                shape = RoundedCornerShape((12 * scale).dp)
            ),
        shape = RoundedCornerShape((12 * scale).dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding((16 * scale).dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = inpatient.fullname,
                        fontSize = (16 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    Spacer(modifier = Modifier.height((4 * scale).dp))
                    Text(
                        text = inpatient.address,
                        fontSize = (13 * scale).sp,
                        color = Color(0xFF757575)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape((6 * scale).dp))
                        .background(
                            when (inpatient.status) {
                                "Đã xuất viện" -> Color(0xFFE3F2FD)
                                else -> Color(0xFFE0E0E0)
                            }
                        )
                        .padding(
                            horizontal = (10 * scale).dp,
                            vertical = (4 * scale).dp
                        )
                ) {
                    Text(
                        text = inpatient.status,
                        fontSize = (11 * scale).sp,
                        fontWeight = FontWeight.Medium,
                        color = when (inpatient.status) {
                            "Đã xuất viện" -> Color(0xFF1976D2)
                            else -> Color(0xFF616161)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height((12 * scale).dp))
            Divider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height((12 * scale).dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                inpatient.admissionDate?.let {
                    Column {
                        Text(
                            text = "Nhập viện",
                            fontSize = (11 * scale).sp,
                            color = Color(0xFF757575)
                        )
                        Text(
                            text = it.toString(),
                            fontSize = (13 * scale).sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF212121)
                        )
                    }
                }

                inpatient.dischargeDate?.let {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Xuất viện",
                            fontSize = (11 * scale).sp,
                            color = Color(0xFF757575)
                        )
                        Text(
                            text = it.toString(),
                            fontSize = (13 * scale).sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF212121)
                        )
                    }
                }
            }
        }
    }
}