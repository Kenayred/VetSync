package com.example.vetsync.controlador

import android.util.Patterns
import com.example.vetsync.modelo.UsuarioRepository
import com.example.vetsync.modelo.Usuario
import com.example.vetsync.modelo.RolUsuario
import com.example.vetsync.modelo.SesionUsuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class UsuarioControlador {

    private val dbManager = UsuarioRepository()
    private val auth = FirebaseAuth.getInstance()

    private fun completarInicioSesionConPerfil(
        usuarioEncontrado: Usuario,
        onSuccess: (Usuario) -> Unit,
        onError: (String) -> Unit
    ) {
        val usuarioFirebase = auth.currentUser

        if (
            usuarioFirebase == null ||
            usuarioFirebase.uid != usuarioEncontrado.id
        ) {
            onError("No se pudo validar la sesión de Firebase.")
            return
        }

        // Actualizamos los datos locales de Firebase Authentication.
        usuarioFirebase.reload()
            .addOnSuccessListener {

                val correoFirebase = auth.currentUser?.email
                    ?.trim()
                    .orEmpty()

                val correoFinal = correoFirebase.ifBlank {
                    usuarioEncontrado.correo
                }

                val usuarioFinal = usuarioEncontrado.copy(
                    id = usuarioFirebase.uid,
                    correo = correoFinal
                )

                // Si el correo ya coincide, no necesitamos escribir nuevamente.
                if (correoFinal == usuarioEncontrado.correo) {
                    SesionUsuario.iniciarSesion(usuarioFinal)
                    onSuccess(usuarioFinal)
                    return@addOnSuccessListener
                }

                // Sincronizamos Firestore con el correo verificado en Auth.
                dbManager.actualizarDatosUsuario(
                    idUsuario = usuarioFirebase.uid,
                    datosActualizados = mapOf(
                        "correo" to correoFinal
                    ),
                    onSuccess = {
                        SesionUsuario.iniciarSesion(usuarioFinal)
                        onSuccess(usuarioFinal)
                    },
                    onFailure = { error ->
                        onError(
                            error.message
                                ?: "No se pudo sincronizar el correo de tu cuenta."
                        )
                    }
                )
            }
            .addOnFailureListener { error ->
                onError(
                    error.message
                        ?: "No se pudo actualizar la información de autenticación."
                )
            }
    }

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
                            completarInicioSesionConPerfil(
                                usuarioEncontrado = usuarioEncontrado,
                                onSuccess = onSuccess,
                                onError = onError
                            )
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

                if (firebaseUser == null) {
                    onError(
                        "No se pudo recuperar la cuenta autenticada."
                    )
                    return@addOnSuccessListener
                }

                dbManager.obtenerUsuarioPorIdConResultado(
                    id = firebaseUser.uid,

                    // El perfil ya existe: respetamos sus datos y su rol.
                    onSuccess = { usuarioExistente ->
                        completarInicioSesionConPerfil(
                            usuarioEncontrado = usuarioExistente,
                            onSuccess = onSuccess,
                            onError = onError
                        )
                    },

                    // Solo creamos un perfil cuando Firestore confirma
                    // que realmente no existe.
                    onNotFound = {

                        val correoGoogle =
                            firebaseUser.email?.trim().orEmpty()

                        if (correoGoogle.isBlank()) {
                            onError(
                                "No se pudo recuperar el correo de la cuenta de Google."
                            )
                        } else {

                            val nuevoUsuario = Usuario(
                                id = firebaseUser.uid,
                                username = correoGoogle.substringBefore("@"),
                                contrasena = "",
                                nombre = firebaseUser.displayName
                                    ?: "Usuario de Google",
                                correo = correoGoogle,
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

                                onError = { error ->
                                    onError(error)
                                }
                            )
                        }
                    },

                    // Un error de conexión o permisos ya no crea ni
                    // sobrescribe perfiles.
                    onFailure = { error ->

                        onError(
                            error.message
                                ?: "No se pudieron recuperar los datos de tu cuenta."
                        )
                    }
                )
            }
            .addOnFailureListener { error ->

                onError(
                    "Error al autenticar con Google: " +
                            (error.localizedMessage ?: "Inténtalo nuevamente.")
                )
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

                val usuarioRestaurado = usuarioEncontrado.copy(
                    id = usuarioFirebase.uid
                )

                completarInicioSesionConPerfil(
                    usuarioEncontrado = usuarioRestaurado,
                    onSuccess = onSuccess,
                    onError = onError
                )
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
        onError: (String) -> Unit,
        nuevoUbicacion: String? = null
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
        val ubicacionLimpia = (
                nuevoUbicacion ?: usuarioActual.ubicacion
                ).trim()

        if (ubicacionLimpia.length > 150) {
            onError(
                "La ubicación no debe superar los 150 caracteres."
            )
            return
        }

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

        // 5. Verificar la sesión de Firebase.
        val usuarioFirebase = auth.currentUser

        if (
            usuarioFirebase == null ||
            usuarioFirebase.uid != usuarioActual.id
        ) {
            onError("La sesión no es válida. Inicia sesión nuevamente.")
            return
        }

        val correoActualFirebase = usuarioFirebase.email
            ?.trim()
            .orEmpty()

        if (correoActualFirebase.isBlank()) {
            onError("No se pudo recuperar el correo de tu cuenta.")
            return
        }

// 6. Comprobar si realmente se desea cambiar el correo.
        val requiereVerificacionCorreo =
            correoLimpio != correoActualFirebase

// 7. Verificar la disponibilidad del nombre de usuario.
        val verificarUsername: () -> Unit = {

            dbManager.verificarUsernameExistente(
                username = usernameLimpio,
                idUsuarioExcluir = usuarioActual.id,

                onDisponible = {

                    // Guardamos los demás datos sin adelantar el correo nuevo.
                    val actualizaciones = mapOf(
                        "nombre" to nombreLimpio,
                        "username" to usernameLimpio,
                        "telefono" to telefonoLimpio,
                        "correo" to correoActualFirebase,
                        "ubicacion" to ubicacionLimpia
                    )

                    val guardarDatosPerfil: () -> Unit = {

                        dbManager.actualizarDatosUsuario(
                            idUsuario = usuarioActual.id,
                            datosActualizados = actualizaciones,

                            onSuccess = {

                                val usuarioModificado = usuarioActual.copy(
                                    nombre = nombreLimpio,
                                    username = usernameLimpio,
                                    telefono = telefonoLimpio,
                                    correo = correoActualFirebase,
                                    ubicacion = ubicacionLimpia
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
                    }

                    if (requiereVerificacionCorreo) {

                        // Firebase envía el enlace de verificación.
                        usuarioFirebase.verifyBeforeUpdateEmail(correoLimpio)
                            .addOnSuccessListener {

                                guardarDatosPerfil()
                            }
                            .addOnFailureListener { error ->

                                val mensaje = when (error) {

                                    is FirebaseAuthRecentLoginRequiredException -> {
                                        "Por seguridad, cierra sesión e inicia sesión nuevamente antes de cambiar tu correo."
                                    }

                                    is FirebaseAuthUserCollisionException -> {
                                        "El correo electrónico ya pertenece a otra cuenta."
                                    }

                                    else -> {
                                        error.localizedMessage
                                            ?: "No se pudo enviar el correo de verificación."
                                    }
                                }

                                onError(mensaje)
                            }

                    } else {

                        guardarDatosPerfil()
                    }
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
        }

// 8. Verificar la disponibilidad del correo nuevo.
        if (requiereVerificacionCorreo) {

            dbManager.verificarCorreoExistente(
                correo = correoLimpio,
                idUsuarioExcluir = usuarioActual.id,

                onDisponible = {
                    verificarUsername()
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

        } else {

            verificarUsername()
        }
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

    // --------------------------------------------------
    // CONSULTAR CLIENTES DESDE ADMINISTRACIÓN
    // --------------------------------------------------
    fun obtenerClientesAdmin(
        onSuccess: (List<Usuario>) -> Unit,
        onError: (String) -> Unit
    ) {

        // Verificar permisos administrativos.
        if (
            SesionUsuario.usuarioActual?.rol !=
            RolUsuario.ADMINISTRADOR
        ) {
            onError(
                "No tienes permisos para consultar los clientes."
            )
            return
        }

        dbManager.obtenerClientes(

            onSuccess = { clientes ->
                onSuccess(clientes)
            },

            onFailure = { error ->
                onError(
                    error.message
                        ?: "No se pudieron recuperar los clientes."
                )
            }
        )
    }

    fun enviarCorreoRecuperacion(
        correo: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val correoLimpio = correo.trim()

        if (correoLimpio.isBlank()) {
            onError("Por favor, ingresa tu correo electrónico.")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correoLimpio).matches()) {
            onError("El formato del correo electrónico no es válido.")
            return
        }

        auth.sendPasswordResetEmail(correoLimpio)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onError("No se pudo enviar el correo: ${e.localizedMessage ?: "Verifica tu conexión."}")
            }
    }

    fun cambiarContrasena(
        contrasenaActual: String,
        nuevaContrasena: String,
        confirmarContrasena: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = auth.currentUser
        val email = user?.email

        if (user == null || email.isNullOrBlank()) {
            onError("No se encontró una sesión activa válida.")
            return
        }

        // Verificar si el usuario inició sesión con correo/contraseña (no solo Google)
        val tieneProveedorPassword = user.providerData.any {
            it.providerId == com.google.firebase.auth.EmailAuthProvider.PROVIDER_ID
        }

        if (!tieneProveedorPassword) {
            onError("Tu cuenta está vinculada con Google. Gestiona tu contraseña desde tu cuenta de Google.")
            return
        }

        if (contrasenaActual.isBlank()) {
            onError("Ingresa tu contraseña actual.")
            return
        }

        if (nuevaContrasena.length < 6) {
            onError("La nueva contraseña debe tener al menos 6 caracteres.")
            return
        }

        if (nuevaContrasena != confirmarContrasena) {
            onError("Las contraseñas nuevas no coinciden.")
            return
        }

        if (contrasenaActual == nuevaContrasena) {
            onError("La nueva contraseña debe ser diferente a la actual.")
            return
        }

        val credencial = com.google.firebase.auth.EmailAuthProvider.getCredential(
            email,
            contrasenaActual
        )

        user.reauthenticate(credencial)
            .addOnSuccessListener {
                user.updatePassword(nuevaContrasena)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        onError("No se pudo actualizar la contraseña: ${e.localizedMessage}")
                    }
            }
            .addOnFailureListener { error ->

                val mensaje = when (
                    (error as? com.google.firebase.auth.FirebaseAuthException)?.errorCode
                ) {
                    "ERROR_INVALID_CREDENTIAL",
                    "ERROR_WRONG_PASSWORD" -> {
                        "La contraseña actual es incorrecta."
                    }

                    "ERROR_NETWORK_REQUEST_FAILED" -> {
                        "No se pudo conectar con el servidor. Comprueba tu conexión."
                    }

                    else -> {
                        error.localizedMessage
                            ?: "No se pudo verificar tu contraseña actual."
                    }
                }

                onError(mensaje)
            }
    }

}