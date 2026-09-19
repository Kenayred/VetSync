package com.example.vetsync.vista

import com.example.vetsync.controlador.CitaController
import com.example.vetsync.controlador.GestorMascotas
import com.example.vetsync.controlador.ServicioController
import com.example.vetsync.modelo.EstadoCita
import com.example.vetsync.modelo.Usuario
import com.example.vetsync.controlador.NotaClinicaController
import com.example.vetsync.excepciones.OperacionNoPermitidaException
import com.example.vetsync.utils.Logger
import com.example.vetsync.utils.ConsolaUtil
import java.util.Scanner

object MenuAdministrador {

    fun ejecutarMenu(
        scanner: Scanner,
        usuarioActual: Usuario,
        mascotaController: GestorMascotas,
        servicioController: ServicioController,
        citaController: CitaController,
        notaClinicaController: NotaClinicaController
    ) {

        var opcion: Int
        do {

            println("\n========================================")
            println("      VETSYNC - ADMINISTRADOR")
            println("========================================")
            println("Usuario: ${usuarioActual.nombre}")
            println("Rol: ${usuarioActual.rol}")
            println("----------------------------------------")
            println("1. Ver citas")
            println("2. Gestionar estado de cita")
            println("3. Ver pacientes")
            println("4. Agregar nota clínica")
            println("5. Ver historial clínico")
            println("6. Ver servicios")
            println("7. Cerrar sesión")
            println("========================================")

            opcion = ConsolaUtil.leerOpcion(
                scanner,
                "Ingrese una opción: ",
                minimo = 1,
                maximo = 7
            )

            when (opcion) {

                1 -> {
                    mostrarCitas(
                        citaController,
                        mascotaController,
                        servicioController
                    )
                }

                2 -> {
                    gestionarEstadoCita(
                        scanner,
                        citaController
                    )
                }

                3 -> {
                    mostrarPacientes(
                        mascotaController
                    )
                }

                4 -> {
                    NotaClinicaView.agregarNotaClinica(
                        scanner,
                        citaController,
                        mascotaController,
                        notaClinicaController
                    )
                }

                5 -> {
                    NotaClinicaView.mostrarHistorial(
                        scanner,
                        mascotaController,
                        notaClinicaController
                    )
                }

                6 -> {
                    servicioController.mostrarServicios()
                }

                7 -> {
                    println("Cerrando sesión...")
                }

                else -> {
                    println("Opción inválida.")
                }
            }

        } while (opcion != 7)
    }

    private fun mostrarCitas(
        citaController: CitaController,
        mascotaController: GestorMascotas,
        servicioController: ServicioController
    ) {

        val citas = citaController.listar()

        if (citas.isEmpty()) {
            println("\nNo hay citas registradas.")
            return
        }

        println("\n========== CITAS REGISTRADAS ==========")

        citas.forEach { cita ->

            val mascota = mascotaController
                .obtenerMascotasUsuario(cita.propietarioId)
                .find {
                    it.id == cita.mascotaId
                }

            val servicio = servicioController
                .listar()
                .find {
                    it.id == cita.servicioId
                }

            println(
                "\nID: ${cita.id}"
            )

            println(
                "Mascota: ${mascota?.nombre ?: "No encontrada"}"
            )

            println(
                "Servicio: ${servicio?.nombre ?: "No encontrado"}"
            )

            println(
                "Fecha: ${cita.fecha}"
            )

            println(
                "Hora: ${cita.hora}"
            )

            println(
                "Costo: $${"%.2f".format(cita.costo)}"
            )

            println(
                "Veterinario: ${cita.veterinario}"
            )

            println(
                "Estado: ${cita.estado}"
            )

            if (cita.motivo.isNotBlank()) {
                println(
                    "Motivo: ${cita.motivo}"
                )
            }

            println("----------------------------------------")
        }
    }

