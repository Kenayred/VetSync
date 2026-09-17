package com.example.vetsync.controlador

import com.example.vetsync.modelo.RolUsuario
import com.example.vetsync.modelo.Usuario

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

        val nuevoId = usuariosRegistrados.size + 1

        val nuevoUsuario = Usuario(
            id = nuevoId,
            username = username,
            contrasena = contrasena,
            nombre = nombre
        )

        usuariosRegistrados.add(nuevoUsuario)

        println(
            "¡Registro exitoso! " +
                    "Bienvenido a VetSync, $nombre."
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