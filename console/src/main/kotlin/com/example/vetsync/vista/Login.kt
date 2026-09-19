package com.example.vetsync.vista

import com.example.vetsync.controlador.UsuarioController
import com.example.vetsync.modelo.Usuario
import com.example.vetsync.utils.ConsolaUtil
import com.example.vetsync.utils.Logger
import java.util.Scanner

object LoginView {

    fun mostrarPantalla(
        scanner: Scanner,
        controller: UsuarioController
    ): Usuario? {

        println("\n--- 🐾 VetSync: Iniciar Sesión ---")

        val username = ConsolaUtil.leerTexto(
            scanner,
            "Usuario: "
        )

        val contrasena = ConsolaUtil.leerTexto(
            scanner,
            "Contraseña: "
        )

        val usuarioLogueado =
            controller.iniciarSesion(
                username,
                contrasena
            )

        if (usuarioLogueado != null) {

            println(
                "¡Bienvenido, " +
                        "${usuarioLogueado.nombre}! " +
                        "(Rol: ${usuarioLogueado.rol})"
            )

        } else {

            println(
                "Credenciales incorrectas."
            )

            Logger.warning(
                modulo = "Autenticación",
                mensaje = "Intento de inicio de sesión fallido para usuario: $username"
            )
        }

        return usuarioLogueado
    }
}