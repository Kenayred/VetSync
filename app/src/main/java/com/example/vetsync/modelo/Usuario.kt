package com.example.vetsync.modelo

data class Usuario(
    override var id: String = "",
    var username: String = "",
    var contrasena: String = "",
    override var nombre: String = "",
    var correo: String = "",
    var telefono: String = "",
    var rol: RolUsuario = RolUsuario.CLIENTE,
    var fotoUrl: String = ""
) : Persona(id, nombre) {

    override fun obtenerTipo(): String {
        return when (rol) {
            RolUsuario.CLIENTE -> "Cliente"
            RolUsuario.ADMINISTRADOR -> "Administrador"
        }
    }
}