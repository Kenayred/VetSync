package com.example.vetsync.modelo

data class Servicio(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val costo: Double = 0.0,
    val activo: Boolean = true
)