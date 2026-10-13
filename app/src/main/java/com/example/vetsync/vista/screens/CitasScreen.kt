package com.example.vetsync.vista.screens

import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vetsync.controlador.CitaControlador
import com.example.vetsync.modelo.Cita
import com.example.vetsync.modelo.EstadoCita
import java.util.Locale

@Composable
fun CitasScreen(
    onAgendarCitaClick: () -> Unit,
    controladorCitas: CitaControlador = remember {
        CitaControlador()
    }
) {
    val context = LocalContext.current

    var citas by remember {
        mutableStateOf<List<Cita>>(emptyList())
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

    var cancelando by remember {
        mutableStateOf(false)
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Mis citas",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Consulta tus próximas visitas y el historial de citas.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onAgendarCitaClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agendar una cita")
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                }
            )

            EstadoCita.values().forEach { estado ->
                FilterChip(
                    selected = filtroSeleccionado == estado,
                    onClick = {
                        filtroSeleccionado = estado
                    },
                    label = {
                        Text(estado.etiqueta)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (cargando) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (errorCarga != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = errorCarga ?: "Ocurrió un error.",
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        cargarCitas()
                    }
                ) {
                    Text("Intentar nuevamente")
                }
            }
        } else if (citasFiltradas.isEmpty()) {
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
                        text = if (filtroSeleccionado == null) {
                            "Todavía no tienes citas registradas."
                        } else {
                            "No hay citas con este estado."
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Tus citas registradas aparecerán aquí.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = citasFiltradas,
                    key = { it.id }
                ) { cita ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = cita.nombreServicio,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Mascota: ${cita.nombreMascota}")
                            Text("Fecha: ${cita.fecha}")
                            Text("Hora: ${cita.hora}")

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Estado: ${cita.estado.etiqueta}",
                                fontWeight = FontWeight.SemiBold,
                                color = when (cita.estado) {
                                    EstadoCita.PENDIENTE ->
                                        MaterialTheme.colorScheme.tertiary

                                    EstadoCita.CONFIRMADA ->
                                        MaterialTheme.colorScheme.primary

                                    EstadoCita.COMPLETADA ->
                                        MaterialTheme.colorScheme.secondary

                                    EstadoCita.CANCELADA ->
                                        MaterialTheme.colorScheme.error
                                }
                            )

                            Text(
                                text = "Costo: ${
                                    String.format(
                                        Locale.US,
                                        "$%.2f",
                                        cita.costo
                                    )
                                }"
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = {
                                    citaSeleccionada = cita
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Ver detalles")
                            }

                            if (cita.estado == EstadoCita.PENDIENTE) {
                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedButton(
                                    onClick = {
                                        citaParaCancelar = cita
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text("Cancelar cita")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

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
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Servicio: ${cita.nombreServicio}")
                    Text("Mascota: ${cita.nombreMascota}")
                    Text("Fecha: ${cita.fecha}")
                    Text("Hora: ${cita.hora}")
                    Text("Veterinario: ${cita.veterinario}")
                    Text(
                        text = "Costo: ${
                            String.format(
                                Locale.US,
                                "$%.2f",
                                cita.costo
                            )
                        }"
                    )
                    Text("Estado: ${cita.estado.etiqueta}")

                    if (cita.motivo.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Motivo de consulta",
                            fontWeight = FontWeight.SemiBold
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
                    Text("Cerrar")
                }
            }
        )
    }

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
                            "para el ${cita.fecha} a las ${cita.hora}? " +
                            "Esta acción cambiará su estado a Cancelada."
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
                        }
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
            }
        )
    }
}