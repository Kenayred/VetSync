package com.example.vetsync.modelo

abstract class Persona(
    open val id: Int,
    open val nombre: String
) {

    abstract fun obtenerTipo(): String

    fun mostrarInformacionBasica() {
        println("ID: $id | Nombre: $nombre | Tipo: ${obtenerTipo()}")
    }
}