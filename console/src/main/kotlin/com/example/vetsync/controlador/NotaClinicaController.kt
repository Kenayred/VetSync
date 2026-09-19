package com.example.vetsync.controlador

import com.example.vetsync.modelo.Gestionable
import com.example.vetsync.modelo.NotaClinica
import com.example.vetsync.excepciones.ValidacionException

class NotaClinicaController : Gestionable<NotaClinica> {

    private val notasClinicas =
        mutableListOf<NotaClinica>()

    // =========================================================
    // CREATE
    // =========================================================

    override fun agregar(item: NotaClinica) {

        validarNotaClinica(item)

        val existe = notasClinicas.any {
            it.id == item.id
        }

        require(!existe) {
            "Ya existe una nota clínica con el ID ${item.id}."
        }

        notasClinicas.add(item)

        println(
            "Nota clínica registrada correctamente."
        )
    }

    // =========================================================
    // READ
    // =========================================================

    override fun listar(): List<NotaClinica> {
        return notasClinicas.toList()
    }

    fun buscarPorId(id: Int): NotaClinica? {

        return notasClinicas.find {
            it.id == id
        }
    }

    fun obtenerHistorialMascota(
        mascotaId: Int
    ): List<NotaClinica> {

        return notasClinicas.filter {
            it.mascotaId == mascotaId
        }
    }

    fun obtenerNotaDeCita(
        citaId: Int
    ): NotaClinica? {

        return notasClinicas.find {
            it.citaId == citaId
        }
    }

    // =========================================================
    // UPDATE
    // =========================================================

    override fun actualizar(
        item: NotaClinica
    ): Boolean {

        validarNotaClinica(item)

        val indice = notasClinicas.indexOfFirst {
            it.id == item.id
        }

        if (indice == -1) {
            return false
        }

        notasClinicas[indice] = item

        return true
    }

    // =========================================================
    // DELETE
    // =========================================================

    override fun eliminar(id: Int): Boolean {

        return notasClinicas.removeIf {
            it.id == id
        }
    }

    // =========================================================
    // VALIDACIONES
    // =========================================================

    private fun validarNotaClinica(
        nota: NotaClinica
    ) {

        if (nota.diagnostico.isBlank()) {

            throw ValidacionException(
                "El diagnóstico es obligatorio."
            )
        }

        if (nota.receta.isBlank()) {

            throw ValidacionException(
                "La receta médica es obligatoria."
            )
        }

        if (nota.peso != null && nota.peso <= 0) {

            throw ValidacionException(
                "El peso debe ser mayor que 0."
            )
        }

        if (
            nota.temperatura != null &&
            nota.temperatura <= 0
        ) {

            throw ValidacionException(
                "La temperatura debe ser mayor que 0."
            )
        }
    }
}