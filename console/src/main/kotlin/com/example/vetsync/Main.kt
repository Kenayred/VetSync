package com.example.vetsync

import com.example.vetsync.controlador.GestorMascotas
import com.example.vetsync.controlador.ServicioController
import com.example.vetsync.controlador.CitaController
import com.example.vetsync.controlador.UsuarioController
import com.example.vetsync.vista.LoginView
import com.example.vetsync.vista.Menu
import com.example.vetsync.vista.RegistroView
import com.example.vetsync.utils.ConsolaUtil
import com.example.vetsync.modelo.RolUsuario
import com.example.vetsync.vista.MenuAdministrador
import com.example.vetsync.controlador.NotaClinicaController
import com.example.vetsync.controlador.ReporteController
import java.util.Scanner

fun main() {

    val scanner = Scanner(System.`in`)
    val usuarioController = UsuarioController()
    val mascotaController = GestorMascotas()
    val servicioController = ServicioController()

    val citaController = CitaController(
        mascotaController,
        servicioController
    )

    val notaClinicaController = NotaClinicaController()

    val reporteController = ReporteController(
        citaController,
        servicioController
    )

    var opcionInicio: Int

    println("🐾 Bienvenido al sistema VetSync 🐾")

    // Ciclo externo: Menú de Acceso
    do {

        ConsolaUtil.limpiarPantalla()

        println("\n--- 🐾 Menú de Acceso ---")
        println("1. Iniciar sesión")
        println("2. Registrarse")
        println("3. Salir")

        opcionInicio = ConsolaUtil.leerOpcion(
            scanner,
            "Ingrese una opción: ",
            minimo = 1,
            maximo = 3
        )

        when (opcionInicio) {

            1 -> {

                val usuarioActual =
                    LoginView.mostrarPantalla(
                        scanner,
                        usuarioController
                    )

                if (usuarioActual != null) {

                    when (usuarioActual.rol) {

                        RolUsuario.CLIENTE -> {

                            Menu.ejecutarMenu(
                                scanner,
                                usuarioActual,
                                mascotaController,
                                servicioController,
                                citaController
                            )
                        }

                        RolUsuario.ADMINISTRADOR -> {

                            MenuAdministrador.ejecutarMenu(
                                scanner,
                                usuarioActual,
                                mascotaController,
                                servicioController,
                                citaController,
                                notaClinicaController,
                                reporteController
                            )
                        }
                    }
                }
            }

            2 -> {

                RegistroView.mostrarPantalla(
                    scanner,
                    usuarioController
                )
            }

            3 -> {

                println(
                    "Apagando el sistema VetSync. " +
                            "¡Hasta pronto!"
                )
            }

            else -> {

                println(
                    "Opción inválida. Intente nuevamente."
                )
            }
        }

    } while (opcionInicio != 3)

    scanner.close()
}