package com.example.vetsync.modelo
import com.example.vetsync.modelo.Mascota

class Usuario(
    val ID: Int,
    val username: String,
    val contrasena: String,
    val nombre: String,
    val rol: String = "Cliente",
    //val mascotas: MutableList<Mascota> = mutableListOf()
)