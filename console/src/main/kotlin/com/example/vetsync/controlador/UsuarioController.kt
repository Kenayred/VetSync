package com.example.vetsync.controlador

import com.example.vetsync.modelo.RolUsuario
import com.example.vetsync.modelo.Usuario
import com.example.vetsync.excepciones.ValidacionException
import com.example.vetsync.utils.Validador

class UsuarioController {

    private val usuariosRegistrados =
        mutableListOf<Usuario>()

    init {
        usuariosRegistrados.add(
            Usuario(
                id = 1,
                username = "Laura",
                contrasena = "1234",
                nombre = "Laura",
                rol = RolUsuario.CLIENTE
            )
        )

        usuariosRegistrados.add(
            Usuario(
                id = 2,
                username = "vega",
                contrasena = "admin",
                nombre = "Dr. Vega",
                rol = RolUsuario.ADMINISTRADOR
            )
        )
    }

    fun registrarNuevoCliente(
        username: String,
        contrasena: String,
        nombre: String
    ) {

        val usernameLimpio =
            Validador.texto(
                username,
                "nombre de usuario"
            )

        Validador.contrasena(contrasena)

        val nombreLimpio =
            Validador.texto(
                nombre,
                "nombre"
            )

        val existeUsername =
            usuariosRegistrados.any {
                it.username.equals(
                    usernameLimpio,
                    ignoreCase = true
                )
            }

        if (existeUsername) {
            throw ValidacionException(
                "El nombre de usuario '$usernameLimpio' ya está registrado."
            )
        }

        val nuevoId =
            usuariosRegistrados
                .maxOfOrNull { it.id }
                ?.plus(1)
                ?: 1

        val nuevoUsuario = Usuario(
            id = nuevoId,
            username = usernameLimpio,
            contrasena = contrasena,
            nombre = nombreLimpio,
            rol = RolUsuario.CLIENTE
        )

        usuariosRegistrados.add(
            nuevoUsuario
        )

        println(
            "¡Registro exitoso! " +
                    "Bienvenido a VetSync, $nombreLimpio."
        )
    }
    fun iniciarSesion(
        username: String,
        contrasena: String
    ): Usuario? {

        return usuariosRegistrados.find {
            it.username == username &&
                    it.contrasena == contrasena
        }
    }

    fun listarUsuarios(): List<Usuario> {
        return usuariosRegistrados.toList()
    }
}