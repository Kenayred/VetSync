package com.example.vetsync.vista

import com.example.vetsync.controlador.UsuarioController
import com.example.vetsync.utils.ConsolaUtil
import com.example.vetsync.utils.Logger
import com.example.vetsync.excepciones.ValidacionException
import java.util.Scanner

object RegistroView {

    fun mostrarPantalla(
        scanner: Scanner,
        controller: UsuarioController
    ) {

        println(
            "\n--- VetSync: Registro de nuevo cliente ---"
        )

        val username =
            ConsolaUtil.leerTexto(
                scanner,
                "Nombre de usuario: "
            )

        val password =
            ConsolaUtil.leerTexto(
                scanner,
                "Contraseña: "
            )

        val nombre =
            ConsolaUtil.leerTexto(
                scanner,
                "Nombre completo: "
            )

        try {

            controller.registrarNuevoCliente(
                username,
                password,
                nombre
            )

        } catch (e: ValidacionException) {

            println(
                "No se pudo completar el registro."
            )

            println(
                "Motivo: ${e.message}"
            )

            Logger.error(
                modulo = "Registro",
                mensaje = e.message
                    ?: "Error de validación",
                excepcion = e
            )
        }
    }
}