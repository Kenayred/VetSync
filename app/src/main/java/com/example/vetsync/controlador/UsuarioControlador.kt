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
        val usernameLimpio = username.trim()
        val nombreLimpio = nombreCompleto.trim()
        val correoLimpio = correo.trim()
        val telefonoLimpio = telefono.trim()

        // 1. Validar campos obligatorios.
        if (
            usernameLimpio.isBlank() ||
            nombreLimpio.isBlank() ||
            correoLimpio.isBlank() ||
            contrasena.isBlank()
        ) {
            onError("Por favor, completa todos los campos obligatorios.")
            return
        }

        // 2. Validar longitud del nombre.
        if (nombreLimpio.length < 3) {
            onError("El nombre completo debe tener al menos 3 caracteres.")
            return
        }

        // 3. Validar formato del nombre de usuario.
        val formatoUsername = Regex("^[\\p{L}\\p{N}._-]{3,20}$")

        if (!formatoUsername.matches(usernameLimpio)) {
            onError(
                "El nombre de usuario debe tener entre 3 y 20 caracteres " +
                        "y solo puede incluir letras, números, puntos, guiones " +
                        "o guiones bajos."
            )
            return
        }

        // 4. Validar el correo.
        if (!Patterns.EMAIL_ADDRESS.matcher(correoLimpio).matches()) {
            onError("Ingresa un correo electrónico válido.")
            return
        }

        // 5. Validar contraseña.
        if (contrasena.length < 6) {
            onError("La contraseña debe tener al menos 6 caracteres.")
            return
        }

        // 6. Comprobar que las contraseñas coincidan.
        if (contrasena != confirmarContrasena) {
            onError("Las contraseñas no coinciden.")
            return
        }

        // 7. Comprobar aceptación de términos.
        if (!terminosAceptados) {
            onError("Debes aceptar los términos y condiciones.")
            return
        }

        // 8. Comprobar que el correo esté disponible.
        dbManager.verificarCorreoExistente(
            correo = correoLimpio,
            onDisponible = {

                // 9. Comprobar que el nombre de usuario esté disponible.
                dbManager.verificarUsernameExistente(
                    username = usernameLimpio,
                    onDisponible = {

                        val nuevoId = UUID.randomUUID().toString()
                        val contrasenaHasheada =
                            BCrypt.hashpw(contrasena, BCrypt.gensalt())

                        val nuevoUsuario = Usuario(
                            id = nuevoId,
                            username = usernameLimpio,
                            contrasena = contrasenaHasheada,
                            nombre = nombreLimpio,
                            correo = correoLimpio,
                            telefono = telefonoLimpio,
                            rol = RolUsuario.CLIENTE,
                            fotoUrl = fotoUrl
                        )

                        dbManager.guardarUsuario(
                            usuario = nuevoUsuario,
                            onSuccess = {
                                onSuccess()
                            },
                            onError = { mensaje ->
                                onError(mensaje)
                            }
                        )
                    },
                    onOcupado = {
                        onError(
                            "Este nombre de usuario ya está registrado."
                        )
                    },
                    onError = { mensaje ->
                        onError(mensaje)
                    }
                )
            },
            onOcupado = {
                onError("Este correo electrónico ya está registrado.")
            },
            onError = { mensaje ->
                onError(mensaje)
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

        val nombreLimpio = nuevoNombre.trim()
        val usernameLimpio = nuevoUsername.trim()
        val telefonoLimpio = nuevoTelefono.trim()
        val correoLimpio = nuevoCorreo.trim()

        // 1. Validar los campos obligatorios.
        if (
            nombreLimpio.isBlank() ||
            usernameLimpio.isBlank() ||
            correoLimpio.isBlank()
        ) {
            onError("El nombre, el nombre de usuario y el correo son obligatorios.")
            return
        }

        // 2. Validar la longitud del nombre.
        if (nombreLimpio.length < 3) {
            onError("El nombre completo debe tener al menos 3 caracteres.")
            return
        }

        // 3. Validar el formato del nombre de usuario.
        val formatoUsername = Regex("^[\\p{L}\\p{N}._-]{3,20}$")

        if (!formatoUsername.matches(usernameLimpio)) {
            onError(
                "El nombre de usuario debe tener entre 3 y 20 caracteres " +
                        "y solo puede incluir letras, números, puntos, guiones " +
                        "o guiones bajos."
            )
            return
        }

        // 4. Validar el formato del correo electrónico.
        if (!Patterns.EMAIL_ADDRESS.matcher(correoLimpio).matches()) {
            onError("Ingresa un correo electrónico válido.")
            return
        }

        // 5. Comprobar que el correo no pertenezca a otro usuario.
        dbManager.verificarCorreoExistente(
            correo = correoLimpio,
            idUsuarioExcluir = usuarioActual.id,
            onDisponible = {

                // 6. Comprobar que el nombre de usuario esté disponible.
                dbManager.verificarUsernameExistente(
                    username = usernameLimpio,
                    idUsuarioExcluir = usuarioActual.id,
                    onDisponible = {

                        val actualizaciones = mapOf(
                            "nombre" to nombreLimpio,
                            "username" to usernameLimpio,
                            "telefono" to telefonoLimpio,
                            "correo" to correoLimpio
                        )

                        // 7. Guardar los cambios en Firestore.
                        dbManager.actualizarDatosUsuario(
                            idUsuario = usuarioActual.id,
                            datosActualizados = actualizaciones,

                            onSuccess = {
                                val usuarioModificado = usuarioActual.copy(
                                    nombre = nombreLimpio,
                                    username = usernameLimpio,
                                    telefono = telefonoLimpio,
                                    correo = correoLimpio
                                )

                                SesionUsuario.iniciarSesion(usuarioModificado)

                                onSuccess()
                            },

                            onFailure = { error ->
                                onError(
                                    error.message
                                        ?: "Error al actualizar el perfil."
                                )
                            }
                        )
                    },

                    onOcupado = {
                        onError(
                            "Este nombre de usuario ya está registrado por otra cuenta."
                        )
                    },

                    onError = { mensaje ->
                        onError(mensaje)
                    }
                )
            },

            onOcupado = {
                onError(
                    "Este correo electrónico ya está registrado por otra cuenta."
                )
            },

            onError = { mensaje ->
                onError(mensaje)
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