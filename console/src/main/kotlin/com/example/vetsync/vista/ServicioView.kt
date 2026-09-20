package com.example.vetsync.vista

import com.example.vetsync.controlador.ServicioController
import com.example.vetsync.excepciones.ValidacionException
import com.example.vetsync.modelo.Servicio
import com.example.vetsync.utils.ConsolaUtil
import com.example.vetsync.utils.Logger
import com.example.vetsync.utils.Validador
import java.util.Scanner

object ServicioView {

    fun gestionarServicios(
        scanner: Scanner,
        servicioController: ServicioController
    ) {

        var opcion: Int

        do {
            println("\n========================================")
            println("        GESTIÓN DE SERVICIOS")
            println("========================================")
            println("1. Listar servicios")
            println("2. Agregar servicio")
            println("3. Actualizar servicio")
            println("4. Desactivar servicio")
            println("5. Regresar")
            println("========================================")

            opcion = ConsolaUtil.leerOpcion(
                scanner,
                "Ingrese una opción: ",
                1,
                5
            )

            when (opcion) {

                1 -> listarServicios(servicioController)

                2 -> agregarServicio(
                    scanner,
                    servicioController
                )

                3 -> actualizarServicio(
                    scanner,
                    servicioController
                )

                4 -> desactivarServicio(
                    scanner,
                    servicioController
                )

                5 -> println("\nRegresando al menú de administrador...")
            }

        } while (opcion != 5)
    }

    private fun listarServicios(
        servicioController: ServicioController
    ) {

        println("\n========== SERVICIOS ACTIVOS ==========")

        val servicios = servicioController.obtenerServiciosActivos()

        if (servicios.isEmpty()) {
            println("No hay servicios activos registrados.")
            return
        }

        servicios.forEachIndexed { indice, servicio ->
            println(
                "${indice + 1}. ID: ${servicio.id} | " +
                        "Nombre: ${servicio.nombre} | " +
                        "Costo: $${"%.2f".format(servicio.costo)} | " +
                        "Descripción: ${servicio.descripcion}"
            )
        }
    }

    private fun agregarServicio(
        scanner: Scanner,
        servicioController: ServicioController
    ) {

        println("\n========== AGREGAR SERVICIO ==========")

        try {

            val nombre = ConsolaUtil.leerTexto(
                scanner,
                "Nombre del servicio: "
            )

            val descripcion = ConsolaUtil.leerTexto(
                scanner,
                "Descripción: "
            )

            val costo = ConsolaUtil.leerDecimal(
                scanner,
                "Costo: $",
                0.01
            )

            Validador.texto(nombre, "Nombre")
            Validador.texto(descripcion, "Descripción")
            Validador.costo(costo)

            val nuevoId = servicioController.listar()
                .maxOfOrNull { it.id }
                ?.plus(1)
                ?: 1

            val servicio = Servicio(
                id = nuevoId,
                nombre = nombre,
                descripcion = descripcion,
                costo = costo,
                activo = true
            )

            servicioController.agregar(servicio)

            println("\nServicio agregado correctamente.")
            println("ID asignado: $nuevoId")

        } catch (e: ValidacionException) {

            println("Error de validación: ${e.message}")

            Logger.error(
                "Servicios",
                e.message ?: "Error de validación",
                e
            )

        } catch (e: Exception) {

            println("Ocurrió un error al agregar el servicio.")

            Logger.error(
                "Servicios",
                "Error al agregar servicio",
                e
            )
        }
    }

    private fun actualizarServicio(
        scanner: Scanner,
        servicioController: ServicioController
    ) {

        println("\n========== ACTUALIZAR SERVICIO ==========")

        val id = ConsolaUtil.leerEntero(
            scanner,
            "Ingrese el ID del servicio: ",
            1
        )

        val servicio = servicioController.buscarPorId(id)

        if (servicio == null) {
            println("No existe un servicio con ese ID.")
            return
        }

        if (!servicio.activo) {
            println("El servicio está inactivo y no puede actualizarse.")
            return
        }

        try {

            println("\nServicio actual:")
            println("Nombre: ${servicio.nombre}")
            println("Descripción: ${servicio.descripcion}")
            println("Costo: $${"%.2f".format(servicio.costo)}")

            val nombre = ConsolaUtil.leerTexto(
                scanner,
                "\nNuevo nombre: "
            )

            val descripcion = ConsolaUtil.leerTexto(
                scanner,
                "Nueva descripción: "
            )

            val costo = ConsolaUtil.leerDecimal(
                scanner,
                "Nuevo costo: $",
                0.01
            )

            Validador.texto(nombre, "Nombre")
            Validador.texto(descripcion, "Descripción")
            Validador.costo(costo)

            val servicioActualizado = servicio.copy(
                nombre = nombre,
                descripcion = descripcion,
                costo = costo
            )

            val actualizado =
                servicioController.actualizar(servicioActualizado)

            if (actualizado) {
                println("\nServicio actualizado correctamente.")
            } else {
                println("\nNo se pudo actualizar el servicio.")
            }

        } catch (e: ValidacionException) {

            println("Error de validación: ${e.message}")

            Logger.error(
                "Servicios",
                e.message ?: "Error de validación",
                e
            )

        } catch (e: Exception) {

            println("Ocurrió un error al actualizar el servicio.")

            Logger.error(
                "Servicios",
                "Error al actualizar servicio",
                e
            )
        }
    }

    private fun desactivarServicio(
        scanner: Scanner,
        servicioController: ServicioController
    ) {

        println("\n========== DESACTIVAR SERVICIO ==========")

        val id = ConsolaUtil.leerEntero(
            scanner,
            "Ingrese el ID del servicio: ",
            1
        )

        val servicio = servicioController.buscarPorId(id)

        if (servicio == null) {
            println("No existe un servicio con ese ID.")
            return
        }

        if (!servicio.activo) {
            println("El servicio ya está inactivo.")
            return
        }

        println("\nServicio seleccionado:")
        println("Nombre: ${servicio.nombre}")
        println("Costo: $${"%.2f".format(servicio.costo)}")

        println("\n¿Desea desactivar este servicio?")
        println("1. Sí")
        println("2. No")

        val confirmacion = ConsolaUtil.leerOpcion(
            scanner,
            "Seleccione una opción: ",
            1,
            2
        )

        if (confirmacion == 1) {

            val desactivado = servicioController.eliminar(id)

            if (desactivado) {
                println("\nServicio desactivado correctamente.")
            } else {
                println("\nNo se pudo desactivar el servicio.")
            }

        } else {
            println("\nOperación cancelada.")
        }
    }
}