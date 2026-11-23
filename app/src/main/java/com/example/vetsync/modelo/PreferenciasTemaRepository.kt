package com.example.vetsync.modelo

import android.content.Context

class PreferenciasTemaRepository(context: Context) {

    private val preferencias = context.applicationContext
        .getSharedPreferences(
            "vetsync_preferencias_tema",
            Context.MODE_PRIVATE
        )

    fun obtenerModoOscuro(usuarioId: String): Boolean {
        if (usuarioId.isBlank()) {
            return false
        }

        return preferencias.getBoolean(
            "modo_oscuro_$usuarioId",
            false
        )
    }

    fun guardarModoOscuro(
        usuarioId: String,
        modoOscuro: Boolean
    ) {
        if (usuarioId.isBlank()) {
            return
        }

        preferencias.edit()
            .putBoolean("modo_oscuro_$usuarioId", modoOscuro)
            .apply()
    }
}