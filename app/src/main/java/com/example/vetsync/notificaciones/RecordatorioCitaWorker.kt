package com.example.vetsync.notificaciones

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class RecordatorioCitaWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    private val db = FirebaseFirestore.getInstance()

    override fun doWork(): Result {

        val citaId = inputData.getString(CLAVE_CITA_ID)
            ?: return Result.failure()

        val usuarioIdEsperado = inputData.getString(
            CLAVE_USUARIO_ID
        )?.takeIf {
            it.isNotBlank()
        } ?: return Result.failure()

        val fechaHoraEsperada = inputData.getLong(
            CLAVE_FECHA_HORA,
            -1L
        )

        if (fechaHoraEsperada <= 0L) {
            return Result.failure()
        }

        return try {

            // Consultamos la cita directamente en el servidor.
            val documento = Tasks.await(
                db.collection("citas")
                    .document(citaId)
                    .get(Source.SERVER)
            )

            // La cita pudo haber sido eliminada.
            if (!documento.exists()) {
                return Result.success()
            }

            // Verificar que la cita pertenezca al usuario
            // que programó originalmente el recordatorio.
            val propietarioReal =
                documento.getString("propietarioId") ?: ""

            if (propietarioReal != usuarioIdEsperado) {
                return Result.success()
            }

            val estado = documento.getString("estado") ?: ""

            // Solo enviamos recordatorios de citas confirmadas.
            if (estado != "CONFIRMADA") {
                return Result.success()
            }

            val fecha = documento.getString("fecha") ?: ""
            val hora = documento.getString("hora") ?: ""

            val fechaHoraActual = convertirFechaHoraAMillis(
                fecha = fecha,
                hora = hora
            ) ?: return Result.success()

            // Evitamos enviar recordatorios antiguos si la cita
            // fue reprogramada después de programar este trabajo.
            if (fechaHoraActual != fechaHoraEsperada) {
                return Result.success()
            }

            // No enviamos recordatorios de citas que ya comenzaron.
            if (System.currentTimeMillis() >= fechaHoraActual) {
                return Result.success()
            }

            val nombreMascota =
                documento.getString("nombreMascota")
                    ?.takeIf { it.isNotBlank() }
                    ?: "tu mascota"

            val mensaje =
                "Recuerda que tienes una cita para $nombreMascota " +
                        "el $fecha a las $hora. ¡Te esperamos!"

            // Nos aseguramos de que exista el canal.
            NotificacionSistema.crearCanal(applicationContext)

            // Generamos un identificador estable para esta cita.
            val idNotificacion = obtenerIdNotificacion(citaId)

            val notificacionMostrada = NotificacionSistema.mostrar(
                context = applicationContext,
                id = idNotificacion,
                titulo = "Recordatorio de cita",
                mensaje = mensaje
            )

            // Si no se pudo mostrar por falta de permiso,
            // dejamos el trabajo como fallido para permitir
            // una nueva programación en la siguiente sincronización.
            if (!notificacionMostrada) {
                return Result.failure()
            }

            Result.success()

        } catch (error: Exception) {

            // Si falla la consulta a Firestore, WorkManager
            // intentará ejecutar nuevamente el trabajo.
            Result.retry()
        }
    }

    private fun convertirFechaHoraAMillis(
        fecha: String,
        hora: String
    ): Long? {

        return runCatching {

            val fechaLocal = LocalDate.parse(
                fecha,
                DateTimeFormatter.ISO_LOCAL_DATE
            )

            val horaLocal = LocalTime.parse(
                hora,
                DateTimeFormatter.ofPattern("HH:mm")
            )

            LocalDateTime.of(
                fechaLocal,
                horaLocal
            )
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

        }.getOrNull()
    }

    private fun obtenerIdNotificacion(
        citaId: String
    ): Int {

        val identificador = citaId.hashCode() and Int.MAX_VALUE

        return if (identificador == 0) {
            1
        } else {
            identificador
        }
    }

    companion object {

        const val CLAVE_CITA_ID = "cita_id"

        const val CLAVE_FECHA_HORA = "fecha_hora_cita"

        const val CLAVE_USUARIO_ID = "usuario_id"
    }
}