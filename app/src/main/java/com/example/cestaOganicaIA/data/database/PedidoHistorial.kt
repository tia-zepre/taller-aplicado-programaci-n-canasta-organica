package com.example.cestaOganicaIA.data.database

import com.example.cestaOganicaIA.data.model.Producto

/**
 * Almacén temporal en memoria para el historial de pedidos.
 * Se usa mientras se migra a una base de datos real o Firebase.
 */
object PedidoHistorial {
    private val _pedidos = mutableListOf<Producto>()
    val pedidos: List<Producto> get() = _pedidos.toList()

    fun agregar(producto: Producto) {
        _pedidos.add(producto)
    }
}
