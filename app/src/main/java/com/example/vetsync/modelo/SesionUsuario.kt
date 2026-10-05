package com.example.vetsync.modelo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object SesionUsuario {
    var usuarioActual by mutableStateOf<Usuario?>(null)
        private set

    val primerNombre: String
        get() = usuarioActual?.nombre?.split(" ")?.firstOrNull() ?: "Usuario"

    val idUsuario: String
        get() = usuarioActual?.id ?: ""

    val fotoPerfil: String
        get() = usuarioActual?.fotoUrl ?: ""
    fun iniciarSesion(usuario: Usuario) {
        usuarioActual = usuario
    }

    fun cerrarSesion() {
        usuarioActual = null
    }
}