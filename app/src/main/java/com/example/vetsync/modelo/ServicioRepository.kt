package com.example.vetsync.modelo

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class ServicioRepository {

    private val db = FirebaseFirestore.getInstance()

    private val serviciosIniciales = listOf(
        Servicio(
            id = "consulta_general",
            nombre = "Consulta general",
            descripcion = "Evaluación general de la mascota.",
            costo = 30.0
        ),
        Servicio(
            id = "vacunacion",
            nombre = "Vacunación",
            descripcion = "Aplicación de vacunas según indicación veterinaria.",
            costo = 35.0
        ),
        Servicio(
            id = "peluqueria",
            nombre = "Peluquería",
            descripcion = "Servicio de higiene y cuidado estético.",
            costo = 25.0
        ),
        Servicio(
            id = "emergencia",
            nombre = "Emergencia",
            descripcion = "Atención veterinaria de emergencia.",
            costo = 60.0
        )
    )

    fun obtenerServiciosActivos(
        onSuccess: (List<Servicio>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("servicios")
            .get()
            .addOnSuccessListener { snapshot ->

                if (snapshot.isEmpty) {
                    insertarServiciosIniciales(
                        onSuccess = onSuccess,
                        onFailure = onFailure
                    )
                } else {
                    val servicios = snapshot.documents
                        .mapNotNull { document ->
                            convertirServicio(document)
                        }
                        .filter { servicio ->
                            servicio.activo
                        }
                        .sortedBy { servicio ->
                            servicio.nombre
                        }

                    onSuccess(servicios)
                }
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
    }

    fun obtenerServicioActivoPorId(
        idServicio: String,
        onSuccess: (Servicio?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (idServicio.isBlank()) {
            onSuccess(null)
            return
        }

        db.collection("servicios")
            .document(idServicio)
            .get()
            .addOnSuccessListener { document ->

                val servicio = if (document.exists()) {
                    convertirServicio(document)
                } else {
                    null
                }

                onSuccess(servicio?.takeIf { it.activo })
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
    }

    private fun insertarServiciosIniciales(
        onSuccess: (List<Servicio>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val batch = db.batch()

        serviciosIniciales.forEach { servicio ->
            val referencia = db.collection("servicios")
                .document(servicio.id)

            batch.set(
                referencia,
                mapOf(
                    "nombre" to servicio.nombre,
                    "descripcion" to servicio.descripcion,
                    "costo" to servicio.costo,
                    "activo" to servicio.activo
                )
            )
        }

        batch.commit()
            .addOnSuccessListener {
                onSuccess(serviciosIniciales)
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
    }

    private fun convertirServicio(
        document: DocumentSnapshot
    ): Servicio? {
        val nombre = document.getString("nombre")
            ?: return null

        val costo = document.getDouble("costo")
            ?: document.getLong("costo")?.toDouble()
            ?: 0.0

        return Servicio(
            id = document.id,
            nombre = nombre,
            descripcion = document.getString("descripcion") ?: "",
            costo = costo,
            activo = document.getBoolean("activo") ?: true
        )
    }
}