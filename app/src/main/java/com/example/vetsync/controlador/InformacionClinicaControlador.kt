
package com.example.vetsync.controlador

import android.util.Patterns
import com.example.vetsync.modelo.InformacionClinica
import com.example.vetsync.modelo.InformacionClinicaRepository
import com.example.vetsync.modelo.RolUsuario
import com.example.vetsync.modelo.SesionUsuario

class InformacionClinicaControlador {

    private val repository = InformacionClinicaRepository()

    // --------------------------------------------------
    // VERIFICAR PERMISOS ADMINISTRATIVOS
    // --------------------------------------------------

    private fun tienePermisosAdmin(): Boolean {
        return SesionUsuario.usuarioActual?.rol ==
                RolUsuario.ADMINISTRADOR &&
                SesionUsuario.idUsuario.isNotBlank()
    }

    // --------------------------------------------------
    // CONSULTAR INFORMACIÓN DE LA CLÍNICA
    // --------------------------------------------------

    fun obtenerInformacionClinica(
        onSuccess: (InformacionClinica) -> Unit,
        onError: (String) -> Unit
    ) {

        if (!tienePermisosAdmin()) {
            onError(
                "No tienes permisos para consultar la información de la clínica."
            )
            return
        }

        repository.obtenerInformacionClinica(

            onSuccess = { informacion ->
                onSuccess(informacion)
            },

            onFailure = { error ->
                onError(
                    error.message
                        ?: "No se pudo recuperar la información de la clínica."
                )
            }
        )
    }

    // --------------------------------------------------
    // ACTUALIZAR INFORMACIÓN DE LA CLÍNICA
    // --------------------------------------------------

    fun guardarInformacionClinica(
        informacion: InformacionClinica,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        if (!tienePermisosAdmin()) {
            onError(
                "No tienes permisos para modificar la información de la clínica."
            )
            return
        }

        val nombreLimpio = informacion.nombre.trim()
        val direccionLimpia = informacion.direccion.trim()
        val telefonoLimpio = informacion.telefono.trim()
        val correoLimpio = informacion.correo.trim()
        val descripcionLimpia = informacion.descripcion.trim()

        // 1. Validar nombre.
        if (nombreLimpio.isBlank()) {
            onError("El nombre de la clínica es obligatorio.")
            return
        }

        if (nombreLimpio.length < 3 || nombreLimpio.length > 100) {
            onError(
                "El nombre de la clínica debe tener entre 3 y 100 caracteres."
            )
            return
        }

        // 2. Validar dirección.
        if (direccionLimpia.isBlank()) {
            onError("La dirección de la clínica es obligatoria.")
            return
        }

        if (direccionLimpia.length > 200) {
            onError(
                "La dirección no debe superar los 200 caracteres."
            )
            return
        }

        // 3. Validar teléfono.
        if (
            telefonoLimpio.isBlank() ||
            !Patterns.PHONE.matcher(telefonoLimpio).matches()
        ) {
            onError("Ingresa un número telefónico válido.")
            return
        }

        val cantidadDigitos = telefonoLimpio.count {
            it.isDigit()
        }

        if (cantidadDigitos !in 8..15) {
            onError(
                "El teléfono debe contener entre 8 y 15 dígitos."
            )
            return
        }

        // 4. Validar correo.
        if (
            correoLimpio.isBlank() ||
            !Patterns.EMAIL_ADDRESS.matcher(correoLimpio).matches()
        ) {
            onError("Ingresa un correo electrónico válido.")
            return
        }

        // 5. Validar descripción.
        if (descripcionLimpia.length > 500) {
            onError(
                "La descripción no debe superar los 500 caracteres."
            )
            return
        }

        // 6. Construir objeto limpio.
        val informacionValidada = InformacionClinica(
            nombre = nombreLimpio,
            direccion = direccionLimpia,
            telefono = telefonoLimpio,
            correo = correoLimpio,
            descripcion = descripcionLimpia
        )

        // 7. Guardar en Firestore.
        repository.guardarInformacionClinica(
            informacion = informacionValidada,

            onSuccess = {
                onSuccess()
            },

            onFailure = { error ->
                onError(
                    error.message
                        ?: "No se pudo guardar la información de la clínica."
                )
            }
        )
    }
}
