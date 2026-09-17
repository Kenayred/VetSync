package com.example.vetsync.modelo

data class Mascota(
    val id: Int,
    val nombre: String,
    var especie: String,
    var edad: Int,
    val duenoId: Int
) {
    fun mostrarDetalles() {
        println(
            "ID: $id | Mascota: $nombre | " +
                    "Especie: $especie | Edad: $edad años"
        )
    }
}