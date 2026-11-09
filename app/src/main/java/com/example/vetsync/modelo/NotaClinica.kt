package com.example.vetsync.modelo

data class NotaClinica(
    var id: String = "",
    var mascotaId: String = "",
    var propietarioId: String = "",
    var citaId: String = "",
    var fecha: String = "",
    var motivo: String = "",
    var peso: Double? = null,
    var temperatura: Double? = null,
    var diagnostico: String = "",
    var receta: String = "",
    var observaciones: String? = null,
    var fechaCreacion: Long = 0L
)