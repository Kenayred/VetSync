package com.example.vetsync.notificaciones

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.vetsync.R

object NotificacionSistema {

    const val CANAL_RECORDATORIOS = "vetsync_recordatorios"

    // --------------------------------------------------
    // CREAR CANAL DE NOTIFICACIONES
    // --------------------------------------------------

    fun crearCanal(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val canal = NotificationChannel(
                CANAL_RECORDATORIOS,
                "Recordatorios VetSync",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {

                description =
                    "Avisos y recordatorios sobre las citas veterinarias."

                enableVibration(true)
            }

            val administrador = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            administrador.createNotificationChannel(canal)
        }
    }

    // --------------------------------------------------
    // COMPROBAR PERMISO
    // --------------------------------------------------

    fun tienePermiso(context: Context): Boolean {

        val permisoAndroid = if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
        ) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        val notificacionesHabilitadas =
            NotificationManagerCompat
                .from(context)
                .areNotificationsEnabled()

        val canalHabilitado = if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        ) {
            val administrador = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            administrador.getNotificationChannel(
                CANAL_RECORDATORIOS
            )?.importance != NotificationManager.IMPORTANCE_NONE
        } else {
            true
        }

        return permisoAndroid &&
                notificacionesHabilitadas &&
                canalHabilitado
    }

    // --------------------------------------------------
    // MOSTRAR NOTIFICACIÓN DEL SISTEMA
    // --------------------------------------------------

    fun mostrar(
        context: Context,
        id: Int,
        titulo: String,
        mensaje: String
    ): Boolean {

        if (!tienePermiso(context)) {
            return false
        }

        val notificacion = NotificationCompat.Builder(
            context,
            CANAL_RECORDATORIOS
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(mensaje)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .build()

        return try {

            NotificationManagerCompat
                .from(context)
                .notify(id, notificacion)

            true

        } catch (error: SecurityException) {

            false
        }
    }
}