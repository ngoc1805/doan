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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import card.CardSession
import kotlinx.coroutines.launch
import ui.theme.*

class WelcomeScreen : Screen {
    @Composable
    override fun Content() {
        WelcomeContent()
    }
}

@Composable
private fun WelcomeContent() {
    val navigator = LocalNavigator.currentOrThrow
    val scope     = rememberCoroutineScope()

    var isConnecting by remember { mutableStateOf(false) }
    var errorMsg     by remember { mutableStateOf<String?>(null) }

    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue  = 0.92f, targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse)
    )
    val ringAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.15f, targetValue = 0.4f,
        animationSpec = infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse)
    )
    val spinAngle by infiniteTransition.animateFloat(
        initialValue  = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFF0F7FF), SurfaceWhite, Color(0xFFE8F5E9))))
    ) {
        // Background circles
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(PrimaryBlue.copy(alpha = 0.04f), size.width * 0.55f,
                center = androidx.compose.ui.geometry.Offset(size.width * 0.1f, size.height * 0.15f))
            drawCircle(AccentTeal.copy(alpha = 0.04f), size.width * 0.45f,
                center = androidx.compose.ui.geometry.Offset(size.width * 0.9f, size.height * 0.8f))
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // ── Logo ──────────────────────────────────────────────────
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(PrimaryLight.copy(alpha = ringAlpha * 0.4f), size.minDimension * 0.48f)
                    drawCircle(PrimaryLight.copy(alpha = ringAlpha * 0.2f), size.minDimension * 0.56f)
                }
                if (isConnecting) {
                    Canvas(modifier = Modifier.fillMaxSize(0.88f).rotate(spinAngle)) {
                        drawArc(
                            brush = Brush.sweepGradient(listOf(Color.Transparent, PrimaryLight, Color.Transparent)),
                            startAngle = 0f, sweepAngle = 240f, useCenter = false,
                            topLeft = androidx.compose.ui.geometry.Offset(3.dp.toPx() / 2, 3.dp.toPx() / 2), size = androidx.compose.ui.geometry.Size(size.width - 3.dp.toPx(), size.height - 3.dp.toPx()), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(130.dp)
                        .graphicsLayer(scaleX = pulse, scaleY = pulse)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(GradientStart, GradientEnd)))
                ) {
                    Icon(
                        if (isConnecting) Icons.Filled.Sync else Icons.Filled.CreditCard,
                        null, tint = Color.White,
                        modifier = Modifier.size(56.dp)
                            .let { if (isConnecting) it.rotate(spinAngle) else it }
                    )
                }
            }

            Spacer(Modifier.height(36.dp))

            Text("Thẻ Đặt Lịch Khám",
                style = MaterialTheme.typography.h4, color = TextDark,
                fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text("Hệ thống quản lý thẻ bệnh viện thông minh",
                style = MaterialTheme.typography.body1, color = TextGray, textAlign = TextAlign.Center)

            Spacer(Modifier.height(40.dp))

            // AID badge
            Surface(color = Color(0xFFE8EAF6), shape = RoundedCornerShape(50), elevation = 0.dp) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(Icons.Filled.Tag, null, tint = PrimaryBlue, modifier = Modifier.size(14.dp))
                    Text("AID  11 22 33 44 55 00",
                        style = MaterialTheme.typography.caption, color = PrimaryBlue,
                        fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(40.dp))

            // ── Connect button ─────────────────────────────────────────
            Button(
                onClick = {
                    if (isConnecting) return@Button
                    errorMsg     = null
                    isConnecting = true
                    scope.launch {
                        val result = CardSession.connect()
                        isConnecting = false
                        result.fold(
                            onSuccess = { navigator.push(HomeScreen()) },
                            onFailure = { errorMsg = it.message ?: "Lỗi kết nối không xác định" }
                        )
                    }
                },
                enabled  = !isConnecting,
                modifier = Modifier.width(280.dp).height(54.dp),
                shape    = RoundedCornerShape(16.dp),
                colors   = ButtonDefaults.buttonColors(
                    backgroundColor         = PrimaryBlue,
                    contentColor            = Color.White,
                    disabledBackgroundColor = PrimaryBlue.copy(alpha = 0.6f),
                    disabledContentColor    = Color.White,
                ),
                elevation = ButtonDefaults.elevation(defaultElevation = 6.dp, pressedElevation = 2.dp),
            ) {
                if (isConnecting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(12.dp))
                    Text("Đang kết nối...", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                } else {
                    Icon(Icons.Filled.NearMe, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Kết nối thẻ", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }

            // Error
            AnimatedVisibility(errorMsg != null, enter = fadeIn() + slideInVertically { -it / 2 }, exit = fadeOut()) {
                errorMsg?.let { msg ->
                    Column {
                        Spacer(Modifier.height(20.dp))
                        Surface(color = ErrorLight, shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.widthIn(max = 380.dp), elevation = 0.dp) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(Icons.Filled.ErrorOutline, null, tint = ErrorRed, modifier = Modifier.size(20.dp))
                                Text(msg, style = MaterialTheme.typography.body2, color = TextDark)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Filled.Info, null, tint = TextGray.copy(alpha = 0.5f), modifier = Modifier.size(14.dp))
                Text("Đặt thẻ vào đầu đọc NFC / Chip trước khi bấm kết nối",
                    style = MaterialTheme.typography.caption, color = TextGray.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center)
            }
        }
    }
}