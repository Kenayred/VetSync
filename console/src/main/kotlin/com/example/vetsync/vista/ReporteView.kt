package com.example.vetsync.vista

import com.example.vetsync.controlador.ReporteController
import com.example.vetsync.utils.ConsolaUtil
import java.util.Scanner

object ReporteView {

    fun mostrarReporte(
        scanner: Scanner,
        reporteController: ReporteController
    ) {
        ConsolaUtil.limpiarPantalla()

        println("========================================")
        println("          REPORTE DEL SISTEMA")
        println("========================================")

        println()
        println("RESUMEN DE CITAS")
        println("----------------------------------------")
        println("Total de citas: ${reporteController.obtenerTotalCitas()}")
        println("Citas pendientes: ${reporteController.obtenerCitasPendientes()}")
        println("Citas confirmadas: ${reporteController.obtenerCitasConfirmadas()}")
        println("Citas completadas: ${reporteController.obtenerCitasCompletadas()}")
        println("Citas canceladas: ${reporteController.obtenerCitasCanceladas()}")

        println()
        println("RESUMEN FINANCIERO")
        println("----------------------------------------")
        println(
            "Ingresos por citas completadas: " +
                    "$${"%.2f".format(reporteController.obtenerIngresosTotales())}"
        )

        println()
        println("SERVICIOS")
        println("----------------------------------------")
        println(
            "Servicios activos: " +
                    reporteController.obtenerCantidadServicios()
        )

        println()
        println("DEMANDA DE SERVICIOS")
        println("----------------------------------------")

        val demandaServicios = reporteController.obtenerDemandaServicios()

        if (demandaServicios.isEmpty()) {
            println("No hay servicios registrados.")
        } else {
            demandaServicios.forEach { (nombreServicio, cantidad) ->
                println("$nombreServicio: $cantidad")
            }

            val servicioMasRealizado = reporteController.obtenerServicioMasRealizado()

            if (servicioMasRealizado != null) {
                println()
                println(
                    "Servicio más realizado: " +
                            servicioMasRealizado.first
                )
                println(
                    "Cantidad de veces realizado: " +
                            servicioMasRealizado.second
                )
            }
        }

        println()
        println("========================================")
        println("Presione ENTER para continuar...")
        scanner.nextLine()
    }
}