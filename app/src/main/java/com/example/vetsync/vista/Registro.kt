package com.example.vetsync.vista

import com.example.vetsync.controlador.UsuarioController
import java.util.Scanner

object RegistroView {

    fun mostrarPantalla(scanner: Scanner, controller: UsuarioController) {
        println("\n--- VetSync: Registro de nuevo cliente ---")

        print("Nombre de usuario: ")
        val username = scanner.next()

        print("Contraseña: ")
        val password = scanner.next()

        print("Nombre real: ")
        val nombre = scanner.next()

        controller.registrarNuevoCliente(username, password, nombre)
    }
}