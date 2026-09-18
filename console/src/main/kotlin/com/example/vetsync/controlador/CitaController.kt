package com.example.vetsync.controlador

import com.example.vetsync.modelo.Cita
import com.example.vetsync.modelo.EstadoCita
import com.example.vetsync.modelo.Gestionable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class CitaController(
    private val mascotaController: GestorMascotas,
    private val servicioController: ServicioController
) : Gestionable<Cita> {

    private val citas = mutableListOf<Cita>()

    private val formatoFecha =
        DateTimeFormatter.ofPattern("dd/MM/yyyy")

    private val formatoHora =
        DateTimeFormatter.ofPattern("HH:mm")

    // =========================================================
    // CREATE
    // =========================================================

    override fun agregar(item: Cita) {

        val mascota = mascotaController
            .obtenerMascotasUsuario(item.propietarioId)
            .find { it.id == item.mascotaId }

        require(mascota != null) {
            "La mascota no existe o no pertenece al propietario indicado."
        }

        val servicio = servicioController.buscarPorId(item.servicioId)

        require(servicio != null) {
            "El servicio no existe o no está disponible."
        }

        val fechaValida = validarFecha(item.fecha)

        validarHora(
            fechaValida,
            item.hora
        )

        require(
            !existeCitaEnHorario(
                item.fecha,
                item.hora
            )
        ) {
            "El horario seleccionado ya está ocupado."
        }

        item.fecha = fechaValida.format(formatoFecha)

        // El costo siempre se obtiene del servicio actual
        item.costo = servicio.costo

        citas.add(item)
    }

    fun crearCita(
        mascotaId: Int,
        propietarioId: Int,
        servicioId: Int,
        fecha: String,
        hora: String,
        motivo: String = ""
    ): Cita {

        val nuevoId =
            citas.maxOfOrNull { it.id }?.plus(1) ?: 1

        val nuevaCita = Cita(
            id = nuevoId,
            mascotaId = mascotaId,
            propietarioId = propietarioId,
            servicioId = servicioId,
            fecha = fecha,
            hora = hora,
            estado = EstadoCita.PENDIENTE,
            costo = 0.0,
            motivo = motivo,
            veterinario = "Veterinario"
        )

        agregar(nuevaCita)

        println("\nCita creada correctamente.")

        println(
            "ID: ${nuevaCita.id} | " +
                    "Fecha: ${nuevaCita.fecha} | " +
                    "Hora: ${nuevaCita.hora}"
        )

        return nuevaCita
    }

    // =========================================================
    // READ
    // =========================================================

    override fun listar(): List<Cita> {
        return citas.toList()
    }

    fun buscarPorId(id: Int): Cita? {
        return citas.find { it.id == id }
    }

    fun listarCitasPropietario(
        propietarioId: Int
    ): List<Cita> {
        return citas.filter {
            it.propietarioId == propietarioId
        }
    }

    // =========================================================
    // UPDATE
    // =========================================================

    override fun actualizar(item: Cita): Boolean {

        val indice = citas.indexOfFirst {
            it.id == item.id
        }

        if (indice == -1) {
            return false
        }

        citas[indice] = item

        return true
    }

    // =========================================================
    // DELETE / CANCELACIÓN LÓGICA
    // =========================================================

    override fun eliminar(id: Int): Boolean {

        val cita = buscarPorId(id)
            ?: return false

        if (
            cita.estado == EstadoCita.COMPLETADA ||
            cita.estado == EstadoCita.CANCELADA
        ) {
            return false
        }

        cita.estado = EstadoCita.CANCELADA

        return true
    }

    // =========================================================
    // DISPONIBILIDAD
    // =========================================================

    fun existeCitaEnHorario(
        fecha: String,
        hora: String
    ): Boolean {

        return citas.any {
            it.fecha == fecha &&
                    it.hora == hora &&
                    it.estado != EstadoCita.CANCELADA
        }
    }

    // =========================================================
    // REPROGRAMAR
    // =========================================================

    fun reprogramarCita(
        id: Int,
        nuevaFecha: String,
        nuevaHora: String
    ): Boolean {

        val cita = buscarPorId(id)
            ?: return false

        require(cita.estado == EstadoCita.PENDIENTE) {
            "Solo las citas pendientes pueden ser reprogramadas."
        }

        val fechaValida = validarFecha(nuevaFecha)

        validarHora(fechaValida, nuevaHora)

        require(
            !existeCitaEnHorario(
                nuevaFecha,
                nuevaHora
            ) ||
                    (
                            cita.fecha == nuevaFecha &&
                                    cita.hora == nuevaHora
                            )
        ) {
            "El nuevo horario seleccionado ya está ocupado."
        }

        cita.fecha = fechaValida.format(formatoFecha)
        cita.hora = nuevaHora

        return true
    }

    // =========================================================
    // CAMBIO DE ESTADO
    // =========================================================

    fun cambiarEstado(
        id: Int,
        nuevoEstado: EstadoCita
    ): Boolean {

        val cita = buscarPorId(id)
            ?: return false

        if (!transicionValida(cita.estado, nuevoEstado)) {
            return false
        }

        cita.estado = nuevoEstado

        return true
    }

    private fun transicionValida(
        estadoActual: EstadoCita,
        nuevoEstado: EstadoCita
    ): Boolean {

        return when (estadoActual) {

            EstadoCita.PENDIENTE ->
                nuevoEstado == EstadoCita.CONFIRMADA ||
                        nuevoEstado == EstadoCita.CANCELADA

            EstadoCita.CONFIRMADA ->
                nuevoEstado == EstadoCita.COMPLETADA ||
                        nuevoEstado == EstadoCita.CANCELADA

            EstadoCita.COMPLETADA ->
                false

            EstadoCita.CANCELADA ->
                false
        }
    }

    // =========================================================
    // VALIDACIONES
    // =========================================================

    private fun validarFecha(
        fecha: String
    ): LocalDate {

        val fechaParseada = try {

            LocalDate.parse(
                fecha,
                formatoFecha
            )

        } catch (e: DateTimeParseException) {

            throw IllegalArgumentException(
                "La fecha debe utilizar el formato dd/MM/yyyy."
            )
        }

        require(
            !fechaParseada.isBefore(LocalDate.now())
        ) {
            "La fecha de la cita no puede ser anterior a hoy."
        }

        require(
            fechaParseada.dayOfWeek != DayOfWeek.SUNDAY
        ) {
            "La veterinaria permanece cerrada los domingos."
        }

        return fechaParseada
    }

    private fun validarHora(
        fecha: LocalDate,
        hora: String
    ): LocalTime {

        val horaParseada = try {

            LocalTime.parse(
                hora,
                formatoHora
            )

        } catch (e: DateTimeParseException) {

            throw IllegalArgumentException(
                "La hora debe utilizar el formato HH:mm."
            )
        }

        val apertura = LocalTime.of(9, 0)

        val cierre = if (
            fecha.dayOfWeek == DayOfWeek.SATURDAY
        ) {
            LocalTime.of(16, 0)
        } else {
            LocalTime.of(17, 0)
        }

        require(
            horaParseada >= apertura &&
                    horaParseada < cierre
        ) {
            if (fecha.dayOfWeek == DayOfWeek.SATURDAY) {
                "Los sábados el horario de atención es de 09:00 a 16:00."
            } else {
                "El horario de atención es de 09:00 a 17:00."
            }
        }

        return horaParseada
    }
}