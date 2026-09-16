package com.example.vetsync.modelo

class Mascota(val nombre: String, val especie: String, var edad: Int, var duenoID: Int) {
    fun mostrarDetalles() {
        println("Mascota: $nombre | Especie: $especie | Edad: $edad años")
    }
}