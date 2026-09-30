package com.example.vetsync.modelo

abstract class Persona(
    open var id: Int = 0,
    open var nombre: String = ""
) {

    abstract fun obtenerTipo(): String

    fun mostrarInformacionBasica() {
        println("ID: $id | Nombre: $nombre | Tipo: ${obtenerTipo()}")
    }
}