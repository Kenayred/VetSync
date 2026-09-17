package com.example.vetsync.modelo

data class NotaClinica(
    val id: Int,
    val mascotaId: Int,
    val citaId: Int,
    val fecha: String,
    val motivo: String,
    val peso: Double?,
    val temperatura: Double?,
    val diagnostico: String,
    val receta: String,
    val observaciones: String?
)