package com.example.vetsync.vista.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ViewAgenda
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.vetsync.controlador.CitaControlador
import com.example.vetsync.modelo.Cita
import com.example.vetsync.modelo.EstadoCita
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.example.vetsync.controlador.NotaClinicaControlador
import androidx.compose.foundation.horizontalScroll

@Composable
fun AdminCitasScreen(
    modifier: Modifier = Modifier
) {
    val controlador = remember {
        CitaControlador()
    }

    val controladorNota = remember {
        NotaClinicaControlador()
    }

    var citaParaNota by remember {
        mutableStateOf<Cita?>(null)
    }

    var errorNota by remember {
        mutableStateOf<String?>(null)
    }

    var citas by remember {
        mutableStateOf<List<Cita>>(emptyList())
    }

    var filtroEstadoSeleccionado by remember {
        mutableStateOf("Total")
    }

    var cargando by remember {
        mutableStateOf(true)
    }

    var procesandoId by remember {
        mutableStateOf<String?>(null)
    }

    var errorMensaje by remember {
        mutableStateOf<String?>(null)
    }

    var mensajeExito by remember {
        mutableStateOf<String?>(null)
    }

    fun guardarNotaClinica(
        cita: Cita,
        motivo: String,
        peso: Double?,
        temperatura: Double?,
        diagnostico: String,
        receta: String,
        observaciones: String
    ) {
        procesandoId = cita.id
        errorNota = null
        errorMensaje = null
        mensajeExito = null

        controladorNota.registrarNotaClinica(
            cita = cita,
            motivo = motivo,
            peso = peso,
            temperatura = temperatura,
            diagnostico = diagnostico,
            receta = receta,
            observaciones = observaciones,

            onSuccess = {
                // Actualizamos la cita en la lista del administrador.
                citas = citas.map { actual ->
                    if (actual.id == cita.id) {
                        actual.copy(
                            estado = EstadoCita.COMPLETADA
                        )
                    } else {
                        actual
                    }
                }

                procesandoId = null
                citaParaNota = null
                errorNota = null

                mensajeExito =
                    "La nota clínica de ${cita.nombreMascota} " +
                            "fue registrada correctamente. El cliente recibió " +
                            "una notificación."
            },

            onError = { error ->
                procesandoId = null
                errorNota = error
            }
        )
    }

    fun cargarCitas() {
        cargando = true
        errorMensaje = null

        controlador.obtenerTodasLasCitasAdmin(
            onSuccess = { lista ->
                citas = lista
                cargando = false
            },
            onFailure = { error ->
                cargando = false
                errorMensaje = error.message
                    ?: "No se pudieron cargar las citas."
            }
        )
    }

    fun confirmarCita(cita: Cita) {
        procesandoId = cita.id
        errorMensaje = null
        mensajeExito = null

        controlador.confirmarCitaAdmin(
            idCita = cita.id,

            onSuccess = {
                citas = citas.map { actual ->
                    if (actual.id == cita.id) {
                        actual.copy(
                            estado = EstadoCita.CONFIRMADA
                        )
                    } else {
                        actual
                    }
                }

                procesandoId = null

                mensajeExito =
                    "La cita de ${cita.nombreMascota} " +
                            "fue confirmada y se notificó al cliente."
            },

            onFailure = { error ->
                procesandoId = null

                errorMensaje = error.message
                    ?: "No se pudo confirmar la cita."
            }
        )
    }

    LaunchedEffect(Unit) {
        cargarCitas()
    }

    // --------------------------------------------------
    // FILTRAR CITAS POR ESTADO
    // --------------------------------------------------
    val citasFiltradas = when (filtroEstadoSeleccionado) {

        "Pendientes" -> citas.filter {
            it.estado == EstadoCita.PENDIENTE
        }

        "Confirmadas" -> citas.filter {
            it.estado == EstadoCita.CONFIRMADA
        }

        "Canceladas" -> citas.filter {
            it.estado == EstadoCita.CANCELADA
        }

        "Completadas" -> citas.filter {
            it.estado == EstadoCita.COMPLETADA
        }

        else -> citas
    }

    val formatoFecha = DateTimeFormatter.ofPattern(
        "dd/MM/yyyy"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFDFCF9))
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            )
    ) {

        Text(
            text = "Agenda de Citas",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Fecha: ${LocalDate.now().format(formatoFecha)}",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Resumen dinámico.
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(
                1.dp,
                Color(0xFFE5E5E5)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Resumen de citas",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                FilaResumen(
                    color = Color(0xFFCDE2D4),
                    etiqueta = "Total de citas",
                    valor = citas.size.toString(),
                    isBold = true
                )

                FilaResumen(
                    color = Color(0xFFFDE89C),
                    etiqueta = "Pendientes",
                    valor = citas.count {
                        it.estado == EstadoCita.PENDIENTE
                    }.toString()
                )

                FilaResumen(
                    color = Color(0xFF91C5A9),
                    etiqueta = "Confirmadas",
                    valor = citas.count {
                        it.estado == EstadoCita.CONFIRMADA
                    }.toString()
                )

                FilaResumen(
                    color = Color(0xFFF7D1C9),
                    etiqueta = "Canceladas",
                    valor = citas.count {
                        it.estado == EstadoCita.CANCELADA
                    }.toString()
                )

                FilaResumen(
                    color = Color(0xFFE0E0E0),
                    etiqueta = "Completadas",
                    valor = citas.count {
                        it.estado == EstadoCita.COMPLETADA
                    }.toString()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Citas registradas",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )

        Spacer(modifier = Modifier.height(12.dp))

        FiltrosEstadoCitas(
            citas = citas,
            filtroSeleccionado = filtroEstadoSeleccionado,
            onFiltroSeleccionado = {
                filtroEstadoSeleccionado = it
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (mensajeExito != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE2EFE7)
                )
            ) {
                Text(
                    text = mensajeExito ?: "",
                    modifier = Modifier.padding(14.dp),
                    color = Color(0xFF285C3A),
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        if (errorMensaje != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFE8E6)
                )
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Text(
                        text = errorMensaje ?: "",
                        color = Color(0xFFB3261E),
                        fontSize = 13.sp
                    )

                    TextButton(
                        onClick = { cargarCitas() }
                    ) {
                        Text("Reintentar")
                    }
                }
            }
        }

        when {

            cargando -> {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF4A7D5E)
                    )
                }
            }

            errorMensaje != null -> {
                // El error ya se muestra arriba.
            }

            citas.isEmpty() -> {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Todavía no hay citas registradas.",
                        modifier = Modifier.padding(24.dp),
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }

            citasFiltradas.isEmpty() -> {

                Card(
                    modifier = Modifier.fillMaxWidth(),

                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),

                    shape = RoundedCornerShape(12.dp),

                    border = BorderStroke(
                        1.dp,
                        Color(0xFFE5E5E5)
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),

                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "No hay citas ${filtroEstadoSeleccionado.lowercase()}.",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.DarkGray,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        TextButton(
                            onClick = {
                                filtroEstadoSeleccionado = "Total"
                            }
                        ) {
                            Text(
                                text = "Mostrar todas las citas",
                                color = Color(0xFF4A7D5E)
                            )
                        }
                    }
                }
            }

            else -> {

                citasFiltradas.forEach { cita ->

                    CitaAdminItem(
                        cita = cita,
                        procesando = procesandoId == cita.id,
                        bloqueado = procesandoId != null,
                        onConfirmar = {
                            confirmarCita(cita)
                        },
                        onRegistrarNota = {
                            citaParaNota = cita
                            errorNota = null
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
    citaParaNota?.let { cita ->

        FormularioNotaClinicaDialog(
            cita = cita,
            guardando = procesandoId == cita.id,
            error = errorNota,

            onDismiss = {
                if (procesandoId != cita.id) {
                    citaParaNota = null
                    errorNota = null
                }
            },

            onGuardar = { motivo,
                          peso,
                          temperatura,
                          diagnostico,
                          receta,
                          observaciones ->

                guardarNotaClinica(
                    cita = cita,
                    motivo = motivo,
                    peso = peso,
                    temperatura = temperatura,
                    diagnostico = diagnostico,
                    receta = receta,
                    observaciones = observaciones
                )
            }
        )
    }
}

@Composable
private fun FiltrosEstadoCitas(
    citas: List<Cita>,
    filtroSeleccionado: String,
    onFiltroSeleccionado: (String) -> Unit
) {

    val opciones = listOf(
        "Total" to citas.size,

        "Pendientes" to citas.count {
            it.estado == EstadoCita.PENDIENTE
        },

        "Confirmadas" to citas.count {
            it.estado == EstadoCita.CONFIRMADA
        },

        "Canceladas" to citas.count {
            it.estado == EstadoCita.CANCELADA
        },

        "Completadas" to citas.count {
            it.estado == EstadoCita.COMPLETADA
        }
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = "Filtrar por estado",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),

            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            opciones.forEach { (nombre, cantidad) ->

                FilterChip(
                    selected = filtroSeleccionado == nombre,

                    onClick = {
                        onFiltroSeleccionado(nombre)
                    },

                    label = {
                        Text(
                            text = "$nombre ($cantidad)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    },

                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFE2EFE7),
                        selectedLabelColor = Color(0xFF285C3A),
                        containerColor = Color.White,
                        labelColor = Color.DarkGray
                    )
                )
            }
        }
    }
}

@Composable
private fun FormularioNotaClinicaDialog(
    cita: Cita,
    guardando: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onGuardar: (
        String,
        Double?,
        Double?,
        String,
        String,
        String
    ) -> Unit
) {

    var motivo by remember(cita.id) {
        mutableStateOf(cita.motivo)
    }

    var pesoTexto by remember(cita.id) {
        mutableStateOf("")
    }

    var temperaturaTexto by remember(cita.id) {
        mutableStateOf("")
    }

    var diagnostico by remember(cita.id) {
        mutableStateOf("")
    }

    var receta by remember(cita.id) {
        mutableStateOf("")
    }

    var observaciones by remember(cita.id) {
        mutableStateOf("")
    }

    var errorCampos by remember(cita.id) {
        mutableStateOf<String?>(null)
    }

    val mensajeError = errorCampos ?: error

    AlertDialog(
        onDismissRequest = {
            if (!guardando) {
                onDismiss()
            }
        },

        title = {
            Text(
                text = "Registrar nota clínica",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
        },

        text = {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState()),

                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Text(
                    text = "Paciente: ${cita.nombreMascota}",
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF4A7D5E)
                )

                Text(
                    text = "Servicio: ${cita.nombreServicio}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                Text(
                    text = "Fecha: ${cita.fecha} • Hora: ${cita.hora}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                HorizontalDivider(
                    color = Color(0xFFE5E5E5)
                )

                Text(
                    text = "Información clínica",
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = motivo,
                    onValueChange = {
                        motivo = it
                    },
                    label = {
                        Text("Motivo de consulta (opcional)")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    enabled = !guardando
                )

                OutlinedTextField(
                    value = pesoTexto,
                    onValueChange = {
                        pesoTexto = it
                        errorCampos = null
                    },
                    label = {
                        Text("Peso en kg (opcional)")
                    },
                    placeholder = {
                        Text("Ej. 18.5")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !guardando
                )

                OutlinedTextField(
                    value = temperaturaTexto,
                    onValueChange = {
                        temperaturaTexto = it
                        errorCampos = null
                    },
                    label = {
                        Text("Temperatura en °C (opcional)")
                    },
                    placeholder = {
                        Text("Ej. 38.5")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !guardando
                )

                OutlinedTextField(
                    value = diagnostico,
                    onValueChange = {
                        diagnostico = it
                        errorCampos = null
                    },
                    label = {
                        Text("Diagnóstico *")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    enabled = !guardando
                )

                OutlinedTextField(
                    value = receta,
                    onValueChange = {
                        receta = it
                        errorCampos = null
                    },
                    label = {
                        Text("Receta médica *")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    enabled = !guardando
                )

                OutlinedTextField(
                    value = observaciones,
                    onValueChange = {
                        observaciones = it
                    },
                    label = {
                        Text("Observaciones (opcional)")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    enabled = !guardando
                )

                Text(
                    text = "* Campos obligatorios",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                if (mensajeError != null) {

                    Text(
                        text = mensajeError,
                        color = Color(0xFFB3261E),
                        fontSize = 13.sp
                    )
                }
            }
        },

        confirmButton = {

            Button(
                onClick = {

                    errorCampos = null

                    val pesoNormalizado = pesoTexto
                        .trim()
                        .replace(',', '.')

                    val temperaturaNormalizada = temperaturaTexto
                        .trim()
                        .replace(',', '.')

                    val peso = if (
                        pesoNormalizado.isBlank()
                    ) {
                        null
                    } else {
                        pesoNormalizado.toDoubleOrNull()
                    }

                    val temperatura = if (
                        temperaturaNormalizada.isBlank()
                    ) {
                        null
                    } else {
                        temperaturaNormalizada.toDoubleOrNull()
                    }

                    when {

                        pesoNormalizado.isNotBlank() &&
                                (peso == null || peso <= 0) -> {

                            errorCampos =
                                "Ingresa un peso válido mayor que cero."
                        }

                        temperaturaNormalizada.isNotBlank() &&
                                (temperatura == null || temperatura <= 0) -> {

                            errorCampos =
                                "Ingresa una temperatura válida mayor que cero."
                        }

                        diagnostico.isBlank() -> {

                            errorCampos =
                                "El diagnóstico es obligatorio."
                        }

                        receta.isBlank() -> {

                            errorCampos =
                                "La receta médica es obligatoria."
                        }

                        else -> {

                            onGuardar(
                                motivo,
                                peso,
                                temperatura,
                                diagnostico,
                                receta,
                                observaciones
                            )
                        }
                    }
                },

                enabled = !guardando,

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A7D5E)
                )
            ) {

                if (guardando) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text("Guardando...")

                } else {

                    Text("Guardar y completar")
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
        },

        shape = RoundedCornerShape(18.dp),

        containerColor = Color.White
    )
}

@Composable
private fun CitaAdminItem(
    cita: Cita,
    procesando: Boolean,
    bloqueado: Boolean,
    onConfirmar: () -> Unit,
    onRegistrarNota: () -> Unit
) {
    val estadoColor = when (cita.estado) {
        EstadoCita.PENDIENTE -> Color(0xFFFDF0D5)
        EstadoCita.CONFIRMADA -> Color(0xFFE2EFE7)
        EstadoCita.COMPLETADA -> Color(0xFF91C5A9)
        EstadoCita.CANCELADA -> Color(0xFFF7D1C9)
    }

    val estadoTextoColor = when (cita.estado) {
        EstadoCita.PENDIENTE -> Color(0xFFB57B22)
        EstadoCita.CONFIRMADA -> Color(0xFF285C3A)
        EstadoCita.COMPLETADA -> Color.White
        EstadoCita.CANCELADA -> Color(0xFF9D4035)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            1.dp,
            Color(0xFFE5E5E5)
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = cita.nombreMascota.ifBlank {
                        "Mascota"
                    },
                    modifier = Modifier.weight(1f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )

                Surface(
                    color = estadoColor,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = cita.estado.etiqueta,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 5.dp
                        ),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = estadoTextoColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Servicio: ${cita.nombreServicio}",
                fontSize = 14.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Fecha: ${cita.fecha}  •  Hora: ${cita.hora}",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Costo: ${
                    String.format(
                        Locale.US,
                        "\$%.2f",
                        cita.costo
                    )
                }",
                fontSize = 13.sp,
                color = Color.Gray
            )

            if (cita.motivo.isNotBlank()) {
                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Motivo: ${cita.motivo}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            if (
                cita.estado == EstadoCita.PENDIENTE ||
                cita.estado == EstadoCita.CONFIRMADA
            ) {

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Button(
                    onClick = {
                        if (cita.estado == EstadoCita.PENDIENTE) {
                            onConfirmar()
                        } else {
                            onRegistrarNota()
                        }
                    },

                    enabled = !bloqueado,

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A7D5E)
                    ),

                    shape = RoundedCornerShape(8.dp)

                ) {

                    if (procesando) {

                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )
                    }

                    Text(
                        text = when {
                            procesando -> "Procesando..."
                            cita.estado == EstadoCita.PENDIENTE -> "Confirmar cita"
                            else -> "Registrar nota clínica"
                        },
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CardFiltrosBusqueda() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Chips de filtro
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChipCustom("Todas", true, Modifier.weight(1f))
                FilterChipCustom("Pendientes", false, Modifier.weight(1f))
                FilterChipCustom("Completadas", false, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Buscador y botones de vista
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Buscar paciente...", fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE5E5E5),
                        focusedBorderColor = Color(0xFF4A7D5E)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Toggle de vista (Lista / Calendario)
                Row(
                    modifier = Modifier
                        .background(Color(0xFFF5EFEB), RoundedCornerShape(8.dp))
                        .padding(4.dp)
                ) {
                    Box(modifier = Modifier.background(Color.White, RoundedCornerShape(6.dp)).padding(6.dp)) {
                        Icon(Icons.Outlined.ViewAgenda, contentDescription = "Lista", tint = Color.DarkGray, modifier = Modifier.size(20.dp))
                    }
                    Box(modifier = Modifier.padding(6.dp)) {
                        Icon(Icons.Outlined.CalendarMonth, contentDescription = "Calendario", tint = Color.Gray, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChipCustom(text: String, isSelected: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                color = if (isSelected) Color(0xFFF5EFEB) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = if (isSelected) Color(0xFFD7C9BE) else Color(0xFFE5E5E5),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = if (isSelected) Color.DarkGray else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
fun CardCalendarioMensual() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Octubre 2026", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Row {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Anterior", tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ChevronRight, contentDescription = "Siguiente", tint = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Días de la semana
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                listOf("D", "L", "M", "M", "J", "V", "S").forEach { day ->
                    Text(text = day, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Cuadrícula de fechas (Simulada para la vista estática)
            val fechas = listOf(
                listOf("1" to true, "2" to true, "3" to false, "4" to false, "5" to false, "6" to false, "7" to false),
                listOf("8" to false, "9" to false, "10" to false, "11" to false, "12" to false, "13" to false, "14" to false),
                listOf("15" to false, "16" to false, "17" to false, "18" to false, "19" to false, "20" to false, "21" to false),
                listOf("22" to false, "23" to false, "24" to false, "25" to false, "26" to false, "27" to false, "28" to false),
                listOf("29" to false, "30" to false, "31" to false, "" to false, "" to false, "" to false, "" to false)
            )

            fechas.forEach { semana ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceAround) {
                    semana.forEach { (dia, isTenue) ->
                        val isSelected = dia == "24" // Día actual seleccionado
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color(0xFF4A7D5E) else Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = dia,
                                    fontSize = 14.sp,
                                    color = if (isSelected) Color.White else if (isTenue) Color.LightGray else Color.Black
                                )
                                if (isSelected) {
                                    Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color.White))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CardResumenDia() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Resumen del Día", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))

            FilaResumen(color = Color(0xFFCDE2D4), etiqueta = "Total Citas", valor = "12", isBold = true)
            FilaResumen(color = Color(0xFFFDE89C), etiqueta = "Pendientes", valor = "8")
            FilaResumen(color = Color(0xFF91C5A9), etiqueta = "Completadas", valor = "3")
            FilaResumen(color = Color(0xFFF7D1C9), etiqueta = "Canceladas", valor = "1")
        }
    }
}

@Composable
fun FilaResumen(color: Color, etiqueta: String, valor: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = etiqueta, fontSize = 14.sp, color = Color.DarkGray)
        }
        Text(
            text = valor,
            fontSize = 14.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun CitaItem(
    hora: String,
    ampm: String,
    horaColor: Color = Color.Black,
    nombrePaciente: String,
    estado: String,
    estadoBgColor: Color,
    estadoTextColor: Color,
    nombreDueno: String,
    esAvatarLetra: Boolean = false,
    letraAvatar: String = "",
    etiquetas: List<Pair<Pair<ImageVector, String>, Color>>,
    botones: @Composable () -> Unit
) {
    Column {
        // Cabecera de Hora
        Box(modifier = Modifier.fillMaxWidth().background(Color(0xFFF5EFEB), RoundedCornerShape(8.dp)).padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = hora, fontSize = 20.sp, fontWeight = FontWeight.Medium, color = horaColor)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = ampm, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 3.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tarjeta de Detalle
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    // Avatar
                    if (esAvatarLetra) {
                        Box(
                            modifier = Modifier.size(50.dp).clip(CircleShape).background(Color(0xFFCDE2D4)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = letraAvatar, fontSize = 20.sp, color = Color(0xFF4A7D5E))
                        }
                    } else {
                        Box(modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.LightGray)) // Reemplazar con foto
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Info principal
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = nombrePaciente, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(modifier = Modifier.background(estadoBgColor, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                Text(text = estado, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = estadoTextColor)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Person, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = nombreDueno, fontSize = 13.sp, color = Color.Gray)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Etiquetas (Vacuna, Precio, etc.)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            etiquetas.forEach { (contenido, bgColor) ->
                                val (icono, texto) = contenido
                                Row(
                                    modifier = Modifier.background(bgColor, RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(icono, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.DarkGray)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = texto, fontSize = 11.sp, color = Color.DarkGray)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botones de acción
                botones()
            }
        }
    }
}