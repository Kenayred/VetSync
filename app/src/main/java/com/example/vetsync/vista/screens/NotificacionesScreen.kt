package com.example.vetsync.vista.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import com.example.vetsync.modelo.Notificacion
import com.example.vetsync.modelo.NotificacionRepository
import com.example.vetsync.modelo.SesionUsuario
import com.example.vetsync.vista.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.example.vetsync.controlador.CitaControlador
import com.example.vetsync.notificaciones.NotificacionSistema
import com.example.vetsync.notificaciones.ProgramadorRecordatorios

@Composable
fun NotificacionesScreen() {

    val usuarioId = SesionUsuario.idUsuario
    val context = LocalContext.current

    val controladorCitas = remember {
        CitaControlador()
    }

    var permisoSistemaHabilitado by remember(usuarioId) {
        mutableStateOf(
            NotificacionSistema.tienePermiso(context)
        )
    }

    val abrirAjustesNotificaciones =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) {

            permisoSistemaHabilitado =
                NotificacionSistema.tienePermiso(context)

            // Si el cliente habilitó las notificaciones,
            // recuperamos la programación de sus recordatorios.
            if (permisoSistemaHabilitado) {

                controladorCitas.obtenerCitasUsuario(

                    onSuccess = { citas ->

                        ProgramadorRecordatorios.sincronizarRecordatorios(
                            context = context.applicationContext,
                            citas = citas
                        )
                    },

                    onError = { error ->
                        android.util.Log.w(
                            "VetSync",
                            "No se pudieron recuperar los recordatorios: $error"
                        )
                    }
                )
            }
        }

    val repository = remember {
        NotificacionRepository()
    }

    var notificaciones by remember(usuarioId) {
        mutableStateOf<List<Notificacion>>(emptyList())
    }

    var cargando by remember(usuarioId) {
        mutableStateOf(true)
    }

    var errorMensaje by remember(usuarioId) {
        mutableStateOf<String?>(null)
    }

    var mostrarSoloNoLeidas by remember(usuarioId) {
        mutableStateOf(false)
    }

    var marcandoTodas by remember(usuarioId) {
        mutableStateOf(false)
    }

    val cantidadNoLeidas = notificaciones.count {
        !it.leida
    }

    val notificacionesFiltradas = if (mostrarSoloNoLeidas) {
        notificaciones.filter { !it.leida }
    } else {
        notificaciones
    }

    // Escuchamos los cambios de Firestore.
    DisposableEffect(usuarioId) {

        val listener = repository.observarNotificaciones(
            usuarioId = usuarioId,

            onSuccess = { lista ->
                notificaciones = lista
                cargando = false
                errorMensaje = null
            },

            onFailure = { error ->
                cargando = false
                errorMensaje = error.message
                    ?: "No fue posible cargar las notificaciones."
            }
        )

        onDispose {
            listener?.remove()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoCrema)
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            )
    ) {

        Text(
            text = "Notificaciones",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E221F)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Mantente al tanto del cuidado de tus mascotas.",
            fontSize = 14.sp,
            color = GrisTextoSecundario
        )

        if (!permisoSistemaHabilitado) {

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),

                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF4DF)
                ),

                shape = RoundedCornerShape(14.dp),

                border = BorderStroke(
                    1.dp,
                    Color(0xFFF0DFC0)
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {

                    Text(
                        text = "Activa los recordatorios",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF78551C)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Las notificaciones dentro de VetSync " +
                                "seguirán disponibles, pero necesitas " +
                                "habilitar las notificaciones de Android " +
                                "para recibir recordatorios en tu dispositivo.",

                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = Color(0xFF78551C)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {

                            val intent = Intent(
                                Settings.ACTION_APP_NOTIFICATION_SETTINGS
                            ).apply {

                                putExtra(
                                    Settings.EXTRA_APP_PACKAGE,
                                    context.packageName
                                )
                            }

                            abrirAjustesNotificaciones.launch(intent)
                        },

                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = VerdeVetSync
                        )
                    ) {

                        Text("Configurar notificaciones")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (
            !cargando &&
            errorMensaje == null &&
            notificaciones.isNotEmpty()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                FilterChip(
                    selected = !mostrarSoloNoLeidas,
                    onClick = {
                        mostrarSoloNoLeidas = false
                    },
                    label = {
                        Text("Todas")
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = VerdeClaroIcono,
                        selectedLabelColor = VerdeVetSync
                    )
                )

                FilterChip(
                    selected = mostrarSoloNoLeidas,
                    onClick = {
                        mostrarSoloNoLeidas = true
                    },
                    label = {
                        Text("No leídas")
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = VerdeClaroIcono,
                        selectedLabelColor = VerdeVetSync
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                TextButton(
                    enabled = cantidadNoLeidas > 0 && !marcandoTodas,
                    onClick = {

                        marcandoTodas = true

                        repository.marcarTodasComoLeidas(
                            usuarioId = usuarioId,

                            onSuccess = {
                                marcandoTodas = false
                            },

                            onFailure = { error ->
                                marcandoTodas = false
                                errorMensaje = error.message
                                    ?: "No se pudieron marcar las notificaciones."
                            }
                        )
                    }
                ) {

                    if (marcandoTodas) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = VerdeVetSync,
                            strokeWidth = 2.dp
                        )

                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Text(
                        text = if (marcandoTodas) {
                            "Actualizando..."
                        } else {
                            "Marcar todas como leídas"
                        },
                        color = VerdeVetSync,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        when {

            cargando -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = VerdeVetSync
                    )
                }
            }

            errorMensaje != null -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMensaje
                            ?: "Ocurrió un error.",
                        color = Color(0xFFB3261E),
                        fontSize = 14.sp
                    )
                }
            }

            notificacionesFiltradas.isEmpty() -> {

                TarjetaNotificacionesVacia(
                    titulo = if (mostrarSoloNoLeidas) {
                        "Todo al día"
                    } else {
                        "No tienes notificaciones"
                    },
                    mensaje = if (mostrarSoloNoLeidas) {
                        "No tienes avisos pendientes de lectura."
                    } else {
                        "Aquí aparecerán los avisos sobre tus citas " +
                                "y actualizaciones relacionadas con tus mascotas."
                    }
                )
            }

            else -> {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(
                        items = notificacionesFiltradas,
                        key = { it.id }
                    ) { notificacion ->

                        NotificacionItem(
                            notificacion = notificacion,
                            onMarcarLeida = {

                                repository.marcarComoLeida(
                                    usuarioId = usuarioId,
                                    notificacionId = notificacion.id,

                                    onSuccess = {
                                        // El listener actualizará
                                        // automáticamente la pantalla.
                                    },

                                    onFailure = { error ->
                                        errorMensaje = error.message
                                            ?: "No se pudo actualizar la notificación."
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaNotificacionesVacia(
    titulo: String = "No tienes notificaciones",
    mensaje: String = "Aquí aparecerán tus avisos."
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            1.dp,
            Color(0xFFEEECE8)
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notificaciones",
                tint = VerdeVetSync,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = titulo,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E221F)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = mensaje,
                fontSize = 14.sp,
                color = GrisTextoSecundario,
                lineHeight = 21.sp
            )
        }
    }
}

@Composable
private fun NotificacionItem(
    notificacion: Notificacion,
    onMarcarLeida: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notificacion.leida) {
                Color.White
            } else {
                Color(0xFFF0F6EF)
            }
        ),
        border = BorderStroke(
            1.dp,
            Color(0xFFE5E8E5)
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = notificacion.titulo,
                    modifier = Modifier.weight(1f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E221F)
                )

                if (!notificacion.leida) {

                    Surface(
                        color = VerdeClaroIcono,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "Nueva",
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 4.dp
                            ),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = VerdeVetSync
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = notificacion.mensaje,
                fontSize = 14.sp,
                color = GrisTextoSecundario,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = formatearFecha(notificacion.fechaCreacion),
                fontSize = 12.sp,
                color = GrisTextoSecundario
            )

            if (!notificacion.leida) {

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onMarcarLeida
                ) {
                    Text(
                        text = "Marcar como leída",
                        color = VerdeVetSync,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

private fun formatearFecha(fecha: Long): String {

    if (fecha <= 0L) {
        return "Fecha no disponible"
    }

    val formato = SimpleDateFormat(
        "dd MMM yyyy, hh:mm a",
        Locale("es", "SV")
    )

    return formato.format(Date(fecha))
}