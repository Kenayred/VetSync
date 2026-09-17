package com.example.vetsync.modelo

data class Usuario(
    val id: Int,
    val username: String,
    val contrasena: String,
    val nombre: String,
    val rol: RolUsuario = RolUsuario.CLIENTE
)