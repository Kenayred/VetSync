package com.example.vetsync.modelo

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.firestore.ListenerRegistration

class CitaRepository {

    private val db = FirebaseFirestore.getInstance()

    private val coleccionCitas = "citas"

    fun registrarCita(
        cita: Cita,
        onSuccess: (Cita) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (cita.propietarioId.isBlank()) {
            onFailure(
                IllegalArgumentException(
                    "No se puede registrar una cita sin propietario."
                )
            )
            return
        }

        if (cita.mascotaId.isBlank() || cita.servicioId.isBlank()) {
            onFailure(
                IllegalArgumentException(
                    "La mascota y el servicio son obligatorios."
                )
            )
            return
        }

        val referencia = db.collection(coleccionCitas).document()

        val citaNueva = cita.copy(
            id = referencia.id,
            fechaCreacion = System.currentTimeMillis()
        )

        // Guardamos la cita y su notificación en una sola operación.
        val lote = db.batch()

        lote.set(
            referencia,
            convertirAMapa(citaNueva)
        )

        agregarNotificacionAlLote(
            lote = lote,
            usuarioId = citaNueva.propietarioId,
            mascotaId = citaNueva.mascotaId,
            citaId = citaNueva.id,
            tipo = "CITA_SOLICITADA",
            titulo = "Solicitud de cita recibida",
            mensaje = "Tu solicitud de cita para ${citaNueva.nombreMascota} " +
                    "fue recibida para el ${citaNueva.fecha} a las " +
                    "${citaNueva.hora}. Está pendiente de confirmación."
        )

        lote.commit()
            .addOnSuccessListener {
                onSuccess(citaNueva)
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
    }

    fun obtenerCitasPorPropietario(
        idPropietario: String,
        onSuccess: (List<Cita>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (idPropietario.isBlank()) {
            onSuccess(emptyList())
            return
        }

        db.collection(coleccionCitas)
            .whereEqualTo("propietarioId", idPropietario)
            .get()
            .addOnSuccessListener { snapshot ->

                val citas = snapshot.documents
                    .mapNotNull { document ->
                        convertirCita(document)
                    }
                    .sortedWith(
                        compareBy<Cita> { it.fecha }
                            .thenBy { it.hora }
                    )

                onSuccess(citas)
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
    }

    // --------------------------------------------------
    // OBSERVAR CITAS DEL PROPIETARIO EN TIEMPO REAL
    // --------------------------------------------------

    fun observarCitasPorPropietario(
        idPropietario: String,
        onSuccess: (List<Cita>) -> Unit,
        onFailure: (Exception) -> Unit
    ): ListenerRegistration? {

        if (idPropietario.isBlank()) {
            onFailure(
                IllegalArgumentException(
                    "No se pudo identificar al propietario."
                )
            )
            return null
        }

        return db.collection(coleccionCitas)
            .whereEqualTo("propietarioId", idPropietario)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    onFailure(error)
                    return@addSnapshotListener
                }

                val citas = snapshot?.documents
                    ?.mapNotNull { documento ->
                        convertirCita(documento)
                    }
                    ?.sortedWith(
                        compareBy<Cita> { it.fecha }
                            .thenBy { it.hora }
                    )
                    ?: emptyList()

                onSuccess(citas)
            }
    }

    fun obtenerResumenCitasDiaAdmin(
        fechaActual: String,
        onSuccess: (ResumenCitas) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (fechaActual.isBlank()) {
            onSuccess(ResumenCitas())
            return
        }

        db.collection(coleccionCitas)
            .whereEqualTo("fecha", fechaActual)
            .get()
            .addOnSuccessListener { snapshots ->
                var pendientes = 0
                var confirmadas = 0
                var completadas = 0
                var canceladas = 0

                // Convertimos los documentos de Firebase directamente a tu modelo Cita
                val listaCitas = snapshots.toObjects(Cita::class.java)

                // Clasificamos cada cita según su estado
                for (cita in listaCitas) {
                    when (cita.estado) {
                        EstadoCita.PENDIENTE -> pendientes++
                        EstadoCita.CONFIRMADA -> confirmadas++
                        EstadoCita.COMPLETADA -> completadas++
                        EstadoCita.CANCELADA -> canceladas++
                    }
                }

                // Empaquetamos los resultados
                val resumen = ResumenCitas(
                    total = listaCitas.size,
                    pendientes = pendientes,
                    confirmadas = confirmadas,
                    completadas = completadas,
                    canceladas = canceladas
                )

                onSuccess(resumen)
            }
            .addOnFailureListener { excepcion ->
                onFailure(excepcion)
            }
    }

    fun obtenerCitasPorMascota(
        idMascota: String,
        onSuccess: (List<Cita>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (idMascota.isBlank()) {
            onSuccess(emptyList())
            return
        }

        db.collection(coleccionCitas)
            .whereEqualTo("mascotaId", idMascota)
            .get()
            .addOnSuccessListener { snapshot ->

                val citas = snapshot.documents
                    .mapNotNull { document ->
                        convertirCita(document)
                    }
                    .sortedWith(
                        compareByDescending<Cita> { it.fecha }
                            .thenByDescending { it.hora }
                    )

                onSuccess(citas)
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
    }

    fun obtenerCitasPorFecha(
        fecha: String,
        onSuccess: (List<Cita>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (fecha.isBlank()) {
            onFailure(
                IllegalArgumentException(
                    "La fecha es obligatoria para consultar las citas."
                )
            )
            return
        }

        db.collection(coleccionCitas)
            .whereEqualTo("fecha", fecha)
            .get()
            .addOnSuccessListener { snapshot ->

                val citas = snapshot.documents
                    .mapNotNull { document ->
                        convertirCita(document)
                    }

                onSuccess(citas)
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
    }

    fun obtenerTodasLasCitasAdmin(
        onSuccess: (List<Cita>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(coleccionCitas)
            .get()
            .addOnSuccessListener { snapshot ->

                val citas = snapshot.documents
                    .mapNotNull { documento ->
                        convertirCita(documento)
                    }
                    .sortedWith(
                        compareBy<Cita> { it.fecha }
                            .thenBy { it.hora }
                    )

                onSuccess(citas)
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
    }

    fun verificarHorarioDisponible(
        fecha: String,
        hora: String,
        idCitaExcluir: String? = null,
        onResult: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (fecha.isBlank() || hora.isBlank()) {
            onFailure(
                IllegalArgumentException(
                    "La fecha y la hora son obligatorias."
                )
            )
            return
        }

        db.collection(coleccionCitas)
            .whereEqualTo("fecha", fecha)
            .get()
            .addOnSuccessListener { snapshot ->

                val horarioOcupado = snapshot.documents
                    .mapNotNull { document ->
                        convertirCita(document)
                    }
                    .any { cita ->

                        cita.id != idCitaExcluir &&
                                cita.hora == hora &&
                                (
                                        cita.estado == EstadoCita.PENDIENTE ||
                                                cita.estado == EstadoCita.CONFIRMADA
                                        )
                    }

                onResult(!horarioOcupado)
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
    }

    fun actualizarEstado(
        idCita: String,
        nuevoEstado: EstadoCita,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (idCita.isBlank()) {
            onFailure(
                IllegalArgumentException(
                    "El identificador de la cita es obligatorio."
                )
            )
            return
        }

        val referencia = db.collection(coleccionCitas)
            .document(idCita)

        // Primero consultamos los datos de la cita.
        referencia.get()
            .addOnSuccessListener { documento ->

                if (!documento.exists()) {
                    onFailure(
                        IllegalStateException(
                            "La cita seleccionada no existe."
                        )
                    )
                    return@addOnSuccessListener
                }

                val estadoActual = documento.getString("estado")
                    ?: EstadoCita.PENDIENTE.name

                // Evitamos generar notificaciones duplicadas
                // cuando se intenta establecer el mismo estado.
                if (estadoActual == nuevoEstado.name) {
                    onSuccess()
                    return@addOnSuccessListener
                }

                val propietarioId =
                    documento.getString("propietarioId") ?: ""

                val mascotaId =
                    documento.getString("mascotaId") ?: ""

                val nombreMascota =
                    documento.getString("nombreMascota")
                        .takeUnless { it.isNullOrBlank() }
                        ?: "tu mascota"

                val fecha = documento.getString("fecha") ?: ""
                val hora = documento.getString("hora") ?: ""

                // Definimos la notificación según el nuevo estado.
                val datosNotificacion = when (nuevoEstado) {

                    EstadoCita.CONFIRMADA -> Triple(
                        "CITA_CONFIRMADA",
                        "Cita confirmada",
                        "La cita de $nombreMascota para el $fecha " +
                                "a las $hora ha sido confirmada."
                    )

                    EstadoCita.CANCELADA -> Triple(
                        "CITA_CANCELADA",
                        "Cita cancelada",
                        "La cita de $nombreMascota programada para el " +
                                "$fecha a las $hora ha sido cancelada."
                    )

                    EstadoCita.COMPLETADA -> Triple(
                        "CITA_COMPLETADA",
                        "Atención completada",
                        "La atención de $nombreMascota del día $fecha " +
                                "ha sido registrada como completada."
                    )

                    EstadoCita.PENDIENTE -> null
                }

                if (datosNotificacion != null && propietarioId.isBlank()) {
                    onFailure(
                        IllegalStateException(
                            "No se encontró el propietario de la cita."
                        )
                    )
                    return@addOnSuccessListener
                }

                // Actualizamos el estado y creamos la notificación
                // dentro del mismo lote.
                val lote = db.batch()

                lote.update(
                    referencia,
                    "estado",
                    nuevoEstado.name
                )

                datosNotificacion?.let { datos ->

                    agregarNotificacionAlLote(
                        lote = lote,
                        usuarioId = propietarioId,
                        mascotaId = mascotaId,
                        citaId = idCita,
                        tipo = datos.first,
                        titulo = datos.second,
                        mensaje = datos.third
                    )
                }

                lote.commit()
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

    fun actualizarHorario(
        idCita: String,
        nuevaFecha: String,
        nuevaHora: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (
            idCita.isBlank() ||
            nuevaFecha.isBlank() ||
            nuevaHora.isBlank()
        ) {
            onFailure(
                IllegalArgumentException(
                    "La cita, la fecha y la hora son obligatorias."
                )
            )
            return
        }

        val referencia = db.collection(coleccionCitas)
            .document(idCita)

        referencia.get()
            .addOnSuccessListener { documento ->

                if (!documento.exists()) {
                    onFailure(
                        IllegalStateException(
                            "La cita seleccionada no existe."
                        )
                    )
                    return@addOnSuccessListener
                }

                val propietarioId =
                    documento.getString("propietarioId") ?: ""

                if (propietarioId.isBlank()) {
                    onFailure(
                        IllegalStateException(
                            "No se encontró el propietario de la cita."
                        )
                    )
                    return@addOnSuccessListener
                }

                val mascotaId =
                    documento.getString("mascotaId") ?: ""

                val nombreMascota =
                    documento.getString("nombreMascota")
                        .takeUnless { it.isNullOrBlank() }
                        ?: "tu mascota"

                val fechaAnterior =
                    documento.getString("fecha") ?: ""

                val horaAnterior =
                    documento.getString("hora") ?: ""

                if (
                    fechaAnterior == nuevaFecha &&
                    horaAnterior == nuevaHora
                ) {
                    onSuccess()
                    return@addOnSuccessListener
                }

                val lote = db.batch()

                lote.update(
                    referencia,
                    mapOf(
                        "fecha" to nuevaFecha,
                        "hora" to nuevaHora
                    )
                )

                agregarNotificacionAlLote(
                    lote = lote,
                    usuarioId = propietarioId,
                    mascotaId = mascotaId,
                    citaId = idCita,
                    tipo = "CITA_REPROGRAMADA",
                    titulo = "Cita reprogramada",
                    mensaje = "La cita de $nombreMascota cambió de " +
                            "$fechaAnterior a las $horaAnterior a " +
                            "$nuevaFecha a las $nuevaHora."
                )

                lote.commit()
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

    private fun agregarNotificacionAlLote(
        lote: WriteBatch,
        usuarioId: String,
        mascotaId: String,
        citaId: String,
        tipo: String,
        titulo: String,
        mensaje: String
    ) {
        val referenciaNotificacion = db
            .collection("notificaciones")
            .document()

        val notificacion = mapOf(
            "usuarioId" to usuarioId,
            "mascotaId" to mascotaId,
            "citaId" to citaId,
            "tipo" to tipo,
            "titulo" to titulo,
            "mensaje" to mensaje,
            "leida" to false,
            "fechaCreacion" to System.currentTimeMillis()
        )

        lote.set(
            referenciaNotificacion,
            notificacion
        )
    }

    private fun convertirAMapa(
        cita: Cita
    ): Map<String, Any> {
        return mapOf(
            "mascotaId" to cita.mascotaId,
            "propietarioId" to cita.propietarioId,
            "servicioId" to cita.servicioId,
            "nombreMascota" to cita.nombreMascota,
            "nombreServicio" to cita.nombreServicio,
            "fecha" to cita.fecha,
            "hora" to cita.hora,
            "estado" to cita.estado.name,
            "costo" to cita.costo,
            "motivo" to cita.motivo,
            "veterinario" to cita.veterinario,
            "fechaCreacion" to cita.fechaCreacion
        )
    }

    private fun convertirCita(
        document: DocumentSnapshot
    ): Cita? {
        if (!document.exists()) {
            return null
        }

        val estadoTexto = document.getString("estado")
            ?: EstadoCita.PENDIENTE.name

        val estado = runCatching {
            EstadoCita.valueOf(estadoTexto)
        }.getOrDefault(EstadoCita.PENDIENTE)

        val costo = document.getDouble("costo")
            ?: document.getLong("costo")?.toDouble()
            ?: 0.0

        return Cita(
            id = document.id,
            mascotaId = document.getString("mascotaId") ?: "",
            propietarioId = document.getString("propietarioId") ?: "",
            servicioId = document.getString("servicioId") ?: "",
            nombreMascota = document.getString("nombreMascota") ?: "",
            nombreServicio = document.getString("nombreServicio") ?: "",
            fecha = document.getString("fecha") ?: "",
            hora = document.getString("hora") ?: "",
            estado = estado,
            costo = costo,
            motivo = document.getString("motivo") ?: "",
            veterinario = document.getString("veterinario") ?: "Veterinario",
            fechaCreacion = document.getLong("fechaCreacion") ?: 0L
        )
    }

    fun obtenerProximosPacientes(
        fechaActual: String,
        onSuccess: (List<CitaConMascota>) -> Unit,
        onFailure: (Exception)->Unit
    ){
        db.collection(coleccionCitas)
            .whereEqualTo("fecha", fechaActual)
            .whereEqualTo("estado", EstadoCita.CONFIRMADA.name)
            .get()
            .addOnSuccessListener{snapshots ->
                val citas = snapshots.toObjects(Cita::class.java)

                val citasOrdenadas = citas.sortedBy{it.hora}
                if(citasOrdenadas.isEmpty()){
                    onSuccess(emptyList())
                    return@addOnSuccessListener
                }

                val listaFinal = mutableListOf<CitaConMascota>()
                var consultasCompletadas = 0

                citasOrdenadas.forEach { cita ->
                    db.collection("mascotas").document(cita.mascotaId).get()
                        .addOnSuccessListener { mascota ->
                            val raza = mascota.getString("raza") ?: "N/D"
                            val edad = mascota.getString("edad") ?: "N/D"

                            listaFinal.add(CitaConMascota(cita,raza,edad))
                            consultasCompletadas++

                            if(consultasCompletadas == citasOrdenadas.size)
                                onSuccess(listaFinal.sortedBy{it.cita.hora})
                        }
                        .addOnFailureListener {
                            listaFinal.add(CitaConMascota(cita,"N/D",""))
                            consultasCompletadas++
                            if(consultasCompletadas == citasOrdenadas.size){
                                onSuccess(listaFinal.sortedBy {it.cita.hora})
                            }
                        }
                }
            }
            .addOnFailureListener { onFailure(it) }
    }

}