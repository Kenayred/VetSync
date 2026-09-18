package com.example.vetsync.controlador

import com.example.vetsync.modelo.Mascota
import com.example.vetsync.modelo.Usuario
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
                        "Edad: ${mascota.edad} años"
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

        print("Nueva especie: ")
        mascota.especie = scanner.next()

        print("Nueva edad: ")
        mascota.edad = scanner.nextInt()

        println(
            "La información de ${mascota.nombre} " +
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

        print("Nombre de la mascota: ")
        val nombre = scanner.next()

        print("Especie: ")
        val especie = scanner.next()

        print("Edad: ")
        val edad = scanner.nextInt()

        val nuevoId = mascotas.size + 1

        val nuevaMascota = Mascota(
            id = nuevoId,
            nombre = nombre,
            especie = especie,
            edad = edad,
            duenoId = usuario.id
        )

        agregarMascota(nuevaMascota)
    }
}