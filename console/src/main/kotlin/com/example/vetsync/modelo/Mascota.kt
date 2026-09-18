package com.example.vetsync.modelo

data class Mascota(
    val id: Int,
    val nombre: String,
    var especie: String,
    var edad: Int,
    val duenoId: Int,
    var raza: String = "No especificada",
    var sexo: String = "No especificado",
    var peso: Double? = null, // Puede ser null (null safety)
    var microchip: String? = null, //
    var alergias: String? = null //
) {

    fun mostrarDetalles() {

        println(
            "ID: $id | " +
                    "Mascota: $nombre | " +
                    "Especie: $especie | " +
                    "Raza: $raza | " +
                    "Edad: $edad años"
        )

        println(
            "Sexo: $sexo | " +
                    "Peso: ${
                        peso?.let { "$it kg" } ?: "No registrado"
                    }"
        )

        println(
            "Microchip: ${
                microchip ?: "No registrado"
            }"
        )

        println(
            "Alergias: ${
                alergias ?: "No registradas"
            }"
        )
    }
}