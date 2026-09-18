package com.example.vetsync.utils

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Logger {

    private val formatoFecha =
        DateTimeFormatter.ofPattern(
            "dd/MM/yyyy HH:mm:ss"
        )

    private val archivoLog =
        File(
            "logs",
            "vetsync_errors.txt"
        )

    init {
        archivoLog.parentFile?.mkdirs()
    }

    fun info(
        modulo: String,
        mensaje: String
    ) {

        registrar(
            "INFO",
            modulo,
            mensaje
        )
    }

    fun warning(
        modulo: String,
        mensaje: String
    ) {

        registrar(
            "WARNING",
            modulo,
            mensaje
        )
    }

    fun error(
        modulo: String,
        mensaje: String
    ) {

        registrar(
            "ERROR",
            modulo,
            mensaje
        )
    }

    fun error(
        modulo: String,
        mensaje: String,
        excepcion: Exception
    ) {

        val detalle = buildString {

            appendLine(mensaje)
            appendLine(
                "Excepción: ${excepcion::class.simpleName}"
            )
            appendLine(
                "Detalle: ${excepcion.message}"
            )
        }

        registrar(
            "ERROR",
            modulo,
            detalle
        )
    }

    private fun registrar(
        nivel: String,
        modulo: String,
        mensaje: String
    ) {

        try {

            val fecha =
                LocalDateTime.now()
                    .format(formatoFecha)

            archivoLog.appendText(
                "[$fecha] [$nivel]\n" +
                        "Módulo: $modulo\n" +
                        "$mensaje\n" +
                        "----------------------------------------\n"
            )

        } catch (e: Exception) {

            println(
                "No fue posible escribir el archivo de log."
            )
        }
    }
}