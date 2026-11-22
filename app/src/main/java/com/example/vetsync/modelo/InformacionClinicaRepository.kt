
package com.example.vetsync.modelo

import com.google.firebase.firestore.FirebaseFirestore

class InformacionClinicaRepository {

    private val db = FirebaseFirestore.getInstance()

    private val referenciaClinica = db
        .collection("configuracion")
        .document("clinica")

    // --------------------------------------------------
    // OBTENER INFORMACIÓN DE LA CLÍNICA
    // --------------------------------------------------

    fun obtenerInformacionClinica(
        onSuccess: (InformacionClinica) -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        referenciaClinica.get()

            .addOnSuccessListener { documento ->

                if (!documento.exists()) {

                    // Si todavía no existe el documento,
                    // mostramos los valores iniciales del modelo.
                    onSuccess(
                        InformacionClinica()
                    )

                    return@addOnSuccessListener
                }

                val informacion = documento.toObject(
                    InformacionClinica::class.java
                )

                if (informacion != null) {

                    onSuccess(informacion)

                } else {

                    onFailure(
                        IllegalStateException(
                            "No se pudo interpretar la información de la clínica."
                        )
                    )
                }
            }

            .addOnFailureListener { error ->
                onFailure(error)
            }
    }

    // --------------------------------------------------
    // GUARDAR INFORMACIÓN DE LA CLÍNICA
    // --------------------------------------------------

    fun guardarInformacionClinica(
        informacion: InformacionClinica,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        referenciaClinica.set(informacion)

            .addOnSuccessListener {
                onSuccess()
            }

            .addOnFailureListener { error ->
                onFailure(error)
            }
    }
}
