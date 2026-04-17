package com.example.huertohogardefinitiveedition.data.database

import androidx.compose.runtime.mutableStateListOf
import com.example.huertohogardefinitiveedition.data.model.Producto

// Debe ser "object" (singleton)
object PedidoHistorial {
    val pedidos = mutableStateListOf<Producto>()

    fun agregar(pedido: Producto) {
        pedidos.add(0, pedido) // último arriba
    }

    fun limpiar() = pedidos.clear()
}


