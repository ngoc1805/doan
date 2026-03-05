import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cafe.adriel.voyager.navigator.Navigator
import ui.screen.WelcomeScreen
import ui.theme.AdminTheme

fun main() = application {
    val state = rememberWindowState(size = DpSize(1200.dp, 760.dp))
    Window(
        onCloseRequest = ::exitApplication,
        state     = state,
        title     = "Quản Lý Thẻ Bệnh Viện — Admin",
        resizable = true,
    ) {
        AdminTheme {
            Navigator(WelcomeScreen())
        }
    }
}
