package com.example.weathersnap.ui.screens


import android.content.Context
import android.net.Uri
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.weathersnap.util.CompressedImageResult
import com.example.weathersnap.util.ImageUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.Executors

private val ButtonFill_lighttype = Color(0xFFCCDE6E)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    onClose: () -> Unit,
    onImageCaptured: (result: CompressedImageResult) -> Unit
) {
    val cameraPermission = rememberPermissionState(android.Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermission.status.isGranted) {
            cameraPermission.launchPermissionRequest()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (cameraPermission.status.isGranted) {
            CameraPreviewContent(
                onClose        = onClose,
                onImageCaptured = onImageCaptured
            )
        } else {
            // Permission denied UI
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text      = "Camera permission required",
                    color     = Color.White,
                    fontSize  = 16.sp
                )
                Button(
                    onClick = { cameraPermission.launchPermissionRequest() },
                    colors  = ButtonDefaults.buttonColors(containerColor = ButtonFill_lighttype)
                ) {
                    Text("Grant Permission", color = Color(0xFF1A2710))
                }
                TextButton(onClick = onClose) {
                    Text("Go Back", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun CameraPreviewContent(
    onClose: () -> Unit,
    onImageCaptured: (result: CompressedImageResult) -> Unit
) {
    val context        = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    var isCapturing by remember { mutableStateOf(false) }
    val imageCaptureRef = remember { mutableStateOf<ImageCapture?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Live preview ─────────────────────────────────────────────────
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory  = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }

                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    val imageCapture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()

                    imageCaptureRef.value = imageCapture

                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageCapture
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            }
        )

        // ── Top bar: title + close ────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text       = "Custom Camera",
                color      = Color.White,
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = onClose,
                shape   = RoundedCornerShape(50),
                colors  = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.15f),
                    contentColor   = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text("Close", fontWeight = FontWeight.SemiBold)
            }
        }

        // ── Bottom: Capture button ────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    val imageCapture = imageCaptureRef.value ?: return@Button
                    isCapturing = true
                    capturePhoto(
                        context      = context,
                        imageCapture = imageCapture,
                        onSuccess    = { result ->
                            isCapturing = false
                            onImageCaptured(result)
                        },
                        onError = {
                            isCapturing = false
                        },
                        coroutineScope = coroutineScope
                    )
                },
                enabled  = !isCapturing,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(56.dp),
                shape  = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonFill_lighttype,
                    contentColor   = Color(0xFF1A2710),
                    disabledContainerColor = ButtonFill_lighttype.copy(alpha = 0.5f)
                )
            ) {
                if (isCapturing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color    = Color(0xFF1A2710),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text       = "Capture",
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 16.sp
                    )
                }
            }
        }
    }
}

private fun capturePhoto(
    context: Context,
    imageCapture: ImageCapture,
    onSuccess: (CompressedImageResult) -> Unit,
    onError: (Exception) -> Unit,
    coroutineScope: kotlinx.coroutines.CoroutineScope
) {
    val outputDir  = File(context.cacheDir, "camera_images").apply { mkdirs() }
    val outputFile = File(outputDir, "raw_${System.currentTimeMillis()}.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()
    val executor = Executors.newSingleThreadExecutor()

    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                coroutineScope.launch {
                    try {
                        val result = withContext(Dispatchers.IO) {
                            ImageUtils.compressImage(context, outputFile)
                        }
                        // Clean up raw file after compression
                        outputFile.delete()
                        onSuccess(result)
                    } catch (e: Exception) {
                        onError(e)
                    }
                }
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }
        }
    )
}