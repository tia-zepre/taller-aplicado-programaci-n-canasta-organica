package com.example.huertohogardefinitiveedition.view

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.huertohogardefinitiveedition.data.database.PedidoHistorial
import com.example.huertohogardefinitiveedition.data.model.Producto
import com.example.huertohogardefinitiveedition.data.repository.ResenaRepository
import com.example.huertohogardefinitiveedition.data.session.SessionManager
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.huertohogardefinitiveedition.viewmodel.DrawerMenuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoFormScreen(
    navController: NavController,
    nombre: String,
    precio: String,
    descripcion: String,
    stock: Int
) {
    val context = LocalContext.current
    val usuarioActual = SessionManager.currentUser
    val direccionPerfil = remember(usuarioActual) { usuarioActual?.direccion?.takeIf { it.isNotBlank() } ?: "Sin dirección" }

    var cantidad by remember { mutableStateOf("1") }
    var fechaEntrega by remember { mutableStateOf("") }
    var mostrarDialogoBoleta by remember { mutableStateOf(false) }
    var mostrarDialogoFecha by remember { mutableStateOf(false) }
    var mostrarDialogoResena by remember { mutableStateOf(false) }

    var resenas by remember { mutableStateOf(ResenaRepository.obtenerResenasPorProducto(nombre)) }
    val estadoFecha = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())

    val precioBase = precio.toIntOrNull() ?: 0
    val cantidadNum = cantidad.toIntOrNull() ?: 0
    val total = precioBase * cantidadNum
    val formatoMoneda = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }

    // El LazyColumn es el contenedor principal que permite el scroll
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- SECCIÓN DE INFO DEL PRODUCTO Y COMPRA ---
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) { // Agrupamos el contenido en una columna
                Text(text = nombre, style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = "Precio Unitario: ${formatoMoneda.format(precioBase)}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Stock disponible: $stock unidades",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Justify
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = cantidad,
                    onValueChange = { val nt = it.filter { c -> c.isDigit() }; cantidad = if (nt.startsWith("0") && nt.length > 1) nt.substring(1) else nt },
                    label = { Text("Cantidad") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = fechaEntrega,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha de Entrega") },
                    placeholder = { Text("Selecciona una fecha") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Seleccionar Fecha",
                            modifier = Modifier.clickable { mostrarDialogoFecha = true }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text("Total a Pagar", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(
                    text = formatoMoneda.format(total),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (cantidad.isBlank() || cantidadNum <= 0 || fechaEntrega.isBlank()) {
                            Toast.makeText(context, "Debes completar la cantidad y la fecha", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (cantidadNum > stock) {
                            Toast.makeText(context, "No hay suficiente stock. Solo quedan: $stock", Toast.LENGTH_LONG).show()
                            return@Button
                        }
                        mostrarDialogoBoleta = true
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Comprar Ahora", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Divider()
                Button(
                    onClick = {
                        if (cantidad.isBlank() || cantidadNum <= 0 || fechaEntrega.isBlank()) {
                            Toast.makeText(context, "Debes completar la cantidad y la fecha", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (cantidadNum > stock) {
                            Toast.makeText(context, "No hay suficiente stock. Solo quedan: $stock", Toast.LENGTH_LONG).show()
                            return@Button
                        }

                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Agregar al Carrito", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // --- SECCIÓN DE RESEÑAS ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Reseñas y Calificaciones", style = MaterialTheme.typography.titleLarge)
                if (usuarioActual != null) {
                    IconButton(onClick = { mostrarDialogoResena = true }) {
                        Icon(Icons.Default.AddComment, "Añadir Reseña")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // --- LISTA DE RESEÑAS ---
        if (resenas.isEmpty()) {
            item {
                Text("Este producto aún no tiene reseñas. ¡Sé el primero!", modifier = Modifier.padding(16.dp), color = Color.Gray)
            }
        } else {
            items(resenas) { resena ->
                CardResena(resena = resena)
            }
        }
    }

    // --- DIÁLOGOS ---

    // Diálogo de Fecha
    if (mostrarDialogoFecha) {
        DatePickerDialog(
            onDismissRequest = { mostrarDialogoFecha = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val hoy = System.currentTimeMillis()
                        val millis = estadoFecha.selectedDateMillis
                        if (millis != null) {
                            if  (millis!! >= hoy - 86400000L) {

                                fechaEntrega = SimpleDateFormat(
                                    "dd/MM/yyyy",
                                    Locale.getDefault()
                                ).format(Date(millis))
                            }else {

                                Toast.makeText(
                                    context,
                                    "No puedes seleccionar una fecha anterior a hoy",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        mostrarDialogoFecha = false
                    }
                ) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoFecha = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = estadoFecha)
        }
    }

    // Diálogo de Boleta
    if (mostrarDialogoBoleta) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoBoleta = false },
            title = { Text("🎉 Compra Realizada 🎉") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("¡Gracias por tu compra, ${usuarioActual?.nombre ?: "Cliente"}!", fontWeight = FontWeight.Bold)
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                    Text("Resumen del Pedido:")
                    Text(" • Producto: $nombre")
                    Text(" • Cantidad: $cantidadNum")
                    Text(" • Fecha de Entrega: $fechaEntrega", fontWeight = FontWeight.SemiBold)
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                    Text("Total Pagado: ${formatoMoneda.format(total)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // 1. Damos la orden al singleton del ViewModel para que actualice el stock
                        DrawerMenuViewModel.instance?.actualizarStock(nombre, cantidadNum)

                        // 2. Guardamos en el historial local
                        val pedido = Producto(
                            nombre = nombre,
                            precio = NumberFormat.getCurrencyInstance(Locale("es", "CL")).format(total),
                            stock = 0,
                            cantidad = cantidadNum.toString(),

                        )
                        PedidoHistorial.agregar(pedido)

                        // 3. Cerramos el diálogo y navegamos
                        mostrarDialogoBoleta = false
                        navController.navigate("historial_pedidos")
                    }
                ) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoBoleta = false }) { Text("Cancelar") }
            }
        )
    }

    // Diálogo para Añadir Reseña
    if (mostrarDialogoResena) {
        DialogoAnadirResena(
            onDismiss = { mostrarDialogoResena = false },
            onPublicar = { calificacion, comentario ->
                if (usuarioActual != null) {
                    ResenaRepository.agregarResena(
                        nombreProducto = nombre,
                        idUsuario = usuarioActual.idUsuario,
                        nombreUsuario = usuarioActual.nombre,
                        calificacion = calificacion,
                        comentario = comentario
                    )
                    resenas = ResenaRepository.obtenerResenasPorProducto(nombre)
                    mostrarDialogoResena = false
                }
            }
        )
    }
}


@Composable
private fun DialogoAnadirResena(
    onDismiss: () -> Unit,
    onPublicar: (calificacion: Int, comentario: String) -> Unit
) {
    var calificacion by remember { mutableStateOf(0) }
    var comentario by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Escribe tu reseña") },
        text = {
            Column {
                Text("Califica el producto:", style = MaterialTheme.typography.bodyLarge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(5) { index ->
                        IconButton(onClick = { calificacion = index + 1 }) {
                            val icon = if (index < calificacion) Icons.Filled.Star else Icons.Filled.StarBorder
                            val tint = if (index < calificacion) Color(0xFFFFC107) else Color.Gray
                            Icon(icon, null, tint = tint, modifier = Modifier.size(36.dp))
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = comentario,
                    onValueChange = { comentario = it },
                    label = { Text("Tu comentario") },
                    placeholder = { Text("Escribe aquí qué te pareció el producto...") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                // Solo se puede publicar si se ha calificado y comentado
                if (calificacion > 0 && comentario.isNotBlank()) {
                    onPublicar(calificacion, comentario)
                }
            }) { Text("Publicar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}




