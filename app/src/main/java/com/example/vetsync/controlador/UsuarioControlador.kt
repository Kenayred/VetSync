package com.example.vetsync.controlador

import android.util.Patterns
import com.example.vetsync.modelo.FirebaseDatabaseManager
import com.example.vetsync.modelo.Usuario
import com.example.vetsync.modelo.RolUsuario
import com.google.firebase.database.DatabaseReference

class UsuarioControlador {

    private val dbManager = FirebaseDatabaseManager()

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
        // Validaciones
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

        val nuevoId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()

        val nuevoUsuario = Usuario(
            id = nuevoId,
            username = username.trim(),
            contrasena = contrasena,
            nombre = nombreCompleto.trim(),
            correo = correo.trim(),
            telefono = telefono.trim(),
            rol = RolUsuario.CLIENTE
        )

        val path = "usuarios/$nuevoId"

        dbManager.insertData(
            data = nuevoUsuario,
            path = path,
            completionListener = DatabaseReference.CompletionListener { databaseError, _ ->
                if (databaseError == null) {
                    onSuccess()
                } else {
                    onError("Error al registrar: ${databaseError.message}")
                }
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

        dbManager.readData("usuarios", object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                var usuarioEncontrado: Usuario? = null

                for (hijo in snapshot.children) {
                    val usuario = hijo.getValue(Usuario::class.java)
                    if (usuario != null) {
                        val coincideIdentificador = usuario.username.equals(username.trim(), ignoreCase = true) ||
                                usuario.correo.equals(username.trim(), ignoreCase = true)

                        if (coincideIdentificador && usuario.contrasena == password) {
                            usuarioEncontrado = usuario
                            break
                        }
                    }
                }

                if (usuarioEncontrado != null) {
                    onSuccess(usuarioEncontrado)
                } else {
                    onError("Credenciales incorrectas o usuario no registrado.")
                }
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                onError("Error de conexión: ${error.message}")
            }
        })
    }
}