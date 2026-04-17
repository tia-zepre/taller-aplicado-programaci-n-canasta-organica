package com.example.huertohogardefinitiveedition.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.huertohogardefinitiveedition.data.database.PedidoHistorial
import com.example.huertohogardefinitiveedition.data.model.Producto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialPedidosScreen() {
    val historial = PedidoHistorial.pedidos

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Historial de Pedidos",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        if (historial.isEmpty()) {
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Aún no hay pedidos registrados.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Como los id de Producto pueden repetirse (0 por defecto),
                // usamos un key estable alternativo:
                items(historial, key = { it.hashCode() }) { pedido ->
                    PedidoCard(pedido)
                }
            }
        }
    }
}

@Composable
private fun PedidoCard(pedido: Producto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text("🎉 Pedido Realizado 🎉", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp)); Divider(); Spacer(Modifier.height(8.dp))
            Text("• Producto: ${pedido.nombre}")
            Text("• Cantidad: ${pedido.cantidad}")
            Text("• Dirección: ${pedido.direccion}")
            Spacer(Modifier.height(8.dp)); Divider(); Spacer(Modifier.height(8.dp))
            Text("Total pagado: ${pedido.precio}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
        }
    }
}
