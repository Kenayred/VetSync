package com.example.vetsync.vista

import java.util.Scanner
import com.example.vetsync.controlador.UsuarioController
import com.example.vetsync.modelo.Usuario
import com.example.vetsync.utils.ConsolaUtil

object LoginView {
    val consola = ConsolaUtil;
    fun mostrarPantalla(scanner: Scanner, controller: UsuarioController): Usuario? {
        println("\n--- 🐾 VetSync: Iniciar Sesión ---")
        print("Usuario: ")
        val username = scanner.next()
        print("Contraseña: ")
        val contrasena = scanner.next()

        val usuarioLogueado = controller.iniciarSesion(username, contrasena)

        if (usuarioLogueado != null) {
            println("¡Bienvenido, ${usuarioLogueado.nombre}! (Rol: ${usuarioLogueado.rol})")
        } else {
            println("Credenciales incorrectas. Intente de nuevo.")
        }

        return usuarioLogueado
    }

}