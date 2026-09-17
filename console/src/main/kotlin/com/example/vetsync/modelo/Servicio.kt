package com.example.vetsync.modelo

data class Servicio(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    var costo: Double,
    var activo: Boolean = true
)