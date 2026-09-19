package com.example.vetsync.vista

import com.example.vetsync.controlador.CitaController
import com.example.vetsync.controlador.GestorMascotas
import com.example.vetsync.controlador.NotaClinicaController
import com.example.vetsync.modelo.EstadoCita
import com.example.vetsync.utils.ConsolaUtil
import java.util.Scanner

object NotaClinicaView {

    // =========================================================
    // AGREGAR NOTA CLÍNICA
    // =========================================================

    fun agregarNotaClinica(
        scanner: Scanner,
        citaController: CitaController,
        mascotaController: GestorMascotas,
        notaClinicaController: NotaClinicaController
    ) {

        println("\n========== AGREGAR NOTA CLÍNICA ==========")

        val citas = citaController.listar().filter {
            it.estado == EstadoCita.CONFIRMADA
        }

        if (citas.isEmpty()) {

            println(
                "No existen citas confirmadas pendientes de atención."
            )

            return
        }

        println("\n--- Citas disponibles para atención ---")

        citas.forEach { cita ->

            val mascota = mascotaController
                .obtenerMascotasUsuario(cita.propietarioId)
                .find {
                    it.id == cita.mascotaId
                }

            println(
                "ID: ${cita.id} | " +
                        "Mascota: ${mascota?.nombre ?: "No encontrada"} | " +
                        "Fecha: ${cita.fecha} | " +
                        "Hora: ${cita.hora}"
            )
        }

        val citaId = ConsolaUtil.leerEntero(
            scanner,
            "\nIngrese el ID de la cita: ",
            minimo = 1
        )

        val cita = citaController.buscarPorId(citaId)

        if (cita == null) {
            println(
                "No existe una cita con ese ID."
            )
            return
        }

        if (cita.estado != EstadoCita.CONFIRMADA) {
            println(
                "Solo se puede registrar una nota " +
                        "en una cita confirmada."
            )
            return
        }

        // =====================================================
        // DATOS DE LA CONSULTA
        // =====================================================

        val motivo = ConsolaUtil.leerTexto(
            scanner,
            "\nMotivo de consulta (opcional): ",
            obligatorio = false
        )

        val pesoIngresado = ConsolaUtil.leerDecimal(
            scanner,
            "Peso en kg (0 si desea omitirlo): ",
            minimo = 0.0
        )

        val peso = if (pesoIngresado > 0) {
            pesoIngresado
        } else {
            null
        }

        val temperaturaIngresada = ConsolaUtil.leerDecimal(
            scanner,
            "Temperatura en °C (0 si desea omitirla): ",
            minimo = 0.0
        )

        val temperatura = if (temperaturaIngresada > 0) {
            temperaturaIngresada
        } else {
            null
        }

        val diagnostico = ConsolaUtil.leerTexto(scanner,
            "\nDiagnóstico (obligatorio): "
            )

        val receta = ConsolaUtil.leerTexto(
                scanner,
                "Receta médica (obligatoria): "
            )

        val observaciones = ConsolaUtil.leerTexto(
            scanner,
            "Observaciones (opcional): ",
            obligatorio = false
        )

        try {

            val nuevoId =
                notaClinicaController
                    .listar()
                    .maxOfOrNull {
                        it.id
                    }
                    ?.plus(1) ?: 1

            val nota = com.example.vetsync.modelo.NotaClinica(

                id = nuevoId,

                mascotaId = cita.mascotaId,

                citaId = cita.id,

                fecha = cita.fecha,

                motivo = motivo,

                peso = peso,

                temperatura = temperatura,

                diagnostico = diagnostico,

                receta = receta,

                observaciones =
                    observaciones.ifBlank {
                        null
                    }
            )

            notaClinicaController.agregar(nota)

            val citaCompletada =
                citaController.cambiarEstado(
                    cita.id,
                    EstadoCita.COMPLETADA
                )

            if (citaCompletada) {

                println(
                    "\nLa consulta fue completada correctamente."
                )

            } else {

                println(
                    "\nLa nota se registró, " +
                            "pero no fue posible completar la cita."
                )
            }

        } catch (e: IllegalArgumentException) {

            println(
                "\nNo se pudo guardar la nota clínica."
            )

            println(
                "Motivo: ${e.message}"
            )
        }
    }

    // =========================================================
    // HISTORIAL CLÍNICO
    // =========================================================

    fun mostrarHistorial(
        scanner: Scanner,
        mascotaController: GestorMascotas,
        notaClinicaController: NotaClinicaController
    ) {

        println("\n========== HISTORIAL CLÍNICO ==========")

        val mascotas =
            mascotaController.obtenerTodasLasMascotas()

        if (mascotas.isEmpty()) {

            println(
                "No existen mascotas registradas."
            )

            return
        }

        mascotas.forEach { mascota ->

            println(
                "ID: ${mascota.id} | " +
                        "Nombre: ${mascota.nombre} | " +
                        "Especie: ${mascota.especie}"
            )
        }

        val mascotaId = ConsolaUtil.leerEntero(
            scanner,
            "\nIngrese el ID de la mascota: ",
            minimo = 1
        )

        val mascota =
            mascotas.find {
                it.id == mascotaId
            }

        if (mascota == null) {

            println(
                "No existe una mascota con ese ID."
            )

            return
        }

        val historial =
            notaClinicaController
                .obtenerHistorialMascota(
                    mascotaId
                )

        if (historial.isEmpty()) {

            println(
                "\n${mascota.nombre} todavía no tiene " +
                        "registros clínicos."
            )

            return
        }

        println(
            "\n--- Historial de ${mascota.nombre} ---"
        )

        historial.forEach { nota ->

            println(
                "\nFecha: ${nota.fecha}"
            )

            println(
                "Motivo: ${
                    nota.motivo.ifBlank {
                        "No especificado"
                    }
                }"
            )

            println(
                "Peso: ${
                    nota.peso?.let {
                        "$it kg"
                    } ?: "No registrado"
                }"
            )

            println(
                "Temperatura: ${
                    nota.temperatura?.let {
                        "$it °C"
                    } ?: "No registrada"
                }"
            )

            println(
                "Diagnóstico: ${nota.diagnostico}"
            )

            println(
                "Receta: ${nota.receta}"
            )

            println(
                "Observaciones: ${
                    nota.observaciones ?: "Ninguna"
                }"
            )

            println("----------------------------------------")
        }
    }
}