package com.example.vetsync.modelo

enum class EstadoCita(
    val etiqueta: String
) {
    PENDIENTE("Pendiente"),
    CONFIRMADA("Confirmada"),
    COMPLETADA("Completada"),
    CANCELADA("Cancelada")
}