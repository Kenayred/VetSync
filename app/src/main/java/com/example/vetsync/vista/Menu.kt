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
            println("3. Salir")
            print("Ingrese una opción: ")

            opcion = scanner.nextInt()

            when (opcion) {
                1 -> gestor.agregarMascota(scanner, usuarioActual)
                2 -> gestor.mostrarMascotasUsuario(usuarioActual.ID, usuarioActual.nombre)
                3 -> println("Saliendo del sistema VetSync. ¡Hasta luego!")
                else -> println("Opción inválida. Intente de nuevo.")
            }
        } while (opcion != 3)
    }


}