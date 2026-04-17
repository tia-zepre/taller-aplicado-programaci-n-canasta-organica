package com.example.huertohogardefinitiveedition.data.repository

import com.example.huertohogardefinitiveedition.data.dao.ProductoDao
import com.example.huertohogardefinitiveedition.data.model.Producto
import kotlinx.coroutines.flow.Flow


class ProductoRepository (private val productoDao: ProductoDao){

    suspend fun insertarProducto(producto: Producto){
        productoDao.insertarProducto(producto)
    }

    fun obtenerProductos(): Flow<List<Producto>>{
        return productoDao.obtenerProductos()
    }

}// fin class