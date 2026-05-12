package com.example.cestaOganicaIA.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cestaOganicaIA.data.database.CarritoItemEntity
import com.example.cestaOganicaIA.data.database.PedidoEntity
import com.example.cestaOganicaIA.data.repository.CarritoRepository
import com.example.cestaOganicaIA.data.repository.PedidoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CarritoViewModel(
    private val carritoRepo: CarritoRepository,
    private val pedidoRepo: PedidoRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<CarritoItemEntity>>(emptyList())
    val items: StateFlow<List<CarritoItemEntity>> = _items.asStateFlow()

    val total: StateFlow<Int> = _items.map { lista ->
        lista.sumOf { it.precioUnitario * it.cantidad }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val cantidadTotal: StateFlow<Int> = _items.map { lista ->
        lista.sumOf { it.cantidad }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    private val _pedidoConfirmado = MutableStateFlow(false)
    val pedidoConfirmado: StateFlow<Boolean> = _pedidoConfirmado.asStateFlow()

    fun cargarCarrito(uid: Int) {
        viewModelScope.launch {
            carritoRepo.itemsDeUsuario(uid).collect { _items.value = it }
        }
    }

    fun agregarAlCarrito(uid: Int, nombre: String, precio: Int, imagenResId: Int, cantidad: Int) {
        viewModelScope.launch {
            carritoRepo.agregarOActualizar(uid, nombre, precio, imagenResId, cantidad)
        }
    }

    fun cambiarCantidad(item: CarritoItemEntity, nuevaCantidad: Int) {
        viewModelScope.launch { carritoRepo.cambiarCantidad(item, nuevaCantidad) }
    }

    fun eliminarItem(item: CarritoItemEntity) {
        viewModelScope.launch { carritoRepo.eliminarItem(item) }
    }

    fun vaciarCarrito(uid: Int) {
        viewModelScope.launch { carritoRepo.vaciarCarrito(uid) }
    }

    fun confirmarPedido(
        uid: Int,
        fechaEntrega: String,
        direccion: String,
        onStockDescontar: (String, Int) -> Unit
    ) {
        viewModelScope.launch {
            val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            _items.value.forEach { item ->
                pedidoRepo.confirmarPedido(
                    PedidoEntity(
                        usuarioId = uid,
                        nombreProducto = item.nombreProducto,
                        precioUnitario = item.precioUnitario,
                        cantidad = item.cantidad,
                        total = item.precioUnitario * item.cantidad,
                        fechaEntrega = fechaEntrega,
                        direccionEntrega = direccion,
                        fechaPedido = fecha,
                        estado = "Confirmado"
                    )
                )
                onStockDescontar(item.nombreProducto, item.cantidad)
            }
            carritoRepo.vaciarCarrito(uid)
            _pedidoConfirmado.value = true
        }
    }

    fun resetPedidoConfirmado() { _pedidoConfirmado.value = false }

    class Factory(
        private val carritoRepo: CarritoRepository,
        private val pedidoRepo: PedidoRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CarritoViewModel(carritoRepo, pedidoRepo) as T
    }
}
