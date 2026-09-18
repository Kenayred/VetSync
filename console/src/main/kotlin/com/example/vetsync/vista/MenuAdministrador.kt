package com.example.vetsync.vista

import com.example.vetsync.controlador.CitaController
import com.example.vetsync.controlador.GestorMascotas
import com.example.vetsync.controlador.ServicioController
import com.example.vetsync.modelo.EstadoCita
import com.example.vetsync.modelo.Usuario
import java.util.Scanner

object MenuAdministrador {

    fun ejecutarMenu(
        scanner: Scanner,
        usuarioActual: Usuario,
        mascotaController: GestorMascotas,
        servicioController: ServicioController,
        citaController: CitaController
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
            println("4. Ver servicios")
            println("5. Cerrar sesión")
            println("========================================")

            print("Ingrese una opción: ")

            opcion = scanner.nextInt()

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
                    servicioController.mostrarServicios()
                }

                5 -> {
                    println("Cerrando sesión...")
                }

                else -> {
                    println("Opción inválida.")
                }
            }

        } while (opcion != 5)
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

        print("\nIngrese el ID de la cita: ")

        val id = scanner.nextInt()

        val cita = citaController.buscarPorId(id)

        if (cita == null) {

            println("No existe una cita con ese ID.")
            return
        }

        println("\nEstado actual: ${cita.estado}")
        println("1. Confirmar")
        println("2. Completar")
        println("3. Cancelar")

        print("Seleccione una acción: ")

        val opcion = scanner.nextInt()

        val nuevoEstado = when (opcion) {

            1 -> EstadoCita.CONFIRMADA

            2 -> EstadoCita.COMPLETADA

            3 -> EstadoCita.CANCELADA

            else -> {
                println("Opción inválida.")
                return
            }
        }

        val resultado = citaController.cambiarEstado(
            id,
            nuevoEstado
        )

        if (resultado) {

            println(
                "La cita $id cambió a estado $nuevoEstado correctamente."
            )

        } else {

            println(
                "No se pudo cambiar el estado de la cita."
            )

            println(
                "La transición ${cita.estado} → $nuevoEstado no está permitida."
            )
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