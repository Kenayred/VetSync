package com.example.vetsync.controlador

import com.example.vetsync.modelo.NotaClinica
import com.example.vetsync.modelo.NotaClinicaRepository
import com.example.vetsync.modelo.SesionUsuario
import com.example.vetsync.modelo.Cita
import com.example.vetsync.modelo.EstadoCita
import com.example.vetsync.modelo.RolUsuario

class NotaClinicaControlador {

    private val notaRepository = NotaClinicaRepository()

    fun obtenerHistorialMascota(
        mascotaId: String,
        onSuccess: (List<NotaClinica>) -> Unit,
        onError: (String) -> Unit
    ) {

        val propietarioId = SesionUsuario.idUsuario

        if (propietarioId.isBlank()) {
            onError(
                "No existe una sesión activa. Inicia sesión nuevamente."
            )
            return
        }

        if (mascotaId.isBlank()) {
            onError(
                "No se ha seleccionado una mascota válida."
            )
            return
        }

        notaRepository.obtenerHistorialPorMascota(
            mascotaId = mascotaId,
            propietarioId = propietarioId,

            onSuccess = { historial ->
                onSuccess(historial)
            },

            onFailure = { error ->
                onError(
                    error.message
                        ?: "No se pudo recuperar el historial clínico."
                )
            }
        )
    }

    // --------------------------------------------------
    // CONSULTAR HISTORIAL CLÍNICO COMO ADMINISTRADOR
    // --------------------------------------------------
    fun obtenerHistorialMascotaAdmin(
        mascotaId: String,
        onSuccess: (List<NotaClinica>) -> Unit,
        onError: (String) -> Unit
    ) {

        if (
            SesionUsuario.usuarioActual?.rol !=
            RolUsuario.ADMINISTRADOR
        ) {
            onError(
                "No tienes permisos para consultar historiales clínicos."
            )
            return
        }

        if (mascotaId.isBlank()) {
            onError(
                "No se ha seleccionado una mascota válida."
            )
            return
        }

        notaRepository.obtenerHistorialPorMascotaAdmin(
            mascotaId = mascotaId,

            onSuccess = { historial ->
                onSuccess(historial)
            },

            onFailure = { error ->
                onError(
                    error.message
                        ?: "No se pudo recuperar el historial clínico."
                )
            }
        )
    }

    fun registrarNotaClinica(
        cita: Cita,
        motivo: String = "",
        peso: Double? = null,
        temperatura: Double? = null,
        diagnostico: String,
        receta: String,
        observaciones: String = "",
        onSuccess: (NotaClinica) -> Unit,
        onError: (String) -> Unit
    ) {
        // 1. Verificar que exista una sesión de administrador.
        if (
            SesionUsuario.usuarioActual?.rol !=
            RolUsuario.ADMINISTRADOR
        ) {
            onError(
                "No tienes permisos para registrar notas clínicas."
            )
            return
        }

        // 2. Verificar los identificadores de la cita.
        if (
            cita.id.isBlank() ||
            cita.mascotaId.isBlank() ||
            cita.propietarioId.isBlank()
        ) {
            onError(
                "La información de la cita está incompleta."
            )
            return
        }

        // 3. Solo se atienden citas confirmadas.
        if (cita.estado != EstadoCita.CONFIRMADA) {
            onError(
                "Solo puedes registrar notas clínicas de citas confirmadas."
            )
            return
        }

        // 4. Validar los campos obligatorios.
        if (
            diagnostico.isBlank() ||
            receta.isBlank()
        ) {
            onError(
                "El diagnóstico y la receta médica son obligatorios."
            )
            return
        }

        // 5. Validar los valores numéricos opcionales.
        if (peso != null && peso <= 0) {
            onError(
                "El peso debe ser un valor mayor que cero."
            )
            return
        }

        if (temperatura != null && temperatura <= 0) {
            onError(
                "La temperatura debe ser un valor mayor que cero."
            )
            return
        }

        // 6. Construir la nota relacionada con la cita.
        val nuevaNota = NotaClinica(
            mascotaId = cita.mascotaId,
            propietarioId = cita.propietarioId,
            citaId = cita.id,
            fecha = cita.fecha,
            motivo = motivo.trim(),
            peso = peso,
            temperatura = temperatura,
            diagnostico = diagnostico.trim(),
            receta = receta.trim(),
            observaciones = observaciones.trim()
                .takeIf { it.isNotBlank() }
        )

        // 7. Guardar la nota, completar la cita y notificar.
        notaRepository.registrarNotaClinicaYCompletarCita(
            nota = nuevaNota,

            onSuccess = { notaGuardada ->
                onSuccess(notaGuardada)
            },

            onFailure = { error ->
                onError(
                    error.message
                        ?: "No se pudo registrar la nota clínica."
                )
            }
        )
    }
}