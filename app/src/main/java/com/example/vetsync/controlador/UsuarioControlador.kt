package com.example.vetsync.controlador

import android.util.Patterns
import com.example.vetsync.modelo.UsuarioRepository
import com.example.vetsync.modelo.Usuario
import com.example.vetsync.modelo.RolUsuario
import com.example.vetsync.modelo.SesionUsuario
import com.google.firebase.Firebase
import java.util.UUID
import org.mindrot.jbcrypt.BCrypt
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class UsuarioControlador {

    private val dbManager = UsuarioRepository()
    private val auth = FirebaseAuth.getInstance()

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

        if (usernameLimpio.isBlank() || nombreLimpio.isBlank() || correoLimpio.isBlank() || contrasena.isBlank()) {
            onError("Por favor, completa todos los campos obligatorios.")
            return
        }
        if (nombreLimpio.length < 3) {
            onError("El nombre completo debe tener al menos 3 caracteres.")
            return
        }
        val formatoUsername = Regex("^[\\p{L}\\p{N}._-]{3,20}$")
        if (!formatoUsername.matches(usernameLimpio)) {
            onError("El nombre de usuario debe tener entre 3 y 20 caracteres y solo puede incluir letras, números, puntos, guiones o guiones bajos.")
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correoLimpio).matches()) {
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
            correo = correoLimpio,
            onDisponible = {
                dbManager.verificarUsernameExistente(
                    username = usernameLimpio,
                    onDisponible = {

                        auth.createUserWithEmailAndPassword(correoLimpio, contrasena)
                            .addOnSuccessListener { authResult ->
                                val firebaseUser = authResult.user
                                if (firebaseUser != null) {

                                    val nuevoUsuario = Usuario(
                                        id = firebaseUser.uid,
                                        username = usernameLimpio,
                                        contrasena = "",
                                        nombre = nombreLimpio,
                                        correo = correoLimpio,
                                        telefono = telefonoLimpio,
                                        rol = RolUsuario.CLIENTE,
                                        fotoUrl = fotoUrl
                                    )

                                    dbManager.guardarUsuario(
                                        usuario = nuevoUsuario,
                                        onSuccess = { onSuccess() },
                                        onError = { mensaje -> onError(mensaje) }
                                    )
                                }
                            }
                            .addOnFailureListener { e ->
                                onError("Error al registrar en Firebase Auth: ${e.localizedMessage}")
                            }
                    },
                    onOcupado = { onError("Este nombre de usuario ya está registrado.") },
                    onError = { mensaje -> onError(mensaje) }
                )
            },
            onOcupado = { onError("Este correo electrónico ya está registrado.") },
            onError = { mensaje -> onError(mensaje) }
        )
    }

    fun iniciarSesion(
        correo: String,
        password: String,
        onSuccess: (Usuario) -> Unit,
        onError: (String) -> Unit
    ) {
        if (correo.isBlank() || password.isBlank()) {
            onError("Ingresa tu correo y contraseña.")
            return
        }

        // 3. Inicio de sesión con Auth
        auth.signInWithEmailAndPassword(correo.trim(), password)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid
                if (uid != null) {
                    // Si el login es exitoso, buscamos sus datos en Firestore
                    dbManager.obtenerUsuarioPorId(
                        id = uid,
                        onSuccess = { usuarioEncontrado ->
                            SesionUsuario.iniciarSesion(usuarioEncontrado)
                            onSuccess(usuarioEncontrado)
                        },
                        onError = { error -> onError(error) }
                    )
                }
            }
            .addOnFailureListener { e ->
                onError("Correo o contraseña incorrectos.")
            }
    }

    fun iniciarSesionConGoogle(
        idToken: String,
        onSuccess: (Usuario) -> Unit,
        onError: (String) -> Unit
    ) {
        val credencial = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credencial)
            .addOnSuccessListener { authResult ->
                val firebaseUser = authResult.user
                if (firebaseUser != null) {

                    dbManager.obtenerUsuarioPorId(
                        id = firebaseUser.uid,
                        onSuccess = { usuarioExistente ->
                            // Ya existía en la base de datos
                            SesionUsuario.iniciarSesion(usuarioExistente)
                            onSuccess(usuarioExistente)
                        },
                        onError = {
                            // No existe (es nuevo), lo registramos automáticamente
                            val nuevoUsuario = Usuario(
                                id = firebaseUser.uid,
                                username = firebaseUser.email?.substringBefore("@") ?: "user_${firebaseUser.uid.take(5)}",
                                contrasena = "",
                                nombre = firebaseUser.displayName ?: "Usuario de Google",
                                correo = firebaseUser.email ?: "",
                                telefono = "",
                                rol = RolUsuario.CLIENTE,
                                fotoUrl = firebaseUser.photoUrl?.toString() ?: ""
                            )

                            dbManager.guardarUsuario(
                                usuario = nuevoUsuario,
                                onSuccess = {
                                    SesionUsuario.iniciarSesion(nuevoUsuario)
                                    onSuccess(nuevoUsuario)
                                },
                                onError = { error -> onError(error) }
                            )
                        }
                    )
                }
            }
            .addOnFailureListener { e ->
                onError("Error al autenticar con Google: ${e.localizedMessage}")
            }
    }

    fun cerrarSesion() {
        auth.signOut()
        SesionUsuario.cerrarSesion()
    }

    fun restaurarSesion(
        onSuccess: (Usuario) -> Unit,
        onNoSession: () -> Unit,
        onError: (String) -> Unit
    ) {
        val usuarioFirebase = auth.currentUser

        // No existe una sesión autenticada.
        if (usuarioFirebase == null) {
            SesionUsuario.cerrarSesion()
            onNoSession()
            return
        }

        // Recuperar el perfil asociado al UID de Firebase.
        dbManager.obtenerUsuarioParaRestaurarSesion(
            id = usuarioFirebase.uid,

            onSuccess = { usuarioEncontrado ->

                // El UID de Firebase es el identificador oficial.
                val usuarioRestaurado = usuarioEncontrado.copy(
                    id = usuarioFirebase.uid
                )

                SesionUsuario.iniciarSesion(usuarioRestaurado)

                onSuccess(usuarioRestaurado)
            },

            onNotFound = {
                // Existe autenticación, pero no existe el perfil.
                cerrarSesion()

                onError(
                    "No se encontró el perfil asociado a tu cuenta. " +
                            "Inicia sesión nuevamente."
                )
            },

            onFailure = { error ->
                onError(
                    error.message
                        ?: "No se pudo recuperar la sesión. Comprueba tu conexión."
                )
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


}