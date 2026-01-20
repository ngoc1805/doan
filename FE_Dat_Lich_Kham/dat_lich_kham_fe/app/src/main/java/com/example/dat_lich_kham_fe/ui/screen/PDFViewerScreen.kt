package com.example.dat_lich_kham_fe.ui.screen

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.ui.component.AppBarView
import com.example.dat_lich_kham_fe.util.divideAndRound
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PDFViewerScreen(
    navController: NavController,
    fileName: String,
    fileUrl: String
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var pdfBitmaps by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    val scale = divideAndRound(screenWidthValue)

    LaunchedEffect(fileUrl) {
        try {
            withContext(Dispatchers.IO) {
                // Download PDF file
                val url = URL(fileUrl)
                val inputStream = url.openStream()
                val file = File(context.cacheDir, "temp_pdf.pdf")
                val outputStream = FileOutputStream(file)

                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }

                // Render PDF pages as bitmaps
                val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                val pdfRenderer = PdfRenderer(fileDescriptor)
                val bitmaps = mutableListOf<Bitmap>()

                for (i in 0 until pdfRenderer.pageCount) {
                    val page = pdfRenderer.openPage(i)
                    val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    bitmaps.add(bitmap)
                    page.close()
                }

                pdfRenderer.close()
                fileDescriptor.close()
                file.delete() // Clean up temp file

                withContext(Dispatchers.Main) {
                    pdfBitmaps = bitmaps
                    isLoading = false
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                errorMessage = "Không thể tải file PDF: ${e.message}"
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                containerColor = colorResource(id = R.color.darkblue)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height((110 * scale).dp), // Tăng chiều cao của AppBar

            title = {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Tiêu đề căn giữa
                    Text(
                        text = fileName,
                        color = colorResource(id = R.color.white),
                        fontSize = (20 * divideAndRound(screenWidthValue)).sp, // Tăng kích thước chữ
                        modifier = Modifier.align(Alignment.TopCenter)
                            .offset(y = (16 * divideAndRound(screenWidthValue)).dp)
                    )
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.align(Alignment.BottomStart).offset(y = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            tint = Color.White,
                            contentDescription = "ArrowBack Icon"
                        )

                    }
                }
            }
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF1976D2)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Đang tải PDF...")
                    }
                }
            }
            errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = errorMessage!!,
                            color = Color.Red
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                errorMessage = null
                                isLoading = true
                            }
                        ) {
                            Text("Thử lại")
                        }
                    }
                }
            }
            pdfBitmaps.isNotEmpty() -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(pdfBitmaps.mapIndexed { index, bitmap -> index to bitmap }) { (index, bitmap) ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Trang ${index + 1}",
                                    modifier = Modifier.padding(8.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "PDF Page ${index + 1}",
                                    modifier = Modifier.fillMaxWidth(),
                                    contentScale = ContentScale.FillWidth
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
