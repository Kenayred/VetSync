package com.example.vetsync.modelo


data class Mascota(
    val id: Int = 0,
    val idDueno: Int = 0,
    val nombre: String = "",
    val especie: String = "",
    val raza: String = "",
    val edad: Int = 0,
    val sexo: String = "",
    val fechaNacimiento: String = "",
    val peso: Double = 0.0,
    val colorMarcas: String = "",
    val observaciones: String = "",
    val fotoUrl: String = ""
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

//        println(
//            "Microchip: ${
//                microchip ?: "No registrado"
//            }"
//        )
//
//        println(
//            "Alergias: ${
//                alergias ?: "No registradas"
//            }"
//        )
    }
}