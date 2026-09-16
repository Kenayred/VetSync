package com.example.vetsync.vista

import com.example.vetsync.controlador.GestorMascotas
import com.example.vetsync.modelo.Usuario
import com.example.vetsync.utils.ConsolaUtil
import java.util.Scanner

object Menu {
    private val scanner = Scanner(System.`in`)
    private val gestor = GestorMascotas()
    private val consola = ConsolaUtil
    fun ejecutarMenu(scanner: Scanner, usuarioActual: Usuario) {
        var opcion: Int
        do {

            println("\n--- VetSync: Menú Principal ---")
            println("1. Registrar nueva mascota")
            println("2. Mostrar mascotas registradas")
            println("3. Actualizar Informacion mascotas")
            println("4. Borrar mascota")
            println("5. Salir")
            print("Ingrese una opción: ")

            opcion = scanner.nextInt()

            when (opcion) {
                1 -> gestor.agregarMascota(scanner, usuarioActual)
                2 -> gestor.mostrarMascotasUsuario(usuarioActual.ID, usuarioActual.nombre)
                3 -> gestor.actualizarMascota(scanner, usuarioActual.ID, usuarioActual.nombre)
                4 -> gestor.eliminarMascotaPorIndice(scanner, usuarioActual.ID,usuarioActual.nombre)
                5 -> println("Saliendo del sistema VetSync.")
                else -> println("Opción inválida. Intente de nuevo.")
            }
        } while (opcion != 5)
    }


}