package com.example.vetsync.controlador

import android.util.Patterns
import com.example.vetsync.modelo.UsuarioRepository
import com.example.vetsync.modelo.Usuario
import com.example.vetsync.modelo.RolUsuario
import java.util.UUID

class UsuarioControlador {

    private val dbManager = UsuarioRepository()

    fun registrarUsuario(
        username: String,
        nombreCompleto: String,
        correo: String,
        telefono: String,
        contrasena: String,
        confirmarContrasena: String,
        terminosAceptados: Boolean,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (username.isBlank() || nombreCompleto.isBlank() || correo.isBlank() || contrasena.isBlank()) {
            onError("Por favor, completa todos los campos obligatorios.")
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches()) {
            onError("Ingresa un correo electrónico válido.")
            return
        }
        if (contrasena.length < 6) {
            onError("La contraseña debe tener al menos 6 caracteres.")
            return
        }
        if (contrasena != confirmarContrasena) {
            onError("Las contraseñas no coinciden.")
            return
        }
        if (!terminosAceptados) {
            onError("Debes aceptar los términos y condiciones.")
            return
        }

        val nuevoId = UUID.randomUUID().toString()

        val nuevoUsuario = Usuario(
            id = nuevoId,
            username = username.trim(),
            contrasena = contrasena,
            nombre = nombreCompleto.trim(),
            correo = correo.trim(),
            telefono = telefono.trim(),
            rol = RolUsuario.CLIENTE
        )

        dbManager.guardarUsuario(
            usuario = nuevoUsuario,
            onSuccess = { onSuccess() },
            onError = { error -> onError(error) }
        )
    }

    fun iniciarSesion(
        username: String,
        password: String,
        onSuccess: (Usuario) -> Unit,
        onError: (String) -> Unit
    ) {
        if (username.isBlank() || password.isBlank()) {
            onError("Ingresa tu usuario/correo y contraseña.")
            return
        }

        dbManager.buscarUsuario(
            credencial = username.trim(),
            contrasena = password,
            onSuccess = { usuarioEncontrado -> onSuccess(usuarioEncontrado) },
            onError = { error -> onError(error) }
        )
    }
}