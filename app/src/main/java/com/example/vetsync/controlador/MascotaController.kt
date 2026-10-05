package com.example.vetsync.controlador

import com.example.vetsync.modelo.MascotaRepository
import com.example.vetsync.modelo.Mascota
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.example.vetsync.modelo.SesionUsuario
import java.util.UUID


class MascotaControlador {

    private val dbManager = MascotaRepository()

    fun registrarMascota(
        idUsuario: String = "",
        nombre: String,
        especie: String,
        raza: String,
        edadTexto: String,
        sexo: String,
        fechaNacimiento: String,
        pesoTexto: String,
        colorMarcas: String,
        observaciones: String,
        fotoUrl: String = "",
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (nombre.isBlank() || especie.isBlank() || raza.isBlank() || sexo.isBlank() || fechaNacimiento.isBlank() || pesoTexto.isBlank() || edadTexto.isBlank() ) {
            onError("Por favor, completa todos los campos principales de la mascota.")
            return
        }

        val pesoConvertido = pesoTexto.trim().replace(",", ".").toDoubleOrNull()
        if (pesoConvertido == null || pesoConvertido <= 0.0) {
            onError("Ingresa un peso válido en kg (ejemplo: 15.5).")
            return
        }

        val nuevoId = UUID.randomUUID().toString()

        val nuevaMascota = Mascota(
            id = nuevoId,
            idDueno = idUsuario,
            nombre = nombre.trim(),
            especie = especie.trim(),
            raza = raza.trim(),
            edad = edadTexto,
            sexo = sexo.trim(),
            fechaNacimiento = fechaNacimiento.trim(),
            peso = pesoConvertido,
            colorMarcas = colorMarcas.trim(),
            observaciones = observaciones.trim(),
            fotoUrl = fotoUrl
        )

        dbManager.registrarMascota(
            mascota = nuevaMascota,
            onSuccess = { onSuccess() },
            onFailure = { error -> onError(error.message ?: "Error desconocido") }
        )
    }

    fun obtenerMascotasUsuario(
        idUsuario: String = SesionUsuario.idUsuario,
        onSuccess: (List<Mascota>) -> Unit,
        onError: (String) -> Unit
    ){

        dbManager.obtenerMascotasPorUsuario(
            idUsuario = idUsuario,
            onSuccess = { listaMascota-> onSuccess(listaMascota) },
            onFailure = { error -> onError(error.message ?: "Error desconocido") }
            )

    }

    fun actualizarFotoMascota(
        mascota: Mascota,
        nuevaFotoBase64: String,
        onSuccess: (Mascota) -> Unit,
        onError: (String) -> Unit
    ) {
        if (nuevaFotoBase64.isBlank()) {
            onError("No se pudo procesar la imagen seleccionada.")
            return
        }

        val mascotaActualizada = mascota.copy(fotoUrl = nuevaFotoBase64)

        dbManager.actualizarFotoMascota(
            idMascota = mascota.id,
            nuevaFotoUrl = nuevaFotoBase64,
            onSuccess = {
                onSuccess(mascotaActualizada)
            },
            onFailure = { error ->
                onError(error.message ?: "Error al actualizar la foto en la base de datos.")
            }
        )
    }

    fun eliminarMascota(
        mascotaId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ){
        dbManager.eliminarMascotaa(
            idMascota = mascotaId,
            onSuccess = {
                onSuccess()
            },
            onFailure = {
                    error -> onError(error.message ?: "Error al borrar la mascota")
            }
        )
    }
}