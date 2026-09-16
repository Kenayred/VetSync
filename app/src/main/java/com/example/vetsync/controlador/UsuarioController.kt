package com.example.vetsync.controlador

import com.example.vetsync.modelo.Usuario

class UsuarioController(){
    private val usuariosRegistrados = mutableListOf<Usuario>()

    init {
        // Agregamos usuarios de prueba según los roles de VetSync
        usuariosRegistrados.add(Usuario(1,"Laura", "1234","Laura","Cliente"))
        usuariosRegistrados.add(Usuario(2,"vega", "admin","Dr.Vega", "Administrador"))
    }

    fun registrarNuevoCliente(username: String, contrasena: String, nombre: String) {

        val nuevoID = usuariosRegistrados.size + 1
        val nuevoUsuario = Usuario(nuevoID, username, contrasena, nombre)

        usuariosRegistrados.add(nuevoUsuario)
        println("¡Registro exitoso! Bienvenido a VetSync, $nombre.")
    }

    // Validacion de credenciales
    fun iniciarSesion(username: String, contrasena: String): Usuario? {
        return usuariosRegistrados.find { it.username == username && it.contrasena == contrasena }
    }


}