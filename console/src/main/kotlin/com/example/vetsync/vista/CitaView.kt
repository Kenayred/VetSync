package com.example.vetsync.vista

import com.example.vetsync.controlador.CitaController
import com.example.vetsync.controlador.GestorMascotas
import com.example.vetsync.controlador.ServicioController
import com.example.vetsync.modelo.EstadoCita
import com.example.vetsync.modelo.Usuario
import com.example.vetsync.excepciones.CitaNoDisponibleException
import com.example.vetsync.excepciones.OperacionNoPermitidaException
import com.example.vetsync.utils.Logger
import java.util.Scanner

object CitaView {

    // =========================================================
    // CREAR CITA
    // =========================================================

    fun crearCita(
        scanner: Scanner,
        usuarioActual: Usuario,
        mascotaController: GestorMascotas,
        servicioController: ServicioController,
        citaController: CitaController
    ) {

        println("\n========== NUEVA CITA ==========")

        // -----------------------------------------------------
        // 1. Seleccionar mascota
        // -----------------------------------------------------

        val mascotas = mascotaController
            .obtenerMascotasUsuario(usuarioActual.id)

        if (mascotas.isEmpty()) {
            println("No tienes mascotas registradas.")
            println("Primero debes registrar una mascota.")
            return
        }

        println("\n--- Seleccione una mascota ---")

        mascotas.forEachIndexed { index, mascota ->
            println(
                "${index + 1}. " +
                        "${mascota.nombre} | " +
                        "${mascota.especie} | " +
                        "${mascota.edad} años"
            )
        }

        print("Seleccione una opción: ")
        val opcionMascota = scanner.nextInt()

        val indiceMascota = opcionMascota - 1

        if (indiceMascota !in mascotas.indices) {
            println("Opción de mascota inválida.")
            return
        }

        val mascotaSeleccionada = mascotas[indiceMascota]

        // -----------------------------------------------------
        // 2. Seleccionar servicio
        // -----------------------------------------------------

        val servicios = servicioController
            .obtenerServiciosActivos()

        if (servicios.isEmpty()) {
            println("No hay servicios disponibles.")
            return
        }

        println("\n--- Servicios disponibles ---")

        servicios.forEachIndexed { index, servicio ->

            println(
                "${index + 1}. " +
                        "${servicio.nombre} | " +
                        "$${"%.2f".format(servicio.costo)}"
            )

            println(
                "   ${servicio.descripcion}"
            )
        }

        print("Seleccione un servicio: ")
        val opcionServicio = scanner.nextInt()

        val indiceServicio = opcionServicio - 1

        if (indiceServicio !in servicios.indices) {
            println("Opción de servicio inválida.")
            return
        }

        val servicioSeleccionado = servicios[indiceServicio]

        // -----------------------------------------------------
        // 3. Fecha
        // -----------------------------------------------------

        print("\nIngrese la fecha (dd/MM/yyyy): ")
        val fecha = scanner.next()

        // -----------------------------------------------------
        // 4. Hora
        // -----------------------------------------------------

        print("Ingrese la hora (HH:mm): ")
        val hora = scanner.next()

        // -----------------------------------------------------
        // 5. Motivo
        // -----------------------------------------------------

        print("Motivo de la consulta (opcional): ")
        val motivo = scanner.nextLine().trim()

        /*
         * Si el usuario todavía estaba en la misma línea
         * después de ingresar la hora, puede quedar vacío.
         */
        val motivoFinal = if (motivo.isBlank()) {
            ""
        } else {
            motivo
        }

        // -----------------------------------------------------
        // Crear cita
        // -----------------------------------------------------

        try {

            val nuevaCita = citaController.crearCita(
                mascotaId = mascotaSeleccionada.id,
                propietarioId = usuarioActual.id,
                servicioId = servicioSeleccionado.id,
                fecha = fecha,
                hora = hora,
                motivo = motivoFinal
            )

            println("\n========== CITA SOLICITADA ==========")

            println("ID de cita: ${nuevaCita.id}")
            println("Mascota: ${mascotaSeleccionada.nombre}")
            println("Servicio: ${servicioSeleccionado.nombre}")
            println("Fecha: ${nuevaCita.fecha}")
            println("Hora: ${nuevaCita.hora}")
            println("Veterinario: ${nuevaCita.veterinario}")
            println("Costo: $${"%.2f".format(nuevaCita.costo)}")
            println("Estado: ${nuevaCita.estado}")

            println("=====================================")

        } catch (e: CitaNoDisponibleException) {

            println(
                "\nNo se pudo crear la cita."
            )

            println(
                "Motivo: ${e.message}"
            )

            Logger.error(
                modulo = "Citas",
                mensaje = e.message ?: "Cita no disponible",
                excepcion = e
            )

        } catch (e: IllegalArgumentException) {

            println(
                "\nNo se pudo crear la cita."
            )

            println(
                "Motivo: ${e.message}"
            )

            Logger.error(
                modulo = "Citas",
                mensaje = e.message ?: "Error de validación",
                excepcion = e
            )
        }
    }

