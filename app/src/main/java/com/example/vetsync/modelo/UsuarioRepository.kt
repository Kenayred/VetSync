package com.example.vetsync.modelo

import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import org.mindrot.jbcrypt.BCrypt

class UsuarioRepository {

    private val db = FirebaseFirestore.getInstance()

    fun guardarUsuario(usuario: Usuario, onSuccess: () -> Unit, onError: (String) -> Unit) {
        db.collection("usuarios").document(usuario.id)
            .set(usuario)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Error al guardar usuario") }
    }

    fun verificarCorreoExistente(
        correo: String,
        onDisponible: () -> Unit,
        onOcupado: () -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("usuarios")
            .whereEqualTo("correo", correo.trim())
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    onDisponible()
                } else {
                    onOcupado()
                }
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Error al verificar la disponibilidad del correo.")
            }
    }
    fun buscarUsuario(credencial: String, contrasena: String, onSuccess: (Usuario) -> Unit, onError: (String) -> Unit) {
        db.collection("usuarios")
            .where(
                Filter.or(
                    Filter.equalTo("username", credencial),
                    Filter.equalTo("correo", credencial)
                )
            )
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    onError("Credenciales incorrectas o usuario no registrado.")
                    return@addOnSuccessListener
                }

                var usuarioValido: Usuario? = null

                for (doc in snapshot.documents) {
                    val user = doc.toObject(Usuario::class.java)

                    if (user != null) {
                        try {
                            if (BCrypt.checkpw(contrasena, user.contrasena)) {
                                usuarioValido = user
                                break
                            }
                        } catch (e: Exception) {

                        }
                    }
                }

                if (usuarioValido != null) {
                    onSuccess(usuarioValido)
                } else {

                    onError("Credenciales incorrectas.")
                }
            }
            .addOnFailureListener { e -> onError("Error de conexión: ${e.message}") }
    }

    fun actualizarDatosUsuario(
        idUsuario: String,
        datosActualizados: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("usuarios").document(idUsuario)
            .update(datosActualizados)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun actualizarFotoPerfil(
        idUsuario: String,
        nuevaFotoUrl: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("usuarios").document(idUsuario)
            .update("fotoUrl", nuevaFotoUrl)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

}