package com.example.vetsync.utils

import com.example.vetsync.excepciones.ValidacionException
import java.util.Scanner

object ConsolaUtil {

    fun limpiarPantalla() {
        println("\n".repeat(50))
    }

    fun mostrarSeparador() {
        println("========================================")
    }

    fun leerTexto(
        scanner: Scanner,
        mensaje: String,
        obligatorio: Boolean = true
    ): String {

        while (true) {

            print(mensaje)

            val texto = scanner.nextLine().trim()

            if (texto.isNotBlank()) {
                return texto
            }

            if (!obligatorio) {
                return ""
            }

            println(
                "El campo no puede estar vacío."
            )
        }
    }

    fun leerEntero(
        scanner: Scanner,
        mensaje: String,
        minimo: Int? = null,
        maximo: Int? = null
    ): Int {

        while (true) {

            print(mensaje)

            val entrada = scanner.nextLine().trim()

            try {

                val valor = entrada.toInt()

                if (minimo != null && valor < minimo) {

                    println(
                        "El valor debe ser mayor o igual a $minimo."
                    )

                    continue
                }

                if (maximo != null && valor > maximo) {

                    println(
                        "El valor debe ser menor o igual a $maximo."
                    )

                    continue
                }

                return valor

            } catch (e: NumberFormatException) {

                println(
                    "Entrada inválida. " +
                            "Debe ingresar un número entero."
                )
            }
        }
    }

    fun leerDecimal(
        scanner: Scanner,
        mensaje: String,
        minimo: Double? = null,
        maximo: Double? = null
    ): Double {

        while (true) {

            print(mensaje)

            val entrada = scanner
                .nextLine()
                .trim()
                .replace(",", ".")

            try {

                val valor = entrada.toDouble()

                if (minimo != null && valor < minimo) {

                    println(
                        "El valor debe ser mayor o igual a $minimo."
                    )

                    continue
                }

                if (maximo != null && valor > maximo) {

                    println(
                        "El valor debe ser menor o igual a $maximo."
                    )

                    continue
                }

                return valor

            } catch (e: NumberFormatException) {

                println(
                    "Entrada inválida. " +
                            "Debe ingresar un número decimal."
                )
            }
        }
    }

    fun leerOpcion(
        scanner: Scanner,
        mensaje: String,
        minimo: Int,
        maximo: Int
    ): Int {

        while (true) {

            val opcion = leerEntero(
                scanner,
                mensaje
            )

            if (opcion in minimo..maximo) {
                return opcion
            }

            println(
                "Seleccione una opción entre " +
                        "$minimo y $maximo."
            )
        }
    }

    fun validarTexto(
        valor: String,
        campo: String
    ) {

        if (valor.isBlank()) {

            throw ValidacionException(
                "El campo $campo es obligatorio."
            )
        }
    }
}