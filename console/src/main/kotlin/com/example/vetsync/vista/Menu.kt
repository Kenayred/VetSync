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
            println("5. Cerrar sesión")

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
                    println("Cerrando sesión...")
                }

                else -> {
                    println("Opción inválida.")
                }
            }

        } while (opcion != 5)
    }
}