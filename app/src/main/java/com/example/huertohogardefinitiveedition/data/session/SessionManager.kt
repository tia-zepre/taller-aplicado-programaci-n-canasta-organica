package com.example.huertohogardefinitiveedition.data.session

import com.example.huertohogardefinitiveedition.data.model.Credential

object SessionManager {
    // Usuario autenticado en memoria (para toda la app)
    var currentUser: Credential? = null
        private set

    // Guardar sesión al iniciar
    fun login(user: Credential) {
        currentUser = user
    }

    // Limpiar sesión al salir
    fun logout() {
        currentUser = null
    }

    // Actualizar campos del usuario en sesión (ej: al editar perfil)
    fun updateCurrent(updated: Credential) {
        if (currentUser?.idUsuario == updated.idUsuario) {
            currentUser = updated
        }
    }
}