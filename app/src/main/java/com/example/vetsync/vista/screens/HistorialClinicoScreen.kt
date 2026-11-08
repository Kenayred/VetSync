package com.example.vetsync.vista.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vetsync.controlador.MascotaControlador
import com.example.vetsync.controlador.NotaClinicaControlador
import com.example.vetsync.modelo.Mascota
import com.example.vetsync.modelo.NotaClinica
import com.example.vetsync.modelo.SesionUsuario
import com.example.vetsync.vista.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.example.vetsync.vista.components.LoadingDots

@Composable
fun HistorialClinicoScreen(
    mascota: Mascota? = null,
    onBackClick: () -> Unit = {}
) {

    val controladorMascota = remember {
        MascotaControlador()
    }

    val controladorNota = remember {
        NotaClinicaControlador()
    }

    var mascotas by remember {
        mutableStateOf<List<Mascota>>(emptyList())
    }

    var mascotaSeleccionada by remember(mascota?.id) {
        mutableStateOf(mascota)
    }

    var historial by remember {
        mutableStateOf<List<NotaClinica>>(emptyList())
    }

    var cargandoMascotas by remember {
        mutableStateOf(mascota == null)
    }

    var cargandoHistorial by remember {
        mutableStateOf(false)
    }

    var errorMascotas by remember {
        mutableStateOf<String?>(null)
    }

    var errorHistorial by remember {
        mutableStateOf<String?>(null)
    }

    var recargar by remember {
        mutableIntStateOf(0)
    }

    // =====================================================
    // CARGAR MASCOTAS DEL CLIENTE CUANDO NO SE RECIBE UNA
    // =====================================================

    LaunchedEffect(
        mascota?.id,
        SesionUsuario.idUsuario,
        recargar
    ) {

        if (mascota == null) {

            cargandoMascotas = true
            errorMascotas = null

            controladorMascota.obtenerMascotasUsuario(
                idUsuario = SesionUsuario.idUsuario,

                onSuccess = { lista ->
                    mascotas = lista
                    cargandoMascotas = false
                },

                onError = { error ->
                    errorMascotas = error
                    cargandoMascotas = false
                }
            )

        } else {

            mascotas = listOf(mascota)
            mascotaSeleccionada = mascota
            cargandoMascotas = false
        }
    }

    // =====================================================
    // CARGAR HISTORIAL DE LA MASCOTA SELECCIONADA
    // =====================================================

    LaunchedEffect(
        mascotaSeleccionada?.id,
        SesionUsuario.idUsuario,
        recargar
    ) {

        val mascotaActual = mascotaSeleccionada

        if (mascotaActual == null) {
            historial = emptyList()
            return@LaunchedEffect
        }

        cargandoHistorial = true
        errorHistorial = null

        controladorNota.obtenerHistorialMascota(
            mascotaId = mascotaActual.id,

            onSuccess = { lista ->
                historial = lista
                cargandoHistorial = false
            },

            onError = { error ->
                errorHistorial = error
                cargandoHistorial = false
            }
        )
    }

    // =====================================================
    // INTERFAZ
    // =====================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoCrema)
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = 20.dp,
                vertical = 12.dp
            )
    ) {

        // CABECERA

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBackClick
            ) {

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Regresar",
                    tint = VerdeVetSync
                )
            }

            Text(
                text = "Historial clínico",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E221F)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (mascotaSeleccionada == null) {

            // =================================================
            // SELECCIÓN DE MASCOTA
            // =================================================

            Text(
                text = "Selecciona una mascota",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E221F)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Consulta los registros médicos de tus compañeros.",
                fontSize = 14.sp,
                color = GrisTextoSecundario
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (cargandoMascotas) {

                LoadingDots()

            } else if (errorMascotas != null) {

                EstadoHistorial(
                    mensaje = errorMascotas
                        ?: "No se pudieron cargar tus mascotas.",
                    esError = true,
                    onRetry = {
                        recargar++
                    }
                )

            } else if (mascotas.isEmpty()) {

                EstadoHistorial(
                    mensaje = "Todavía no tienes mascotas registradas."
                )

            } else {

                mascotas.forEach { item ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),

                        shape = RoundedCornerShape(16.dp),

                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),

                        border = BorderStroke(
                            1.dp,
                            Color(0xFFEEECE8)
                        )
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),

                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    text = item.nombre,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E221F)
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text = "${item.especie} • ${item.raza}",
                                    fontSize = 13.sp,
                                    color = GrisTextoSecundario
                                )
                            }

                            Button(
                                onClick = {
                                    mascotaSeleccionada = item
                                },

                                colors = ButtonDefaults.buttonColors(
                                    containerColor = VerdeVetSync
                                ),

                                shape = RoundedCornerShape(10.dp)
                            ) {

                                Text("Consultar")
                            }
                        }
                    }
                }
            }

        } else {

            // =================================================
            // HISTORIAL DE UNA MASCOTA
            // =================================================

            val mascotaActual = mascotaSeleccionada!!

            Card(
                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(18.dp),

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
                        .padding(18.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Outlined.DateRange,
                            contentDescription = null,
                            tint = VerdeVetSync,
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(12.dp)
                        )

                        Column {

                            Text(
                                text = mascotaActual.nombre,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E221F)
                            )

                            Text(
                                text = "${mascotaActual.especie} • ${mascotaActual.raza}",
                                fontSize = 13.sp,
                                color = GrisTextoSecundario
                            )
                        }
                    }
                }
            }

            if (mascota == null) {

                TextButton(
                    onClick = {
                        mascotaSeleccionada = null
                        historial = emptyList()
                        errorHistorial = null
                    }
                ) {

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Text("Cambiar mascota")
                }
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text = "Registros médicos",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E221F)
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            if (cargandoHistorial) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp),

                    contentAlignment = Alignment.Center
                ) {
                    LoadingDots()
                }

            } else if (errorHistorial != null) {

                EstadoHistorial(
                    mensaje = errorHistorial
                        ?: "No se pudo cargar el historial.",
                    esError = true,
                    onRetry = {
                        recargar++
                    }
                )

            } else if (historial.isEmpty()) {

                EstadoHistorial(
                    mensaje = "${mascotaActual.nombre} todavía no tiene registros clínicos."
                )

            } else {

                historial.forEach { nota ->

                    NotaClinicaCard(nota)

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(30.dp)
        )
    }
}

