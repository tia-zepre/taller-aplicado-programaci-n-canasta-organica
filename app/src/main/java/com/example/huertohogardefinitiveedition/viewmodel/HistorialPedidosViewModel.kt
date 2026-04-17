package com.example.huertohogardefinitiveedition.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huertohogardefinitiveedition.data.dao.ProductoDao
import com.example.huertohogardefinitiveedition.data.model.Producto
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistorialPedidosViewModel(
    private val productoDao: ProductoDao
) : ViewModel() {

    val historial = productoDao.obtenerProductos()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun agregarPedido(producto: Producto) {
        viewModelScope.launch { productoDao.insertarProducto(producto) }
    }
}
