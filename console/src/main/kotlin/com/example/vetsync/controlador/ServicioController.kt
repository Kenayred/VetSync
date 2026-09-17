package com.example.vetsync.controlador

import com.example.vetsync.modelo.Gestionable
import com.example.vetsync.modelo.Servicio

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
        servicios.add(item)

        println(
            "Servicio '${item.nombre}' agregado correctamente."
        )
    }

    override fun listar(): List<Servicio> {
        return servicios.toList()
    }

    override fun actualizar(item: Servicio): Boolean {

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