// =========================================================
// TARJETA DE REGISTRO CLÍNICO
// =========================================================

@Composable
private fun NotaClinicaCard(
    nota: NotaClinica
) {

    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(18.dp),

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
                .padding(18.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Outlined.DateRange,
                    contentDescription = null,
                    tint = VerdeVetSync
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = formatearFechaClinica(nota.fecha),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VerdeVetSync
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 14.dp),
                color = Color(0xFFEEECE8)
            )

            if (nota.motivo.isNotBlank()) {
                CampoClinico(
                    titulo = "Motivo de consulta",
                    contenido = nota.motivo
                )
            }

            if (nota.peso != null) {
                CampoClinico(
                    titulo = "Peso",
                    contenido = "${nota.peso} kg"
                )
            }

            if (nota.temperatura != null) {
                CampoClinico(
                    titulo = "Temperatura",
                    contenido = "${nota.temperatura} °C"
                )
            }

            CampoClinico(
                titulo = "Diagnóstico",
                contenido = nota.diagnostico
            )

            CampoClinico(
                titulo = "Receta médica",
                contenido = nota.receta
            )

            if (!nota.observaciones.isNullOrBlank()) {
                CampoClinico(
                    titulo = "Observaciones",
                    contenido = nota.observaciones.orEmpty()
                )
            }
        }
    }
}

// =========================================================
// CAMPO DE INFORMACIÓN CLÍNICA
// =========================================================

@Composable
private fun CampoClinico(
    titulo: String,
    contenido: String
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {

        Text(
            text = titulo,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = GrisTextoSecundario
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = contenido,
            fontSize = 14.sp,
            color = Color(0xFF1E221F),
            lineHeight = 20.sp
        )
    }
}

// =========================================================
// ESTADOS VACÍOS Y ERRORES
// =========================================================

@Composable
private fun EstadoHistorial(
    mensaje: String,
    esError: Boolean = false,
    onRetry: (() -> Unit)? = null
) {

    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(18.dp),

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
                .padding(24.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Outlined.DateRange,
                contentDescription = null,
                tint = if (esError) RojoAlerta else VerdeVetSync,
                modifier = Modifier.size(42.dp)
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = mensaje,
                fontSize = 14.sp,
                color = GrisTextoSecundario
            )

            if (onRetry != null) {

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                OutlinedButton(
                    onClick = onRetry
                ) {
                    Text("Intentar nuevamente")
                }
            }
        }
    }
}

// =========================================================
// FORMATO DE FECHA
// =========================================================

private fun formatearFechaClinica(
    fecha: String
): String {

    return try {

        LocalDate.parse(fecha).format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
        )

    } catch (e: Exception) {

        fecha
    }
}