package com.example.vetsync.vista

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.vetsync.controlador.CitaControlador
import com.example.vetsync.modelo.Cita
import com.example.vetsync.modelo.EstadoCita
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReprogramarCitaDialog(
    cita: Cita,
    controladorCitas: CitaControlador,
    onDismiss: () -> Unit,
    onReprogramada: () -> Unit
) {
    var mostrarSelectorFecha by remember {
        mutableStateOf(false)
    }

    var horaSeleccionada by remember(cita.id) {
        mutableStateOf<String?>(null)
    }

    var horariosOcupados by remember(cita.id) {
        mutableStateOf<Set<String>>(emptySet())
    }

    var cargandoHorarios by remember {
        mutableStateOf(false)
    }

    var guardando by remember {
        mutableStateOf(false)
    }

    var errorDisponibilidad by remember {
        mutableStateOf<String?>(null)
    }

    var mensajeError by remember {
        mutableStateOf<String?>(null)
    }

    val fechaInicialMillis = remember(cita.id) {
        val fechaInicial = obtenerFechaInicialValida(cita.fecha)

        fechaInicial
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
    }

    val selectorFecha = rememberDatePickerState(
        initialSelectedDateMillis = fechaInicialMillis
    )

    val fechaSeleccionada = selectorFecha.selectedDateMillis?.let {
        Instant.ofEpochMilli(it)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()
    }

    val horariosDelDia = remember(fechaSeleccionada) {
        val fecha = fechaSeleccionada
        val hoy = LocalDate.now()

        if (
            fecha == null ||
            fecha.isBefore(hoy) ||
            fecha.dayOfWeek == DayOfWeek.SUNDAY
        ) {
            emptyList()
        } else {
            val rangoHoras = if (
                fecha.dayOfWeek == DayOfWeek.SATURDAY
            ) {
                9 until 16
            } else {
                9 until 17
            }

            rangoHoras
                .map { hora ->
                    hora.toString().padStart(2, '0') + ":00"
                }
                .filter { hora ->
                    fecha != hoy ||
                            LocalTime.parse(hora).isAfter(LocalTime.now())
                }
        }
    }

    LaunchedEffect(fechaSeleccionada, cita.id) {
        horaSeleccionada = null
        errorDisponibilidad = null
        mensajeError = null
        horariosOcupados = emptySet()

        val fecha = fechaSeleccionada

        if (fecha == null) {
            cargandoHorarios = false
        } else if (
            fecha.isBefore(LocalDate.now()) ||
            fecha.dayOfWeek == DayOfWeek.SUNDAY ||
            horariosDelDia.isEmpty()
        ) {
            cargandoHorarios = false
        } else {
            cargandoHorarios = true

            controladorCitas.obtenerCitasPorFecha(
                fecha = fecha.toString(),
                onSuccess = { citasDelDia ->
                    horariosOcupados = citasDelDia
                        .filter { citaExistente ->
                            citaExistente.id != cita.id &&
                                    (
                                            citaExistente.estado ==
                                                    EstadoCita.PENDIENTE ||
                                                    citaExistente.estado ==
                                                    EstadoCita.CONFIRMADA
                                            )
                        }
                        .map { it.hora }
                        .toSet()

                    cargandoHorarios = false
                },
                onError = { error ->
                    errorDisponibilidad = error
                    cargandoHorarios = false
                }
            )
        }
    }

    AlertDialog(
        onDismissRequest = {
            if (!guardando) {
                onDismiss()
            }
        },
        title = {
            Text("Reprogramar cita")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Mascota: ${cita.nombreMascota}"
                )

                Text(
                    text = "Servicio: ${cita.nombreServicio}"
                )

                Text(
                    text = "Selecciona la nueva fecha y hora."
                )

                OutlinedButton(
                    onClick = {
                        mostrarSelectorFecha = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !guardando
                ) {
                    Text(
                        text = fechaSeleccionada?.toString()
                            ?: "Seleccionar fecha"
                    )
                }

                when {
                    fechaSeleccionada == null -> {
                        Text("Selecciona una fecha.")
                    }

                    fechaSeleccionada.dayOfWeek == DayOfWeek.SUNDAY -> {
                        Text(
                            text = "La clínica permanece cerrada los domingos.",
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    fechaSeleccionada.isBefore(LocalDate.now()) -> {
                        Text(
                            text = "Selecciona una fecha actual o futura.",
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    cargandoHorarios -> {
                        CircularProgressIndicator()
                        Text("Consultando horarios...")
                    }

                    errorDisponibilidad != null -> {
                        Text(
                            text = errorDisponibilidad
                                ?: "No se pudo consultar la disponibilidad.",
                            color = MaterialTheme.colorScheme.error
                        )

                        OutlinedButton(
                            onClick = {
                                cargandoHorarios = true
                                errorDisponibilidad = null

                                controladorCitas.obtenerCitasPorFecha(
                                    fecha = fechaSeleccionada.toString(),
                                    onSuccess = { citasDelDia ->
                                        horariosOcupados = citasDelDia
                                            .filter { citaExistente ->
                                                citaExistente.id != cita.id &&
                                                        (
                                                                citaExistente.estado == EstadoCita.PENDIENTE ||
                                                                        citaExistente.estado == EstadoCita.CONFIRMADA
                                                                )
                                            }
                                            .map { it.hora }
                                            .toSet()

                                        cargandoHorarios = false
                                    },
                                    onError = { error ->
                                        errorDisponibilidad = error
                                        cargandoHorarios = false
                                    }
                                )
                            },
                            enabled = !cargandoHorarios
                        ) {
                            Text("Consultar nuevamente")
                        }
                    }

                    horariosDelDia.isEmpty() -> {
                        Text(
                            "No quedan horarios disponibles para esta fecha."
                        )
                    }

                    else -> {
                        Text(
                            text = "Horarios disponibles",
                            style = MaterialTheme.typography.titleSmall
                        )

                        Row(
                            modifier = Modifier.horizontalScroll(
                                rememberScrollState()
                            ),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            horariosDelDia.forEach { hora ->
                                val esMismoHorarioOriginal =
                                    fechaSeleccionada.toString() == cita.fecha &&
                                            hora == cita.hora

                                val ocupado =
                                    hora in horariosOcupados ||
                                            esMismoHorarioOriginal

                                FilterChip(
                                    selected = horaSeleccionada == hora,
                                    onClick = {
                                        horaSeleccionada = hora
                                        mensajeError = null
                                    },
                                    enabled = !ocupado && !guardando,
                                    label = {
                                        Text(
                                            if (hora in horariosOcupados) {
                                                "$hora · Ocupado"
                                            } else {
                                                hora
                                            }
                                        )
                                    }
                                )
                            }
                        }

                        Text(
                            text = "Los horarios ocupados no se pueden seleccionar.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                mensajeError?.let { mensaje ->
                    Text(
                        text = mensaje,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !guardando &&
                        !cargandoHorarios &&
                        errorDisponibilidad == null &&
                        fechaSeleccionada != null &&
                        horaSeleccionada != null &&
                        horaSeleccionada !in horariosOcupados,
                onClick = {
                    val fecha = fechaSeleccionada
                    val hora = horaSeleccionada

                    if (fecha == null || hora == null) {
                        mensajeError =
                            "Selecciona una fecha y una hora."
                    } else {
                        guardando = true
                        mensajeError = null

                        controladorCitas.reprogramarCita(
                            idCita = cita.id,
                            nuevaFecha = fecha.toString(),
                            nuevaHora = hora,
                            onSuccess = {
                                guardando = false
                                onReprogramada()
                            },
                            onError = { error ->
                                guardando = false
                                mensajeError = error
                            }
                        )
                    }
                }
            ) {
                if (guardando) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .height(20.dp)
                            .padding(end = 8.dp),
                        strokeWidth = 2.dp
                    )
                    Text("Guardando...")
                } else {
                    Text("Guardar cambios")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !guardando
            ) {
                Text("Cancelar")
            }
        }
    )

    if (mostrarSelectorFecha) {
        DatePickerDialog(
            onDismissRequest = {
                mostrarSelectorFecha = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarSelectorFecha = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarSelectorFecha = false
                    }
                ) {
                    Text("Cerrar")
                }
            }
        ) {
            DatePicker(
                state = selectorFecha
            )
        }
    }
}

private fun obtenerFechaInicialValida(
    fechaCita: String
): LocalDate {
    val hoy = LocalDate.now()

    val fechaOriginal = runCatching {
        LocalDate.parse(fechaCita)
    }.getOrNull()

    if (
        fechaOriginal != null &&
        !fechaOriginal.isBefore(hoy) &&
        fechaOriginal.dayOfWeek != DayOfWeek.SUNDAY
    ) {
        return fechaOriginal
    }

    var siguienteFecha = hoy.plusDays(1)

    while (siguienteFecha.dayOfWeek == DayOfWeek.SUNDAY) {
        siguienteFecha = siguienteFecha.plusDays(1)
    }

    return siguienteFecha
}