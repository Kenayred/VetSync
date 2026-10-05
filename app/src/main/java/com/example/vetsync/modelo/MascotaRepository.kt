package com.example.vetsync.modelo

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Filter
import android.util.Log

class MascotaRepository{

    private val db = FirebaseFirestore.getInstance()

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

    fun eliminarMascotaa(
        idMascota: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ){
        db.collection("mascotas").document(idMascota)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }
}