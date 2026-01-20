package com.example.nhom1_fe_datnhahang.ui.screen

import android.Manifest
import android.os.Build
import android.util.Log
import android.util.Size
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size as ComposeSize
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nhom1_fe_datnhahang.R
import com.example.nhom1_fe_datnhahang.util.divideAndRound
import com.example.nhom1_fe_datnhahang.viewmodel.QrResultViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QrScannerScreen(
    navController: NavController,
    qrResultViewModel: QrResultViewModel = viewModel()
) {
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    var processed by remember { mutableStateOf(false) }
    var flashOn by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value

    // Camera ref for torch control
    var cameraForTorch by remember { mutableStateOf<androidx.camera.core.Camera?>(null) }

    LaunchedEffect(flashOn) {
        // Khi flashOn thay đổi, cập nhật torch
        cameraForTorch?.cameraControl?.enableTorch(flashOn)
    }

    LaunchedEffect(Unit) {
        cameraPermissionState.launchPermissionRequest()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (cameraPermissionState.status.isGranted) {
            // Camera Preview
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = androidx.camera.core.Preview.Builder()
                            .build()
                            .also { it.setSurfaceProvider(previewView.surfaceProvider) }

                        val imageAnalyzer = ImageAnalysis.Builder()
                            .setTargetResolution(Size(1280, 720))
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also {
                                it.setAnalyzer(
                                    ctx.mainExecutor,
                                    { imageProxy: ImageProxy ->
                                        processImageProxy(imageProxy, navController, qrResultViewModel, processed) { processed = true }
                                    }
                                )
                            }

                        cameraProvider.unbindAll()
                        val camera = cameraProvider.bindToLifecycle(
                            ctx as androidx.lifecycle.LifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageAnalyzer
                        )
                        cameraForTorch = camera
                        camera.cameraControl.enableTorch(flashOn)
                    }, ctx.mainExecutor)
                    previewView
                }
            )

            // Overlay UI
            QrScannerOverlay(
                onBackClick = { navController.popBackStack() },
                flashOn = flashOn,
                onFlashClick = { flashOn = !flashOn }
            )
        } else {
            // Permission denied state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Cần quyền truy cập camera để quét mã QR",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = (16* divideAndRound(screenWidthValue)).sp
                    )
                    Spacer(modifier = Modifier.height((16* divideAndRound(screenWidthValue)).dp))
                    Button(
                        onClick = { cameraPermissionState.launchPermissionRequest() }
                    ) {
                        Text("Cấp quyền camera")
                    }
                }
            }
        }
    }
}

