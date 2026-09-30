package com.example.vetsync.controlador

import com.example.vetsync.modelo.FirebaseDatabaseManager
import com.example.vetsync.modelo.Mascota
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.example.vetsync.modelo.SesionUsuario

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
        if (nombre.isBlank() || especie.isBlank() || raza.isBlank() || sexo.isBlank() || fechaNacimiento.isBlank() || pesoTexto.isBlank()) {
            onError("Por favor, completa todos los campos principales de la mascota.")
            return
        }

        val pesoConvertido = pesoTexto.trim().replace(",", ".").toDoubleOrNull()
        if (pesoConvertido == null || pesoConvertido <= 0.0) {
            onError("Ingresa un peso válido en kg (ejemplo: 15.5).")
            return
        }

        val nuevoId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()

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

    fun obtenerMascotasUsuario(
        idUsuario: Int = SesionUsuario.idUsuario,
        onSuccess: (List<Mascota>) -> Unit,
        onError: (String) -> Unit
    ){

        dbManager.readData("mascotas", object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot){
                val listaMascotas = mutableListOf<Mascota>()

                for (hijo in snapshot.children){
                    val mascota = hijo.getValue(Mascota::class.java)
                    if(mascota != null && mascota.idDueno == idUsuario){
                        listaMascotas.add(mascota)
                    }
                }
                onSuccess(listaMascotas)
            }
            override fun  onCancelled(error: DatabaseError){
                onError("ERror al cargar las mascotas: ${error.message}")
            }
        })

    }

    // Agrega esta función dentro de tu clase MascotaControlador:
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
        val path = "mascotas/${mascota.id}"

        dbManager.insertData(
            data = mascotaActualizada,
            path = path,
            completionListener = DatabaseReference.CompletionListener { databaseError, _ ->
                if (databaseError == null) {
                    onSuccess(mascotaActualizada)
                } else {
                    onError("Error al actualizar la foto: ${databaseError.message}")
                }
            }
        )
    }
}