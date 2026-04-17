package com.example.huertohogardefinitiveedition.data.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector

// Representa un producto dentro de una categoría
data class ProductoItem(
    val nombre: String,
    val precio: String,
    val descripcion: String,
    val stock: Int, // <-- ¡CAMBIO AÑADIDO!
    @DrawableRes val imagenResId: Int
)


// Representa una categoría completa
data class Categoria(
    val nombre: String,
    val icono: ImageVector,
    val productos: List<ProductoItem>
)
    