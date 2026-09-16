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
        println("✅ ¡La mascota ${mascota.nombre} ha sido registrada con éxito!")
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

        val nuevaMascota = Mascota(nombre, especie, edad, usuario.ID)
        agregarMascota(nuevaMascota)
        println("Paciente agregado con éxito!")
    }


}