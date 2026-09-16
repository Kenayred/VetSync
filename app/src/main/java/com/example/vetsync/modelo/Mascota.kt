package com.example.vetsync.modelo

class Mascota(
    val id: Int,
    val nombre: String,
    var especie: String,
    var edad: Int,
    var duenoID: Int
) {
    fun mostrarDetalles() {
        println("ID: ${id} | Mascota: $nombre | Especie: $especie | Edad: $edad años")
    }
}