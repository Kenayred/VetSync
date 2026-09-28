package com.example.vetsync.controlador

import com.example.vetsync.modelo.FirebaseDatabaseManager
import com.example.vetsync.modelo.Mascota
import com.google.firebase.database.DatabaseReference

class MascotaControlador {

    private val dbManager = FirebaseDatabaseManager()

    fun registrarMascota(
        idUsuario: Int = 0,
        nombre: String,
        especie: String,
        raza: String,
        edad: Int = 0,
        sexo: String,
        fechaNacimiento: String,
        pesoTexto: String,
        colorMarcas: String,
        observaciones: String,
        fotoUrl: String = "",
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // 1. Validar campos obligatorios
        if (nombre.isBlank() || especie.isBlank() || raza.isBlank() || sexo.isBlank() || fechaNacimiento.isBlank() || pesoTexto.isBlank()) {
            onError("Por favor, completa todos los campos principales de la mascota.")
            return
        }

        // 2. Validar que el peso sea un número válido y mayor a 0
        val pesoConvertido = pesoTexto.trim().replace(",", ".").toDoubleOrNull()
        if (pesoConvertido == null || pesoConvertido <= 0.0) {
            onError("Ingresa un peso válido en kg (ejemplo: 15.5).")
            return
        }

        // 3. Generar ID único para la mascota
        val nuevoId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()

        // 4. Instanciar el modelo Mascota
        val nuevaMascota = Mascota(
            id = nuevoId,
            idDueno = idUsuario,
            nombre = nombre.trim(),
            especie = especie.trim(),
            raza = raza.trim(),
            edad = edad,
            sexo = sexo.trim(),
            fechaNacimiento = fechaNacimiento.trim(),
            peso = pesoConvertido,
            colorMarcas = colorMarcas.trim(),
            observaciones = observaciones.trim(),
            fotoUrl = fotoUrl
        )


        val path = "mascotas/$nuevoId"

        dbManager.insertData(
            data = nuevaMascota,
            path = path,
            completionListener = DatabaseReference.CompletionListener { databaseError, _ ->
                if (databaseError == null) {
                    onSuccess()
                } else {
                    onError("Error al guardar la mascota: ${databaseError.message}")
                }
            }
        )
    }
}