    private fun gestionarEstadoCita(
        scanner: Scanner,
        citaController: CitaController
    ) {

        val citas = citaController.listar()

        if (citas.isEmpty()) {
            println("\nNo hay citas registradas.")
            return
        }

        println("\n========== GESTIONAR CITA ==========")

        citas.forEach { cita ->

            println(
                "ID: ${cita.id} | " +
                        "${cita.fecha} ${cita.hora} | " +
                        "Estado: ${cita.estado}"
            )
        }

        val id = ConsolaUtil.leerEntero(
            scanner,
            "\nIngrese el ID de la cita: ",
            minimo = 1
        )

        val cita = citaController.buscarPorId(id)

        if (cita == null) {

            println("No existe una cita con ese ID.")
            return
        }

        println("\nCita seleccionada:")
        println("ID: ${cita.id}")
        println("Fecha: ${cita.fecha}")
        println("Hora: ${cita.hora}")
        println("Estado actual: ${cita.estado}")

        // =====================================================
        // MOSTRAR SOLO LAS ACCIONES VÁLIDAS
        // =====================================================

        when (cita.estado) {

            EstadoCita.PENDIENTE -> {

                println("\nAcciones disponibles:")
                println("1. Confirmar")
                println("2. Cancelar")

                print("Seleccione una acción: ")

                val opcion = ConsolaUtil.leerOpcion(
                    scanner,
                    "Seleccione una acción: ",
                    minimo = 1,
                    maximo = 2
                )

                val nuevoEstado = when (opcion) {

                    1 -> EstadoCita.CONFIRMADA

                    2 -> EstadoCita.CANCELADA

                    else -> {
                        println("Opción inválida.")
                        return
                    }
                }

                try {

                    val resultado =
                        citaController.cambiarEstado(
                            id,
                            nuevoEstado
                        )

                    if (resultado) {

                        println(
                            "La cita $id cambió a " +
                                    "estado $nuevoEstado correctamente."
                        )
                    }

                } catch (
                    e: OperacionNoPermitidaException
                ) {

                    println(
                        "Operación no permitida: ${e.message}"
                    )

                    Logger.error(
                        modulo = "Gestión de citas",
                        mensaje = e.message
                            ?: "Transición no permitida",
                        excepcion = e
                    )
                }
            }

            EstadoCita.CONFIRMADA -> {

                println("\nAcciones disponibles:")
                println("1. Completar")
                println("2. Cancelar")

                print("Seleccione una acción: ")

                val opcion = ConsolaUtil.leerOpcion(
                    scanner,
                    "Seleccione una acción: ",
                    minimo = 1,
                    maximo = 2
                )

                val nuevoEstado = when (opcion) {

                    1 -> EstadoCita.COMPLETADA

                    2 -> EstadoCita.CANCELADA

                    else -> {
                        println("Opción inválida.")
                        return
                    }
                }

                try {

                    val resultado =
                        citaController.cambiarEstado(
                            id,
                            nuevoEstado
                        )

                    if (resultado) {

                        println(
                            "La cita $id cambió a " +
                                    "estado $nuevoEstado correctamente."
                        )
                    }

                } catch (
                    e: OperacionNoPermitidaException
                ) {

                    println(
                        "Operación no permitida: ${e.message}"
                    )

                    Logger.error(
                        modulo = "Gestión de citas",
                        mensaje = e.message
                            ?: "Transición no permitida",
                        excepcion = e
                    )
                }
            }

            EstadoCita.COMPLETADA -> {

                println(
                    "\nLa cita ya está COMPLETADA."
                )

                println(
                    "No existen acciones de cambio de estado disponibles."
                )
            }

            EstadoCita.CANCELADA -> {

                println(
                    "\nLa cita ya está CANCELADA."
                )

                println(
                    "No existen acciones de cambio de estado disponibles."
                )
            }
        }
    }

    private fun mostrarPacientes(
        mascotaController: GestorMascotas
    ) {

        val mascotas = mascotaController.obtenerTodasLasMascotas()

        if (mascotas.isEmpty()) {

            println(
                "\nNo hay pacientes registrados."
            )

            return
        }

        println("\n========== PACIENTES ==========")

        mascotas.forEach { mascota ->

            println(
                "ID: ${mascota.id} | " +
                        "Nombre: ${mascota.nombre} | " +
                        "Especie: ${mascota.especie} | " +
                        "Edad: ${mascota.edad} años | " +
                        "Propietario ID: ${mascota.duenoId}"
            )
        }

        println("--------------------------------")
    }
}