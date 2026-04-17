package com.example.huertohogardefinitiveedition.ui.gestion


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.huertohogardefinitiveedition.data.session.SessionManager
import com.example.huertohogardefinitiveedition.data.repository.UserRepository


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionPerfilScreen(navController: NavController) {

    // Si no hay sesión, vuelve al login
    val current = remember { SessionManager.currentUser }
    if (current == null) {
        LaunchedEffect(Unit) { navController.navigate("login") }
        return
    }

    // Colores locales Huerto Hogar
    val colors = lightColorScheme(
        primary    = Color(0xFF4CAF50), // verde
        onPrimary  = Color.White,
        secondary  = Color(0xFFFF9800), // naranjo
        onSecondary= Color.White,
        surface    = Color(0xFFFFF8F5),
        onSurface  = Color(0xFF3A3A3A),
        error      = Color(0xFFB00020)
    )

    MaterialTheme(colorScheme = colors) {
        // Estado del formulario (prefill)
        var nombre by remember { mutableStateOf(current.nombre) }
        var telefono by remember { mutableStateOf(current.telefono) }
        var direccion by remember { mutableStateOf(current.direccion) }

        // Errores
        var nombreErr by remember { mutableStateOf<String?>(null) }
        var telefonoErr by remember { mutableStateOf<String?>(null) }
        var direccionErr by remember { mutableStateOf<String?>(null) }
        var generalErr by remember { mutableStateOf<String?>(null) }

        // Diálogo OK
        var showOk by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Mis datos", color = colors.onPrimary) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = colors.primary
                    )
                )
            }
        ) { inner ->
            Column(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize()
                    .background(colors.surface)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Usuario (solo lectura)
                OutlinedTextField(
                    value = current.usuario,
                    onValueChange = {},
                    label = { Text("Usuario") },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )

                // Correo (solo lectura)
                OutlinedTextField(
                    value = current.correo,
                    onValueChange = {},
                    label = { Text("Correo") },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )

                // Nombre
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it; nombreErr = null; generalErr = null },
                    label = { Text("Nombre completo") },
                    isError = nombreErr != null,
                    supportingText = { if (nombreErr != null) Text(nombreErr!!) },
                    modifier = Modifier.fillMaxWidth()
                )

                // Teléfono (9 dígitos)
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { t ->
                        telefono = t.filter { it.isDigit() }.take(9)
                        telefonoErr = null; generalErr = null
                    },
                    label = { Text("Teléfono (9 dígitos)") },
                    isError = telefonoErr != null,
                    supportingText = { if (telefonoErr != null) Text(telefonoErr!!) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )

                // Dirección
                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it; direccionErr = null; generalErr = null },
                    label = { Text("Dirección de entrega") },
                    isError = direccionErr != null,
                    supportingText = { if (direccionErr != null) Text(direccionErr!!) },
                    modifier = Modifier.fillMaxWidth()
                )

                if (generalErr != null) {
                    Text(generalErr!!, color = colors.error)
                }

                Spacer(Modifier.height(8.dp))

                // Guardar cambios
                Button(
                    onClick = {
                        nombreErr = null; telefonoErr = null; direccionErr = null; generalErr = null
                        var ok = true
                        if (nombre.isBlank()) { nombreErr = "Campo obligatorio"; ok = false }
                        if (telefono.length != 9) { telefonoErr = "Debe tener 9 dígitos"; ok = false }
                        if (direccion.isBlank()) { direccionErr = "Campo obligatorio"; ok = false }
                        if (!ok) return@Button

                        val res = UserRepository.updateProfile(
                            idUsuario = current.idUsuario,
                            nombre = nombre.trim(),
                            telefono = telefono.trim(),
                            direccion = direccion.trim()
                        )
                        res.onSuccess { updated ->
                            SessionManager.updateCurrent(updated)
                            showOk = true
                        }.onFailure { e ->
                            generalErr = e.message ?: "No se pudo guardar"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(44.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary
                    )
                ) { Text("Guardar cambios") }

                // Botón volver (abajo)
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(44.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.secondary,
                        contentColor = colors.onSecondary
                    )
                ) { Text("Volver atrás") }
            }

            if (showOk) {
                AlertDialog(
                    onDismissRequest = { showOk = false },
                    title = { Text("Datos actualizados") },
                    text = { Text("Tu información se guardó correctamente.") },
                    confirmButton = {
                        TextButton(onClick = { showOk = false }) {
                            Text("OK", color = colors.primary)
                        }
                    }
                )
            }
        }
    }
}
