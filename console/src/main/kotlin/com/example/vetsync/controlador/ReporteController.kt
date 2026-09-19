package com.example.vetsync.controlador

import com.example.vetsync.modelo.EstadoCita

class ReporteController(
    private val citaController: CitaController,
    private val servicioController: ServicioController
) {

    fun obtenerTotalCitas(): Int {
        return citaController.listar().size
    }

    fun obtenerCitasCompletadas(): Int {
        return citaController.listar()
            .count { it.estado == EstadoCita.COMPLETADA }
    }

    fun obtenerCitasPendientes(): Int {
        return citaController.listar()
            .count { it.estado == EstadoCita.PENDIENTE }
    }

    fun obtenerCitasConfirmadas(): Int {
        return citaController.listar()
            .count { it.estado == EstadoCita.CONFIRMADA }
    }

    fun obtenerCitasCanceladas(): Int {
        return citaController.listar()
            .count { it.estado == EstadoCita.CANCELADA }
    }

    fun obtenerIngresosTotales(): Double {
        return citaController.listar()
            .filter { it.estado == EstadoCita.COMPLETADA }
            .sumOf { it.costo }
    }

    fun obtenerCantidadServicios(): Int {
        return servicioController.obtenerServiciosActivos().size
    }
}