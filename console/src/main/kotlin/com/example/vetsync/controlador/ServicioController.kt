package com.example.vetsync.controlador

import com.example.vetsync.modelo.Gestionable
import com.example.vetsync.modelo.Servicio
import com.example.vetsync.excepciones.ValidacionException
import com.example.vetsync.utils.Validador

class ServicioController : Gestionable<Servicio> {

    private val servicios = mutableListOf<Servicio>()

    init {
        cargarServiciosIniciales()
    }

    private fun cargarServiciosIniciales() {

        servicios.add(
            Servicio(
                id = 1,
                nombre = "Consulta General",
                descripcion = "Consulta médica general para la mascota",
                costo = 30.0
            )
        )

        servicios.add(
            Servicio(
                id = 2,
                nombre = "Vacunación",
                descripcion = "Aplicación de vacunas para la mascota",
                costo = 35.0
            )
        )

        servicios.add(
            Servicio(
                id = 3,
                nombre = "Peluquería",
                descripcion = "Servicio de baño y estética",
                costo = 25.0
            )
        )

        servicios.add(
            Servicio(
                id = 4,
                nombre = "Emergencia",
                descripcion = "Atención veterinaria de emergencia",
                costo = 60.0
            )
        )
    }

    override fun agregar(item: Servicio) {

        if (buscarPorId(item.id) != null) {
            throw ValidacionException(
                "Ya existe un servicio registrado con el ID ${item.id}."
            )
        }

        Validador.texto(item.nombre, "Nombre")
        Validador.texto(item.descripcion, "Descripción")
        Validador.costo(item.costo)

        servicios.add(item)
    }

    //Evita IDs duplicados
    private fun validarServicio(servicio: Servicio) {

        require(servicio.nombre.isNotBlank()) {
            "El nombre del servicio no puede estar vacío."
        }

        require(servicio.descripcion.isNotBlank()) {
            "La descripción del servicio no puede estar vacía."
        }

        require(servicio.costo > 0) {
            "El costo del servicio debe ser mayor que 0."
        }
    }

    override fun listar(): List<Servicio> {
        return servicios.toList()
    }

    fun obtenerServiciosActivos(): List<Servicio> {
        return servicios.filter { it.activo }
    }

    override fun actualizar(item: Servicio): Boolean {
        validarServicio(item)
        val indice = servicios.indexOfFirst {
            it.id == item.id
        }

        if (indice == -1) {
            return false
        }

        servicios[indice] = item
        return true
    }

    override fun eliminar(id: Int): Boolean {

        val servicio = servicios.find {
            it.id == id
        } ?: return false

        servicio.activo = false

        return true
    }

    fun buscarPorId(id: Int): Servicio? {

        return servicios.find {
            it.id == id && it.activo
        }
    }

    fun mostrarServicios() {

        val serviciosActivos = servicios.filter {
            it.activo
        }

        if (serviciosActivos.isEmpty()) {
            println("No hay servicios disponibles.")
            return
        }

        println("\n========== SERVICIOS DISPONIBLES ==========")

        serviciosActivos.forEach { servicio ->

            println(
                "ID: ${servicio.id} | " +
                        "${servicio.nombre} | " +
                        "$${"%.2f".format(servicio.costo)}"
            )

            println(
                "Descripción: ${servicio.descripcion}"
            )

            println("-------------------------------------------")
        }
    }
}