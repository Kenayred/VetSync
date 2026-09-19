package com.example.vetsync.modelo

data class Usuario(
    override val id: Int,
    val username: String,
    val contrasena: String,
    override val nombre: String,
    val rol: RolUsuario = RolUsuario.CLIENTE
) : Persona(id, nombre) {

    override fun obtenerTipo(): String {
        return when (rol) {
            RolUsuario.CLIENTE -> "Cliente"
            RolUsuario.ADMINISTRADOR -> "Administrador"
        }
    }
}