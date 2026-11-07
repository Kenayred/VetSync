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

    fun editarMascota(
        mascota: Mascota,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("mascotas")
            .document(mascota.id)
            .set(mascota)
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

    fun obtenerMascotasAdmin(
        onSuccess: (List<MascotaConDueno>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection("mascotas").get()
            .addOnSuccessListener { snap ->
                val mascotas = snap.toObjects(Mascota::class.java)
                Log.e("PRUEBA_MASCOTAS", "Repositorio: Descargadas ${mascotas.size} mascotas brutas") // LOG 2

                if (mascotas.isEmpty()) {
                    onSuccess(emptyList())
                    return@addOnSuccessListener
                }

                val listaFinal = mutableListOf<MascotaConDueno>()
                var consultasCompletadas = 0 // Nuestro contador maestro

                mascotas.forEachIndexed { index, items ->
                    val numero = index + 1
                    Log.e("PRUEBA_MASCOTAS", "Evaluando mascota $numero: ${items.nombre} | ID Dueño: '${items.idDueno}'")

                    // 1. Limpiamos el ID por si se guardó con espacios accidentales
                    val idLimpio = items.idDueno.trim()

                    if (idLimpio.isNotBlank()) {
                        db.collection("usuarios")
                            .document(idLimpio)
                            .get()
                            .addOnSuccessListener { dueno ->
                                val nombreDueno = dueno.getString("nombre") ?: "N/D"
                                listaFinal.add(MascotaConDueno(items, nombreDueno))

                                consultasCompletadas++
                                Log.e("PRUEBA_MASCOTAS", "Éxito mascota $numero. Completadas: $consultasCompletadas/9")

                                if (consultasCompletadas == mascotas.size) {
                                    Log.e("PRUEBA_MASCOTAS", "¡Terminó el ciclo! Enviando a la vista...")
                                    onSuccess(listaFinal)
                                }
                            }
                            .addOnFailureListener { error ->
                                Log.e("PRUEBA_MASCOTAS", "Fallo mascota $numero: ${error.message}")
                                listaFinal.add(MascotaConDueno(items, "Error al cargar"))

                                consultasCompletadas++
                                if (consultasCompletadas == mascotas.size) {
                                    onSuccess(listaFinal)
                                }
                            }
                    } else {
                        Log.e("PRUEBA_MASCOTAS", "Mascota $numero sin ID de dueño válido.")
                        listaFinal.add(MascotaConDueno(items, "Sin dueño asignado"))

                        consultasCompletadas++
                        if (consultasCompletadas == mascotas.size) {
                            Log.e("PRUEBA_MASCOTAS", "¡Terminó el ciclo (con vacíos)! Enviando...")
                            onSuccess(listaFinal)
                        }
                    }
                }
            }
            .addOnFailureListener { onError(it) }
    }

}