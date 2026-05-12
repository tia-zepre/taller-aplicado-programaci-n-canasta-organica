package com.example.cestaOganicaIA.ui.auth


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cestaOganicaIA.R
import com.example.cestaOganicaIA.data.model.Credential
import com.example.cestaOganicaIA.data.repository.UserRepository
import com.example.cestaOganicaIA.data.session.SessionManager
import com.example.cestaOganicaIA.ui.shared.AppRoutes
import com.example.cestaOganicaIA.ui.shared.HuertoHogarTheme
import com.example.cestaOganicaIA.viewmodel.LoginViewModel

@Composable
fun LoginScreen(navController: NavController, vm: LoginViewModel = viewModel()) {
    val state = vm.uiState
    var showPass by remember { mutableStateOf(false) }

    HuertoHogarTheme {
        Scaffold { inner ->
            Column(
                modifier = Modifier.padding(inner).fillMaxSize().padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Bienvenido a Huerto Hogar",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 16.dp))

                Image(painter = painterResource(R.drawable.logo_huerto_hogar),
                    contentDescription = "Logo",
                    modifier = Modifier.fillMaxWidth().height(140.dp).padding(bottom = 8.dp),
                    contentScale = ContentScale.Fit)

                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = state.username, onValueChange = vm::onUsernameChange,
                    label = { Text("Usuario o correo") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.password, onValueChange = vm::onPasswordChange,
                    label = { Text("Contraseña") }, singleLine = true,
                    visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPass = !showPass }) {
                            Icon(if (showPass) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                if (state.error != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(state.error, color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall)
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (state.username.isBlank() || state.password.isBlank()) {
                            vm.setError("Completa usuario/correo y contraseña"); return@Button
                        }
                        UserRepository.login(state.username, state.password)
                            .onSuccess { user ->
                                SessionManager.login(user)
                                navController.navigate(AppRoutes.CATALOGO) {
                                    popUpTo(AppRoutes.LOGIN) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                            .onFailure { vm.setError(it.message) }
                    },
                    enabled = !state.isLoading,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) { Text(if (state.isLoading) "Validando..." else "Iniciar sesión") }

                Spacer(Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        SessionManager.login(Credential(idUsuario = -1, nombre = "Invitado",
                            correo = "", usuario = "invitado", telefono = "", direccion = "", password = ""))
                        navController.navigate(AppRoutes.CATALOGO) {
                            popUpTo(AppRoutes.LOGIN) { inclusive = true }
                        }
                    },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) { Text("Ingresar como invitado") }

                Spacer(Modifier.height(24.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = { navController.navigate(AppRoutes.REGISTRO) }) {
                        Text("Crear cuenta", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    }
                    TextButton(onClick = { navController.navigate(AppRoutes.RECUPERAR_CLAVE) }) {
                        Text("¿Olvidaste tu contraseña?", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}