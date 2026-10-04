package com.example.vetsync.modelo

import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore

class UsuarioRepository {

    private val db = FirebaseFirestore.getInstance()

    fun guardarUsuario(usuario: Usuario, onSuccess: () -> Unit, onError: (String) -> Unit) {
        db.collection("usuarios").document(usuario.id)
            .set(usuario)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Error al guardar usuario") }
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
                    if (user != null && user.contrasena == contrasena) {
                        usuarioValido = user
                        break
                    }
                }

                if (usuarioValido != null) {
                    onSuccess(usuarioValido)
                } else {
                    onError("Contraseña incorrecta.")
                }
            }
            .addOnFailureListener { e -> onError("Error de conexión: ${e.message}") }
    }

}