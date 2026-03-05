package ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import ui.theme.*

@Composable
fun PanelCard(
    title: String, subtitle: String,
    icon: ImageVector, iconColor: Color,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Surface(color = iconColor.copy(alpha = 0.07f),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            elevation = 0.dp, modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(iconColor),
                    contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = Color.White, modifier = Modifier.size(25.dp))
                }
                Column {
                    Text(title, style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold, color = TextDark)
                    Text(subtitle, style = MaterialTheme.typography.body2, color = TextGray)
                }
            }
        }
        Surface(color = CardWhite, shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
            elevation = 3.dp, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                content()
            }
        }
    }
}

@Composable
fun AdminField(
    label: String, value: String, onValue: (String) -> Unit,
    icon: ImageVector, placeholder: String,
    isPassword: Boolean = false, isNumeric: Boolean = false,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.caption, color = TextGray, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = value, onValueChange = onValue,
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = if (isNumeric) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default,
            placeholder = { Text(placeholder, style = MaterialTheme.typography.body2, color = TextGray.copy(alpha = 0.45f)) },
            leadingIcon = { Icon(icon, null, tint = AdminPurple.copy(alpha = 0.55f), modifier = Modifier.size(18.dp)) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = AdminPurple, unfocusedBorderColor = DividerColor,
                cursorColor = AdminPurple, textColor = TextDark,
            ),
        )
    }
}

@Composable
fun ActionBtn(
    label: String, isLoading: Boolean, color: Color, icon: ImageVector,
    enabled: Boolean, onClick: () -> Unit,
) {
    Button(onClick = onClick, enabled = enabled && !isLoading,
        modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = color, contentColor = Color.White,
            disabledBackgroundColor = color.copy(alpha = 0.3f), disabledContentColor = Color.White),
        elevation = ButtonDefaults.elevation(defaultElevation = 4.dp, pressedElevation = 1.dp)) {
        if (isLoading) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            Spacer(Modifier.width(10.dp))
            Text("Đang xử lý...", fontWeight = FontWeight.Bold)
        } else {
            Icon(icon, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(label, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ResultMsg(result: Pair<Boolean, String>?) {
    AnimatedVisibility(result != null, enter = fadeIn() + slideInVertically { it / 2 }, exit = fadeOut()) {
        result?.let { (ok, msg) ->
            Surface(color = if (ok) SuccessLight else ErrorLight, shape = RoundedCornerShape(12.dp),
                elevation = 0.dp, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(if (ok) Icons.Filled.CheckCircle else Icons.Filled.ErrorOutline, null,
                        tint = if (ok) Success else ErrorRed, modifier = Modifier.size(20.dp))
                    Text(msg, style = MaterialTheme.typography.body2, color = TextDark)
                }
            }
        }
    }
}