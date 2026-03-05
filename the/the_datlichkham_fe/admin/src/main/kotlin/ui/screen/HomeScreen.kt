package ui.screen.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import card.CardSession
import kotlinx.coroutines.delay
import ui.theme.*

class HomeScreen : Screen {
    @Composable
    override fun Content() = HomeContent()
}

// Enum Tab quản lý điều hướng
enum class Tab(val label: String, val icon: ImageVector, val color: Color) {
    CREATE("Tạo thẻ mới", Icons.Filled.AddCard, AdminPurple),
    UNLOCK("Mở khóa thẻ", Icons.Filled.LockOpen, AccentTeal),
    PIN("Cấp lại PIN", Icons.Filled.Key, Color(0xFFF57F17)),
}

@Composable
private fun HomeContent() {
    val navigator = LocalNavigator.currentOrThrow
    var tab by remember { mutableStateOf(Tab.CREATE) }
    var cardAlive by remember { mutableStateOf(CardSession.isConnected) }

    // Watchdog kiểm tra thẻ
    LaunchedEffect(Unit) {
        while (true) {
            delay(1500)
            val alive = CardSession.checkAlive()
            cardAlive = alive
            if (!alive) { navigator.popUntilRoot(); break }
        }
    }

    val inf = rememberInfiniteTransition()
    val glow by inf.animateFloat(0.3f, 0.8f, infiniteRepeatable(tween(1300, easing = FastOutSlowInEasing), RepeatMode.Reverse))

    Box(modifier = Modifier.fillMaxSize().background(SurfaceWhite)) {
        Column(Modifier.fillMaxSize()) {
            TopBar(tab, onTabChange = { tab = it }, onDisconnect = {
                CardSession.disconnect(); navigator.popUntilRoot()
            })

            Row(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                // Left Panel: Chứa Card Visual và Status
                Column(modifier = Modifier.weight(0.85f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    CardVisual(glow)
                    StatusBadge(cardAlive)
                    TerminalInfo(CardSession.atr, CardSession.terminalName)
                }

                // Right Panel: Thay đổi nội dung dựa trên Tab đang chọn
                Box(modifier = Modifier.weight(1.5f).fillMaxHeight()) {
                    AnimatedContent(targetState = tab, transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(150)) }) { t ->
                        when (t) {
                            Tab.CREATE -> CreateCardPanel()
                            Tab.UNLOCK -> UnlockCardPanel()
                            Tab.PIN -> ResetPinPanel()
                        }
                    }
                }
            }
        }
        Text("Admin v1.0.0", style = MaterialTheme.typography.caption, color = TextGray.copy(alpha = 0.35f), modifier = Modifier.align(Alignment.BottomStart).padding(12.dp))
    }
}

// --- BẠN GIỮ LẠI CÁC HÀM TopBar, CardVisual, StatusBadge, TerminalInfo ở đây ---