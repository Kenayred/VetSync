package com.example.vetsync.vista.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vetsync.controlador.CitaControlador
import com.example.vetsync.controlador.MascotaControlador
import com.example.vetsync.modelo.Cita
import com.example.vetsync.modelo.EstadoCita
import com.example.vetsync.modelo.ImagenUtils
import com.example.vetsync.modelo.Mascota
import com.example.vetsync.modelo.SesionUsuario
import com.example.vetsync.notificaciones.ProgramadorRecordatorios
import com.example.vetsync.vista.ReprogramarCitaDialog
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val formatoFechaTarjeta = DateTimeFormatter.ofPattern(
    "d MMM yyyy",
    Locale("es", "SV")
)

@Composable
fun CitasScreen(
    onAgendarCitaClick: () -> Unit,
    controladorCitas: CitaControlador = remember {
        CitaControlador()
    },
    controladorMascotas: MascotaControlador = remember {
        MascotaControlador()
    }
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme

    var citas by remember {
        mutableStateOf<List<Cita>>(emptyList())
    }

    var mascotas by remember {
        mutableStateOf<List<Mascota>>(emptyList())
    }

    var cargando by remember {
        mutableStateOf(true)
    }

    var errorCarga by remember {
        mutableStateOf<String?>(null)
    }

    var filtroSeleccionado by remember {
        mutableStateOf<EstadoCita?>(null)
    }

    var citaSeleccionada by remember {
        mutableStateOf<Cita?>(null)
    }

    var citaParaCancelar by remember {
        mutableStateOf<Cita?>(null)
    }

    var citaParaReprogramar by remember {
        mutableStateOf<Cita?>(null)
    }

    var cancelando by remember {
        mutableStateOf(false)
    }

    val mascotasPorId = remember(mascotas) {
        mascotas.associateBy { it.id }
    }

    fun cargarCitas() {
        cargando = true
        errorCarga = null

        controladorCitas.obtenerCitasUsuario(
            onSuccess = { lista ->
                citas = lista

                ProgramadorRecordatorios.sincronizarRecordatorios(
                    context = context.applicationContext,
                    citas = lista
                )

                cargando = false
            },
            onError = { mensaje ->
                errorCarga = mensaje
                cargando = false
            }
        )
    }

    LaunchedEffect(SesionUsuario.idUsuario) {
        val idUsuario = SesionUsuario.idUsuario

        if (idUsuario.isNotBlank()) {
            controladorMascotas.obtenerMascotasUsuario(
                idUsuario = idUsuario,
                onSuccess = { resultado ->
                    mascotas = resultado
                },
                onError = {
                    mascotas = emptyList()
                }
            )
        } else {
            mascotas = emptyList()
        }
    }

    // Observar las citas en tiempo real y sincronizar recordatorios.
    DisposableEffect(SesionUsuario.idUsuario) {
        val idUsuario = SesionUsuario.idUsuario

        if (idUsuario.isBlank()) {
            citas = emptyList()
            errorCarga = null
            cargando = false

            onDispose { }
        } else {
            cargando = true
            errorCarga = null

            val listener = controladorCitas.observarCitasUsuario(
                onSuccess = { lista ->
                    citas = lista
                    errorCarga = null
                    cargando = false

                    ProgramadorRecordatorios.sincronizarRecordatorios(
                        context = context.applicationContext,
                        citas = lista
                    )
                },
                onError = { mensaje ->
                    errorCarga = mensaje
                    cargando = false
                }
            )

            onDispose {
                listener?.remove()
            }
        }
    }

    val citasFiltradas = remember(citas, filtroSeleccionado) {
        if (filtroSeleccionado == null) {
            citas
        } else {
            citas.filter {
                it.estado == filtroSeleccionado
            }
        }
    }

    if (citaSeleccionada != null) {
        val citaDetalle = citaSeleccionada!!

        DetalleCitaScreen(
            cita = citaDetalle,
            mascota = mascotasPorId[citaDetalle.mascotaId],
            onVolver = {
                citaSeleccionada = null
            },
            onReprogramar = {
                citaSeleccionada = null
                citaParaReprogramar = citaDetalle
            },
            onCancelar = {
                citaSeleccionada = null
                citaParaCancelar = citaDetalle
            }
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Mis citas",
                    fontSize = 29.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Gestiona los servicios programados para tus mascotas.",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = filtroSeleccionado == null,
                        onClick = {
                            filtroSeleccionado = null
                        },
                        label = {
                            Text("Todas")
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colorScheme.primaryContainer,
                            selectedLabelColor = colorScheme.onPrimaryContainer,
                            labelColor = colorScheme.onSurfaceVariant
                        )
                    )

                    EstadoCita.values().forEach { estado ->
                        FilterChip(
                            selected = filtroSeleccionado == estado,
                            onClick = {
                                filtroSeleccionado = estado
                            },
                            label = {
                                Text(estado.etiqueta)
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = colorScheme.primaryContainer,
                                selectedLabelColor = colorScheme.onPrimaryContainer,
                                labelColor = colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                when {
                    cargando -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    color = colorScheme.primary
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Cargando tus citas...",
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    errorCarga != null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = errorCarga.orEmpty(),
                                    color = colorScheme.error
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedButton(
                                    onClick = {
                                        cargarCitas()
                                    }
                                ) {
                                    Text("Intentar nuevamente")
                                }
                            }
                        }
                    }

                    citasFiltradas.isEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(32.dp),
                                        tint = colorScheme.onPrimaryContainer
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = if (filtroSeleccionado == null) {
                                        "Todavía no tienes citas"
                                    } else {
                                        "No hay citas con este estado"
                                    },
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.onBackground
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = if (filtroSeleccionado == null) {
                                        "Agenda una visita para tu mascota."
                                    } else {
                                        "Prueba otro filtro para consultar tus citas."
                                    },
                                    fontSize = 13.sp,
                                    color = colorScheme.onSurfaceVariant
                                )

                                if (filtroSeleccionado != null) {
                                    Spacer(modifier = Modifier.height(10.dp))

                                    TextButton(
                                        onClick = {
                                            filtroSeleccionado = null
                                        }
                                    ) {
                                        Text(
                                            "Ver todas las citas",
                                            color = colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 86.dp)
                        ) {
                            items(
                                items = citasFiltradas,
                                key = { it.id }
                            ) { cita ->
                                TarjetaCitaCliente(
                                    cita = cita,
                                    mascota = mascotasPorId[cita.mascotaId],
                                    onVerDetalles = {
                                        citaSeleccionada = cita
                                    },
                                    onReprogramar = {
                                        citaParaReprogramar = cita
                                    },
                                    onCancelar = {
                                        citaParaCancelar = cita
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Button(
                onClick = onAgendarCitaClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 18.dp, bottom = 10.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(17.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 5.dp
                ),
                contentPadding = PaddingValues(
                    horizontal = 18.dp,
                    vertical = 8.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "Nueva cita",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }

    // Confirmación de cancelación.
    citaParaCancelar?.let { cita ->
        AlertDialog(
            onDismissRequest = {
                if (!cancelando) {
                    citaParaCancelar = null
                }
            },
            title = {
                Text("Cancelar cita")
            },
            text = {
                Text(
                    "¿Deseas cancelar la cita de ${cita.nombreMascota} " +
                            "para el ${formatearFechaCita(cita.fecha)} " +
                            "a las ${cita.hora}?"
                )
            },
            confirmButton = {
                TextButton(
                    enabled = !cancelando,
                    onClick = {
                        cancelando = true

                        controladorCitas.cancelarCita(
                            idCita = cita.id,
                            onSuccess = {
                                cancelando = false
                                citaParaCancelar = null

                                Toast.makeText(
                                    context,
                                    "La cita se canceló correctamente.",
                                    Toast.LENGTH_SHORT
                                ).show()

                                cargarCitas()
                            },
                            onError = { mensaje ->
                                cancelando = false

                                Toast.makeText(
                                    context,
                                    mensaje,
                                    Toast.LENGTH_LONG
                                ).show()

                                cargarCitas()
                            }
                        )
                    }
                ) {
                    Text(
                        text = if (cancelando) {
                            "Cancelando..."
                        } else {
                            "Sí, cancelar"
                        },
                        color = colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !cancelando,
                    onClick = {
                        citaParaCancelar = null
                    }
                ) {
                    Text("Conservar cita")
                }
            },
            containerColor = colorScheme.surface
        )
    }

    // Reprogramación de cita pendiente.
    citaParaReprogramar?.let { cita ->
        ReprogramarCitaDialog(
            cita = cita,
            controladorCitas = controladorCitas,
            onDismiss = {
                citaParaReprogramar = null
            },
            onReprogramada = {
                citaParaReprogramar = null

                Toast.makeText(
                    context,
                    "La cita se reprogramó correctamente.",
                    Toast.LENGTH_SHORT
                ).show()

                cargarCitas()
            }
        )
    }
}

@Composable
private fun TarjetaCitaCliente(
    cita: Cita,
    mascota: Mascota?,
    onVerDetalles: () -> Unit,
    onReprogramar: () -> Unit,
    onCancelar: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(13.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FotoMascotaCita(
                    mascota = mascota,
                    nombre = cita.nombreMascota
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = cita.nombreMascota,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    val descripcionMascota = mascota?.let {
                        listOf(it.especie, it.raza)
                            .filter { dato -> dato.isNotBlank() }
                            .joinToString(" · ")
                    }.orEmpty()

                    if (descripcionMascota.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = descripcionMascota,
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = cita.nombreServicio,
                        fontSize = 13.sp,
                        color = colorScheme.primary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                EstadoCitaBadge(cita.estado)
            }

            Spacer(modifier = Modifier.height(13.dp))

            DividerCita()

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Fecha",
                        fontSize = 11.sp,
                        color = colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = formatearFechaCita(cita.fecha),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.onSurface
                    )
                }

                Column(
                    modifier = Modifier.weight(0.8f)
                ) {
                    Text(
                        text = "Hora",
                        fontSize = 11.sp,
                        color = colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = cita.hora,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.onSurface
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Costo",
                        fontSize = 11.sp,
                        color = colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = formatearCosto(cita.costo),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (cita.estado == EstadoCita.CANCELADA) {
                            colorScheme.onSurfaceVariant
                        } else {
                            colorScheme.primary
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onVerDetalles,
                    contentPadding = PaddingValues(
                        horizontal = 9.dp,
                        vertical = 4.dp
                    )
                ) {
                    Text(
                        text = "Ver detalles",
                        fontSize = 12.sp,
                        color = colorScheme.primary
                    )
                }

                if (cita.estado == EstadoCita.PENDIENTE) {
                    TextButton(
                        onClick = onReprogramar,
                        contentPadding = PaddingValues(
                            horizontal = 9.dp,
                            vertical = 4.dp
                        )
                    ) {
                        Text(
                            text = "Reprogramar",
                            fontSize = 12.sp,
                            color = colorScheme.primary
                        )
                    }

                    TextButton(
                        onClick = onCancelar,
                        contentPadding = PaddingValues(
                            horizontal = 9.dp,
                            vertical = 4.dp
                        )
                    ) {
                        Text(
                            text = "Cancelar",
                            fontSize = 12.sp,
                            color = colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FotoMascotaCita(
    mascota: Mascota?,
    nombre: String
) {
    val colorScheme = MaterialTheme.colorScheme

    val bitmap = remember(mascota?.fotoUrl) {
        mascota?.fotoUrl
            ?.takeIf { it.isNotBlank() }
            ?.let { foto ->
                runCatching {
                    ImagenUtils.base64ABitmap(foto)
                }.getOrNull()
            }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = "Foto de $nombre",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(66.dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = colorScheme.primaryContainer,
                    shape = CircleShape
                )
        )
    } else {
        Box(
            modifier = Modifier
                .size(66.dp)
                .clip(CircleShape)
                .background(colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = nombre.firstOrNull()?.uppercase() ?: "?",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun EstadoCitaBadge(
    estado: EstadoCita
) {
    val colorScheme = MaterialTheme.colorScheme

    val fondo = when (estado) {
        EstadoCita.PENDIENTE -> colorScheme.tertiaryContainer
        EstadoCita.CONFIRMADA -> colorScheme.secondaryContainer
        EstadoCita.COMPLETADA -> colorScheme.surfaceVariant
        EstadoCita.CANCELADA -> colorScheme.errorContainer
    }

    val texto = when (estado) {
        EstadoCita.PENDIENTE -> colorScheme.onTertiaryContainer
        EstadoCita.CONFIRMADA -> colorScheme.onSecondaryContainer
        EstadoCita.COMPLETADA -> colorScheme.onSurfaceVariant
        EstadoCita.CANCELADA -> colorScheme.onErrorContainer
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = fondo,
        contentColor = texto
    ) {
        Text(
            text = estado.etiqueta,
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 5.dp
            ),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = texto,
            maxLines = 1
        )
    }
}

@Composable
private fun DividerCita() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant,
        thickness = 1.dp
    )
}

private fun formatearFechaCita(
    fecha: String
): String {
    return runCatching {
        LocalDate.parse(fecha).format(formatoFechaTarjeta)
    }.getOrDefault(fecha)
}

private fun formatearCosto(
    costo: Double
): String {
    return "$" + String.format(Locale.US, "%.2f", costo)
}

@Composable
private fun DetalleCitaScreen(
    cita: Cita,
    mascota: Mascota?,
    onVolver: () -> Unit,
    onReprogramar: () -> Unit,
    onCancelar: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onVolver,
                contentPadding = PaddingValues(
                    horizontal = 4.dp,
                    vertical = 8.dp
                )
            ) {
                Text(
                    text = "←",
                    fontSize = 25.sp,
                    color = colorScheme.primary
                )
            }

            Text(
                text = "Detalle de cita",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = cita.nombreServicio,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Ref. #${cita.id.takeLast(8).uppercase(Locale.ROOT)}",
                fontSize = 12.sp,
                color = colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(9.dp))

            EstadoCitaBadge(cita.estado)

            Spacer(modifier = Modifier.height(18.dp))

            // Información del paciente.
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    colorScheme.outlineVariant
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Paciente",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(13.dp)
                    ) {
                        FotoMascotaCita(
                            mascota = mascota,
                            nombre = cita.nombreMascota
                        )

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = cita.nombreMascota,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            val descripcionMascota = mascota?.let {
                                listOf(it.especie, it.raza)
                                    .filter { dato -> dato.isNotBlank() }
                                    .joinToString(" · ")
                            }.orEmpty()

                            Text(
                                text = descripcionMascota.ifBlank {
                                    "Mascota registrada"
                                },
                                fontSize = 13.sp,
                                color = colorScheme.onSurfaceVariant
                            )

                            if (mascota?.edad?.isNotBlank() == true) {
                                Spacer(modifier = Modifier.height(3.dp))

                                Text(
                                    text = "Edad: ${mascota.edad}",
                                    fontSize = 12.sp,
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Información del veterinario.
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    colorScheme.outlineVariant
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "V",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onPrimaryContainer
                        )
                    }

                    Column {
                        Text(
                            text = "Veterinario",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = cita.veterinario,
                            fontSize = 13.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Fecha, hora y motivo.
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    colorScheme.outlineVariant
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Fecha y hora",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Fecha",
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = formatearFechaCita(cita.fecha),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colorScheme.onSurface
                            )
                        }

                        Column(
                            modifier = Modifier.weight(0.8f)
                        ) {
                            Text(
                                text = "Hora",
                                fontSize = 12.sp,
                                color = colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = cita.hora,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    DividerCita()

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Motivo de visita",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = cita.motivo.ifBlank {
                            "No se indicó un motivo de consulta."
                        },
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Desglose del costo guardado en la cita.
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    colorScheme.outlineVariant
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Desglose estimado",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = cita.nombreServicio,
                            modifier = Modifier.weight(1f),
                            fontSize = 13.sp,
                            color = colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = formatearCosto(cita.costo),
                            fontSize = 13.sp,
                            color = colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    DividerCita()

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total estimado",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )

                        Text(
                            text = formatearCosto(cita.costo),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "El costo corresponde a la tarifa guardada " +
                                "para el servicio solicitado.",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Acciones disponibles únicamente para citas pendientes.
        if (cita.estado == EstadoCita.PENDIENTE) {
            Button(
                onClick = onCancelar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.errorContainer,
                    contentColor = colorScheme.onErrorContainer
                )
            ) {
                Text(
                    text = "⊗  Cancelar cita",
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(9.dp))

            Button(
                onClick = onReprogramar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "Reprogramar cita",
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Volver a mis citas")
            }
        }
    }
}