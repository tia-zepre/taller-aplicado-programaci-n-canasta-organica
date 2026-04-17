package com.example.huertohogardefinitiveedition.ui.registro

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults.outlinedButtonColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material3.lightColorScheme
import com.example.huertohogardefinitiveedition.data.model.Credential
import com.example.huertohogardefinitiveedition.data.repository.UserRepository

@Composable
fun RegistrarseScreen(navController: NavController) {

    val HuertoHogarColors = lightColorScheme(
        primary = Color(0xFF4CAF50),
        onPrimary = Color.White,
        secondary = Color(0xFFFF9800),
        onSecondary = Color.White,
        surface = Color(0xFFFFF8F5),
        onSurface = Color(0xFF3A3A3A)
    )

    MaterialTheme(colorScheme = HuertoHogarColors) {
        var nombre by remember { mutableStateOf("") }
        var correo by remember { mutableStateOf("") }
        var usuario by remember { mutableStateOf("") }
        var telefono by remember { mutableStateOf("") }
        var direccion by remember { mutableStateOf("") }
        var clave by remember { mutableStateOf("") }
        var confirmar by remember { mutableStateOf("") }

        var verClave by remember { mutableStateOf(false) }
        var verConfirmar by remember { mutableStateOf(false) }

        var nombreErr by remember { mutableStateOf<String?>(null) }
        var correoErr by remember { mutableStateOf<String?>(null) }
        var usuarioErr by remember { mutableStateOf<String?>(null) }
        var telefonoErr by remember { mutableStateOf<String?>(null) }
        var direccionErr by remember { mutableStateOf<String?>(null) }
        var claveErr by remember { mutableStateOf<String?>(null) }
        var confirmarErr by remember { mutableStateOf<String?>(null) }
        var errorMsg by remember { mutableStateOf<String?>(null) }

        var showDialog by remember { mutableStateOf(false) }

        fun isValidEmail(s: String): Boolean {
            val emailRegex = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
            return s.matches(emailRegex)
        }
        fun isStrongPassword(s: String): Boolean {
            if (s.length < 8) return false
            val hasLetter = s.any { it.isLetter() }
            val hasDigit = s.any { it.isDigit() }
            return hasLetter && hasDigit
        }
        fun isPhoneCL9(s: String): Boolean {
            val digits = s.filter { it.isDigit() }
            return digits.length == 9
        }

        Scaffold { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Crear cuenta",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it; nombreErr = null },
                        label = { Text("Nombre completo") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        isError = nombreErr != null,
                        supportingText = { if (nombreErr != null) Text(nombreErr!!) }
                    )

                    OutlinedTextField(
                        value = correo,
                        onValueChange = { correo = it; correoErr = null },
                        label = { Text("Correo electrónico") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        isError = correoErr != null,
                        supportingText = { if (correoErr != null) Text(correoErr!!) }
                    )

                    OutlinedTextField(
                        value = usuario,
                        onValueChange = { usuario = it; usuarioErr = null },
                        label = { Text("Usuario") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        isError = usuarioErr != null,
                        supportingText = { if (usuarioErr != null) Text(usuarioErr!!) }
                    )

                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { new ->
                            telefono = new.filter { it.isDigit() }.take(9)
                            telefonoErr = null
                        },
                        label = { Text("Teléfono (9 dígitos)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        isError = telefonoErr != null,
                        supportingText = { if (telefonoErr != null) Text(telefonoErr!!) }
                    )

                    OutlinedTextField(
                        value = direccion,
                        onValueChange = { direccion = it; direccionErr = null },
                        label = { Text("Dirección de entrega") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        isError = direccionErr != null,
                        supportingText = { if (direccionErr != null) Text(direccionErr!!) }
                    )

                    OutlinedTextField(
                        value = clave,
                        onValueChange = { clave = it; claveErr = null; confirmarErr = null },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        visualTransformation = if (verClave) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { verClave = !verClave }) {
                                Icon(
                                    imageVector = if (verClave) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        isError = claveErr != null,
                        supportingText = {
                            if (claveErr != null) Text(claveErr!!)
                            else Text("Mínimo 8 caracteres, con letras y números")
                        }
                    )

                    OutlinedTextField(
                        value = confirmar,
                        onValueChange = { confirmar = it; confirmarErr = null },
                        label = { Text("Confirmar contraseña") },
                        singleLine = true,
                        visualTransformation = if (verConfirmar) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { verConfirmar = !verConfirmar }) {
                                Icon(
                                    imageVector = if (verConfirmar) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        isError = confirmarErr != null,
                        supportingText = { if (confirmarErr != null) Text(confirmarErr!!) }
                    )
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        nombreErr = null; correoErr = null; usuarioErr = null
                        telefonoErr = null; direccionErr = null
                        claveErr = null; confirmarErr = null; errorMsg = null

                        var ok = true

                        if (nombre.isBlank()) { nombreErr = "Campo obligatorio"; ok = false }
                        if (correo.isBlank()) { correoErr = "Campo obligatorio"; ok = false }
                        else if (!isValidEmail(correo)) { correoErr = "Correo inválido"; ok = false }
                        if (usuario.isBlank()) { usuarioErr = "Campo obligatorio"; ok = false }
                        if (telefono.isBlank()) { telefonoErr = "Campo obligatorio"; ok = false }
                        else if (!isPhoneCL9(telefono)) { telefonoErr = "Debe tener 9 dígitos"; ok = false }
                        if (direccion.isBlank()) { direccionErr = "Campo obligatorio"; ok = false }
                        if (clave.isBlank()) { claveErr = "Campo obligatorio"; ok = false }
                        else if (!isStrongPassword(clave)) { claveErr = "Contraseña débil"; ok = false }
                        if (confirmar.isBlank()) { confirmarErr = "Campo obligatorio"; ok = false }
                        else if (confirmar != clave) { confirmarErr = "Las contraseñas no coinciden"; ok = false }

                        if (!ok) {
                            errorMsg = "Revisa los campos marcados en rojo"
                            return@Button
                        }

                        val candidate = Credential(
                            idUsuario = 0,
                            nombre = nombre.trim(),
                            correo = correo.trim(),
                            usuario = usuario.trim(),
                            telefono = telefono.trim(),
                            direccion = direccion.trim(),
                            password = clave
                        )

                        val res = UserRepository.register(candidate)
                        res.onSuccess {
                            showDialog = true
                            // (opcional) limpiar campos:
                            // nombre=""; correo=""; usuario=""; telefono=""; direccion=""; clave=""; confirmar=""
                        }.onFailure { e ->
                            errorMsg = e.message ?: "No fue posible registrar"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(44.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(50)
                ) {
                    Text("Crear cuenta")
                }

                if (errorMsg != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = errorMsg!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { navController.navigate("login") },
                    colors = outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Ya tengo cuenta (volver)")
                }
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Registro exitoso 🎉") },
                    text = { Text("Tu cuenta ha sido creada correctamente.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDialog = false
                                navController.navigate("login")
                            }
                        ) { Text("Aceptar", color = MaterialTheme.colorScheme.primary) }
                    }
                )
            }
        }
    }
}
