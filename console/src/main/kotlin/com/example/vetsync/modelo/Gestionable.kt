package com.example.vetsync.modelo

interface Gestionable<T> {
    fun agregar(item: T)
    fun listar(): List<T>
    fun actualizar(item: T): Boolean
    fun eliminar(id: Int): Boolean
}