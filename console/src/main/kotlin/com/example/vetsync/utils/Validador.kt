package com.example.vetsync.utils

import com.example.vetsync.excepciones.ValidacionException

object Validador {

    fun texto(valor: String, campo: String
    ): String {

        val texto = valor.trim()

        if (texto.isBlank()) {
            throw ValidacionException(
                "El campo $campo es obligatorio."
            )
        }

        return texto
    }

    fun edad(edad: Int
    ): Int {

        if (edad < 0) {
            throw ValidacionException(
                "La edad no puede ser negativa."
            )
        }

        return edad
    }

    fun peso(peso: Double
    ): Double {

        if (peso <= 0) {
            throw ValidacionException(
                "El peso debe ser mayor que 0."
            )
        }

        return peso
    }

    fun costo(costo: Double
    ): Double {

        if (costo <= 0) {
            throw ValidacionException(
                "El costo debe ser mayor que 0."
            )
        }

        return costo
    }

    fun correo(correo: String
    ): String {

        val correoLimpio = correo.trim()

        val patron =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()

        if (!patron.matches(correoLimpio)) {
            throw ValidacionException(
                "El correo electrónico no tiene un formato válido."
            )
        }

        return correoLimpio
    }

    fun contrasena(contrasena: String
    ): String {

        if (contrasena.length < 4) {
            throw ValidacionException(
                "La contraseña debe tener al menos 4 caracteres."
            )
        }

        return contrasena
    }
}