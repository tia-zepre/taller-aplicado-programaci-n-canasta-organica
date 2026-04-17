package com.example.huertohogardefinitiveedition.ui.gestion


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.huertohogardefinitiveedition.data.repository.UserRepository

@Composable
fun RecuperarContrasenaScreen(navController: NavController) {

    // Colores locales Huerto Hogar (sin tocar el tema global)
    val colors = lightColorScheme(
        primary    = Color(0xFF4CAF50),
        onPrimary  = Color.White,
        secondary  = Color(0xFFFF9800),
        onSecondary= Color.White,
        surface    = Color(0xFFFFF8F5),
        onSurface  = Color(0xFF3A3A3A),
        error      = Color(0xFFB00020)
    )

    MaterialTheme(colorScheme = colors) {

        var userOrEmail by remember { mutableStateOf("") }
        var pass by remember { mutableStateOf("") }
        var confirm by remember { mutableStateOf("") }

        var showPass by remember { mutableStateOf(false) }
        var showConfirm by remember { mutableStateOf(false) }

        var userErr by remember { mutableStateOf<String?>(null) }
        var passErr by remember { mutableStateOf<String?>(null) }
        var confirmErr by remember { mutableStateOf<String?>(null) }
        var generalErr by remember { mutableStateOf<String?>(null) }

        var okDialog by remember { mutableStateOf(false) }

        fun isStrongPassword(s: String): Boolean {
            if (s.length < 8) return false
            val hasLetter = s.any { it.isLetter() }
            val hasDigit  = s.any { it.isDigit() }
            return hasLetter && hasDigit
        }

        Scaffold { inner ->
            Column(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Recuperar contraseña",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Usuario o correo
                    OutlinedTextField(
                        value = userOrEmail,
                        onValueChange = { userOrEmail = it; userErr = null; generalErr = null },
                        label = { Text("Usuario o correo") },
                        singleLine = true,
                        isError = userErr != null,
                        supportingText = { if (userErr != null) Text(userErr!!) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Nueva contraseña
                    OutlinedTextField(
                        value = pass,
                        onValueChange = { pass = it; passErr = null; confirmErr = null; generalErr = null },
                        label = { Text("Nueva contraseña") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPass = !showPass }) {
                                Icon(
                                    imageVector = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        isError = passErr != null,
                        supportingText = {
                            if (passErr != null) Text(passErr!!)
                            else Text("Mínimo 8 caracteres, con letras y números")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Confirmar
                    OutlinedTextField(
                        value = confirm,
                        onValueChange = { confirm = it; confirmErr = null; generalErr = null },
                        label = { Text("Confirmar nueva contraseña") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showConfirm = !showConfirm }) {
                                Icon(
                                    imageVector = if (showConfirm) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        isError = confirmErr != null,
                        supportingText = { if (confirmErr != null) Text(confirmErr!!) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (generalErr != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(generalErr!!, color = MaterialTheme.colorScheme.error)
                }

                Spacer(Modifier.height(16.dp))

                // Botón cambiar
                Button(
                    onClick = {
                        userErr = null; passErr = null; confirmErr = null; generalErr = null
                        var ok = true

                        if (userOrEmail.isBlank()) {
                            userErr = "Ingresa tu usuario o correo"; ok = false
                        }
                        if (!isStrongPassword(pass)) {
                            passErr = "Contraseña débil"; ok = false
                        }
                        if (confirm != pass) {
                            confirmErr = "No coincide con la nueva contraseña"; ok = false
                        }
                        if (!ok) return@Button

                        val res = UserRepository.updatePassword(userOrEmail.trim(), pass)
                        res.onSuccess {
                            okDialog = true
                        }.onFailure { e ->
                            generalErr = e.message ?: "No se pudo actualizar"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(44.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor   = MaterialTheme.colorScheme.onPrimary
                    )
                ) { Text("Cambiar contraseña") }

                Spacer(Modifier.height(8.dp))

                // Botón volver
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary
                    )
                ) { Text("Volver") }
            }

            // Diálogo OK
            if (okDialog) {
                AlertDialog(
                    onDismissRequest = { okDialog = false },
                    title = { Text("Listo") },
                    text = { Text("Tu contraseña ha sido actualizada.") },
                    confirmButton = {
                        TextButton(onClick = {
                            okDialog = false
                            navController.navigate("login") {
                                popUpTo("login") { inclusive = true }
                                launchSingleTop = true
                            }
                        }) { Text("Ir a iniciar sesión") }
                    }
                )
            }
        }
    }
}


