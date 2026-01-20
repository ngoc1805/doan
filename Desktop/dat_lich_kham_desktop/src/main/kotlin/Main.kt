import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cafe.adriel.voyager.navigator.Navigator
import data.repository.FileRepository
import data.repository.LoginRepository
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module
import ui.screen.LoginScreen
import ui.screen.admin.AdminMainScreen
import ui.screen.doctor.DoctorMainScreen
import ui.screen.serviceroom.ServicrRoomMainScreen
import viewmodel.FileViewModel

val appModule = module {
    // Repositories
    single { LoginRepository() }
    single { FileRepository() }

    // ViewModels
    factory { FileViewModel() }
}

fun main() = application {
    startKoin {
        modules(appModule)
    }
    val loginRepository = LoginRepository()
    val token = loginRepository.getAccessToken()
    val info = loginRepository.getUserInfo()

    val startScreen = if (token != null && info != null) {
        when (info.role) {
            "bacsi" -> DoctorMainScreen()
            "chucnang" -> ServicrRoomMainScreen()
            "admin" -> AdminMainScreen()
            else -> LoginScreen()
        }
    } else {
        LoginScreen()
    }



    Window(onCloseRequest = ::exitApplication) {
        Navigator(startScreen)
    }
}
