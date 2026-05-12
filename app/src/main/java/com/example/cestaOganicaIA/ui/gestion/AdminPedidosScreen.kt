package com.example.cestaOganicaIA.ui.gestion

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cestaOganicaIA.data.database.PedidoEntity
import com.example.cestaOganicaIA.viewmodel.AdminViewModel
import com.example.cestaOganicaIA.ui.shared.AppRoutes
import com.example.cestaOganicaIA.ui.shared.HuertoHogarTheme
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPedidosScreen(
    navController: NavController,
    viewModel: AdminViewModel
) {
    val todosPedidos by viewModel.todosPedidos.collectAsState()
    
    // Agrupamos por ordenId para mostrar tarjetas de órdenes completas
    val ordenesAgrupadas = remember(todosPedidos) {
        todosPedidos.groupBy { it.ordenId }
    }

    HuertoHogarTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Monitor Global de Pedidos") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { padding ->
            if (ordenesAgrupadas.isEmpty()) {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("No hay pedidos registrados en el sistema", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(ordenesAgrupadas.entries.toList()) { entry ->
                        AdminOrdenCard(
                            ordenId = entry.key,
                            items = entry.value,
                            onStatusChange = { nuevoEstado ->
                                viewModel.actualizarEstadoOrden(entry.key, nuevoEstado)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminOrdenCard(
    ordenId: String,
    items: List<PedidoEntity>,
    onStatusChange: (String) -> Unit
) {
    val formatoMoneda = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }
    val primerItem = items.first()
    val totalOrden = items.sumOf { it.total }
    
    var showStatusDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("ORDEN #$ordenId", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                Text(primerItem.fechaPedido, style = MaterialTheme.typography.bodySmall)
            }
            
            Spacer(Modifier.height(8.dp))
            
            // Información del Cliente
            val cliente = if (primerItem.usuarioId == -1) {
                "${primerItem.nombreContacto} (Invitado)"
            } else {
                primerItem.nombreContacto.takeIf { it.isNotBlank() } ?: "Usuario #${primerItem.usuarioId}"
            }
            
            Text("Cliente: $cliente", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            if (primerItem.correoContacto.isNotBlank()) {
                Text(primerItem.correoContacto, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            HorizontalDivider(Modifier.padding(vertical = 12.dp))

            // Lista de productos
            items.forEach { pedido ->
                Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (pedido.imagenResId != 0) {
                        Image(
                            painter = painterResource(pedido.imagenResId),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp).background(Color.White, RoundedCornerShape(4.dp)),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Text("${pedido.cantidad}x ${pedido.nombreProducto}", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                    Text(formatoMoneda.format(pedido.total), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            HorizontalDivider(Modifier.padding(vertical = 12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(Modifier.width(8.dp))
                Text(primerItem.direccionEntrega, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            
            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                // Botón para cambiar estado
                OutlinedButton(
                    onClick = { showStatusDialog = true },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(primerItem.estado.uppercase(), style = MaterialTheme.typography.labelSmall)
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(14.dp).padding(start = 4.dp))
                }
                
                Text(
                    text = "TOTAL: ${formatoMoneda.format(totalOrden)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    if (showStatusDialog) {
        val estados = listOf("Confirmado", "Preparando", "En Camino", "Entregado", "Cancelado")
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text("Cambiar Estado de la Orden") },
            text = {
                Column {
                    estados.forEach { estado ->
                        Row(
                            Modifier.fillMaxWidth().clickable { 
                                onStatusChange(estado)
                                showStatusDialog = false
                            }.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = (primerItem.estado == estado), onClick = null)
                            Spacer(Modifier.width(12.dp))
                            Text(estado)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}

import androidx.compose.foundation.clickable
