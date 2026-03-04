package com.example.dat_lich_kham_fe.chatbot

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.ui.component.AppBarView
import com.example.dat_lich_kham_fe.ui.theme.Purple80
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun ChatPageWithMemory(navController: NavController) {
    val context = LocalContext.current
    val chatViewModel = remember { ChatViewModel(context) }

    val gradientColors = listOf(
        Color(0xFFF5F7FA),
        Color(0xFFE8EAF6),
        Color(0xFFF3E5F5)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(gradientColors))
    ) {
        // AppBar với memory indicator
        Column {
            AppBarView(
                title = "Trò chuyện cùng AI",
                color = R.color.white,
                backgroundColor = R.color.darkblue,
                alignment = Alignment.Center,
                isVisible = true,
                onDeleteNavClicked = { navController.navigate("MainScreen/0") }
            )

            // Memory Context Indicator (ĐÃ SỬA: bỏ sessionId)
            MemoryIndicator(
                hasMemory = chatViewModel.hasMemoryContext
            )
        }

        // Messages area
        MessageList(
            modifier = Modifier.weight(1f),
            messageList = chatViewModel.messageList
        )

        // Message input (ĐÃ XÓA: New Session Button)
        MessageInput(
            onMessageSend = { message ->
                chatViewModel.sendMessage(message)
            }
        )
    }
}

// ĐÃ SỬA: Bỏ parameter sessionId
@Composable
fun MemoryIndicator(hasMemory: Boolean) {
    AnimatedVisibility(
        visible = hasMemory,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF10B981).copy(alpha = 0.1f)
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Memory",
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Bot đang nhớ ngữ cảnh cuộc trò chuyện của bạn",
                    fontSize = 12.sp,
                    color = Color(0xFF10B981),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ĐÃ XÓA: NewSessionButton - không cần nữa vì chỉ có 1 session duy nhất

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInput(onMessageSend: (String) -> Unit) {
    var message by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    val sendButtonScale by animateFloatAsState(
        targetValue = if (message.isNotEmpty()) 1f else 0.8f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "sendButtonScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = "Nhập tin nhắn...",
                        color = Color.Gray.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color(0xFF6366F1)
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (message.isNotEmpty()) {
                            onMessageSend(message)
                            message = ""
                            keyboardController?.hide()
                        }
                    }
                ),
                maxLines = 4
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF8B5CF6),
                                Color(0xFF6366F1)
                            )
                        )
                    )
                    .clickable(
                        enabled = message.isNotEmpty(),
                        onClick = {
                            if (message.isNotEmpty()) {
                                onMessageSend(message)
                                message = ""
                                keyboardController?.hide()
                            }
                        }
                    )
                    .graphicsLayer {
                        scaleX = sendButtonScale
                        scaleY = sendButtonScale
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun MessageList(
    modifier: Modifier = Modifier,
    messageList: List<MessageModel>
) {
    val listState = rememberLazyListState()

    LaunchedEffect(messageList.size) {
        if (messageList.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    if (messageList.isEmpty()) {
        EmptyMessageState(modifier = modifier)
    } else {
        LazyColumn(
            modifier = modifier.padding(horizontal = 8.dp),
            state = listState,
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(
                items = messageList.reversed(),
                key = { it.hashCode() }
            ) { message ->
                AnimatedMessageRow(messageModel = message)
            }
        }
    }
}

@Composable
fun EmptyMessageState(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Purple80.copy(alpha = 0.2f),
                            Purple80.copy(alpha = 0.1f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_question_answer_24),
                contentDescription = "AI Icon",
                tint = Purple80.copy(alpha = alpha),
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Xin chào! 👋",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF374151)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tôi có trí nhớ và sẽ nhớ những gì bạn nói!\nHãy hỏi tôi bất kỳ điều gì bạn muốn biết!",
            fontSize = 16.sp,
            color = Color(0xFF6B7280),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Gợi ý câu hỏi:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF9CA3AF)
            )

            SuggestionChip(text = "Triệu chứng sức khỏe")
            SuggestionChip(text = "Lịch hẹn khám")
            SuggestionChip(text = "Thông tin bác sĩ")
        }
    }
}

@Composable
fun SuggestionChip(text: String) {
    Surface(
        modifier = Modifier.padding(horizontal = 4.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.8f),
        shadowElevation = 2.dp
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 12.sp,
            color = Color(0xFF6B7280)
        )
    }
}

@Composable
fun AnimatedMessageRow(messageModel: MessageModel) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(50)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it / 4 },
            animationSpec = tween(300, easing = EaseOutCubic)
        ) + fadeIn(animationSpec = tween(300))
    ) {
        MessageRow(messageModel = messageModel)
    }
}

@Composable
fun MessageRow(messageModel: MessageModel) {
    val isModel = messageModel.role == "model"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isModel) Arrangement.Start else Arrangement.End
    ) {
        if (isModel) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF8B5CF6),
                                Color(0xFF6366F1)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_question_answer_24),
                    contentDescription = "AI",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
        }

        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(
                topStart = if (isModel) 4.dp else 20.dp,
                topEnd = if (isModel) 20.dp else 4.dp,
                bottomStart = 20.dp,
                bottomEnd = 20.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isModel) {
                    Color.White
                } else {
                    Color(0xFF6366F1)
                }
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isModel) 2.dp else 4.dp
            )
        ) {
            Text(
                text = messageModel.message,
                modifier = Modifier.padding(16.dp),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = if (isModel) Color(0xFF374151) else Color.White,
                fontWeight = if (isModel) FontWeight.Normal else FontWeight.Medium
            )
        }

        if (!isModel) {
            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF10B981)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}