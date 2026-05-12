package com.example.cestaOganicaIA.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cestaOganicaIA.data.model.Credential
import com.example.cestaOganicaIA.data.repository.AuthRepository
import com.example.cestaOganicaIA.data.repository.UserRepository
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val authRepo = AuthRepository()
    
    var isLoading by mutableStateOf(false)
        private set
    var errorMsg by mutableStateOf<String?>(null)
        private set
    var registrationSuccess by mutableStateOf(false)
        private set

    fun register(nombre: String, correo: String, usuario: String, telefono: String, direccion: String, clave: String) {
        viewModelScope.launch {
            isLoading = true
            errorMsg = null
            
            val authResult = authRepo.register(correo, clave)
            authResult.onSuccess { firebaseUser ->
                val newProfile = Credential(
                    uid = firebaseUser.uid,
                    nombre = nombre,
                    correo = correo,
                    usuario = usuario,
                    telefono = telefono,
                    direccion = direccion
                )
                UserRepository.saveProfile(newProfile).onSuccess {
                    registrationSuccess = true
                }.onFailure {
                    errorMsg = "Perfil creado, pero error al guardar datos: ${it.message}"
                }
            }.onFailure {
                errorMsg = it.message ?: "Error al crear cuenta"
            }
            isLoading = false
        }
    }

    fun resetError() { errorMsg = null }
}
