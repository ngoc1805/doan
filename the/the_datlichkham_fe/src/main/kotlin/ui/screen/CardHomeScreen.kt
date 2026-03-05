package ui.screen

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
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import card.CardSession
import kotlinx.coroutines.delay
import ui.theme.*

class CardHomeScreen : Screen {
    @Composable
    override fun Content() {
        CardHomeContent()
    }
}

@Composable
private fun CardHomeContent() {
    val navigator = LocalNavigator.currentOrThrow

    // Watchdog: kiểm tra thẻ mỗi 1.5s, nếu bị rút → về WelcomeScreen
    var cardAlive by remember { mutableStateOf(CardSession.isConnected) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1500)
            val alive = CardSession.checkAlive()
            cardAlive = alive
            if (!alive) {
                navigator.popUntilRoot()
                break
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition()
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.4f, targetValue = 0.85f,
        animationSpec = infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse)
    )

    Box(modifier = Modifier.fillMaxSize().background(SurfaceWhite)) {
        Column(modifier = Modifier.fillMaxSize()) {

            TopBar(CardSession.terminalName, onDisconnect = {
                CardSession.disconnect()
                navigator.popUntilRoot()
            })

            Row(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                // Left – card visual
                Column(
                    modifier = Modifier.weight(1.2f).fillMaxHeight(),
                    verticalArrangement  = Arrangement.spacedBy(20.dp),
                    horizontalAlignment  = Alignment.CenterHorizontally,
                ) {
                    CardVisual(glowAlpha)
                    StatusBadge(cardAlive)
                    AtrCard(CardSession.atr)
                }

                // Right – functions
                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    AidInfoCard()
                    FunctionGrid()
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        Text("v1.0.0", style = MaterialTheme.typography.caption, color = TextGray.copy(alpha = 0.4f),
            modifier = Modifier.align(Alignment.BottomStart).padding(12.dp))
    }
}

@Composable
private fun TopBar(terminalName: String, onDisconnect: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().height(64.dp)
            .background(Brush.horizontalGradient(listOf(GradientStart, GradientEnd)))
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Filled.LocalHospital, null, tint = Color.White, modifier = Modifier.size(26.dp))
                Column {
                    Text("Thẻ Đặt Lịch Khám", style = MaterialTheme.typography.h6,
                        color = Color.White, fontWeight = FontWeight.Bold)
                    Text(terminalName.ifEmpty { "SmartCard Terminal" },
                        style = MaterialTheme.typography.caption, color = Color.White.copy(alpha = 0.72f))
                }
            }
            OutlinedButton(
                onClick  = onDisconnect,
                shape    = RoundedCornerShape(10.dp),
                border   = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                colors   = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                modifier = Modifier.height(36.dp),
            ) {
                Icon(Icons.Filled.PowerSettingsNew, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Ngắt kết nối", style = MaterialTheme.typography.caption, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun CardVisual(glowAlpha: Float) {
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth().aspectRatio(1.3f).padding(8.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val r = minOf(size.width, size.height) * 0.42f
            drawCircle(AccentGreen.copy(alpha = glowAlpha * 0.12f), r * 1.35f, center)
            drawCircle(AccentGreen.copy(alpha = glowAlpha * 0.07f), r * 1.65f, center)
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(0.8f).clip(RoundedCornerShape(24.dp))
                .background(Brush.linearGradient(listOf(AccentTeal, AccentGreen),
                    start = Offset(0f, 0f), end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)))
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Box(modifier = Modifier.padding(bottom = 10.dp).size(52.dp)
                    .clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.CreditScore, null, tint = Color.White, modifier = Modifier.size(30.dp))
                }
                Text("Thẻ đã kết nối", style = MaterialTheme.typography.h6,
                    color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text("✓  Kết nối thành công", style = MaterialTheme.typography.body2,
                    color = Color.White.copy(alpha = 0.82f), textAlign = TextAlign.Center)
                Spacer(Modifier.height(16.dp))
                Text("AID  11 22 33 44 55 00", style = MaterialTheme.typography.caption,
                    color = Color.White.copy(alpha = 0.5f), fontFamily = FontFamily.Monospace)
            }
            Icon(Icons.Filled.Contactless, null, tint = Color.White.copy(alpha = 0.22f),
                modifier = Modifier.align(Alignment.TopEnd).padding(14.dp).size(38.dp))
        }
    }
}

