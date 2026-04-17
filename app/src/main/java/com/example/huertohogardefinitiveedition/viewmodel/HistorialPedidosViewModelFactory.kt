package com.example.huertohogardefinitiveedition.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.huertohogardefinitiveedition.data.dao.ProductoDao

class HistorialPedidosViewModelFactory(
    private val productoDao: ProductoDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistorialPedidosViewModel::class.java)) {
            return HistorialPedidosViewModel(productoDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
