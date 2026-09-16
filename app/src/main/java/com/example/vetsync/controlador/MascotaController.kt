package com.example.vetsync.controlador

import com.example.vetsync.modelo.Mascota
import com.example.vetsync.modelo.Usuario
import com.example.vetsync.vista.Menu
import java.util.Scanner
import kotlinx.coroutines.*

class GestorMascotas {
    private val mascotas: MutableList<Mascota> = mutableListOf()
    fun agregarMascota(mascota: Mascota) {

        mascotas.add(mascota)
        println("¡La mascota ${mascota.nombre} ha sido registrada con éxito!")
    }

    fun mostrarMascotasUsuarioConIndice(idDuenoActual: Int, duenoNombre: String): List<Mascota> {
        val mascotasDelUsuario = mascotas.filter { it.duenoID == idDuenoActual }

        if (mascotasDelUsuario.isEmpty()) {
            println("No hay pacientes registrados actualmente bajo tu perfil.")
        } else {
            println("\n---  Mis Pacientes Registrados ---")
            for ((index, mascota) in mascotasDelUsuario.withIndex()) {
                val numeroVisible = index + 1
                println("$numeroVisible. Nombre: ${mascota.nombre} | Especie: ${mascota.especie} | Edad: ${mascota.edad} años ")
            }
            println("-----------------------------------------")
        }
        return mascotasDelUsuario
    }

    fun actualizarMascota(scanner: Scanner, idUsuario: Int, duenoNombre: String){

        val mascotasDelUsuario = mostrarMascotasUsuarioConIndice(idUsuario, duenoNombre)

        if (mascotasDelUsuario.isNotEmpty()) {
            print("Ingrese el número de la lista de la mascota que desea actualizar: ")
            val opcion = scanner.nextInt()
            val indiceReal = opcion - 1

            if (indiceReal in mascotasDelUsuario.indices) {
                val mascotaSeleccionada = mascotasDelUsuario[indiceReal]

                print("Nueva especie (actual: ${mascotaSeleccionada.especie}): ")
                mascotaSeleccionada.especie = scanner.next()

                print("Nueva edad (actual: ${mascotaSeleccionada.edad}): ")
                mascotaSeleccionada.edad = scanner.nextInt()

                println("${mascotaSeleccionada.nombre} han sido actualizados con éxito!")
            } else {
                println("Número de opción inválido.")
            }
        }

    }

    fun eliminarMascotaPorIndice(scanner: Scanner, idUsuario: Int, duenoNombre: String) {
        val mascotasDelUsuario = mostrarMascotasUsuarioConIndice(idUsuario, duenoNombre)

        if (mascotasDelUsuario.isNotEmpty()) {
            print("Ingrese el número de la lista de la mascota que desea eliminar: ")
            val opcion = scanner.nextInt()
            val indiceReal = opcion - 1

            if (indiceReal in mascotasDelUsuario.indices) {
                val mascotaAEliminar = mascotasDelUsuario[indiceReal]

                // Removemos el objeto exacto de la lista principal
                mascotas.remove(mascotaAEliminar)
                println(" ${mascotaAEliminar.nombre} ha sido eliminada del sistema correctamente!")
            } else {
                println("Número de opción inválido.")
            }
        }
    }

    // Función para mostrar todas las mascotas registradas
    fun mostrarMascotasUsuario(idDuenoActual: Int, duenoNombre: String) {

        val mascotasDelUsuario = mascotas.filter { it.duenoID == idDuenoActual }

        if (mascotasDelUsuario.isEmpty()) {
            println("No hay pacientes registrados actualmente en la clínica.")
        } else {
            println("\n--- Lista de Pacientes Registrados ---")
            for (mascota in mascotasDelUsuario) {
                // Suponiendo que tu clase Mascota tiene nombre, especie y edad

                println("Paciente: ${mascota.nombre} | Especie: ${mascota.especie} | Edad: ${mascota.edad} años | Dueño: ${duenoNombre}")
            }
            println("-----------------------------------------")
        }
    }

    fun mostrarMascotasTodas(usuariosRegistrados: List<Usuario>){

        if (mascotas.isEmpty()) {
            println("No hay pacientes registrados actualmente en la clínica.")
        } else {
            println("\n--- Lista de Pacientes Registrados ---")
            for (mascota in mascotas) {
                // Suponiendo que tu clase Mascota tiene nombre, especie y edad
                var dueno = usuariosRegistrados.find{it.ID == mascota.duenoID}
                val nombreDueno = dueno?.nombre ?: "Usuario no encontrado"
                println("Paciente: ${mascota.nombre} | Especie: ${mascota.especie} | Edad: ${mascota.edad} años | Dueño: ${nombreDueno}")
            }
            println("-----------------------------------------")
        }
    }

     fun agregarMascota(scanner: Scanner, usuario: Usuario) {
        println("--- Agregar Paciente ---")
        print("Nombre de la mascota: ")
        val nombre = scanner.next()
        print("Especie (Ej. Perro/Gato): ")
        val especie = scanner.next()
        print("Edad: ")
        val edad = scanner.nextInt()

         val nuevoID = mascotas.size+1;

        val nuevaMascota = Mascota(nuevoID, nombre, especie, edad, usuario.ID)
        agregarMascota(nuevaMascota)
        println("Paciente agregado con éxito!")
    }


}