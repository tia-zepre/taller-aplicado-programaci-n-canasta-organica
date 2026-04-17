package com.example.huertohogardefinitiveedition.ui.login

// Sirve para manejar los datos de la UI que
// necesita mostrar o controlar

data class LoginUiState (
    val username:String="",
    val password:String="",
    val isLoading:Boolean =false,
    val error:String?=null

)