@Composable
private fun StatusBadge(alive: Boolean) {
    Surface(color = if (alive) SuccessLight else ErrorLight, shape = RoundedCornerShape(50),
        modifier = Modifier.defaultMinSize(minWidth = 200.dp)) {
        Row(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(if (alive) Icons.Filled.CheckCircle else Icons.Filled.Warning, null,
                tint = if (alive) Success else ErrorRed, modifier = Modifier.size(18.dp))
            Text(if (alive) "Thẻ đang kết nối" else "Thẻ bị rút — đang quay về...",
                style = MaterialTheme.typography.body2,
                color = if (alive) Success else ErrorRed, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun AtrCard(atr: String) {
    Surface(color = SuccessLight, shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
        Row(modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(40.dp).clip(CircleShape).background(Success.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Memory, null, tint = Success, modifier = Modifier.size(20.dp))
            }
            Column {
                Text("ATR (Answer To Reset)", style = MaterialTheme.typography.caption, color = Success)
                Text(atr.ifEmpty { "—" }, style = MaterialTheme.typography.body2, color = TextDark,
                    fontFamily = FontFamily.Monospace, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun AidInfoCard() {
    Surface(color = Color(0xFFE8EAF6), shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(), elevation = 0.dp) {
        Row(modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(PrimaryBlue.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Tag, null, tint = PrimaryBlue, modifier = Modifier.size(22.dp))
            }
            Column {
                Text("Application Identifier (AID)", style = MaterialTheme.typography.caption, color = TextGray)
                Text("11 22 33 44 55 00", style = MaterialTheme.typography.body1, color = TextDark,
                    fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                Text("Thẻ bệnh viện JavaCard", style = MaterialTheme.typography.caption, color = PrimaryBlue)
            }
        }
    }
}

private data class CardFunction(val title: String, val subtitle: String, val icon: ImageVector, val color: Color)

private val cardFunctions = listOf(
    CardFunction("Thông tin", "Xem thông tin thẻ",   Icons.Filled.Person,               PrimaryBlue),
    CardFunction("Số dư",     "Kiểm tra số dư",       Icons.Filled.AccountBalanceWallet, AccentTeal),
    CardFunction("Đổi PIN",   "Cập nhật mã PIN",      Icons.Filled.Lock,                 Color(0xFF7B1FA2)),
    CardFunction("Lịch khám", "Xem lịch đặt khám",   Icons.Filled.CalendarToday,        AccentGreen),
    CardFunction("Nạp tiền",  "Nạp tiền vào thẻ",    Icons.Filled.AttachMoney,          Color(0xFFF57F17)),
)

@Composable
private fun FunctionGrid() {
    SectionCard(title = "Chức năng", icon = Icons.Filled.GridView) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            cardFunctions.chunked(2).forEach { row ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { fn -> FunctionTile(fn, modifier = Modifier.weight(1f)) }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun FunctionTile(fn: CardFunction, modifier: Modifier = Modifier) {
    Surface(onClick = { /* TODO: BE ready */ }, color = fn.color.copy(alpha = 0.08f),
        shape = RoundedCornerShape(14.dp), modifier = modifier, elevation = 0.dp) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(fn.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center) {
                Icon(fn.icon, fn.title, tint = fn.color, modifier = Modifier.size(18.dp))
            }
            Text(fn.title, style = MaterialTheme.typography.body2, color = TextDark, fontWeight = FontWeight.SemiBold)
            Text(fn.subtitle, style = MaterialTheme.typography.caption, color = TextGray)
        }
    }
}

@Composable
private fun SectionCard(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Surface(color = CardWhite, shape = RoundedCornerShape(20.dp), elevation = 3.dp, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(Modifier.size(34.dp).clip(RoundedCornerShape(9.dp)).background(PrimaryBlue.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                }
                Text(title, style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)
            }
            Divider(color = DividerColor)
            content()
        }
    }
}
