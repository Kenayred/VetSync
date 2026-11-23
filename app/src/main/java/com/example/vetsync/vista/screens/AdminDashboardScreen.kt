package com.example.vetsync.vista.screens

import android.util.Log

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.vetsync.controlador.CitaControlador
import com.example.vetsync.modelo.CitaConMascota
import com.example.vetsync.modelo.ResumenCitas

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    controladorCita: CitaControlador = remember {
        CitaControlador()
    },
    modifier: Modifier = Modifier,
    onNavigateToCitas: () -> Unit = {},
    onNavigateToPacientes: () -> Unit = {}
) {

    val colorScheme = MaterialTheme.colorScheme

    var resumenCitas by remember {
        mutableStateOf(ResumenCitas())
    }

    var proximosPacientes by remember {
        mutableStateOf<List<CitaConMascota>>(emptyList())
    }

    var ingresosEstimados by remember {
        mutableStateOf(0.0)
    }

    // Estados de carga independientes.

    var cargandoResumen by remember {
        mutableStateOf(true)
    }

    var cargandoPacientes by remember {
        mutableStateOf(true)
    }

    var cargandoIngresos by remember {
        mutableStateOf(true)
    }

    // Errores independientes.

    var errorResumen by remember {
        mutableStateOf<String?>(null)
    }

    var errorPacientes by remember {
        mutableStateOf<String?>(null)
    }

    var errorIngresos by remember {
        mutableStateOf<String?>(null)
    }

    // --------------------------------------------------
    // CARGAR INFORMACIÓN DEL DASHBOARD
    // --------------------------------------------------

    fun cargarDashboard() {

        cargandoResumen = true
        cargandoPacientes = true
        cargandoIngresos = true

        errorResumen = null
        errorPacientes = null
        errorIngresos = null

        // 1. Resumen de citas.

        controladorCita.obtenerResumenCitasDiaAdmin(

            onSuccess = { resumen ->

                resumenCitas = resumen
                cargandoResumen = false
            },

            onFailure = { error ->

                errorResumen = error.message
                    ?: "No se pudo cargar el resumen de citas."

                cargandoResumen = false

                Log.e(
                    "AdminDashboard",
                    "Error en resumen: ${error.message}"
                )
            }
        )

        // 2. Próximos pacientes.

        controladorCita.obtenerProximosPacientes(

            onSuccess = { lista ->

                proximosPacientes = lista
                cargandoPacientes = false
            },

            onFailure = { error ->

                errorPacientes = error.message
                    ?: "No se pudieron cargar los pacientes."

                cargandoPacientes = false

                Log.e(
                    "AdminDashboard",
                    "Error en pacientes: ${error.message}"
                )
            }
        )

        // 3. Ingresos estimados.

        controladorCita.obtenerIngresosEstimadosDiaAdmin(

            onSuccess = { ingresos ->

                ingresosEstimados = ingresos
                cargandoIngresos = false
            },

            onFailure = { error ->

                errorIngresos = error.message
                    ?: "No se pudieron calcular los ingresos."

                cargandoIngresos = false

                Log.e(
                    "AdminDashboard",
                    "Error en ingresos: ${error.message}"
                )
            }
        )
    }

    LaunchedEffect(Unit) {
        cargarDashboard()
    }

    val fechaActual = remember {

        LocalDate.now().format(
            DateTimeFormatter.ofPattern(
                "EEEE d 'de' MMMM 'de' yyyy",
                Locale("es", "SV")
            )
        )
    }

    val erroresDashboard = listOfNotNull(
        errorResumen,
        errorPacientes,
        errorIngresos
    )

    // --------------------------------------------------
    // CONTENEDOR PRINCIPAL
    // --------------------------------------------------

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {

        Text(
            text = "Resumen del Día",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground
        )

        Text(
            text = fechaActual.replaceFirstChar {
                it.uppercase(Locale("es", "SV"))
            },
            fontSize = 13.sp,
            color = colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --------------------------------------------------
        // MENSAJES DE ERROR
        // --------------------------------------------------

        if (erroresDashboard.isNotEmpty()) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "Algunos datos no pudieron cargarse.",
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onErrorContainer
                    )

                    erroresDashboard.forEach { mensaje ->

                        Text(
                            text = mensaje,
                            fontSize = 12.sp,
                            color = colorScheme.onErrorContainer
                        )
                    }

                    TextButton(
                        onClick = {
                            cargarDashboard()
                        }
                    ) {

                        Text(
                            text = "Reintentar",
                            color = colorScheme.onErrorContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // --------------------------------------------------
        // TARJETA: TOTAL DE CITAS
        // --------------------------------------------------

        DashboardKpiCard(
            title = "Citas Totales",
            value = if (cargandoResumen) {
                "..."
            } else {
                resumenCitas.total.toString()
            },
            icon = Icons.Default.DateRange,
            iconBgColor = colorScheme.primaryContainer,
            iconTintColor = colorScheme.onPrimaryContainer,
            extraRightText = "Hoy"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // --------------------------------------------------
        // TARJETA: INGRESOS ESTIMADOS
        // --------------------------------------------------

        DashboardKpiCard(
            title = "Ingresos Estimados",
            value = if (cargandoIngresos) {
                "..."
            } else {
                "$" + String.format(
                    Locale.US,
                    "%.2f",
                    ingresosEstimados
                )
            },
            icon = Icons.Outlined.AccountBalanceWallet,
            iconBgColor = colorScheme.tertiaryContainer,
            iconTintColor = colorScheme.onTertiaryContainer,
            supportingText = "Citas confirmadas y completadas de hoy."
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --------------------------------------------------
        // ESTADO DE CITAS
        // --------------------------------------------------

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Estado de Citas",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = colorScheme.onSurface
            )

            TextButton(
                onClick = onNavigateToCitas
            ) {

                Text(
                    text = "Ver agenda",
                    color = colorScheme.primary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Primera fila.

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            EstadoCitaMiniCard(
                modifier = Modifier.weight(1f),
                numero = if (cargandoResumen) {
                    "..."
                } else {
                    resumenCitas.completadas.toString()
                },
                estado = "Completadas",
                numeroColor = colorScheme.onPrimaryContainer,
                bgColor = colorScheme.primaryContainer
            )

            EstadoCitaMiniCard(
                modifier = Modifier.weight(1f),
                numero = if (cargandoResumen) {
                    "..."
                } else {
                    resumenCitas.pendientes.toString()
                },
                estado = "Pendientes",
                numeroColor = colorScheme.onTertiaryContainer,
                bgColor = colorScheme.tertiaryContainer
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Segunda fila.

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            EstadoCitaMiniCard(
                modifier = Modifier.weight(1f),
                numero = if (cargandoResumen) {
                    "..."
                } else {
                    resumenCitas.confirmadas.toString()
                },
                estado = "Confirmadas",
                numeroColor = colorScheme.onSecondaryContainer,
                bgColor = colorScheme.secondaryContainer
            )

            EstadoCitaMiniCard(
                modifier = Modifier.weight(1f),
                numero = if (cargandoResumen) {
                    "..."
                } else {
                    resumenCitas.canceladas.toString()
                },
                estado = "Canceladas",
                numeroColor = colorScheme.onErrorContainer,
                bgColor = colorScheme.errorContainer
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --------------------------------------------------
        // PRÓXIMOS PACIENTES
        // --------------------------------------------------

        Text(
            text = "Próximos Pacientes",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {

            cargandoPacientes -> {

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
            }

            errorPacientes != null -> {

                Text(
                    text = errorPacientes.orEmpty(),
                    fontSize = 13.sp,
                    color = colorScheme.error
                )
            }

            proximosPacientes.isEmpty() -> {

                Text(
                    text = "No hay citas confirmadas para hoy.",
                    color = colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }

            else -> {

                proximosPacientes.forEach { paciente ->

                    PacienteCard(
                        nombre = paciente.cita.nombreMascota,

                        razaEdad = listOf(
                            paciente.raza,
                            paciente.edad
                        )
                            .filter { it.isNotBlank() }
                            .joinToString(" • ")
                            .ifBlank {
                                "Información no disponible"
                            },

                        estado = paciente.cita.estado.etiqueta,

                        estadoBgColor = colorScheme.primaryContainer,

                        estadoTextColor = colorScheme.onPrimaryContainer,

                        hora = paciente.cita.hora,

                        tipoConsulta = paciente.cita.nombreServicio,

                        descripcion = paciente.cita.motivo
                            .ifBlank {
                                "Sin descripción del motivo de consulta."
                            }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --------------------------------------------------
        // ACCESOS RÁPIDOS
        // --------------------------------------------------

        Text(
            text = "Accesos rápidos",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedButton(
                onClick = onNavigateToCitas,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colorScheme.primary
                )
            ) {

                Text(
                    text = "Gestionar citas",
                    fontSize = 12.sp
                )
            }

            OutlinedButton(
                onClick = onNavigateToPacientes,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colorScheme.primary
                )
            ) {

                Text(
                    text = "Ver pacientes",
                    fontSize = 12.sp
                )
            }
        }
    }
}

// --------------------------------------------------
// TARJETA KPI DEL DASHBOARD
// --------------------------------------------------

@Composable
fun DashboardKpiCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconBgColor: Color,
    iconTintColor: Color,
    extraRightText: String? = null,
    supportingText: String? = null
) {

    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            1.dp,
            colorScheme.outlineVariant
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTintColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (extraRightText != null) {

                    Text(
                        text = extraRightText,
                        fontSize = 12.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                color = colorScheme.onSurfaceVariant
            )

            Row(
                verticalAlignment = Alignment.Bottom
            ) {

                Text(
                    text = value,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )

                if (supportingText != null) {

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = supportingText,
                        fontSize = 11.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// --------------------------------------------------
// TARJETA DE ESTADO DE CITA
// --------------------------------------------------

@Composable
fun EstadoCitaMiniCard(
    modifier: Modifier = Modifier,
    numero: String,
    estado: String,
    numeroColor: Color,
    bgColor: Color
) {

    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = numero,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = numeroColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = estado,
                fontSize = 11.sp,
                color = colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

// --------------------------------------------------
// TARJETA DE PACIENTE
// --------------------------------------------------

@Composable
fun PacienteCard(
    nombre: String,
    razaEdad: String,
    estado: String,
    estadoBgColor: Color,
    estadoTextColor: Color,
    hora: String,
    tipoConsulta: String,
    descripcion: String
) {

    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            1.dp,
            colorScheme.outlineVariant
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // Fila superior: avatar, nombre y estado.

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(colorScheme.surfaceVariant)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = nombre,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )

                    Text(
                        text = razaEdad,
                        fontSize = 12.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(estadoBgColor)
                        .padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        )
                ) {

                    Text(
                        text = estado,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = estadoTextColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                color = colorScheme.outlineVariant,
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Fila de hora y tipo de consulta.

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = "Hora",
                        modifier = Modifier.size(14.dp),
                        tint = colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = hora,
                        fontSize = 12.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = tipoConsulta,
                    fontSize = 12.sp,
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Descripción.

            Text(
                text = descripcion,
                fontSize = 13.sp,
                color = colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
        }
    }
}