@Composable
fun QrScannerOverlay(
    onBackClick: () -> Unit,
    flashOn: Boolean,
    onFlashClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthValue = screenWidth.value
    val infiniteTransition = rememberInfiniteTransition(label = "scanning")
    val scanLineOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanLine"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding((16* divideAndRound(screenWidthValue)).dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size((40* divideAndRound(screenWidthValue)).dp)
                    .background(
                        Color.Black.copy(alpha = 0.3f),
                        RoundedCornerShape(20.dp)
                    )
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size((24* divideAndRound(screenWidthValue)).dp)
                )
            }

            Text(
                text = "Quét mã QR",
                color = Color.White,
                fontSize = (20* divideAndRound(screenWidthValue)).sp,
                fontWeight = FontWeight.Bold
            )

            // FLASH BUTTON: đổi màu khi flashOn
            IconButton(
                onClick = { onFlashClick() },
                modifier = Modifier
                    .size((40* divideAndRound(screenWidthValue)).dp)
                    .background(
                        if (flashOn) Color.White else Color.Black.copy(alpha = 0.3f),
                        RoundedCornerShape((20* divideAndRound(screenWidthValue)).dp)
                    )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.flash),
                    contentDescription = "Flash",
                    tint = if (flashOn) Color(0xFF8C2C19) else Color.White,
                    modifier = Modifier.size((24* divideAndRound(screenWidthValue)).dp)
                )
            }
        }

        // Scanning Area
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Semi-transparent overlay with cutout
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val scanAreaSize = (280* divideAndRound(screenWidthValue)).dp.toPx()
                val centerX = size.width / 2
                val centerY = size.height / 2

                // Draw dark overlay
                drawRect(
                    color = Color.Black.copy(alpha = 0.6f),
                    size = size
                )

                // Cut out the scanning area
                drawRoundRect(
                    color = Color.Transparent,
                    topLeft = Offset(
                        centerX - scanAreaSize / 2,
                        centerY - scanAreaSize / 2
                    ),
                    size = ComposeSize(scanAreaSize, scanAreaSize),
                    cornerRadius = CornerRadius((20* divideAndRound(screenWidthValue)).dp.toPx()),
                    blendMode = BlendMode.Clear
                )
            }

            // Scanning frame
            Box(
                modifier = Modifier.size((280* divideAndRound(screenWidthValue)).dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cornerLength = (40* divideAndRound(screenWidthValue)).dp.toPx()
                    var strokeWidth = (4* divideAndRound(screenWidthValue)).dp.toPx()
                    val cornerRadius = (20* divideAndRound(screenWidthValue)).dp.toPx()

                    // Draw corner brackets
                    val paint = Paint().apply {
                        color = Color.White
                        strokeWidth = strokeWidth
                        style = PaintingStyle.Stroke
                    }

                    // Top-left corner
                    drawPath(
                        path = Path().apply {
                            moveTo(cornerRadius, strokeWidth / 2)
                            lineTo(cornerLength, strokeWidth / 2)
                            moveTo(strokeWidth / 2, cornerRadius)
                            lineTo(strokeWidth / 2, cornerLength)
                        },
                        color = Color.White,
                        style = Stroke(width = strokeWidth)
                    )

                    // Top-right corner
                    drawPath(
                        path = Path().apply {
                            moveTo(size.width - cornerLength, strokeWidth / 2)
                            lineTo(size.width - cornerRadius, strokeWidth / 2)
                            moveTo(size.width - strokeWidth / 2, cornerRadius)
                            lineTo(size.width - strokeWidth / 2, cornerLength)
                        },
                        color = Color.White,
                        style = Stroke(width = strokeWidth)
                    )

                    // Bottom-left corner
                    drawPath(
                        path = Path().apply {
                            moveTo(strokeWidth / 2, size.height - cornerLength)
                            lineTo(strokeWidth / 2, size.height - cornerRadius)
                            moveTo(cornerRadius, size.height - strokeWidth / 2)
                            lineTo(cornerLength, size.height - strokeWidth / 2)
                        },
                        color = Color.White,
                        style = Stroke(width = strokeWidth)
                    )

                    // Bottom-right corner
                    drawPath(
                        path = Path().apply {
                            moveTo(size.width - strokeWidth / (2), size.height - cornerLength)
                            lineTo(size.width - strokeWidth / 2, size.height - cornerRadius)
                            moveTo(size.width - cornerLength, size.height - strokeWidth / 2)
                            lineTo(size.width - cornerRadius, size.height - strokeWidth / 2)
                        },
                        color = Color.White,
                        style = Stroke(width = strokeWidth)
                    )

                    // Animated scanning line
                    val lineY = size.height * scanLineOffset
                    val gradient = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Green.copy(alpha = 0.8f),
                            Color.Green,
                            Color.Green.copy(alpha = 0.8f),
                            Color.Transparent
                        ),
                        startY = lineY - (10* divideAndRound(screenWidthValue)).dp.toPx(),
                        endY = lineY + (10* divideAndRound(screenWidthValue)).dp.toPx()
                    )

                    drawRect(
                        brush = gradient,
                        topLeft = Offset((20* divideAndRound(screenWidthValue)).dp.toPx(), lineY - 2.dp.toPx()),
                        size = ComposeSize(size.width - (40* divideAndRound(screenWidthValue)).dp.toPx(), 4.dp.toPx())
                    )
                }
            }
        }

        // Instruction text
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = (120* divideAndRound(screenWidthValue)).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.padding(horizontal = (32* divideAndRound(screenWidthValue)).dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text(
                    text = "Di chuyển camera vào mã QR",
                    modifier = Modifier.padding(horizontal = (24* divideAndRound(screenWidthValue)).dp, vertical = (12* divideAndRound(screenWidthValue)).dp),
                    color = Color.White,
                    fontSize = (16* divideAndRound(screenWidthValue)).sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// Giữ nguyên function processImageProxy
@androidx.annotation.OptIn(ExperimentalGetImage::class)
fun processImageProxy(
    imageProxy: ImageProxy,
    navController: NavController,
    qrResultViewModel: QrResultViewModel,
    processed: Boolean,
    setProcessed: () -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        if (!processed) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val scanner = BarcodeScanning.getClient(
                BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                    .build()
            )
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    barcodes.firstOrNull()?.rawValue?.let { qrText ->
                        Log.d("QrScannerScreen", "QR scanned value: $qrText")
                        qrResultViewModel.onQrResultChange(qrText)
                        setProcessed()
                        navController.navigate("TableScreen"){
                            popUpTo("QrScannerScreen") { inclusive = true }
                        }
                    }
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    } else {
        imageProxy.close()
    }
}
