package com.example.vetsync.modelo

data class Notificacion(
    var id: String = "",
    var usuarioId: String = "",
    var titulo: String = "",
    var mensaje: String = "",
    var tipo: String = "GENERAL",
    var citaId: String = "",
    var mascotaId: String = "",
    var leida: Boolean = false,
    var fechaCreacion: Long = 0L
)