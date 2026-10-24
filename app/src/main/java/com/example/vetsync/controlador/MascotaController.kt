package com.example.vetsync.controlador

import com.example.vetsync.modelo.MascotaRepository
import com.example.vetsync.modelo.Mascota
import com.example.vetsync.modelo.SesionUsuario
import java.util.UUID
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.ResolverStyle
import java.util.Locale


class MascotaControlador {

    private val dbManager = MascotaRepository()

    private val formatoFechaNacimiento =
        DateTimeFormatter
            .ofPattern("dd/MM/uuuu", Locale.getDefault())
            .withResolverStyle(ResolverStyle.STRICT)

    private fun esFechaNacimientoValida(
        fechaTexto: String
    ): Boolean {
        return try {
            val fecha = LocalDate.parse(
                fechaTexto.trim(),
                formatoFechaNacimiento
            )

            !fecha.isAfter(LocalDate.now())
        } catch (e: DateTimeParseException) {
            false
        }
    }

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
        if (idUsuario.isBlank()) {
            onError("No se encontró una sesión activa. Inicia sesión nuevamente.")
            return
        }

        if (
            nombre.isBlank() ||
            especie.isBlank() ||
            raza.isBlank() ||
            sexo.isBlank()
        ) {
            onError("Completa el nombre, la especie, la raza y el sexo de la mascota.")
            return
        }

        val edadConvertida = edadTexto.trim().toIntOrNull()

        if (edadConvertida == null || edadConvertida < 0) {
            onError("La edad debe ser un número entero igual o mayor que cero.")
            return
        }

        if (fechaNacimiento.isBlank()) {
            onError("Selecciona la fecha de nacimiento de la mascota.")
            return
        }

        if (!esFechaNacimientoValida(fechaNacimiento)) {
            onError("La fecha de nacimiento no es válida o está en el futuro.")
            return
        }

        val pesoConvertido = pesoTexto
            .trim()
            .replace(",", ".")
            .toDoubleOrNull()

        if (pesoConvertido == null || !pesoConvertido.isFinite() || pesoConvertido <= 0.0) {
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
            edad = edadConvertida.toString(),
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

    fun editarMascota(
        mascota: Mascota,
        onSuccess: (Mascota) -> Unit,
        onError: (String) -> Unit
    ) {
        if (mascota.nombre.isBlank()) {
            onError("El nombre de la mascota es obligatorio.")
            return
        }

        if (mascota.especie.isBlank() || mascota.especie == "Seleccione el tipo") {
            onError("Debes seleccionar una especie o tipo válido.")
            return
        }

        if (mascota.peso < 0.0) {
            onError("El peso no puede ser negativo.")
            return
        }

        dbManager.editarMascota(
            mascota = mascota,
            onSuccess = {
                onSuccess(mascota)
            },
            onFailure = { excepcion ->
                onError(excepcion.message ?: "Error desconocido al actualizar la mascota.")
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