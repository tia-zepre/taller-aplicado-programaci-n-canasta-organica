package com.example.cestaOganicaIA.data.model

import androidx.annotation.DrawableRes

/**
 * Producto que se muestra en el catálogo.
 */
data class ProductoItem(
    val nombre: String,
    val precio: String,
    val descripcion: String,
    val stock: Int,
    @DrawableRes val imagenResId: Int
)
