package com.example.vetsync.modelo

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class NotificacionRepository {

    private val db = FirebaseFirestore.getInstance()

    fun observarNotificaciones(
        usuarioId: String,
        onSuccess: (List<Notificacion>) -> Unit,
        onFailure: (Exception) -> Unit
    ): ListenerRegistration? {

        if (usuarioId.isBlank()) {
            onFailure(
                IllegalArgumentException(
                    "No se pudo identificar al usuario."
                )
            )
            return null
        }

        return db.collection("notificaciones")
            .whereEqualTo("usuarioId", usuarioId)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    onFailure(error)
                    return@addSnapshotListener
                }

                val notificaciones = snapshot?.documents
                    ?.mapNotNull { documento ->
                        documento.toObject(
                            Notificacion::class.java
                        )?.copy(
                            id = documento.id
                        )
                    }
                    ?.sortedByDescending {
                        it.fechaCreacion
                    } ?: emptyList()

                onSuccess(notificaciones)
            }
    }

    fun marcarComoLeida(
        usuarioId: String,
        notificacionId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        if (usuarioId.isBlank() || notificacionId.isBlank()) {
            onFailure(
                IllegalArgumentException(
                    "Los datos de la notificación son obligatorios."
                )
            )
            return
        }

        val referencia = db.collection("notificaciones")
            .document(notificacionId)

        referencia.get()
            .addOnSuccessListener { documento ->

                if (
                    !documento.exists() ||
                    documento.getString("usuarioId") != usuarioId
                ) {
                    onFailure(
                        SecurityException(
                            "No tienes permiso para modificar esta notificación."
                        )
                    )
                    return@addOnSuccessListener
                }

                referencia.update("leida", true)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { error ->
                        onFailure(error)
                    }
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
    }

    fun marcarTodasComoLeidas(
        usuarioId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (usuarioId.isBlank()) {
            onFailure(
                IllegalArgumentException(
                    "No se pudo identificar al usuario."
                )
            )
            return
        }

        db.collection("notificaciones")
            .whereEqualTo("usuarioId", usuarioId)
            .whereEqualTo("leida", false)
            .get()
            .addOnSuccessListener { snapshot ->

                val documentos = snapshot.documents

                if (documentos.isEmpty()) {
                    onSuccess()
                    return@addOnSuccessListener
                }

                actualizarLotesNotificaciones(
                    documentos = documentos,
                    inicio = 0,
                    onSuccess = onSuccess,
                    onFailure = onFailure
                )
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
    }

    private fun actualizarLotesNotificaciones(
        documentos: List<com.google.firebase.firestore.DocumentSnapshot>,
        inicio: Int,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (inicio >= documentos.size) {
            onSuccess()
            return
        }

        // Firestore permite hasta 500 operaciones por lote.
        val fin = minOf(inicio + 500, documentos.size)

        val lote = db.batch()

        for (indice in inicio until fin) {
            lote.update(
                documentos[indice].reference,
                "leida",
                true
            )
        }

        lote.commit()
            .addOnSuccessListener {
                actualizarLotesNotificaciones(
                    documentos = documentos,
                    inicio = fin,
                    onSuccess = onSuccess,
                    onFailure = onFailure
                )
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
    }
}