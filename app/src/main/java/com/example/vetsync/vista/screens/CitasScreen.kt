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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.vetsync.vista.ReprogramarCitaDialog
import com.example.vetsync.vista.theme.*
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
                    // Si no se pueden cargar las fotos,
                    // las tarjetas mostrarán los avatares de iniciales.
                    mascotas = emptyList()
                }
            )
        } else {
            mascotas = emptyList()
        }
    }

    LaunchedEffect(Unit) {
        cargarCitas()
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoCrema)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            // Encabezado de la pantalla.
            Text(
                text = "VetSync",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeVetSync
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Mis citas",
                fontSize = 29.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF252925)
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Gestiona los servicios programados para tus mascotas.",
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = GrisTextoSecundario
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Filtros de estado.
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
                        selectedContainerColor = VerdeVetSync,
                        selectedLabelColor = Color.White
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
                            selectedContainerColor = VerdeVetSync,
                            selectedLabelColor = Color.White
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
                                color = VerdeVetSync
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Cargando tus citas...",
                                color = GrisTextoSecundario
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
                                color = MaterialTheme.colorScheme.error
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
                                    .background(VerdeClaroIcono),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp),
                                    tint = VerdeVetSync
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
                                color = Color(0xFF252925)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (filtroSeleccionado == null) {
                                    "Agenda una visita para tu mascota."
                                } else {
                                    "Prueba otro filtro para consultar tus citas."
                                },
                                fontSize = 13.sp,
                                color = GrisTextoSecundario
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
                                        color = VerdeVetSync
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
                        contentPadding = PaddingValues(
                            bottom = 86.dp
                        )
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

        // El botón queda por encima de la lista.
        // La navegación inferior sigue siendo responsabilidad de Home.kt.
        Button(
            onClick = onAgendarCitaClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 18.dp, bottom = 10.dp)
                .height(50.dp),
            shape = RoundedCornerShape(17.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdeVetSync
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

    // Diálogo de detalles.
    citaSeleccionada?.let { cita ->
        AlertDialog(
            onDismissRequest = {
                citaSeleccionada = null
            },
            title = {
                Text(
                    text = "Detalles de la cita",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(
                        rememberScrollState()
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = cita.nombreServicio,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    EstadoCitaBadge(cita.estado)

                    DividerCita()

                    Text(
                        text = "Mascota",
                        fontWeight = FontWeight.SemiBold,
                        color = GrisTextoSecundario
                    )

                    Text(cita.nombreMascota)

                    mascotasPorId[cita.mascotaId]?.let { mascota ->
                        if (
                            mascota.especie.isNotBlank() ||
                            mascota.raza.isNotBlank()
                        ) {
                            Text(
                                text = listOf(
                                    mascota.especie,
                                    mascota.raza
                                ).filter { it.isNotBlank() }
                                    .joinToString(" · "),
                                color = GrisTextoSecundario,
                                fontSize = 13.sp
                            )
                        }
                    }

                    DividerCita()

                    Text(
                        text = "Fecha y hora",
                        fontWeight = FontWeight.SemiBold,
                        color = GrisTextoSecundario
                    )

                    Text(formatearFechaCita(cita.fecha))
                    Text("Hora: ${cita.hora}")

                    DividerCita()

                    Text(
                        text = "Veterinario",
                        fontWeight = FontWeight.SemiBold,
                        color = GrisTextoSecundario
                    )

                    Text(cita.veterinario)

                    DividerCita()

                    Text(
                        text = "Costo estimado",
                        fontWeight = FontWeight.SemiBold,
                        color = GrisTextoSecundario
                    )

                    Text(
                        text = formatearCosto(cita.costo),
                        fontWeight = FontWeight.Bold,
                        color = VerdeVetSync
                    )

                    if (cita.motivo.isNotBlank()) {
                        DividerCita()

                        Text(
                            text = "Motivo de consulta",
                            fontWeight = FontWeight.SemiBold,
                            color = GrisTextoSecundario
                        )

                        Text(cita.motivo)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        citaSeleccionada = null
                    }
                ) {
                    Text(
                        "Cerrar",
                        color = VerdeVetSync
                    )
                }
            },
            containerColor = FondoCrema
        )
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
                        if (cancelando) {
                            "Cancelando..."
                        } else {
                            "Sí, cancelar"
                        },
                        color = MaterialTheme.colorScheme.error
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
            containerColor = FondoCrema
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
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
                        color = Color(0xFF252925),
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
                            color = GrisTextoSecundario,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = cita.nombreServicio,
                        fontSize = 13.sp,
                        color = VerdeVetSync,
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
                        color = GrisTextoSecundario
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = formatearFechaCita(cita.fecha),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF252925)
                    )
                }

                Column(
                    modifier = Modifier.weight(0.8f)
                ) {
                    Text(
                        text = "Hora",
                        fontSize = 11.sp,
                        color = GrisTextoSecundario
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = cita.hora,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF252925)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Costo",
                        fontSize = 11.sp,
                        color = GrisTextoSecundario
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = formatearCosto(cita.costo),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (cita.estado == EstadoCita.CANCELADA) {
                            GrisTextoSecundario
                        } else {
                            VerdeVetSync
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
                        color = VerdeVetSync
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
                            color = VerdeVetSync
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
                            color = RojoAlerta
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
                    color = VerdeClaroIcono,
                    shape = CircleShape
                )
        )
    } else {
        Box(
            modifier = Modifier
                .size(66.dp)
                .clip(CircleShape)
                .background(VerdeClaroIcono),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = nombre.firstOrNull()?.uppercase() ?: "?",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeVetSync
            )
        }
    }
}

@Composable
private fun EstadoCitaBadge(
    estado: EstadoCita
) {
    val fondo = when (estado) {
        EstadoCita.PENDIENTE -> BeigePendiente
        EstadoCita.CONFIRMADA -> Color(0xFFD7E7D9)
        EstadoCita.COMPLETADA -> Color(0xFFE5EBE6)
        EstadoCita.CANCELADA -> Color(0xFFFFDEDC)
    }

    val texto = when (estado) {
        EstadoCita.PENDIENTE -> TextoPendiente
        EstadoCita.CONFIRMADA -> Color(0xFF365C3F)
        EstadoCita.COMPLETADA -> Color(0xFF4F6254)
        EstadoCita.CANCELADA -> RojoAlerta
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = fondo
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
    androidx.compose.material3.HorizontalDivider(
        color = Color(0xFFEFEAE5),
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
