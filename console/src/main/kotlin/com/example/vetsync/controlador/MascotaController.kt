package com.example.vetsync.controlador

import com.example.vetsync.modelo.Mascota
import com.example.vetsync.modelo.Usuario
import com.example.vetsync.utils.ConsolaUtil
import java.util.Scanner

class GestorMascotas {

    private val mascotas: MutableList<Mascota> = mutableListOf()

    fun agregarMascota(mascota: Mascota) {
        mascotas.add(mascota)
        println("¡La mascota ${mascota.nombre} ha sido registrada con éxito!")
    }

    fun obtenerMascotasUsuario(idDueno: Int): List<Mascota> {
        return mascotas.filter { it.duenoId == idDueno }
    }

    fun obtenerTodasLasMascotas(): List<Mascota> {
        return mascotas.toList()
    }

    fun mostrarMascotasUsuario(idDueno: Int, duenoNombre: String) {

        val mascotasDelUsuario = obtenerMascotasUsuario(idDueno)

        if (mascotasDelUsuario.isEmpty()) {
            println("No hay pacientes registrados actualmente bajo tu perfil.")
            return
        }

        println("\n--- Mis Mascotas Registradas ---")

        for ((index, mascota) in mascotasDelUsuario.withIndex()) {
            println(
                "${index + 1}. " +
                        "Nombre: ${mascota.nombre} | " +
                        "Especie: ${mascota.especie} | " +
                        "Raza: ${mascota.raza} | " +
                        "Edad: ${mascota.edad} años | " +
                        "Sexo: ${mascota.sexo} | " +
                        "Peso: ${
                            mascota.peso?.let {
                                "$it kg"
                            } ?: "N/R"
                        }"
            )
        }

        println("--------------------------------")
    }

    fun actualizarMascota(
        scanner: Scanner,
        idUsuario: Int
    ) {

        val mascotasDelUsuario = obtenerMascotasUsuario(idUsuario)

        if (mascotasDelUsuario.isEmpty()) {
            println("No tienes mascotas registradas.")
            return
        }

        mostrarMascotasUsuario(idUsuario, "")

        print("Ingrese el número de la mascota: ")
        val opcion = scanner.nextInt()

        val indice = opcion - 1

        if (indice !in mascotasDelUsuario.indices) {
            println("Número de opción inválido.")
            return
        }

        val mascota = mascotasDelUsuario[indice]

        println("\n--- Actualizar ${mascota.nombre} ---")

        print(
            "Nueva especie " +
                    "(actual: ${mascota.especie}): "
        )

        mascota.especie = scanner.next()

        print(
            "Nueva edad " +
                    "(actual: ${mascota.edad}): "
        )

        mascota.edad = scanner.nextInt()

        print(
            "Nueva raza " +
                    "(actual: ${mascota.raza}): "
        )

        mascota.raza = scanner.next()

        print(
            "Nuevo sexo " +
                    "(actual: ${mascota.sexo}): "
        )

        mascota.sexo = scanner.next()

        print(
            "Nuevo peso en kg " +
                    "(0 para no modificar): "
        )

        val nuevoPeso = scanner.nextDouble()

        if (nuevoPeso > 0) {
            mascota.peso = nuevoPeso
        }

        print(
            "Nuevo microchip " +
                    "(actual: ${
                        mascota.microchip ?: "No registrado"
                    }): "
        )

        val nuevoMicrochip = scanner.next()

        if (nuevoMicrochip != "-") {
            mascota.microchip = nuevoMicrochip
        }

        print(
            "Nuevas alergias " +
                    "(actual: ${
                        mascota.alergias ?: "No registradas"
                    }): "
        )

        val nuevasAlergias = scanner.next()

        if (nuevasAlergias != "-") {
            mascota.alergias = nuevasAlergias
        }

        println(
            "\nLa información de ${mascota.nombre} " +
                    "ha sido actualizada correctamente."
        )
    }

    fun eliminarMascota(
        scanner: Scanner,
        idUsuario: Int
    ) {

        val mascotasDelUsuario = obtenerMascotasUsuario(idUsuario)

        if (mascotasDelUsuario.isEmpty()) {
            println("No tienes mascotas registradas.")
            return
        }

        mostrarMascotasUsuario(idUsuario, "")

        print("Ingrese el número de la mascota que desea eliminar: ")
        val opcion = scanner.nextInt()

        val indice = opcion - 1

        if (indice !in mascotasDelUsuario.indices) {
            println("Número de opción inválido.")
            return
        }

        val mascota = mascotasDelUsuario[indice]

        mascotas.remove(mascota)

        println(
            "La mascota ${mascota.nombre} " +
                    "ha sido eliminada correctamente."
        )
    }

    fun mostrarTodasLasMascotas(
        usuariosRegistrados: List<Usuario>
    ) {

        if (mascotas.isEmpty()) {
            println("No hay pacientes registrados actualmente.")
            return
        }

        println("\n--- Pacientes Registrados ---")

        for (mascota in mascotas) {

            val dueno = usuariosRegistrados.find {
                it.id == mascota.duenoId
            }

            val nombreDueno = dueno?.nombre ?: "Usuario no encontrado"

            println(
                "Paciente: ${mascota.nombre} | " +
                        "Especie: ${mascota.especie} | " +
                        "Edad: ${mascota.edad} años | " +
                        "Dueño: $nombreDueno"
            )
        }

        println("-----------------------------")
    }

    fun agregarMascota(
        scanner: Scanner,
        usuario: Usuario
    ) {

        println("\n--- Agregar Paciente ---")

        val nombre = ConsolaUtil.leerTexto(
            scanner,
            "Nombre de la mascota: "
        )

        val especie = ConsolaUtil.leerTexto(
            scanner,
            "Especie: "
        )

        val edad = ConsolaUtil.leerEntero(
            scanner,
            "Edad: ",
            minimo = 0
        )

        val raza = ConsolaUtil.leerTexto(
            scanner,
            "Raza: "
        )

        val sexo = ConsolaUtil.leerTexto(
            scanner,
            "Sexo: "
        )

        val pesoIngresado =
            ConsolaUtil.leerDecimal(
                scanner,
                "Peso en kg (0 si no desea registrarlo): ",
                minimo = 0.0
            )

        val peso = if (pesoIngresado > 0) {
            pesoIngresado
        } else {
            null
        }

        val microchipIngresado =
            ConsolaUtil.leerTexto(
                scanner,
                "Microchip (Enter si no tiene): ",
                obligatorio = false
            )

        val microchip = microchipIngresado.ifBlank {
                null
            }

        val alergiasIngresadas =
            ConsolaUtil.leerTexto(
                scanner,
                "Alergias (Enter si no tiene): ",
                obligatorio = false
            )

        val alergias = alergiasIngresadas.ifBlank {
                null
            }

        val nuevoId = mascotas.maxOfOrNull { it.id}?.plus(1) ?: 1

        val nuevaMascota = Mascota(
            id = nuevoId,
            nombre = nombre,
            especie = especie,
            edad = edad,
            duenoId = usuario.id,
            raza = raza,
            sexo = sexo,
            peso = peso,
            microchip = microchip,
            alergias = alergias
        )

        agregarMascota(nuevaMascota)

        println("Paciente agregado correctamente.")
    }
}