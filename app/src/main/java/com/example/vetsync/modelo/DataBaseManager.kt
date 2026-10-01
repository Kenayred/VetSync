package com.example.vetsync.modelo

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Filter
import android.util.Log

class FirebaseDatabaseManager {
    // Instancia principal de Firestore
    private val db = FirebaseFirestore.getInstance()

    fun guardarUsuario(usuario: Usuario, onSuccess: () -> Unit, onError: (String) -> Unit) {
        db.collection("usuarios").document(usuario.id)
            .set(usuario)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Error al guardar usuario") }
    }

    fun buscarUsuario(credencial: String, contrasena: String, onSuccess: (Usuario) -> Unit, onError: (String) -> Unit) {
        // Consulta avanzada: Busca si el texto coincide con el 'username' O con el 'correo'
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

                // Extraemos el usuario y verificamos la contraseña exacta
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
    fun registrarMascota(mascota: Mascota, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val nuevaReferencia = db.collection("mascotas").document()

        val mascotaConId = mascota.copy(id = nuevaReferencia.id)

        nuevaReferencia.set(mascotaConId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun obtenerMascotasPorUsuario(idUsuario: String, onSuccess: (List<Mascota>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("mascotas")
            .whereEqualTo("idDueno", idUsuario)
            .get()
            .addOnSuccessListener { snapshot ->
                Log.d("VETSYNC_DEBUG", "Documentos encontrados en Firestore: ${snapshot.documents.size}")

                val listaMascotas = snapshot.documents.mapNotNull { documento ->
                    val mascotaConvertida = documento.toObject(Mascota::class.java)
                    if (mascotaConvertida == null) {
                        Log.e("VETSYNC_DEBUG", "Fallo al convertir el documento: ${documento.id}. Revisa los campos de tu data class.")
                    }
                    mascotaConvertida
                }

                Log.d("VETSYNC_DEBUG", "Mascotas finales en la lista: ${listaMascotas.size}")
                onSuccess(listaMascotas)
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun actualizarFotoMascota(
        idMascota: String,
        nuevaFotoUrl: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("mascotas").document(idMascota)
            .update("fotoUrl", nuevaFotoUrl)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun insertData(data: Any, path: String, completionListener: com.google.firebase.database.DatabaseReference.CompletionListener) {
        // No hace nada por ahora
    }

    fun readData(path: String, eventListener: com.google.firebase.database.ValueEventListener) {
        // No hace nada por ahora
    }
}