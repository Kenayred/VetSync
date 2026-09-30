package com.example.vetsync.modelo

data class Usuario(
    override var id: Int = 0,
    var username: String = "",
    var contrasena: String = "",
    override var nombre: String = "",
    var correo: String = "",
    var telefono: String = "", // Opcional
    var rol: RolUsuario = RolUsuario.CLIENTE
) : Persona(id, nombre) {

    override fun obtenerTipo(): String {
        return when (rol) {
            RolUsuario.CLIENTE -> "Cliente"
            RolUsuario.ADMINISTRADOR -> "Administrador"
        }
    }
}