
package com.example.vetsync.util

import android.app.Activity
import android.app.LocaleManager
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.util.Locale

object GestorIdioma {

    const val ESPANOL = "es"
    const val INGLES = "en"

    private const val NOMBRE_PREFERENCIAS =
        "vetsync_configuracion_idioma"

    private const val CLAVE_IDIOMA = "idioma_seleccionado"

    private val idiomasPermitidos = setOf(
        ESPANOL,
        INGLES
    )

    // --------------------------------------------------
    // OBTENER IDIOMA SELECCIONADO
    // --------------------------------------------------

    fun obtenerIdioma(context: Context): String {

        // Android 13 o superior:
        // respetamos también la selección realizada
        // desde los ajustes de idioma del sistema.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            val administradorIdiomas =
                context.getSystemService(LocaleManager::class.java)

            val idiomasSistema =
                administradorIdiomas.applicationLocales

            if (!idiomasSistema.isEmpty) {

                val codigo = idiomasSistema[0].language

                if (codigo in idiomasPermitidos) {
                    return codigo
                }
            }
        }

        // Android 8 a Android 12:
        // recuperamos la preferencia local.

        return context.getSharedPreferences(
            NOMBRE_PREFERENCIAS,
            Context.MODE_PRIVATE
        ).getString(
            CLAVE_IDIOMA,
            ESPANOL
        ) ?: ESPANOL
    }

    // --------------------------------------------------
    // APLICAR CONFIGURACIÓN DE IDIOMA
    // --------------------------------------------------

    fun aplicarConfiguracion(
        context: Context
    ): Context {

        val codigoIdioma = obtenerIdioma(context)

        val idioma = Locale.forLanguageTag(
            codigoIdioma
        )

        Locale.setDefault(idioma)

        val configuracion = Configuration(
            context.resources.configuration
        )

        configuracion.setLocale(idioma)
        configuracion.setLayoutDirection(idioma)

        return context.createConfigurationContext(
            configuracion
        )
    }

    // --------------------------------------------------
    // CAMBIAR IDIOMA
    // --------------------------------------------------

    fun cambiarIdioma(
        context: Context,
        nuevoIdioma: String
    ) {

        if (nuevoIdioma !in idiomasPermitidos) {
            return
        }

        // Guardamos la preferencia para las versiones
        // anteriores a Android 13.

        context.getSharedPreferences(
            NOMBRE_PREFERENCIAS,
            Context.MODE_PRIVATE
        ).edit()
            .putString(
                CLAVE_IDIOMA,
                nuevoIdioma
            )
            .apply()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            // Android 13 o superior:
            // actualizar la preferencia de idioma del sistema.

            val administradorIdiomas =
                context.getSystemService(LocaleManager::class.java)

            administradorIdiomas.applicationLocales =
                LocaleList.forLanguageTags(nuevoIdioma)

        } else {

            // Android 8 a Android 12:
            // recrear la actividad para aplicar
            // la nueva configuración de recursos.

            context.buscarActividad()?.recreate()
        }
    }

    // --------------------------------------------------
    // BUSCAR ACTIVIDAD DESDE EL CONTEXTO
    // --------------------------------------------------

    private fun Context.buscarActividad(): Activity? {

        var contexto: Context = this

        while (contexto is ContextWrapper) {

            if (contexto is Activity) {
                return contexto
            }

            contexto = contexto.baseContext
        }

        return null
    }
}
