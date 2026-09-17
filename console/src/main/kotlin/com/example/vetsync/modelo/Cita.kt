package com.example.vetsync.modelo

data class Cita(
    val id: Int,
    val mascotaId: Int,
    val propietarioId: Int,
    val servicioId: Int,
    var fecha: String,
    var hora: String,
    var estado: EstadoCita,
    var costo: Double,
    var motivo: String = "",
    var veterinario: String = "Veterinario"
)