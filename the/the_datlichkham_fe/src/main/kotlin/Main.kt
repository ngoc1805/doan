import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cafe.adriel.voyager.navigator.Navigator
import ui.screen.WelcomeScreen
import ui.theme.HospitalCardTheme

fun main() = application {
    val windowState = rememberWindowState(size = DpSize(1100.dp, 720.dp))
    Window(
        onCloseRequest = ::exitApplication,
        state     = windowState,
        title     = "Thẻ Đặt Lịch Khám — SmartCard Terminal",
        resizable = true,
    ) {
        HospitalCardTheme {
            Navigator(WelcomeScreen())
        }
    }
}
