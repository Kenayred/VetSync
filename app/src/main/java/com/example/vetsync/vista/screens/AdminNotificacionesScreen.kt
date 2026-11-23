package com.example.vetsync.vista.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vetsync.modelo.Notificacion
import com.example.vetsync.modelo.NotificacionRepository
import com.example.vetsync.modelo.SesionUsuario
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminNotificacionesScreen() {

    val colorScheme = MaterialTheme.colorScheme
    val usuarioId = SesionUsuario.idUsuario

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

        val listener = repository.observarNotificacionesAdmin(
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
            .background(colorScheme.background)
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            )
    ) {

        Text(
            text = "Notificaciones",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Mantente al tanto de las solicitudes y citas clínicas.",
            fontSize = 14.sp,
            color = colorScheme.onSurfaceVariant
        )

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
                        selectedContainerColor = colorScheme.primaryContainer,
                        selectedLabelColor = colorScheme.onPrimaryContainer,
                        containerColor = colorScheme.surface,
                        labelColor = colorScheme.onSurfaceVariant
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
                        selectedContainerColor = colorScheme.primaryContainer,
                        selectedLabelColor = colorScheme.onPrimaryContainer,
                        containerColor = colorScheme.surface,
                        labelColor = colorScheme.onSurfaceVariant
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
                            esAdmin = true,
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
                            color = colorScheme.primary,
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
                        color = colorScheme.primary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        when {

            cargando -> {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = colorScheme.primary
                    )
                }
            }

            errorMensaje != null -> {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = errorMensaje
                                ?: "Ocurrió un error.",
                            modifier = Modifier.padding(18.dp),
                            color = colorScheme.onErrorContainer,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            notificacionesFiltradas.isEmpty() -> {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    TarjetaNotificacionesVacia(
                        titulo = if (mostrarSoloNoLeidas) {
                            "Todo al día"
                        } else {
                            "No tienes notificaciones"
                        },
                        mensaje = if (mostrarSoloNoLeidas) {
                            "No tienes avisos pendientes de lectura."
                        } else {
                            "Aquí aparecerán las nuevas solicitudes de citas"
                        }
                    )
                }
            }

            else -> {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
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
                                    esAdmin = true,
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
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            colorScheme.outlineVariant
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
                tint = colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = titulo,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = mensaje,
                fontSize = 14.sp,
                color = colorScheme.onSurfaceVariant,
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
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notificacion.leida) {
                colorScheme.surface
            } else {
                colorScheme.primaryContainer.copy(alpha = 0.45f)
            }
        ),
        border = BorderStroke(
            1.dp,
            colorScheme.outlineVariant
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
                    color = colorScheme.onSurface
                )

                if (!notificacion.leida) {

                    Surface(
                        color = colorScheme.primaryContainer,
                        contentColor = colorScheme.onPrimaryContainer,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "Nueva",
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 4.dp
                            ),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = notificacion.mensaje,
                fontSize = 14.sp,
                color = colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = formatearFecha(notificacion.fechaCreacion),
                fontSize = 12.sp,
                color = colorScheme.onSurfaceVariant
            )

            if (!notificacion.leida) {

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onMarcarLeida
                ) {
                    Text(
                        text = "Marcar como leída",
                        color = colorScheme.primary,
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