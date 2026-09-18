package com.example.vetsync.vista

import com.example.vetsync.controlador.CitaController
import com.example.vetsync.controlador.GestorMascotas
import com.example.vetsync.controlador.ServicioController
import com.example.vetsync.modelo.Usuario
import java.util.Scanner

object Menu {

    fun ejecutarMenu(
        scanner: Scanner,
        usuarioActual: Usuario,
        mascotaController: GestorMascotas,
        servicioController: ServicioController,
        citaController: CitaController
    ) {

        var opcion: Int

        do {

            println("\n--- 🐾 VetSync: Menú Principal ---")
            println("1. Registrar nueva mascota")
            println("2. Mostrar mascotas")
            println("3. Actualizar mascota")
            println("4. Eliminar mascota")
            println("5. Mostrar servicios")
            println("6. Solicitar cita")
            println("7. Mostrar mis citas")
            println("8. Reprogramar cita")
            println("9. Cancelar cita")
            println("10. Cerrar sesión")

            print("Ingrese una opción: ")

            opcion = scanner.nextInt()

            when (opcion) {

                1 -> {
                    mascotaController.agregarMascota(
                        scanner,
                        usuarioActual
                    )
                }

                2 -> {
                    mascotaController.mostrarMascotasUsuario(
                        usuarioActual.id,
                        usuarioActual.nombre
                    )
                }

                3 -> {
                    mascotaController.actualizarMascota(
                        scanner,
                        usuarioActual.id
                    )
                }

                4 -> {
                    mascotaController.eliminarMascota(
                        scanner,
                        usuarioActual.id
                    )
                }

                5 -> {
                    servicioController.mostrarServicios()
                }

                6 -> {
                    CitaView.crearCita(
                        scanner,
                        usuarioActual,
                        mascotaController,
                        servicioController,
                        citaController
                    )
                }

                7 -> {
                    CitaView.mostrarCitas(
                        usuarioActual,
                        citaController,
                        mascotaController,
                        servicioController
                    )
                }

                8 -> {
                    CitaView.reprogramarCita(
                        scanner,
                        usuarioActual,
                        citaController,
                        mascotaController,
                        servicioController
                    )
                }

                9 -> {
                    CitaView.cancelarCita(
                        scanner,
                        usuarioActual,
                        citaController
                    )
                }

                10 -> {
                    println("Cerrando sesión...")
                }

                else -> {
                    println("Opción inválida.")
                }
            }

        } while (opcion != 10)
    }
}