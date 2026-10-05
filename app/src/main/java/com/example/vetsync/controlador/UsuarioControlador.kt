package com.example.vetsync.controlador

import android.util.Patterns
import com.example.vetsync.modelo.UsuarioRepository
import com.example.vetsync.modelo.Usuario
import com.example.vetsync.modelo.RolUsuario
import com.example.vetsync.modelo.SesionUsuario
import java.util.UUID
import org.mindrot.jbcrypt.BCrypt

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
        fotoUrl: String,
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

        dbManager.verificarCorreoExistente(
            correo = correo,
            onDisponible = {
                val nuevoId = UUID.randomUUID().toString()
                val contrasenaHasheada = BCrypt.hashpw(contrasena, BCrypt.gensalt())

                val nuevoUsuario = Usuario(
                    id = nuevoId,
                    username = username.trim(),
                    contrasena = contrasenaHasheada,
                    nombre = nombreCompleto.trim(),
                    correo = correo.trim(),
                    telefono = telefono.trim(),
                    rol = RolUsuario.CLIENTE,
                    fotoUrl = fotoUrl
                )

                dbManager.guardarUsuario(
                    usuario = nuevoUsuario,
                    onSuccess = { onSuccess() },
                    onError = { error -> onError(error) }
                )
            },
            onOcupado = {
                onError("Este correo electrónico ya está registrado.")
            },
            onError = { errorMensaje ->
                onError(errorMensaje)
            }
        )
    }

    fun actualizarPerfil(
        nuevoNombre: String,
        nuevoUsername: String,
        nuevoTelefono: String,
        nuevoCorreo: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val usuarioActual = SesionUsuario.usuarioActual

        if (usuarioActual == null) {
            onError("No hay una sesión activa.")
            return
        }

        // Validaciones básicas
        if (nuevoNombre.isBlank() || nuevoUsername.isBlank() || nuevoCorreo.isBlank()) {
            onError("El nombre, username y correo son obligatorios.")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(nuevoCorreo.trim()).matches()) {
            onError("Ingresa un correo electrónico válido.")
            return
        }

        val actualizaciones = mapOf(
            "nombre" to nuevoNombre.trim(),
            "username" to nuevoUsername.trim(),
            "telefono" to nuevoTelefono.trim(),
            "correo" to nuevoCorreo.trim()
        )

        dbManager.actualizarDatosUsuario(
            idUsuario = usuarioActual.id,
            datosActualizados = actualizaciones,
            onSuccess = {
                //Update de compose para actualizar datos al instante
                val usuarioModificado = usuarioActual.copy(
                    nombre = nuevoNombre.trim(),
                    username = nuevoUsername.trim(),
                    telefono = nuevoTelefono.trim(),
                    correo = nuevoCorreo.trim()
                )
                SesionUsuario.iniciarSesion(usuarioModificado)

                onSuccess()
            },
            onFailure = { error ->
                onError(error.message ?: "Error al actualizar el perfil.")
            }
        )
    }

    fun actualizarFotoDePerfil(
        nuevaFotoBase64: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val usuarioActual = SesionUsuario.usuarioActual

        if (usuarioActual == null) {
            onError("No hay una sesión activa.")
            return
        }

        if (nuevaFotoBase64.isBlank()) {
            onError("La imagen seleccionada no es válida.")
            return
        }

        dbManager.actualizarFotoPerfil(
            idUsuario = usuarioActual.id,
            nuevaFotoUrl = nuevaFotoBase64,
            onSuccess = {
                val usuarioModificado = usuarioActual.copy(fotoUrl = nuevaFotoBase64)
                SesionUsuario.iniciarSesion(usuarioModificado)

                onSuccess()
            },
            onFailure = { error ->
                onError(error.message ?: "Error al actualizar la foto en la base de datos.")
            }
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