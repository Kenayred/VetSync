package com.example.vetsync.notificaciones

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.vetsync.modelo.Cita
import com.example.vetsync.modelo.EstadoCita
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

object ProgramadorRecordatorios {

    private const val TAG_RECORDATORIO =
        "vetsync_recordatorio"

    private const val TAG_USUARIO =
        "recordatorios_usuario_"

    private const val HORAS_ANTICIPACION = 24L

    private fun obtenerNombreTrabajo(
        citaId: String
    ): String {

        return "recordatorio_cita_$citaId"
    }

    // --------------------------------------------------
    // PROGRAMAR RECORDATORIO
    // --------------------------------------------------

    fun programarRecordatorio(
        context: Context,
        cita: Cita
    ) {

        val appContext = context.applicationContext

        if (cita.id.isBlank()) {
            return
        }

        if (cita.propietarioId.isBlank()) {
            cancelarRecordatorio(
                appContext,
                cita.id
            )
            return
        }

        // Solo se programan citas confirmadas.
        if (cita.estado != EstadoCita.CONFIRMADA) {
            cancelarRecordatorio(
                appContext,
                cita.id
            )
            return
        }

        val fechaHoraCita = convertirFechaHoraAMillis(
            fecha = cita.fecha,
            hora = cita.hora
        )

        if (
            fechaHoraCita == null ||
            fechaHoraCita <= System.currentTimeMillis()
        ) {
            cancelarRecordatorio(
                appContext,
                cita.id
            )
            return
        }

        val nombreTrabajo = obtenerNombreTrabajo(cita.id)

        val etiquetaFecha =
            "recordatorio_fecha_$fechaHoraCita"

        val workManager = WorkManager.getInstance(appContext)

        // Consultamos si ya existe un trabajo para esta misma fecha.
        val trabajosPendientes =
            workManager.getWorkInfosForUniqueWork(nombreTrabajo)

        trabajosPendientes.addListener({

            val trabajos = runCatching {
                trabajosPendientes.get()
            }.getOrElse {
                emptyList()
            }

            val yaProgramado = trabajos.any { trabajo ->

                trabajo.tags.contains(etiquetaFecha) &&
                        (
                                trabajo.state == WorkInfo.State.ENQUEUED ||
                                        trabajo.state == WorkInfo.State.RUNNING ||
                                        trabajo.state == WorkInfo.State.SUCCEEDED
                                )
            }

            // Evitamos programar nuevamente un recordatorio
            // que ya existe para la misma fecha y hora.
            if (yaProgramado) {
                return@addListener
            }

            val anticipacionMillis =
                TimeUnit.HOURS.toMillis(HORAS_ANTICIPACION)

            val momentoRecordatorio =
                fechaHoraCita - anticipacionMillis

            val retraso = maxOf(
                0L,
                momentoRecordatorio - System.currentTimeMillis()
            )

            val solicitud = OneTimeWorkRequestBuilder<
                    RecordatorioCitaWorker
                    >()

                .setInputData(
                    workDataOf(
                        RecordatorioCitaWorker.CLAVE_CITA_ID to cita.id,
                        RecordatorioCitaWorker.CLAVE_FECHA_HORA to fechaHoraCita,
                        RecordatorioCitaWorker.CLAVE_USUARIO_ID to cita.propietarioId
                    )
                )

                .setInitialDelay(
                    retraso,
                    TimeUnit.MILLISECONDS
                )

                .addTag(etiquetaFecha)
                .addTag(TAG_RECORDATORIO)
                .addTag("$TAG_USUARIO${cita.propietarioId}")

                .build()

            workManager.enqueueUniqueWork(
                nombreTrabajo,
                ExistingWorkPolicy.REPLACE,
                solicitud
            )

        }, ContextCompat.getMainExecutor(appContext))
    }

    // --------------------------------------------------
    // CANCELAR RECORDATORIO
    // --------------------------------------------------

    fun cancelarRecordatorio(
        context: Context,
        citaId: String
    ) {

        if (citaId.isBlank()) {
            return
        }

        WorkManager.getInstance(context.applicationContext)
            .cancelUniqueWork(
                obtenerNombreTrabajo(citaId)
            )
    }

    // --------------------------------------------------
    // CANCELAR TODOS LOS RECORDATORIOS DE VETSYNC
    // --------------------------------------------------

    fun cancelarTodosLosRecordatorios(
        context: Context
    ) {
        WorkManager.getInstance(context.applicationContext)
            .cancelAllWorkByTag(TAG_RECORDATORIO)
    }

    // --------------------------------------------------
    // SINCRONIZAR RECORDATORIOS DE LAS CITAS
    // --------------------------------------------------

    fun sincronizarRecordatorios(
        context: Context,
        citas: List<Cita>
    ) {

        citas.forEach { cita ->

            if (cita.estado == EstadoCita.CONFIRMADA) {

                programarRecordatorio(
                    context = context,
                    cita = cita
                )

            } else {

                cancelarRecordatorio(
                    context = context,
                    citaId = cita.id
                )
            }
        }
    }

    // --------------------------------------------------
    // CONVERTIR FECHA Y HORA A MILISEGUNDOS
    // --------------------------------------------------

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
}