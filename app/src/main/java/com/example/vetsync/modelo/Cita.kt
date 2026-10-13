package com.example.vetsync.modelo

data class Cita(
    val id: String = "",

    // Identificadores de las relaciones.
    val mascotaId: String = "",
    val propietarioId: String = "",
    val servicioId: String = "",

    // Datos descriptivos conservados al reservar.
    val nombreMascota: String = "",
    val nombreServicio: String = "",

    // Fecha ISO: yyyy-MM-dd.
    // Hora de 24 horas: HH:mm.
    val fecha: String = "",
    val hora: String = "",

    // Estado y costo de la reserva.
    val estado: EstadoCita = EstadoCita.PENDIENTE,
    val costo: Double = 0.0,

    // Información adicional.
    val motivo: String = "",
    val veterinario: String = "Veterinario",

    // Fecha de creación expresada en milisegundos.
    val fechaCreacion: Long = 0L
)