package com.example.vetsync.vista.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vetsync.controlador.CitaControlador
import com.example.vetsync.controlador.MascotaControlador
import com.example.vetsync.modelo.Cita
import com.example.vetsync.modelo.EstadoCita
import com.example.vetsync.modelo.Mascota
import com.example.vetsync.modelo.Servicio
import com.example.vetsync.modelo.SesionUsuario
import com.example.vetsync.vista.theme.*
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendarCitaScreen(
    mascota: Mascota? = null,
    controladorCitas: CitaControlador = remember {
        CitaControlador()
    },
    controladorMascotas: MascotaControlador = remember {
        MascotaControlador()
    },
    onBackClick: () -> Unit = {},
    onCitaCreada: () -> Unit = {}
) {
    val context = LocalContext.current

    // Datos recuperados de Firestore.
    var mascotas by remember {
        mutableStateOf<List<Mascota>>(emptyList())
    }

    var servicios by remember {
        mutableStateOf<List<Servicio>>(emptyList())
    }

    var cargandoMascotas by remember { mutableStateOf(true) }
    var cargandoServicios by remember { mutableStateOf(true) }
    var guardandoCita by remember { mutableStateOf(false) }

    var errorCarga by remember { mutableStateOf<String?>(null) }
    var errorMensaje by remember { mutableStateOf<String?>(null) }
    var errorDisponibilidad by remember { mutableStateOf<String?>(null) }

    // Selecciones del formulario.
    var mascotaSeleccionadaId by remember(mascota?.id) {
        mutableStateOf(mascota?.id.orEmpty())
    }

    var servicioSeleccionadoId by remember {
        mutableStateOf("")
    }

    var fechaSeleccionada by remember {
        mutableStateOf<LocalDate?>(null)
    }

    var horaSeleccionada by remember {
        mutableStateOf("")
    }

    var motivo by remember { mutableStateOf("") }

    // Menús y calendario.
    var menuMascotasAbierto by remember { mutableStateOf(false) }
    var menuServiciosAbierto by remember { mutableStateOf(false) }
    var mostrarCalendario by remember { mutableStateOf(false) }

    // Disponibilidad para la fecha seleccionada.
    var horariosOcupados by remember {
        mutableStateOf<Set<String>>(emptySet())
    }

    var cargandoHorarios by remember { mutableStateOf(false) }

    val formatoFecha = DateTimeFormatter.ISO_LOCAL_DATE
    val formatoFechaVisible =
        DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {

            override fun isSelectableDate(
                utcTimeMillis: Long
            ): Boolean {
                val fecha = Instant.ofEpochMilli(utcTimeMillis)
                    .atZone(ZoneOffset.UTC)
                    .toLocalDate()

                return !fecha.isBefore(LocalDate.now()) &&
                        fecha.dayOfWeek != DayOfWeek.SUNDAY
            }

            override fun isSelectableYear(year: Int): Boolean {
                return year >= LocalDate.now().year
            }
        }
    )

    // --------------------------------------------------
    // CARGAR MASCOTAS Y SERVICIOS
    // --------------------------------------------------

    LaunchedEffect(SesionUsuario.idUsuario) {
        val idUsuario = SesionUsuario.idUsuario

        cargandoMascotas = true
        cargandoServicios = true
        errorCarga = null

        if (idUsuario.isBlank()) {
            errorCarga = "No hay una sesión activa."
            cargandoMascotas = false
            cargandoServicios = false
            return@LaunchedEffect
        }

        controladorMascotas.obtenerMascotasUsuario(
            idUsuario = idUsuario,

            onSuccess = { resultado ->
                mascotas = resultado

                val mascotaInicial = mascota?.id
                    ?.takeIf { id ->
                        resultado.any { it.id == id }
                    }

                mascotaSeleccionadaId =
                    mascotaInicial
                        ?: resultado.firstOrNull()?.id.orEmpty()

                cargandoMascotas = false
            },

            onError = { error ->
                cargandoMascotas = false
                errorCarga = error
            }
        )

        controladorCitas.obtenerServiciosActivos(
            onSuccess = { resultado ->
                servicios = resultado

                if (resultado.none {
                        it.id == servicioSeleccionadoId
                    }
                ) {
                    servicioSeleccionadoId =
                        resultado.firstOrNull()?.id.orEmpty()
                }

                cargandoServicios = false
            },

            onError = { error ->
                cargandoServicios = false
                errorCarga = error
            }
        )
    }

    // --------------------------------------------------
    // CONSULTAR HORARIOS OCUPADOS
    // --------------------------------------------------

    LaunchedEffect(fechaSeleccionada) {
        val fecha = fechaSeleccionada

        horaSeleccionada = ""
        horariosOcupados = emptySet()
        errorDisponibilidad = null

        if (fecha == null) {
            cargandoHorarios = false
            return@LaunchedEffect
        }

        cargandoHorarios = true

        controladorCitas.obtenerCitasPorFecha(
            fecha = fecha.format(formatoFecha),

            onSuccess = { citas ->
                horariosOcupados = citas
                    .filter {
                        it.estado == EstadoCita.PENDIENTE ||
                                it.estado == EstadoCita.CONFIRMADA
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

    val mascotaSeleccionada = mascotas.find {
        it.id == mascotaSeleccionadaId
    }

    val servicioSeleccionado = servicios.find {
        it.id == servicioSeleccionadoId && it.activo
    }

    // Los horarios representan horas de inicio.
    // Todavía no hemos definido una duración por servicio.
    val horariosDelDia = when (fechaSeleccionada?.dayOfWeek) {
        DayOfWeek.SATURDAY -> (9 until 16).toList()
        DayOfWeek.SUNDAY, null -> emptyList()
        else -> (9 until 17).toList()
    }.map { hora ->
        hora.toString().padStart(2, '0') + ":00"
    }.filter { hora ->
        val horaLocal = LocalTime.parse(hora)

        fechaSeleccionada != LocalDate.now() ||
                horaLocal.isAfter(LocalTime.now())
    }

    val horariosDisponibles = horariosDelDia.filter {
        it !in horariosOcupados
    }

    // --------------------------------------------------
    // INTERFAZ
    // --------------------------------------------------

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoCrema)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Regresar",
                    tint = VerdeVetSync
                )
            }

            Text(
                text = "Agendar cita",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E221F)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Selecciona tu mascota y encuentra un horario disponible.",
            fontSize = 14.sp,
            color = GrisTextoSecundario,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(22.dp))

        if (cargandoMascotas || cargandoServicios) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = VerdeVetSync
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (errorCarga != null) {
            Text(
                text = errorCarga.orEmpty(),
                color = RojoAlerta,
                fontSize = 13.sp
            )
        }

        if (!cargandoMascotas && mascotas.isEmpty()) {
            Text(
                text = "Primero debes registrar una mascota para solicitar una cita.",
                color = GrisTextoSecundario,
                fontSize = 14.sp
            )
        }

        // MASCOTA
        Text(
            text = "Mascota",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF3B3A38)
        )

        Spacer(modifier = Modifier.height(6.dp))

        ExposedDropdownMenuBox(
            expanded = menuMascotasAbierto,
            onExpandedChange = {
                if (!guardandoCita) {
                    menuMascotasAbierto = !menuMascotasAbierto
                }
            }
        ) {
            OutlinedTextField(
                value = mascotaSeleccionada?.let {
                    "${it.nombre} · ${it.especie}"
                }.orEmpty(),
                onValueChange = {},
                readOnly = true,
                enabled = !guardandoCita && mascotas.isNotEmpty(),
                label = { Text("Seleccionar mascota") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = menuMascotasAbierto
                    )
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VerdeVetSync,
                    focusedLabelColor = VerdeVetSync
                )
            )

            ExposedDropdownMenu(
                expanded = menuMascotasAbierto,
                onDismissRequest = {
                    menuMascotasAbierto = false
                }
            ) {
                mascotas.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text("${item.nombre} · ${item.especie} · ${item.raza}")
                        },
                        onClick = {
                            mascotaSeleccionadaId = item.id
                            menuMascotasAbierto = false
                            errorMensaje = null
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // SERVICIO
        Text(
            text = "Servicio veterinario",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF3B3A38)
        )

        Spacer(modifier = Modifier.height(6.dp))

        ExposedDropdownMenuBox(
            expanded = menuServiciosAbierto,
            onExpandedChange = {
                if (!guardandoCita) {
                    menuServiciosAbierto = !menuServiciosAbierto
                }
            }
        ) {
            OutlinedTextField(
                value = servicioSeleccionado?.nombre.orEmpty(),
                onValueChange = {},
                readOnly = true,
                enabled = !guardandoCita && servicios.isNotEmpty(),
                label = { Text("Seleccionar servicio") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = menuServiciosAbierto
                    )
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VerdeVetSync,
                    focusedLabelColor = VerdeVetSync
                )
            )

            ExposedDropdownMenu(
                expanded = menuServiciosAbierto,
                onDismissRequest = {
                    menuServiciosAbierto = false
                }
            ) {
                servicios.filter { it.activo }.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(item.nombre)

                                Text(
                                    text = item.descripcion,
                                    color = GrisTextoSecundario,
                                    fontSize = 12.sp
                                )

                                Text(
                                    text = "$${String.format(Locale.US, "%.2f", item.costo)}",
                                    color = VerdeVetSync,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        onClick = {
                            servicioSeleccionadoId = item.id
                            menuServiciosAbierto = false
                            errorMensaje = null
                        }
                    )
                }
            }
        }

        servicioSeleccionado?.let { servicio ->
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Costo del servicio: $${
                    String.format(Locale.US, "%.2f", servicio.costo)
                }",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = VerdeVetSync
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // FECHA
        Text(
            text = "Fecha de la cita",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF3B3A38)
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedButton(
            onClick = {
                mostrarCalendario = true
                errorMensaje = null
            },
            enabled = !guardandoCita,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Outlined.DateRange,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = fechaSeleccionada?.format(formatoFechaVisible)
                    ?: "Seleccionar fecha"
            )
        }

        if (mostrarCalendario) {
            DatePickerDialog(
                onDismissRequest = {
                    mostrarCalendario = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                fechaSeleccionada = Instant
                                    .ofEpochMilli(millis)
                                    .atZone(ZoneOffset.UTC)
                                    .toLocalDate()
                            }

                            mostrarCalendario = false
                        }
                    ) {
                        Text("Aceptar", color = VerdeVetSync)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            mostrarCalendario = false
                        }
                    ) {
                        Text("Cancelar")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // HORARIOS
        Text(
            text = "Horario disponible",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF3B3A38)
        )

        Spacer(modifier = Modifier.height(8.dp))

        when {
            fechaSeleccionada == null -> {
                Text(
                    text = "Selecciona una fecha para consultar los horarios.",
                    fontSize = 13.sp,
                    color = GrisTextoSecundario
                )
            }

            cargandoHorarios -> {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = VerdeVetSync
                )
            }

            errorDisponibilidad != null -> {
                Text(
                    text = errorDisponibilidad.orEmpty(),
                    color = RojoAlerta,
                    fontSize = 13.sp
                )
            }

            horariosDisponibles.isEmpty() -> {
                Text(
                    text = if (horariosDelDia.isEmpty()) {
                        "No hay horarios disponibles para esta fecha."
                    } else {
                        "Todos los horarios de esta fecha están ocupados."
                    },
                    fontSize = 13.sp,
                    color = GrisTextoSecundario
                )
            }

            else -> {
                horariosDisponibles.chunked(3).forEach { fila ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        fila.forEach { horario ->
                            FilterChip(
                                selected = horaSeleccionada == horario,
                                onClick = {
                                    horaSeleccionada = horario
                                    errorMensaje = null
                                },
                                label = {
                                    Text(horario)
                                },
                                modifier = Modifier.weight(1f),
                                enabled = !guardandoCita
                            )
                        }

                        repeat(3 - fila.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Lunes a viernes: 09:00–17:00 · " +
                            "Sábados: 09:00–16:00 · Domingos: cerrado.",
                    fontSize = 11.sp,
                    color = GrisTextoSecundario,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // MOTIVO
        OutlinedTextField(
            value = motivo,
            onValueChange = {
                if (it.length <= 250) {
                    motivo = it
                    errorMensaje = null
                }
            },
            enabled = !guardandoCita,
            label = { Text("Motivo de consulta (opcional)") },
            placeholder = { Text("Describe brevemente el motivo de la visita") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VerdeVetSync,
                focusedLabelColor = VerdeVetSync
            )
        )

        Text(
            text = "${motivo.length}/250 caracteres",
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End),
            fontSize = 11.sp,
            color = GrisTextoSecundario
        )

        if (errorMensaje != null) {
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = errorMensaje.orEmpty(),
                color = RojoAlerta,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        // CONFIRMAR SOLICITUD
        Button(
            onClick = {
                errorMensaje = null

                when {
                    mascotaSeleccionadaId.isBlank() -> {
                        errorMensaje = "Selecciona una mascota."
                    }

                    servicioSeleccionadoId.isBlank() -> {
                        errorMensaje = "Selecciona un servicio."
                    }

                    fechaSeleccionada == null -> {
                        errorMensaje = "Selecciona una fecha."
                    }

                    horaSeleccionada.isBlank() -> {
                        errorMensaje = "Selecciona un horario disponible."
                    }

                    errorDisponibilidad != null -> {
                        errorMensaje =
                            "No se pudo verificar el horario. " +
                                    "Vuelve a seleccionar la fecha."
                    }

                    horaSeleccionada in horariosOcupados -> {
                        errorMensaje =
                            "Ese horario acaba de ocuparse. Selecciona otro."
                    }

                    else -> {
                        guardandoCita = true

                        controladorCitas.registrarCita(
                            mascotaId = mascotaSeleccionadaId,
                            servicioId = servicioSeleccionadoId,
                            fecha = fechaSeleccionada!!.format(formatoFecha),
                            hora = horaSeleccionada,
                            motivo = motivo.trim(),

                            onSuccess = { cita ->
                                guardandoCita = false

                                android.widget.Toast.makeText(
                                    context,
                                    "Cita solicitada correctamente.",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()

                                onCitaCreada()
                            },

                            onError = { mensaje ->
                                guardandoCita = false
                                errorMensaje = mensaje
                            }
                        )
                    }
                }
            },
            enabled = !guardandoCita &&
                    !cargandoMascotas &&
                    !cargandoServicios &&
                    !cargandoHorarios,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdeVetSync
            )
        ) {
            if (guardandoCita) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Solicitar cita",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(26.dp))
    }
}