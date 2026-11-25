package co.gov.tulua.espaciopublico.ui.census.wizard

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File

/**
 * Vista previa de cámara con CameraX + botón de captura.
 *
 * @param onPhotoTaken  Invocado con la ruta absoluta del archivo capturado.
 * @param modifier      Modifier externo.
 */
@Composable
fun CameraPreview(
    onPhotoTaken: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val imageCapture = remember { ImageCapture.Builder().build() }

    Box(modifier = modifier) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener(
                    {
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                imageCapture,
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    ContextCompat.getMainExecutor(ctx),
                )
                previewView
            },
            modifier = Modifier.fillMaxSize(),
        )

        // Botón de captura
        FloatingActionButton(
            onClick = { capturarFoto(context, imageCapture, onPhotoTaken) },
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .size(72.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Camera,
                contentDescription = "Capturar foto",
                modifier = Modifier.size(32.dp),
            )
        }
    }
}

private fun capturarFoto(
    context: Context,
    imageCapture: ImageCapture,
    onPhotoTaken: (String) -> Unit,
) {
    val archivo = File(context.cacheDir, "censo_foto_${System.currentTimeMillis()}.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(archivo).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                onPhotoTaken(archivo.absolutePath)
            }

            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
            }
        },
    )
}
