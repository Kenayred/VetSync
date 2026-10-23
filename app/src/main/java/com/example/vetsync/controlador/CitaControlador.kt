package com.example.vetsync.controlador

import com.example.vetsync.modelo.Cita
import com.example.vetsync.modelo.CitaRepository
import com.example.vetsync.modelo.EstadoCita
import com.example.vetsync.modelo.MascotaRepository
import com.example.vetsync.modelo.Servicio
import com.example.vetsync.modelo.ServicioRepository
import com.example.vetsync.modelo.SesionUsuario

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class CitaControlador {

    private val citaRepository = CitaRepository()
    private val mascotaRepository = MascotaRepository()
    private val servicioRepository = ServicioRepository()

    private val formatoFecha =
        DateTimeFormatter.ISO_LOCAL_DATE

    private val formatoHora =
        DateTimeFormatter.ofPattern("HH:mm")

    // --------------------------------------------------
    // CONSULTAR SERVICIOS ACTIVOS
    // --------------------------------------------------

    fun obtenerServiciosActivos(
        onSuccess: (List<Servicio>) -> Unit,
        onError: (String) -> Unit
    ) {
        servicioRepository.obtenerServiciosActivos(
            onSuccess = { servicios ->
                onSuccess(servicios)
            },
            onFailure = { error ->
                onError(
                    error.message
                        ?: "No se pudieron cargar los servicios."
                )
            }
        )
    }

    // --------------------------------------------------
    // REGISTRAR UNA CITA
    // --------------------------------------------------

    fun registrarCita(
        mascotaId: String,
        servicioId: String,
        fecha: String,
        hora: String,
        motivo: String = "",
        onSuccess: (Cita) -> Unit,
        onError: (String) -> Unit
    ) {
        val idPropietario = SesionUsuario.idUsuario

        // 1. Verificar que exista una sesión.
        if (idPropietario.isBlank()) {
            onError(
                "No hay una sesión activa. Inicia sesión nuevamente."
            )
            return
        }

        // 2. Validar los campos principales.
        if (mascotaId.isBlank() || servicioId.isBlank()) {
            onError(
                "Debes seleccionar una mascota y un servicio."
            )
            return
        }

        // 3. Validar el motivo opcional.
        if (motivo.length > 250) {
            onError(
                "El motivo de consulta no debe superar los 250 caracteres."
            )
            return
        }

        // 4. Validar fecha, hora y horario de atención.
        val errorHorario = validarFechaHora(fecha, hora)

        if (errorHorario != null) {
            onError(errorHorario)
            return
        }

        // 5. Consultar únicamente las mascotas del usuario.
        mascotaRepository.obtenerMascotasPorUsuario(
            idUsuario = idPropietario,

            onSuccess = { mascotas ->
                val mascotaSeleccionada = mascotas.find {
                    it.id == mascotaId
                }

                if (mascotaSeleccionada == null) {
                    onError(
                        "La mascota seleccionada no existe " +
                                "o no pertenece a tu cuenta."
                    )
                    return@obtenerMascotasPorUsuario
                }

                // 6. Consultar el catálogo de servicios activos.
                servicioRepository.obtenerServiciosActivos(
                    onSuccess = { servicios ->
                        val servicioSeleccionado =
                            servicios.find {
                                it.id == servicioId && it.activo
                            }

                        if (servicioSeleccionado == null) {
                            onError(
                                "El servicio seleccionado no está disponible."
                            )
                            return@obtenerServiciosActivos
                        }

                        // 7. Verificar disponibilidad del horario.
                        citaRepository.verificarHorarioDisponible(
                            fecha = fecha,
                            hora = hora,

                            onResult = { disponible ->
                                if (!disponible) {
                                    onError(
                                        "El horario seleccionado ya está ocupado."
                                    )
                                    return@verificarHorarioDisponible
                                }

                                // 8. Construir la cita con el precio
                                // actual del servicio.
                                val nuevaCita = Cita(
                                    mascotaId = mascotaSeleccionada.id,
                                    propietarioId = idPropietario,
                                    servicioId = servicioSeleccionado.id,
                                    nombreMascota = mascotaSeleccionada.nombre,
                                    nombreServicio = servicioSeleccionado.nombre,
                                    fecha = fecha,
                                    hora = hora,
                                    estado = EstadoCita.PENDIENTE,
                                    costo = servicioSeleccionado.costo,
                                    motivo = motivo.trim(),
                                    veterinario = "Veterinario",
                                    fechaCreacion = System.currentTimeMillis()
                                )

                                // 9. Guardar en Firestore.
                                citaRepository.registrarCita(
                                    cita = nuevaCita,

                                    onSuccess = { citaGuardada ->
                                        onSuccess(citaGuardada)
                                    },

                                    onFailure = { error ->
                                        onError(
                                            error.message
                                                ?: "No se pudo registrar la cita."
                                        )
                                    }
                                )
                            },

                            onFailure = { error ->
                                onError(
                                    error.message
                                        ?: "No se pudo comprobar la disponibilidad."
                                )
                            }
                        )
                    },

                    onFailure = { error ->
                        onError(
                            error.message
                                ?: "No se pudieron consultar los servicios."
                        )
                    }
                )
            },

            onFailure = { error ->
                onError(
                    error.message
                        ?: "No se pudieron consultar las mascotas."
                )
            }
        )
    }

    // --------------------------------------------------
    // REPROGRAMAR UNA CITA
    // --------------------------------------------------

    fun reprogramarCita(
        idCita: String,
        nuevaFecha: String,
        nuevaHora: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val propietarioId = SesionUsuario.idUsuario

        if (propietarioId.isBlank()) {
            onError("No se pudo identificar al usuario actual.")
            return
        }

        if (idCita.isBlank()) {
            onError("La cita seleccionada no es válida.")
            return
        }

        val errorValidacion = validarFechaHoraReprogramacion(
            fecha = nuevaFecha,
            hora = nuevaHora
        )

        if (errorValidacion != null) {
            onError(errorValidacion)
            return
        }

        citaRepository.obtenerCitasPorPropietario(
            idPropietario = propietarioId,
            onSuccess = { citas ->
                val citaOriginal = citas.firstOrNull {
                    it.id == idCita
                }

                when {
                    citaOriginal == null -> {
                        onError("No se encontró la cita seleccionada.")
                    }

                    citaOriginal.estado != EstadoCita.PENDIENTE -> {
                        onError(
                            "Solo se pueden reprogramar citas pendientes."
                        )
                    }

                    citaOriginal.fecha == nuevaFecha &&
                            citaOriginal.hora == nuevaHora -> {
                        onError(
                            "Selecciona una fecha u hora diferente."
                        )
                    }

                    else -> {
                        citaRepository.verificarHorarioDisponible(
                            fecha = nuevaFecha,
                            hora = nuevaHora,
                            idCitaExcluir = idCita,
                            onResult = { disponible ->
                                if (!disponible) {
                                    onError(
                                        "Ese horario ya está ocupado. " +
                                                "Selecciona otro."
                                    )
                                } else {
                                    citaRepository.actualizarHorario(
                                        idCita = idCita,
                                        nuevaFecha = nuevaFecha,
                                        nuevaHora = nuevaHora,
                                        onSuccess = {
                                            onSuccess()
                                        },
                                        onFailure = { error ->
                                            onError(
                                                error.message
                                                    ?: "No se pudo reprogramar la cita."
                                            )
                                        }
                                    )
                                }
                            },
                            onFailure = { error ->
                                onError(
                                    error.message
                                        ?: "No se pudo comprobar la disponibilidad."
                                )
                            }
                        )
                    }
                }
            },
            onFailure = { error ->
                onError(
                    error.message
                        ?: "No se pudo consultar la cita del usuario."
                )
            }
        )
    }

    private fun validarFechaHoraReprogramacion(
        fecha: String,
        hora: String
    ): String? {
        val fechaLocal = try {
            LocalDate.parse(
                fecha,
                DateTimeFormatter.ISO_LOCAL_DATE
            )
        } catch (e: Exception) {
            return "La fecha debe tener el formato AAAA-MM-DD."
        }

        val horaLocal = try {
            if (!hora.matches(Regex("""\d{2}:\d{2}"""))) {
                return "La hora debe tener el formato HH:mm."
            }

            LocalTime.parse(
                hora,
                DateTimeFormatter.ofPattern("HH:mm")
            )
        } catch (e: Exception) {
            return "La hora seleccionada no es válida."
        }

        val hoy = LocalDate.now()

        if (fechaLocal.isBefore(hoy)) {
            return "No puedes seleccionar una fecha pasada."
        }

        if (fechaLocal.dayOfWeek == DayOfWeek.SUNDAY) {
            return "La clínica permanece cerrada los domingos."
        }

        val horaMaxima = if (
            fechaLocal.dayOfWeek == DayOfWeek.SATURDAY
        ) {
            15
        } else {
            16
        }

        if (horaLocal.minute != 0 ||
            horaLocal.hour !in 9..horaMaxima
        ) {
            return if (
                fechaLocal.dayOfWeek == DayOfWeek.SATURDAY
            ) {
                "El sábado puedes seleccionar horarios de 09:00 a 15:00."
            } else {
                "Puedes seleccionar horarios de 09:00 a 16:00."
            }
        }

        if (
            fechaLocal == hoy &&
            !horaLocal.isAfter(LocalTime.now())
        ) {
            return "Selecciona una hora futura."
        }

        return null
    }

    // --------------------------------------------------
    // CONSULTAR LAS CITAS DEL USUARIO
    // --------------------------------------------------

    fun obtenerCitasUsuario(
        onSuccess: (List<Cita>) -> Unit,
        onError: (String) -> Unit
    ) {
        val idPropietario = SesionUsuario.idUsuario

        if (idPropietario.isBlank()) {
            onError("No hay una sesión activa.")
            return
        }

        citaRepository.obtenerCitasPorPropietario(
            idPropietario = idPropietario,

            onSuccess = { citas ->
                onSuccess(citas)
            },

            onFailure = { error ->
                onError(
                    error.message
                        ?: "No se pudieron recuperar las citas."
                )
            }
        )
    }

    // --------------------------------------------------
    // CANCELAR UNA CITA
    // --------------------------------------------------

    fun cancelarCita(
        idCita: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val propietarioId = SesionUsuario.idUsuario

        if (propietarioId.isBlank()) {
            onError("No se pudo identificar al usuario actual.")
            return
        }

        if (idCita.isBlank()) {
            onError("No se ha seleccionado una cita válida.")
            return
        }

        citaRepository.obtenerCitasPorPropietario(
            propietarioId,
            { citas ->
                val citaSeleccionada = citas.firstOrNull {
                    it.id == idCita
                }

                when {
                    citaSeleccionada == null -> {
                        onError("No se encontró la cita seleccionada.")
                    }

                    citaSeleccionada.estado != EstadoCita.PENDIENTE -> {
                        onError(
                            "Solo se pueden cancelar citas pendientes."
                        )
                    }

                    else -> {
                        citaRepository.actualizarEstado(
                            citaSeleccionada.id,
                            EstadoCita.CANCELADA,
                            {
                                onSuccess()
                            },
                            { error ->
                                onError(
                                    error.message
                                        ?: "No se pudo cancelar la cita."
                                )
                            }
                        )
                    }
                }
            },
            { error ->
                onError(
                    error.message
                        ?: "No se pudieron consultar las citas del usuario."
                )
            }
        )
    }

    // --------------------------------------------------
// CONSULTAR CITAS DE UNA FECHA
// --------------------------------------------------

    fun obtenerCitasPorFecha(
        fecha: String,
        onSuccess: (List<Cita>) -> Unit,
        onError: (String) -> Unit
    ) {
        if (fecha.isBlank()) {
            onError("La fecha es obligatoria.")
            return
        }

        citaRepository.obtenerCitasPorFecha(
            fecha = fecha,

            onSuccess = { citas ->
                onSuccess(citas)
            },

            onFailure = { error ->
                onError(
                    error.message
                        ?: "No se pudieron consultar los horarios."
                )
            }
        )
    }

    // --------------------------------------------------
    // VALIDAR FECHA Y HORARIO DE LA CLÍNICA
    // --------------------------------------------------

    private fun validarFechaHora(
        fechaTexto: String,
        horaTexto: String
    ): String? {

        val fecha = try {
            LocalDate.parse(
                fechaTexto,
                formatoFecha
            )
        } catch (error: DateTimeParseException) {
            return "La fecha debe tener el formato AAAA-MM-DD."
        }

        val hora = try {
            LocalTime.parse(
                horaTexto,
                formatoHora
            )
        } catch (error: DateTimeParseException) {
            return "La hora debe tener el formato HH:mm."
        }

        val hoy = LocalDate.now()
        val ahora = LocalTime.now()

        // No permitir fechas anteriores.
        if (fecha.isBefore(hoy)) {
            return "No puedes reservar una cita en una fecha pasada."
        }

        // Si es hoy, la hora también debe ser futura.
        if (
            fecha.isEqual(hoy) &&
            !hora.isAfter(ahora)
        ) {
            return "La hora seleccionada ya pasó."
        }

        // Por ahora, los horarios comienzan en hora completa.
        if (hora.minute != 0 || hora.second != 0) {
            return "Por ahora, selecciona una hora completa, " +
                    "por ejemplo 09:00 o 10:00."
        }

        // La clínica permanece cerrada los domingos.
        if (fecha.dayOfWeek == DayOfWeek.SUNDAY) {
            return "La clínica permanece cerrada los domingos."
        }

        val apertura = LocalTime.of(9, 0)

        val cierre = if (
            fecha.dayOfWeek == DayOfWeek.SATURDAY
        ) {
            LocalTime.of(16, 0)
        } else {
            LocalTime.of(17, 0)
        }

        // La apertura está incluida y el cierre está excluido.
        if (
            hora.isBefore(apertura) ||
            !hora.isBefore(cierre)
        ) {
            return if (fecha.dayOfWeek == DayOfWeek.SATURDAY) {
                "Los sábados el horario permitido es de 09:00 a 16:00."
            } else {
                "De lunes a viernes el horario permitido es de 09:00 a 17:00."
            }
        }

        // Segunda comprobación de seguridad para la fecha actual.
        val fechaHoraCita = LocalDateTime.of(fecha, hora)

        if (fechaHoraCita.isBefore(LocalDateTime.now())) {
            return "La fecha y hora seleccionadas ya pasaron."
        }

        return null
    }
}