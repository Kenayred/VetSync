package com.example.vetsync.vista.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import com.example.vetsync.modelo.ImagenUtils

private val etapasReserva = listOf(
    "Mascota",
    "Servicio",
    "Fecha",
    "Horario",
    "Confirmar"
)

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
    val colorScheme = MaterialTheme.colorScheme

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

    var intentoCarga by remember { mutableStateOf(0) }
    var intentoDisponibilidad by remember { mutableStateOf(0) }

    var pasoActual by remember { mutableStateOf(0) }

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

    var mostrarCalendario by remember { mutableStateOf(false) }

    var horariosOcupados by remember {
        mutableStateOf<Set<String>>(emptySet())
    }

    var cargandoHorarios by remember { mutableStateOf(false) }

    var citaCreada by remember {
        mutableStateOf<Cita?>(null)
    }

    val formatoFecha = DateTimeFormatter.ISO_LOCAL_DATE

    val formatoFechaVisible = DateTimeFormatter.ofPattern(
        "EEEE, d 'de' MMMM 'de' yyyy",
        Locale("es", "SV")
    )

    val mascotaSeleccionada = mascotas.find {
        it.id == mascotaSeleccionadaId
    }

    val servicioSeleccionado = servicios.find {
        it.id == servicioSeleccionadoId && it.activo
    }

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

    // Cargar las mascotas del usuario y los servicios desde Firestore.
    LaunchedEffect(SesionUsuario.idUsuario, intentoCarga) {
        val idUsuario = SesionUsuario.idUsuario

        errorCarga = null
        cargandoMascotas = true
        cargandoServicios = true

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

                val mascotaInicial = mascota?.id?.takeIf { id ->
                    resultado.any { it.id == id }
                }

                mascotaSeleccionadaId = mascotaInicial
                    ?: resultado.firstOrNull()?.id.orEmpty()

                cargandoMascotas = false
            },
            onError = { error ->
                errorCarga = error
                cargandoMascotas = false
            }
        )

        controladorCitas.obtenerServiciosActivos(
            onSuccess = { resultado ->
                servicios = resultado

                // No se selecciona automáticamente el servicio.
                if (resultado.none {
                        it.id == servicioSeleccionadoId
                    }
                ) {
                    servicioSeleccionadoId = ""
                }

                cargandoServicios = false
            },
            onError = { error ->
                errorCarga = error
                cargandoServicios = false
            }
        )
    }

    // Consultar nuevamente la disponibilidad cuando cambia la fecha.
    LaunchedEffect(fechaSeleccionada, intentoDisponibilidad) {
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
                if (fechaSeleccionada == fecha) {
                    horariosOcupados = citas
                        .filter {
                            it.estado == EstadoCita.PENDIENTE ||
                                    it.estado == EstadoCita.CONFIRMADA
                        }
                        .map { it.hora }
                        .toSet()

                    cargandoHorarios = false
                }
            },
            onError = { error ->
                if (fechaSeleccionada == fecha) {
                    errorDisponibilidad = error
                    cargandoHorarios = false
                }
            }
        )
    }

    // Horarios de inicio permitidos por la clínica.
    val horariosDelDia = when (fechaSeleccionada?.dayOfWeek) {
        DayOfWeek.SATURDAY -> (9 until 16).toList()
        DayOfWeek.SUNDAY, null -> emptyList()
        else -> (9 until 17).toList()
    }.map { hora ->
        hora.toString().padStart(2, '0') + ":00"
    }.filter { hora ->
        fechaSeleccionada != LocalDate.now() ||
                LocalTime.parse(hora).isAfter(LocalTime.now())
    }

    val horariosDisponibles = horariosDelDia.filter {
        it !in horariosOcupados
    }

    fun continuarPaso() {
        errorMensaje = null

        when (pasoActual) {
            0 -> {
                if (mascotaSeleccionada == null) {
                    errorMensaje = "Selecciona una mascota."
                } else {
                    pasoActual = 1
                }
            }

            1 -> {
                if (servicioSeleccionado == null) {
                    errorMensaje = "Selecciona un servicio."
                } else {
                    pasoActual = 2
                }
            }

            2 -> {
                if (fechaSeleccionada == null) {
                    errorMensaje = "Selecciona una fecha."
                } else {
                    pasoActual = 3
                }
            }

            3 -> {
                when {
                    fechaSeleccionada == null -> {
                        errorMensaje = "Selecciona una fecha."
                    }

                    errorDisponibilidad != null -> {
                        errorMensaje =
                            "No se pudo verificar la disponibilidad. " +
                                    "Vuelve a consultar los horarios."
                    }

                    horaSeleccionada.isBlank() -> {
                        errorMensaje = "Selecciona un horario disponible."
                    }

                    horaSeleccionada in horariosOcupados -> {
                        errorMensaje =
                            "Ese horario ya no está disponible."
                    }

                    else -> {
                        pasoActual = 4
                    }
                }
            }

            4 -> {
                val mascotaActual = mascotaSeleccionada
                val servicioActual = servicioSeleccionado
                val fechaActual = fechaSeleccionada
                val horaActual = horaSeleccionada

                when {
                    mascotaActual == null -> {
                        errorMensaje = "Selecciona una mascota."
                    }

                    servicioActual == null -> {
                        errorMensaje = "Selecciona un servicio."
                    }

                    fechaActual == null -> {
                        errorMensaje = "Selecciona una fecha."
                    }

                    horaActual.isBlank() -> {
                        errorMensaje = "Selecciona un horario."
                    }

                    errorDisponibilidad != null -> {
                        errorMensaje =
                            "No se puede confirmar sin verificar el horario."
                    }

                    horaActual in horariosOcupados -> {
                        errorMensaje =
                            "Ese horario acaba de ocuparse. Selecciona otro."
                    }

                    else -> {
                        guardandoCita = true

                        controladorCitas.registrarCita(
                            mascotaId = mascotaActual.id,
                            servicioId = servicioActual.id,
                            fecha = fechaActual.format(formatoFecha),
                            hora = horaActual,
                            motivo = motivo.trim(),
                            onSuccess = { resultado ->
                                citaCreada = resultado
                                guardandoCita = false
                            },
                            onError = { error ->
                                errorMensaje = error
                                guardandoCita = false
                            }
                        )
                    }
                }
            }
        }
    }

    val citaConfirmadaVisualmente = citaCreada

    if (citaConfirmadaVisualmente != null) {
        PantallaCitaSolicitada(
            cita = citaConfirmadaVisualmente,
            onVerCitas = onCitaCreada,
            onVolver = onBackClick
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .background(colorScheme.background)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Encabezado.
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = {
                        if (guardandoCita) return@IconButton

                        if (pasoActual > 0) {
                            pasoActual--
                            errorMensaje = null
                        } else {
                            onBackClick()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar",
                        tint = colorScheme.primary
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Nueva cita",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onBackground
                    )

                    Text(
                        text = "Cuida la salud de tu mascota",
                        fontSize = 12.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Indicador de los cinco pasos.
            IndicadorEtapasReserva(pasoActual)

            Spacer(modifier = Modifier.height(20.dp))

            if (cargandoMascotas || cargandoServicios) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = colorScheme.primary
                )
            }

            if (errorCarga != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surface
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = errorCarga.orEmpty(),
                            color = colorScheme.error
                        )

                        TextButton(
                            onClick = {
                                intentoCarga++
                            }
                        ) {
                            Text("Intentar nuevamente")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Contenido del paso actual.
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                when (pasoActual) {
                    0 -> {
                        Text(
                            text = "Seleccionar mascota",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "¿Para quién es la cita de hoy?",
                            fontSize = 14.sp,
                            color = colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (!cargandoMascotas && mascotas.isEmpty()) {
                            MensajePaso(
                                texto = "Todavía no tienes mascotas registradas. " +
                                        "Registra una mascota antes de solicitar una cita."
                            )
                        }

                        mascotas.forEach { item ->
                            val seleccionada =
                                item.id == mascotaSeleccionadaId

                            val bitmapMascota = remember(item.fotoUrl) {
                                ImagenUtils.base64ABitmap(item.fotoUrl)
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp)
                                    .clickable(enabled = !guardandoCita) {
                                        mascotaSeleccionadaId = item.id
                                        errorMensaje = null
                                    },
                                shape = RoundedCornerShape(15.dp),
                                border = BorderStroke(
                                    width = if (seleccionada) 1.5.dp else 1.dp,
                                    color = if (seleccionada) {
                                        colorScheme.primary
                                    } else {
                                        colorScheme.outlineVariant
                                    }
                                ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (seleccionada) {
                                        colorScheme.primaryContainer
                                    } else {
                                        colorScheme.surface
                                    }
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(colorScheme.surfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (bitmapMascota != null) {
                                            Image(
                                                bitmap = bitmapMascota,
                                                contentDescription = "Foto de ${item.nombre}",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        } else {
                                            Text(
                                                text = item.nombre
                                                    .firstOrNull()
                                                    ?.uppercase()
                                                    ?: "?",
                                                color = colorScheme.onSurfaceVariant,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = item.nombre,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = colorScheme.onSurface
                                        )

                                        Spacer(
                                            modifier = Modifier.height(3.dp)
                                        )

                                        Text(
                                            text = "${item.especie} · ${item.raza}",
                                            fontSize = 13.sp,
                                            color = colorScheme.onSurfaceVariant
                                        )

                                        if (item.edad.isNotBlank()) {
                                            Text(
                                                text = "Edad: ${item.edad}",
                                                fontSize = 12.sp,
                                                color = colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    RadioButton(
                                        selected = seleccionada,
                                        onClick = {
                                            mascotaSeleccionadaId = item.id
                                            errorMensaje = null
                                        },
                                        enabled = !guardandoCita,
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = colorScheme.primary
                                        )
                                    )
                                }
                            }
                        }
                    }

                    1 -> {
                        Text(
                            text = "Seleccionar servicio",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Elige el motivo de la visita de tu mascota.",
                            fontSize = 14.sp,
                            color = colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        servicios.filter { it.activo }.forEach { item ->
                            val seleccionada =
                                item.id == servicioSeleccionadoId

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp)
                                    .clickable(enabled = !guardandoCita) {
                                        servicioSeleccionadoId = item.id
                                        errorMensaje = null
                                    },
                                shape = RoundedCornerShape(15.dp),
                                border = BorderStroke(
                                    width = if (seleccionada) 1.5.dp else 1.dp,
                                    color = if (seleccionada) {
                                        colorScheme.primary
                                    } else {
                                        colorScheme.outlineVariant
                                    }
                                ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (seleccionada) {
                                        colorScheme.primaryContainer
                                    } else {
                                        colorScheme.surface
                                    }
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
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.DateRange,
                                            contentDescription = null,
                                            tint = colorScheme.onPrimaryContainer
                                        )
                                    }

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = item.nombre,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = colorScheme.onSurface
                                        )

                                        if (item.descripcion.isNotBlank()) {
                                            Spacer(
                                                modifier = Modifier.height(3.dp)
                                            )

                                            Text(
                                                text = item.descripcion,
                                                fontSize = 12.sp,
                                                color = colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Text(
                                        text = formatearCosto(item.costo),
                                        fontWeight = FontWeight.Bold,
                                        color = colorScheme.primary,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        if (!cargandoServicios && servicios.none { it.activo }) {
                            MensajePaso(
                                texto = "No hay servicios disponibles en este momento."
                            )
                        }

                        servicioSeleccionado?.let { seleccionado ->
                            Spacer(modifier = Modifier.height(6.dp))

                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = colorScheme.primaryContainer
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Costo del servicio",
                                        modifier = Modifier.weight(1f),
                                        color = colorScheme.onPrimaryContainer
                                    )

                                    Text(
                                        text = formatearCosto(seleccionado.costo),
                                        fontWeight = FontWeight.Bold,
                                        color = colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }

                    2 -> {
                        Text(
                            text = "Seleccionar fecha",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = mascotaSeleccionada?.let {
                                "Elige un día para la consulta de ${it.nombre}."
                            } ?: "Elige el día de la consulta.",
                            fontSize = 14.sp,
                            color = colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp)
                            ) {
                                Text(
                                    text = "Fecha de la cita",
                                    fontWeight = FontWeight.SemiBold,
                                    color = colorScheme.onSurface
                                )

                                Spacer(
                                    modifier = Modifier.height(10.dp)
                                )

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

                                    Spacer(
                                        modifier = Modifier.width(8.dp)
                                    )

                                    Text(
                                        text = fechaSeleccionada?.format(
                                            formatoFechaVisible
                                        ) ?: "Elegir fecha en el calendario"
                                    )
                                }

                                Spacer(
                                    modifier = Modifier.height(12.dp)
                                )

                                Text(
                                    text = "Lunes a viernes: 09:00–17:00. " +
                                            "Sábados: 09:00–16:00. " +
                                            "Domingos: cerrado.",
                                    fontSize = 12.sp,
                                    color = colorScheme.onSurfaceVariant,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    3 -> {
                        Text(
                            text = "Seleccionar horario",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = fechaSeleccionada?.format(
                                formatoFechaVisible
                            ) ?: "Selecciona la fecha de tu consulta.",
                            fontSize = 14.sp,
                            color = colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        when {
                            fechaSeleccionada == null -> {
                                MensajePaso(
                                    texto = "Vuelve al paso Fecha y selecciona un día."
                                )
                            }

                            cargandoHorarios -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = colorScheme.primary
                                    )
                                }

                                Text(
                                    text = "Consultando disponibilidad...",
                                    color = colorScheme.onSurfaceVariant
                                )
                            }

                            errorDisponibilidad != null -> {
                                MensajePaso(
                                    texto = errorDisponibilidad.orEmpty(),
                                    esError = true
                                )

                                OutlinedButton(
                                    onClick = {
                                        intentoDisponibilidad++
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Consultar nuevamente")
                                }
                            }

                            horariosDelDia.isEmpty() -> {
                                MensajePaso(
                                    texto = "No quedan horarios de inicio disponibles para esta fecha."
                                )
                            }

                            else -> {
                                listOf(
                                    "Mañana" to horariosDelDia.filter {
                                        LocalTime.parse(it).hour < 13
                                    },
                                    "Tarde" to horariosDelDia.filter {
                                        LocalTime.parse(it).hour >= 13
                                    }
                                ).forEach { (titulo, horarios) ->
                                    if (horarios.isNotEmpty()) {
                                        Text(
                                            text = titulo,
                                            fontWeight = FontWeight.SemiBold,
                                            color = colorScheme.onSurface
                                        )

                                        Spacer(
                                            modifier = Modifier.height(8.dp)
                                        )

                                        horarios.chunked(3).forEach { fila ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement =
                                                    Arrangement.spacedBy(8.dp)
                                            ) {
                                                fila.forEach { hora ->
                                                    val ocupado =
                                                        hora in horariosOcupados

                                                    FilterChip(
                                                        selected =
                                                            horaSeleccionada == hora,
                                                        onClick = {
                                                            horaSeleccionada = hora
                                                            errorMensaje = null
                                                        },
                                                        enabled = !ocupado &&
                                                                !guardandoCita,
                                                        label = {
                                                            Text(
                                                                if (ocupado) {
                                                                    "$hora · Ocupado"
                                                                } else {
                                                                    hora
                                                                }
                                                            )
                                                        },
                                                        modifier =
                                                            Modifier.weight(1f),
                                                        colors =
                                                            FilterChipDefaults.filterChipColors(
                                                                selectedContainerColor =
                                                                    colorScheme.primary,
                                                                selectedLabelColor =
                                                                    colorScheme.onPrimary
                                                            )
                                                    )
                                                }

                                                repeat(3 - fila.size) {
                                                    Spacer(
                                                        modifier =
                                                            Modifier.weight(1f)
                                                    )
                                                }
                                            }

                                            Spacer(
                                                modifier = Modifier.height(6.dp)
                                            )
                                        }

                                        Spacer(
                                            modifier = Modifier.height(10.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = "Los horarios ocupados no se pueden seleccionar.",
                                    fontSize = 12.sp,
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    4 -> {
                        Text(
                            text = "Confirmar cita",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Revisa los datos antes de enviar tu solicitud.",
                            fontSize = 14.sp,
                            color = colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TarjetaResumen(
                            titulo = "Mascota",
                            valor = mascotaSeleccionada?.nombre
                                ?: "Sin seleccionar",
                            detalle = mascotaSeleccionada?.let {
                                "${it.especie} · ${it.raza}"
                            }.orEmpty(),
                            onEditar = {
                                pasoActual = 0
                                errorMensaje = null
                            }
                        )

                        TarjetaResumen(
                            titulo = "Servicio",
                            valor = servicioSeleccionado?.nombre
                                ?: "Sin seleccionar",
                            detalle = servicioSeleccionado?.descripcion.orEmpty(),
                            onEditar = {
                                pasoActual = 1
                                errorMensaje = null
                            }
                        )

                        TarjetaResumen(
                            titulo = "Fecha y hora",
                            valor = fechaSeleccionada?.format(
                                formatoFechaVisible
                            ) ?: "Sin fecha",
                            detalle = horaSeleccionada.ifBlank {
                                "Sin horario"
                            },
                            onEditar = {
                                pasoActual = 2
                                errorMensaje = null
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = motivo,
                            onValueChange = {
                                if (it.length <= 250) {
                                    motivo = it
                                    errorMensaje = null
                                }
                            },
                            enabled = !guardandoCita,
                            label = {
                                Text("Motivo de consulta (opcional)")
                            },
                            placeholder = {
                                Text("Describe brevemente el motivo de la visita")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 4,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colorScheme.primary,
                                focusedLabelColor = colorScheme.primary,
                                cursorColor = colorScheme.primary
                            )
                        )

                        Text(
                            text = "${motivo.length}/250 caracteres",
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.End),
                            fontSize = 11.sp,
                            color = colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp)
                            ) {
                                Text(
                                    text = "Costo estimado",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = colorScheme.onSurface
                                )

                                Spacer(
                                    modifier = Modifier.height(12.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement =
                                        Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = servicioSeleccionado?.nombre
                                            ?: "Servicio",
                                        color = colorScheme.onSurfaceVariant
                                    )

                                    Text(
                                        text = formatearCosto(
                                            servicioSeleccionado?.costo ?: 0.0
                                        ),
                                        fontWeight = FontWeight.SemiBold,
                                        color = colorScheme.onSurface
                                    )
                                }

                                Spacer(
                                    modifier = Modifier.height(12.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement =
                                        Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Total estimado",
                                        fontWeight = FontWeight.Bold,
                                        color = colorScheme.onSurface
                                    )

                                    Text(
                                        text = formatearCosto(
                                            servicioSeleccionado?.costo ?: 0.0
                                        ),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 19.sp,
                                        color = colorScheme.primary
                                    )
                                }

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                Text(
                                    text = "Se utiliza la tarifa vigente del servicio. " +
                                            "Los costos adicionales, si los hubiera, " +
                                            "se informarán durante la atención.",
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp,
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = colorScheme.tertiaryContainer
                            ) {
                                Text(
                                    text = "Pendiente de confirmación",
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 7.dp
                                    ),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }
                }

                if (errorMensaje != null) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = errorMensaje.orEmpty(),
                        color = colorScheme.error,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Botones del asistente.
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (pasoActual > 0) {
                    OutlinedButton(
                        onClick = {
                            if (!guardandoCita) {
                                pasoActual--
                                errorMensaje = null
                            }
                        },
                        enabled = !guardandoCita,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Atrás")
                    }
                } else {
                    OutlinedButton(
                        onClick = onBackClick,
                        enabled = !guardandoCita,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Cancelar")
                    }
                }

                Button(
                    onClick = { continuarPaso() },
                    enabled = !guardandoCita &&
                            !cargandoMascotas &&
                            !cargandoServicios &&
                            !cargandoHorarios,
                    modifier = Modifier
                        .weight(1.5f)
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary,
                        contentColor = colorScheme.onPrimary
                    )
                ) {
                    if (guardandoCita) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(21.dp),
                            color = colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = when (pasoActual) {
                                0, 1, 2, 3 -> "Siguiente →"
                                else -> "Confirmar cita"
                            },
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    // Calendario modal del paso Fecha.
    if (mostrarCalendario && citaConfirmadaVisualmente == null) {
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
                    Text("Aceptar")
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
}

@Composable
private fun IndicadorEtapasReserva(
    pasoActual: Int
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        etapasReserva.forEachIndexed { indice, titulo ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(29.dp)
                        .clip(CircleShape)
                        .background(
                            if (indice <= pasoActual) {
                                colorScheme.primary
                            } else {
                                colorScheme.surfaceVariant
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (indice < pasoActual) {
                            "✓"
                        } else {
                            (indice + 1).toString()
                        },
                        color = if (indice <= pasoActual) {
                            colorScheme.onPrimary
                        } else {
                            colorScheme.onSurfaceVariant
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = titulo,
                    fontSize = 10.sp,
                    fontWeight = if (indice == pasoActual) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    },
                    color = if (indice <= pasoActual) {
                        colorScheme.primary
                    } else {
                        colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}

@Composable
private fun MensajePaso(
    texto: String,
    esError: Boolean = false
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (esError) {
                colorScheme.errorContainer
            } else {
                colorScheme.surface
            }
        )
    ) {
        Text(
            text = texto,
            modifier = Modifier.padding(16.dp),
            color = if (esError) {
                colorScheme.onErrorContainer
            } else {
                colorScheme.onSurfaceVariant
            },
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun TarjetaResumen(
    titulo: String,
    valor: String,
    detalle: String,
    onEditar: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = titulo,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )

                TextButton(
                    onClick = onEditar,
                    contentPadding = PaddingValues(
                        horizontal = 8.dp,
                        vertical = 0.dp
                    )
                ) {
                    Text(
                        text = "Editar",
                        color = colorScheme.primary,
                        fontSize = 12.sp
                    )
                }
            }

            Text(
                text = valor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )

            if (detalle.isNotBlank()) {
                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = detalle,
                    fontSize = 12.sp,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PantallaCitaSolicitada(
    cita: Cita,
    onVerCitas: () -> Unit,
    onVolver: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✓",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "¡Cita solicitada!",
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Hemos recibido tu solicitud y está pendiente de confirmación.",
            fontSize = 14.sp,
            lineHeight = 21.sp,
            color = colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(22.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Resumen de la cita",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )

                ResumenFila(
                    etiqueta = "Mascota",
                    valor = cita.nombreMascota
                )

                ResumenFila(
                    etiqueta = "Servicio",
                    valor = cita.nombreServicio
                )

                ResumenFila(
                    etiqueta = "Fecha",
                    valor = cita.fecha
                )

                ResumenFila(
                    etiqueta = "Hora",
                    valor = cita.hora
                )

                ResumenFila(
                    etiqueta = "Costo estimado",
                    valor = formatearCosto(cita.costo)
                )

                ResumenFila(
                    etiqueta = "Estado",
                    valor = cita.estado.etiqueta
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onVerCitas,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary
            )
        ) {
            Text("Ver mis citas")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Volver")
        }
    }
}

@Composable
private fun ResumenFila(
    etiqueta: String,
    valor: String
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = etiqueta,
            modifier = Modifier.weight(1f),
            fontSize = 13.sp,
            color = colorScheme.onSurfaceVariant
        )

        Text(
            text = valor,
            modifier = Modifier.weight(1.3f),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.onSurface
        )
    }
}

private fun formatearCosto(
    costo: Double
): String {
    return "$" + String.format(Locale.US, "%.2f", costo)
}