package com.example.vetsync.modelo

import com.google.firebase.firestore.FirebaseFirestore

class NotaClinicaRepository {

    private val db = FirebaseFirestore.getInstance()

    fun obtenerHistorialPorMascota(
        mascotaId: String,
        propietarioId: String,
        onSuccess: (List<NotaClinica>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (mascotaId.isBlank() || propietarioId.isBlank()) {
            onFailure(
                IllegalArgumentException(
                    "La mascota y el propietario son obligatorios."
                )
            )
            return
        }

        val referenciaMascota = db.collection("mascotas")
            .document(mascotaId)

        // Primero comprobamos que la mascota pertenezca al cliente.
        referenciaMascota.get()
            .addOnSuccessListener { documentoMascota ->

                if (!documentoMascota.exists()) {
                    onFailure(
                        IllegalStateException(
                            "La mascota seleccionada no existe."
                        )
                    )
                    return@addOnSuccessListener
                }

                val propietarioReal =
                    documentoMascota.getString("idDueno")

                if (propietarioReal != propietarioId) {
                    onFailure(
                        SecurityException(
                            "No tienes permiso para consultar este historial."
                        )
                    )
                    return@addOnSuccessListener
                }

                // Consultamos las notas clínicas de la mascota.
                referenciaMascota
                    .collection("notas_clinicas")
                    .get()
                    .addOnSuccessListener { snapshot ->

                        val historial = snapshot.documents
                            .mapNotNull { documento ->

                                documento.toObject(
                                    NotaClinica::class.java
                                )?.copy(
                                    id = documento.id,
                                    mascotaId = mascotaId,
                                    propietarioId = propietarioId
                                )
                            }
                            .sortedWith(
                                compareByDescending<NotaClinica> {
                                    it.fecha
                                }.thenByDescending {
                                    it.fechaCreacion
                                }
                            )

                        onSuccess(historial)
                    }
                    .addOnFailureListener { error ->
                        onFailure(error)
                    }
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
    }

    fun registrarNotaClinicaYCompletarCita(
        nota: NotaClinica,
        onSuccess: (NotaClinica) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (
            nota.citaId.isBlank() ||
            nota.mascotaId.isBlank() ||
            nota.propietarioId.isBlank()
        ) {
            onFailure(
                IllegalArgumentException(
                    "La cita, la mascota y el propietario son obligatorios."
                )
            )
            return
        }

        if (
            nota.diagnostico.isBlank() ||
            nota.receta.isBlank()
        ) {
            onFailure(
                IllegalArgumentException(
                    "El diagnóstico y la receta son obligatorios."
                )
            )
            return
        }

        val referenciaCita = db.collection("citas")
            .document(nota.citaId)

        val referenciaNota = db.collection("mascotas")
            .document(nota.mascotaId)
            .collection("notas_clinicas")
            .document()

        val referenciaNotificacion = db.collection("notificaciones")
            .document()

        val fechaCreacion = System.currentTimeMillis()

        db.runTransaction<NotaClinica> { transaccion ->

            // Consultamos la cita antes de realizar las escrituras.
            val documentoCita = transaccion.get(referenciaCita)

            if (!documentoCita.exists()) {
                throw IllegalStateException(
                    "La cita seleccionada no existe."
                )
            }

            val propietarioId =
                documentoCita.getString("propietarioId") ?: ""

            val mascotaId =
                documentoCita.getString("mascotaId") ?: ""

            val estadoActual =
                documentoCita.getString("estado") ?: ""

            // Comprobamos que la nota corresponda a la cita.
            if (
                propietarioId != nota.propietarioId ||
                mascotaId != nota.mascotaId
            ) {
                throw SecurityException(
                    "Los datos de la nota no corresponden a la cita seleccionada."
                )
            }

            // Solo se puede registrar la atención de una cita confirmada.
            if (estadoActual != EstadoCita.CONFIRMADA.name) {
                throw IllegalStateException(
                    "Solo se puede registrar la atención de una cita confirmada."
                )
            }

            val nombreMascota =
                documentoCita.getString("nombreMascota")
                    ?.takeIf { it.isNotBlank() }
                    ?: "tu mascota"

            val fechaCita =
                documentoCita.getString("fecha")
                    ?: nota.fecha

            val fechaHoraCita =
                documentoCita.getString("hora") ?: ""

            if (fechaCita.isBlank()) {
                throw IllegalStateException(
                    "La cita no tiene una fecha válida."
                )
            }

            // Preparamos la nota clínica.
            val notaGuardada = nota.copy(
                id = referenciaNota.id,
                fecha = fechaCita,
                fechaCreacion = fechaCreacion,
                motivo = nota.motivo.trim(),
                diagnostico = nota.diagnostico.trim(),
                receta = nota.receta.trim(),
                observaciones = nota.observaciones
                    ?.trim()
                    ?.takeIf { it.isNotBlank() }
            )

            val datosNota = hashMapOf<String, Any>(
                "mascotaId" to notaGuardada.mascotaId,
                "propietarioId" to notaGuardada.propietarioId,
                "citaId" to notaGuardada.citaId,
                "fecha" to notaGuardada.fecha,
                "motivo" to notaGuardada.motivo,
                "diagnostico" to notaGuardada.diagnostico,
                "receta" to notaGuardada.receta,
                "observaciones" to (
                        notaGuardada.observaciones ?: ""
                        ),
                "fechaCreacion" to notaGuardada.fechaCreacion
            )

            // Los campos opcionales solo se incluyen cuando tienen valor.
            notaGuardada.peso?.let {
                datosNota["peso"] = it
            }

            notaGuardada.temperatura?.let {
                datosNota["temperatura"] = it
            }

            // 1. Guardar la nota en la subcolección de la mascota.
            transaccion.set(
                referenciaNota,
                datosNota
            )

            // 2. Completar la cita.
            transaccion.update(
                referenciaCita,
                "estado",
                EstadoCita.COMPLETADA.name
            )

            // 3. Crear la notificación para el propietario.
            val datosNotificacion = mapOf(
                "usuarioId" to propietarioId,
                "mascotaId" to mascotaId,
                "citaId" to nota.citaId,
                "tipo" to "CITA_COMPLETADA",
                "titulo" to "Atención completada",
                "mensaje" to (
                        "La atención de $nombreMascota del día " +
                                "$fechaCita a las $fechaHoraCita " +
                                "ha sido registrada como completada."
                        ),
                "leida" to false,
                "fechaCreacion" to fechaCreacion
            )

            transaccion.set(
                referenciaNotificacion,
                datosNotificacion
            )

            notaGuardada

        }.addOnSuccessListener { notaGuardada ->

            onSuccess(notaGuardada)

        }.addOnFailureListener { error ->

            onFailure(error)
        }
    }
}