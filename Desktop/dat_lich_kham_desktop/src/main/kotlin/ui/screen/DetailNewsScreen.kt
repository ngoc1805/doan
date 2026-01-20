package ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.ktor.util.*
import javafx.embed.swing.JFXPanel
import javafx.scene.web.WebView
import java.awt.BorderLayout
import javax.swing.JPanel
import javafx.application.Platform
import javafx.scene.Scene
import javafx.concurrent.Worker.State

data class DetailNewsScreen(
    val title: String,
    val link: String
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var isLoading by remember { mutableStateOf(true) }

        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            TopAppBar(
                title = { Text("Chi tiết tin tức") },
                navigationIcon = {
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(Icons.Default.ArrowBack, "Quay lại")
                    }
                },
                elevation = 4.dp
            )

            // Loading indicator
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colors.primary
                    )
                }
            }

            // WebView
            SwingPanel(
                background = Color.White,
                modifier = Modifier.fillMaxSize(),
                factory = {
                    JPanel().apply {
                        layout = BorderLayout()
                        val jfxPanel = JFXPanel()
                        add(jfxPanel, BorderLayout.CENTER)

                        Platform.runLater {
                            val webView = WebView()
                            val webEngine = webView.engine

                            // Lắng nghe trạng thái loading
                            webEngine.loadWorker.stateProperty().addListener { _, _, newState ->
                                isLoading = newState == javafx.concurrent.Worker.State.RUNNING
                            }

                            // Load URL
                            webEngine.load(link)

                            // Tạo scene và gán vào JFXPanel
                            val scene = Scene(webView)
                            jfxPanel.scene = scene
                        }
                    }
                }
            )
        }
    }
}