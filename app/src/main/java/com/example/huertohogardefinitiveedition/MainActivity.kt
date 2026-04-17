package com.example.huertohogardefinitiveedition

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.huertohogardefinitiveedition.view.QrScannerScreen
import com.example.huertohogardefinitiveedition.viewmodel.QrViewModel
import com.example.huertohogardefinitiveedition.utils.CameraPermissionHelper
import com.example.huertohogardefinitiveedition.navigation.AppNav

class MainActivity : ComponentActivity() {

    private val qrViewModel: QrViewModel by viewModels()

    // Estado del permiso de cámara
    private var hasCameraPermission by mutableStateOf(false)

    // Launcher para pedir permiso
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            hasCameraPermission = isGranted
            if (isGranted) {
                Toast.makeText(this, "Permiso de cámara concedido", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "Se necesita permiso de cámara para escanear códigos QR",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificar permiso inicial
        hasCameraPermission = CameraPermissionHelper.hasCameraPermission(this)

        setContent {
            MaterialTheme {
                Surface {
                    AppNav(
                        hasCameraPermission = hasCameraPermission,
                        onRequestPermission = {
                            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    )
                }
            }
        }

        // Observa los resultados del QR
        qrViewModel.qrResult.observe(this) { qrResult ->
            qrResult?.let { result ->
                Toast.makeText(this, "Código QR detectado: ${result.content}", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Actualiza el estado del permiso al volver a la app
        hasCameraPermission = CameraPermissionHelper.hasCameraPermission(this)
    }


}