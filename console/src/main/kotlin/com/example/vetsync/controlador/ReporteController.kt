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

    fun obtenerDemandaServicios(): Map<String, Int> {

        val citasCompletadas = citaController.listar()
            .filter { it.estado == EstadoCita.COMPLETADA }

        return servicioController.obtenerServiciosActivos()
            .associate { servicio ->

                val cantidad = citasCompletadas.count {
                    it.servicioId == servicio.id
                }

                servicio.nombre to cantidad
            }
    }

    fun obtenerServicioMasRealizado(): Pair<String, Int>? {

        val demandaServicios = obtenerDemandaServicios()

        val servicioMasRealizado = demandaServicios.maxByOrNull { it.value }

        if (servicioMasRealizado == null || servicioMasRealizado.value == 0) {
            return null
        }

        return servicioMasRealizado.key to servicioMasRealizado.value
    }
}