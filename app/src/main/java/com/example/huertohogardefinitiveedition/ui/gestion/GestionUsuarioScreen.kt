package com.example.huertohogardefinitiveedition.ui.gestion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.huertohogardefinitiveedition.data.model.Credential
import com.example.huertohogardefinitiveedition.data.repository.UserRepository
import com.example.huertohogardefinitiveedition.data.session.SessionManager


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionUsuarioScreen(navController: NavController) {

    // 🎨 Tema local con colores Huerto Hogar
    val HuertoHogarColors = lightColorScheme(
        primary = Color(0xFF4CAF50),   // Verde principal
        onPrimary = Color.White,
        secondary = Color(0xFFFF9800), // Naranjo
        onSecondary = Color.White,
        surface = Color(0xFFFFF8F5),   // Fondo claro cálido
        onSurface = Color(0xFF3A3A3A)  // Texto gris oscuro
    )

    MaterialTheme(colorScheme = HuertoHogarColors) {

        val usuarios = remember {
            mutableStateListOf<Credential>().apply { addAll(UserRepository.all()) }
        }

        var editOpen by remember { mutableStateOf(false) }
        var editUser by remember { mutableStateOf<Credential?>(null) }

        Scaffold(
            topBar = {
                // TopAppBar con menú
                val current = SessionManager.currentUser
                val displayName = "Gestion usuarios"
                val isAdmin =
                    (current?.idUsuario == Credential.Admin.idUsuario) ||
                            (current?.usuario?.equals(Credential.Admin.usuario, ignoreCase = true) == true)

                var menuOpen by remember { mutableStateOf(false) }

                TopAppBar(
                    title = {
                        Column {
                            Text(text = "Perfil: $displayName")
                            if (!current?.correo.isNullOrBlank()) {
                                Text(
                                    text = current!!.correo,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { menuOpen = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menú de perfil",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        DropdownMenu(
                            expanded = menuOpen,
                            onDismissRequest = { menuOpen = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Actualizar datos") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                onClick = {
                                    menuOpen = false
                                    navController.navigate("gestion_usuario")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Historial de pedidos") },
                                leadingIcon = { Icon(Icons.Default.History, contentDescription = null) },
                                onClick = {
                                    menuOpen = false
                                    navController.navigate("historial_pedidos")
                                }
                            )
                            if (isAdmin) {
                                DropdownMenuItem(
                                    text = { Text("Gestionar usuarios") },
                                    leadingIcon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null) },

                                    onClick = {
                                        menuOpen = false
                                        navController.navigate("Gestion")


                                    }
                                )
                            }
                            Divider()
                            DropdownMenuItem(
                                text = { Text("Cerrar sesión") },
                                leadingIcon = { Icon(Icons.Default.Logout, contentDescription = null) },
                                onClick = {
                                    menuOpen = false
                                    SessionManager.logout()
                                    navController.navigate("login") {
                                        popUpTo("login") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
                // FIN HEADER
            }
        ) { inner ->
            Column(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize()
                    .background(HuertoHogarColors.surface)
            ) {
                if (usuarios.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Sin usuarios registrados", color = HuertoHogarColors.onSurface)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(usuarios, key = { it.idUsuario }) { u ->
                            ElevatedCard(
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = Color.White
                                )
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    Text(
                                        "${u.nombre} (@${u.usuario})",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = HuertoHogarColors.onSurface
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text("Correo: ${u.correo}", style = MaterialTheme.typography.bodySmall)
                                    Text("Tel: ${u.telefono}", style = MaterialTheme.typography.bodySmall)
                                    Text("Dir: ${u.direccion}", style = MaterialTheme.typography.bodySmall)

                                    Spacer(Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        TextButton(
                                            onClick = {
                                                editUser = u
                                                editOpen = true
                                            },
                                            colors = ButtonDefaults.textButtonColors(
                                                contentColor = HuertoHogarColors.primary
                                            )
                                        ) {
                                            Text("Editar")
                                        }
                                    }
                                    Spacer(Modifier.height(16.dp))

                                    Button(
                                        onClick = { navController.popBackStack() },
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .fillMaxWidth(0.6f)
                                            .height(44.dp),
                                        shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.secondary, // Naranjo
                                            contentColor = MaterialTheme.colorScheme.onSecondary   // Texto blanco
                                        )
                                    ) {
                                        Text("Volver atrás")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //  Diálogo de edición
            if (editOpen && editUser != null) {
                EditUserDialog(
                    user = editUser!!,
                    onDismiss = { editOpen = false; editUser = null },
                    onSave = { updated ->
                        val res = UserRepository.updateProfile(
                            idUsuario = updated.idUsuario,
                            nombre = updated.nombre,
                            telefono = updated.telefono,
                            direccion = updated.direccion
                        )
                        res.onSuccess { saved ->
                            val idx = usuarios.indexOfFirst { it.idUsuario == saved.idUsuario }
                            if (idx != -1) usuarios[idx] = saved
                            SessionManager.updateCurrent(saved)
                            editOpen = false
                            editUser = null
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun EditUserDialog(
    user: Credential,
    onDismiss: () -> Unit,
    onSave: (Credential) -> Unit
) {
    var nombre by remember { mutableStateOf(user.nombre) }
    var telefono by remember { mutableStateOf(user.telefono) }
    var direccion by remember { mutableStateOf(user.direccion) }

    var nombreErr by remember { mutableStateOf<String?>(null) }
    var telefonoErr by remember { mutableStateOf<String?>(null) }
    var direccionErr by remember { mutableStateOf<String?>(null) }

    val HuertoHogarColors = lightColorScheme(
        primary = Color(0xFF4CAF50),
        onPrimary = Color.White,
        secondary = Color(0xFFFF9800),
        onSecondary = Color.White,
        surface = Color(0xFFFFF8F5),
        onSurface = Color(0xFF3A3A3A)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar usuario") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it; nombreErr = null },
                    label = { Text("Nombre completo") },
                    isError = nombreErr != null,
                    supportingText = { if (nombreErr != null) Text(nombreErr!!) },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = telefono,
                    onValueChange = { t ->
                        telefono = t.filter { it.isDigit() }.take(9)
                        telefonoErr = null
                    },
                    label = { Text("Teléfono (9 dígitos)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = telefonoErr != null,
                    supportingText = { if (telefonoErr != null) Text(telefonoErr!!) },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it; direccionErr = null },
                    label = { Text("Dirección de entrega") },
                    isError = direccionErr != null,
                    supportingText = { if (direccionErr != null) Text(direccionErr!!) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    nombreErr = null; telefonoErr = null; direccionErr = null
                    var ok = true
                    if (nombre.isBlank()) { nombreErr = "Campo obligatorio"; ok = false }
                    if (telefono.length != 9) { telefonoErr = "Debe tener 9 dígitos"; ok = false }
                    if (direccion.isBlank()) { direccionErr = "Campo obligatorio"; ok = false }
                    if (!ok) return@TextButton

                    onSave(
                        user.copy(
                            nombre = nombre.trim(),
                            telefono = telefono.trim(),
                            direccion = direccion.trim()
                        )
                    )
                },
                colors = ButtonDefaults.textButtonColors(contentColor = HuertoHogarColors.primary)
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = HuertoHogarColors.secondary)
            ) {
                Text("Cancelar")
            }
        }
    )
}
