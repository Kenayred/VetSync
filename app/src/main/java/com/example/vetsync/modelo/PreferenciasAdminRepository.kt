
package com.example.vetsync.modelo

import android.content.Context

class PreferenciasAdminRepository(context: Context) {

    private val preferencias = context.applicationContext
        .getSharedPreferences(
            "vetsync_preferencias_admin",
            Context.MODE_PRIVATE
        )

    fun obtenerMostrarIndicadorNotificaciones(
        usuarioId: String
    ): Boolean {
        return preferencias.getBoolean(
            "mostrar_indicador_$usuarioId",
            true
        )
    }

    fun guardarMostrarIndicadorNotificaciones(
        usuarioId: String,
        mostrar: Boolean
    ) {
        preferencias.edit()
            .putBoolean(
                "mostrar_indicador_$usuarioId",
                mostrar
            )
            .apply()
    }
}