    // =========================================================
    // MOSTRAR CITAS DEL CLIENTE
    // =========================================================

    fun mostrarCitas(
        usuarioActual: Usuario,
        citaController: CitaController,
        mascotaController: GestorMascotas,
        servicioController: ServicioController
    ) {

        val citas = citaController
            .listarCitasPropietario(usuarioActual.id)

        if (citas.isEmpty()) {
            println("\nNo tienes citas registradas.")
            return
        }

        println("\n========== MIS CITAS ==========")

        for (cita in citas) {

            val mascota = mascotaController
                .obtenerMascotasUsuario(usuarioActual.id)
                .find { it.id == cita.mascotaId }

            val servicio = servicioController
                .listar()
                .find { it.id == cita.servicioId }

            println(
                "\nID de cita: ${cita.id}"
            )

            println(
                "Mascota: ${mascota?.nombre ?: "No encontrada"}"
            )

            println(
                "Servicio: ${servicio?.nombre ?: "No encontrado"}"
            )

            println(
                "Fecha: ${cita.fecha}"
            )

            println(
                "Hora: ${cita.hora}"
            )

            println(
                "Veterinario: ${cita.veterinario}"
            )

            println(
                "Costo: $${"%.2f".format(cita.costo)}"
            )

            println(
                "Estado: ${cita.estado}"
            )

            if (cita.motivo.isNotBlank()) {
                println(
                    "Motivo: ${cita.motivo}"
                )
            }

            println("--------------------------------")
        }
    }

    // =========================================================
    // REPROGRAMAR
    // =========================================================

    fun reprogramarCita(
        scanner: Scanner,
        usuarioActual: Usuario,
        citaController: CitaController,
        mascotaController: GestorMascotas,
        servicioController: ServicioController
    ) {

        val citas = citaController
            .listarCitasPropietario(usuarioActual.id)

        if (citas.isEmpty()) {
            println("\nNo tienes citas registradas.")
            return
        }

        println("\n========== REPROGRAMAR CITA ==========")

        citas.forEach { cita ->

            println(
                "ID: ${cita.id} | " +
                        "${cita.fecha} ${cita.hora} | " +
                        "Estado: ${cita.estado}"
            )
        }

        print("\nIngrese el ID de la cita: ")
        val idCita = scanner.nextInt()

        val cita = citaController.buscarPorId(idCita)

        if (cita == null) {
            println("No existe una cita con ese ID.")
            return
        }

        if (cita.propietarioId != usuarioActual.id) {
            println("No puedes modificar una cita de otro usuario.")
            return
        }

        if (cita.estado != EstadoCita.PENDIENTE) {
            println(
                "Solo las citas pendientes pueden ser reprogramadas."
            )
            return
        }

        print("Nueva fecha (dd/MM/yyyy): ")
        val nuevaFecha = scanner.next()

        print("Nueva hora (HH:mm): ")
        val nuevaHora = scanner.next()

        try {

            val resultado = citaController.reprogramarCita(
                id = idCita,
                nuevaFecha = nuevaFecha,
                nuevaHora = nuevaHora
            )

            if (resultado) {
                println(
                    "La cita ${cita.id} fue reprogramada correctamente."
                )
            }

        } catch (e: IllegalArgumentException) {

            println(
                "No se pudo reprogramar la cita."
            )

            println(
                "Motivo: ${e.message}"
            )
        }
    }

    // =========================================================
    // CANCELAR
    // =========================================================

    fun cancelarCita(
        scanner: Scanner,
        usuarioActual: Usuario,
        citaController: CitaController
    ) {

        val citas = citaController
            .listarCitasPropietario(usuarioActual.id)

        if (citas.isEmpty()) {
            println("\nNo tienes citas registradas.")
            return
        }

        println("\n========== CANCELAR CITA ==========")

        citas.forEach { cita ->

            println(
                "ID: ${cita.id} | " +
                        "${cita.fecha} ${cita.hora} | " +
                        "Estado: ${cita.estado}"
            )
        }

        print("\nIngrese el ID de la cita: ")
        val idCita = scanner.nextInt()

        val cita = citaController.buscarPorId(idCita)

        if (cita == null) {
            println("No existe una cita con ese ID.")
            return
        }

        if (cita.propietarioId != usuarioActual.id) {
            println(
                "No puedes cancelar una cita de otro usuario."
            )
            return
        }

        if (
            cita.estado == EstadoCita.COMPLETADA ||
            cita.estado == EstadoCita.CANCELADA
        ) {
            println(
                "Esta cita ya no puede ser cancelada."
            )
            return
        }

        val resultado =
            citaController.eliminar(idCita)

        if (resultado) {

            println(
                "La cita $idCita ha sido cancelada correctamente."
            )

        } else {

            println(
                "No fue posible cancelar la cita."
            )
        }
    }
}