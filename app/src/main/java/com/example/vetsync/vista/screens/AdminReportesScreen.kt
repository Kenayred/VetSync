
package com.example.vetsync.vista.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Schedule
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Share
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vetsync.controlador.CitaControlador
import com.example.vetsync.modelo.Cita
import com.example.vetsync.modelo.EstadoCita
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Currency
import java.util.Locale
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime

private enum class PeriodoReporte(val etiqueta: String) {
    HOY("Hoy"),
    ULTIMOS_7_DIAS("7 días"),
    MES_ACTUAL("Este mes"),
    PERSONALIZADO("Personalizado")
}

private data class PuntoActividad(
    val etiqueta: String,
    val cantidad: Int
)

private data class ServicioReporte(
    val nombre: String,
    val cantidad: Int
)

private val VerdeReporte = Color(0xFF2E5B4B)
private val FondoReporte = Color(0xFFFDFCF9)
private val GrisReporte = Color(0xFF6B7280)

@Composable
fun AdminReportesScreen(
    modifier: Modifier = Modifier,
    controlador: CitaControlador = remember {
        CitaControlador()
    }
) {
    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    var pdfPendiente by remember {
        mutableStateOf<ByteArray?>(null)
    }

    var uriReporteGuardado by remember {
        mutableStateOf<Uri?>(null)
    }

    var exportandoPdf by remember {
        mutableStateOf(false)
    }

    var citas by remember {
        mutableStateOf<List<Cita>>(emptyList())
    }

    var cargando by remember {
        mutableStateOf(true)
    }

    var errorCarga by remember {
        mutableStateOf<String?>(null)
    }

    var solicitudRecarga by remember {
        mutableStateOf(0)
    }

    val hoy = LocalDate.now()

    var periodoSeleccionado by remember {
        mutableStateOf(PeriodoReporte.MES_ACTUAL)
    }

    var fechaInicioPersonalizada by remember {
        mutableStateOf(hoy.withDayOfMonth(1))
    }

    var fechaFinPersonalizada by remember {
        mutableStateOf(hoy)
    }

    val guardarPdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(
            "application/pdf"
        )
    ) { uri ->

        val contenido = pdfPendiente

        if (uri == null) {

            pdfPendiente = null
            exportandoPdf = false

        } else if (contenido == null) {

            exportandoPdf = false

            Toast.makeText(
                context,
                "No se pudo preparar el reporte.",
                Toast.LENGTH_LONG
            ).show()

        } else {

            scope.launch {

                try {

                    withContext(Dispatchers.IO) {

                        val salida = context.contentResolver
                            .openOutputStream(uri)
                            ?: throw IllegalStateException(
                                "No se pudo abrir el archivo de destino."
                            )

                        salida.use {
                            it.write(contenido)
                        }
                    }

                    uriReporteGuardado = uri

                    Toast.makeText(
                        context,
                        "Reporte PDF guardado correctamente.",
                        Toast.LENGTH_LONG
                    ).show()

                } catch (error: Exception) {

                    uriReporteGuardado = null

                    Toast.makeText(
                        context,
                        error.message ?: "No se pudo guardar el PDF.",
                        Toast.LENGTH_LONG
                    ).show()

                } finally {

                    pdfPendiente = null
                    exportandoPdf = false
                }
            }
        }
    }

    // --------------------------------------------------
    // CARGAR TODAS LAS CITAS DE FIRESTORE
    // --------------------------------------------------

    LaunchedEffect(solicitudRecarga) {

        cargando = true
        errorCarga = null

        controlador.obtenerTodasLasCitasAdmin(

            onSuccess = { resultado ->

                citas = resultado
                cargando = false
            },

            onFailure = { error ->

                citas = emptyList()

                errorCarga = error.message
                    ?: "No se pudieron cargar los reportes."

                cargando = false
            }
        )
    }

    // --------------------------------------------------
    // DETERMINAR EL PERÍODO SELECCIONADO
    // --------------------------------------------------

    val rangoFecha = remember(
        periodoSeleccionado,
        fechaInicioPersonalizada,
        fechaFinPersonalizada
    ) {
        obtenerRangoReporte(
            periodo = periodoSeleccionado,
            fechaInicioPersonalizada = fechaInicioPersonalizada,
            fechaFinPersonalizada = fechaFinPersonalizada
        )
    }

    val fechaInicio = rangoFecha.first
    val fechaFin = rangoFecha.second

    // --------------------------------------------------
    // FILTRAR CITAS POR FECHA
    // --------------------------------------------------

    val citasFiltradas = remember(
        citas,
        fechaInicio,
        fechaFin
    ) {

        citas.filter { cita ->

            val fechaCita = runCatching {
                LocalDate.parse(cita.fecha)
            }.getOrNull()

            fechaCita != null &&
                    !fechaCita.isBefore(fechaInicio) &&
                    !fechaCita.isAfter(fechaFin)
        }
    }

    // --------------------------------------------------
    // CALCULAR INDICADORES
    // --------------------------------------------------

    val totalCitas = citasFiltradas.size

    val pendientes = citasFiltradas.count {
        it.estado == EstadoCita.PENDIENTE
    }

    val confirmadas = citasFiltradas.count {
        it.estado == EstadoCita.CONFIRMADA
    }

    val completadas = citasFiltradas.count {
        it.estado == EstadoCita.COMPLETADA
    }

    val canceladas = citasFiltradas.count {
        it.estado == EstadoCita.CANCELADA
    }

    // El cálculo considera únicamente servicios completados.
    // No representa dinero efectivamente cobrado.

    val ingresosEstimados = citasFiltradas
        .filter {
            it.estado == EstadoCita.COMPLETADA
        }
        .sumOf {
            if (it.costo.isFinite() && it.costo > 0) {
                it.costo
            } else {
                0.0
            }
        }

    // --------------------------------------------------
    // PREPARAR DATOS PARA LOS GRÁFICOS
    // --------------------------------------------------

    val actividad = remember(
        citasFiltradas,
        fechaInicio,
        fechaFin
    ) {
        generarActividad(
            citas = citasFiltradas,
            fechaInicio = fechaInicio,
            fechaFin = fechaFin
        )
    }

    val servicios = remember(citasFiltradas) {

        citasFiltradas
            .groupBy { cita ->

                cita.nombreServicio
                    .trim()
                    .ifBlank { "Servicio sin nombre" }
            }
            .map { (nombre, citasServicio) ->

                ServicioReporte(
                    nombre = nombre,
                    cantidad = citasServicio.size
                )
            }
            .sortedWith(
                compareByDescending<ServicioReporte> {
                    it.cantidad
                }.thenBy {
                    it.nombre
                }
            )
            .take(5)
    }

    // --------------------------------------------------
    // INTERFAZ
    // --------------------------------------------------

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoReporte)
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = 18.dp,
                vertical = 16.dp
            ),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        // Encabezado.

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Reportes y estadísticas",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Resumen de la actividad de VetSync",
                    fontSize = 13.sp,
                    color = GrisReporte
                )
            }

            IconButton(
                onClick = {
                    solicitudRecarga++
                },
                enabled = !cargando
            ) {

                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = "Actualizar reportes",
                    tint = VerdeReporte
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Button(
                modifier = Modifier.weight(1f),

                enabled = !cargando &&
                        errorCarga == null &&
                        !exportandoPdf,

                onClick = {

                    exportandoPdf = true
                    uriReporteGuardado = null

                    scope.launch {

                        try {

                            val contenido = withContext(Dispatchers.IO) {

                                generarPdfReporte(
                                    citas = citasFiltradas,
                                    fechaInicio = fechaInicio,
                                    fechaFin = fechaFin
                                )
                            }

                            pdfPendiente = contenido

                            guardarPdfLauncher.launch(
                                "VetSync_Reporte_${fechaInicio}_${fechaFin}.pdf"
                            )

                        } catch (error: Exception) {

                            exportandoPdf = false

                            Toast.makeText(
                                context,
                                error.message
                                    ?: "No se pudo generar el reporte.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                },

                colors = ButtonDefaults.buttonColors(
                    containerColor = VerdeReporte
                )
            ) {

                if (exportandoPdf) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )

                } else {

                    Icon(
                        imageVector = Icons.Outlined.PictureAsPdf,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text("Exportar PDF")
                }
            }

            if (uriReporteGuardado != null) {

                OutlinedButton(
                    onClick = {

                        val uri = uriReporteGuardado ?: return@OutlinedButton

                        val intent = Intent(Intent.ACTION_SEND).apply {

                            type = "application/pdf"

                            putExtra(
                                Intent.EXTRA_STREAM,
                                uri
                            )

                            clipData = ClipData.newUri(
                                context.contentResolver,
                                "Reporte VetSync",
                                uri
                            )

                            addFlags(
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                            )
                        }

                        try {

                            context.startActivity(
                                Intent.createChooser(
                                    intent,
                                    "Compartir reporte VetSync"
                                )
                            )

                        } catch (error: Exception) {

                            Toast.makeText(
                                context,
                                "No se pudo compartir el reporte.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                ) {

                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text("Compartir")
                }
            }
        }

        if (cargando) {

            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = VerdeReporte
            )
        }

        if (errorCarga != null) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF0EE)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "No se pudieron cargar los reportes",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB3261E)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = errorCarga.orEmpty(),
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    TextButton(
                        onClick = {
                            solicitudRecarga++
                        }
                    ) {
                        Text("Reintentar")
                    }
                }
            }
        }

        // --------------------------------------------------
        // FILTROS
        // --------------------------------------------------

        Column {

            Text(
                text = "Período del reporte",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                PeriodoReporte.entries.forEach { periodo ->

                    FilterChip(
                        selected = periodoSeleccionado == periodo,
                        onClick = {
                            periodoSeleccionado = periodo
                        },
                        label = {
                            Text(periodo.etiqueta)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFE2EFE7),
                            selectedLabelColor = VerdeReporte
                        )
                    )
                }
            }

            if (
                periodoSeleccionado ==
                PeriodoReporte.PERSONALIZADO
            ) {

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = {

                            DatePickerDialog(
                                context,
                                { _, year, month, day ->

                                    val nuevaFecha = LocalDate.of(
                                        year,
                                        month + 1,
                                        day
                                    )

                                    fechaInicioPersonalizada = nuevaFecha

                                    if (nuevaFecha.isAfter(fechaFinPersonalizada)) {
                                        fechaFinPersonalizada = nuevaFecha
                                    }
                                },
                                fechaInicioPersonalizada.year,
                                fechaInicioPersonalizada.monthValue - 1,
                                fechaInicioPersonalizada.dayOfMonth
                            ).show()
                        }
                    ) {

                        Text(
                            text = "Desde: ${
                                formatearFecha(fechaInicioPersonalizada)
                            }",
                            fontSize = 11.sp
                        )
                    }

                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = {

                            DatePickerDialog(
                                context,
                                { _, year, month, day ->

                                    val nuevaFecha = LocalDate.of(
                                        year,
                                        month + 1,
                                        day
                                    )

                                    fechaFinPersonalizada = nuevaFecha

                                    if (nuevaFecha.isBefore(fechaInicioPersonalizada)) {
                                        fechaInicioPersonalizada = nuevaFecha
                                    }
                                },
                                fechaFinPersonalizada.year,
                                fechaFinPersonalizada.monthValue - 1,
                                fechaFinPersonalizada.dayOfMonth
                            ).show()
                        }
                    ) {

                        Text(
                            text = "Hasta: ${
                                formatearFecha(fechaFinPersonalizada)
                            }",
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Mostrando ${
                    formatearFecha(fechaInicio)
                } - ${
                    formatearFecha(fechaFin)
                }",
                fontSize = 12.sp,
                color = GrisReporte
            )
        }

        // --------------------------------------------------
        // INDICADORES GENERALES
        // --------------------------------------------------

        Text(
            text = "Resumen general",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF222222)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            TarjetaIndicador(
                modifier = Modifier.weight(1f),
                titulo = "Total de citas",
                valor = totalCitas.toString(),
                icono = Icons.Outlined.CalendarMonth,
                color = Color(0xFF477B9D)
            )

            TarjetaIndicador(
                modifier = Modifier.weight(1f),
                titulo = "Completadas",
                valor = completadas.toString(),
                icono = Icons.Outlined.CheckCircle,
                color = Color(0xFF39845A)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            TarjetaIndicador(
                modifier = Modifier.weight(1f),
                titulo = "Pendientes",
                valor = pendientes.toString(),
                icono = Icons.Outlined.Schedule,
                color = Color(0xFFBD8A38)
            )

            TarjetaIndicador(
                modifier = Modifier.weight(1f),
                titulo = "Confirmadas",
                valor = confirmadas.toString(),
                icono = Icons.Outlined.Event,
                color = Color(0xFF478B91)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            TarjetaIndicador(
                modifier = Modifier.weight(1f),
                titulo = "Canceladas",
                valor = canceladas.toString(),
                icono = Icons.Outlined.Cancel,
                color = Color(0xFFB94B48)
            )

            TarjetaIndicador(
                modifier = Modifier.weight(1f),
                titulo = "Ingresos estimados",
                valor = formatearUSD(ingresosEstimados),
                icono = Icons.Outlined.AttachMoney,
                color = VerdeReporte
            )
        }

        Text(
            text = "Los ingresos estimados corresponden a la suma del costo de las citas completadas. No representan cobros verificados.",
            fontSize = 11.sp,
            color = GrisReporte,
            lineHeight = 16.sp
        )

        // --------------------------------------------------
        // GRÁFICO DE ACTIVIDAD
        // --------------------------------------------------

        GraficoActividad(
            puntos = actividad
        )

        // --------------------------------------------------
        // SERVICIOS MÁS SOLICITADOS
        // --------------------------------------------------

        GraficoServicios(
            servicios = servicios
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}



private fun generarPdfReporte(
    citas: List<Cita>,
    fechaInicio: LocalDate,
    fechaFin: LocalDate
): ByteArray {

    val anchoPagina = 595
    val altoPagina = 842

    val margen = 40f

    val citasOrdenadas = citas.sortedWith(
        compareBy<Cita> {
            it.fecha
        }.thenBy {
            it.hora
        }
    )

    val pendientes = citas.count {
        it.estado == EstadoCita.PENDIENTE
    }

    val confirmadas = citas.count {
        it.estado == EstadoCita.CONFIRMADA
    }

    val completadas = citas.count {
        it.estado == EstadoCita.COMPLETADA
    }

    val canceladas = citas.count {
        it.estado == EstadoCita.CANCELADA
    }

    val ingresos = citas
        .filter {
            it.estado == EstadoCita.COMPLETADA
        }
        .sumOf {
            if (it.costo.isFinite() && it.costo > 0) {
                it.costo
            } else {
                0.0
            }
        }

    val servicios = citas
        .groupBy {
            it.nombreServicio
                .trim()
                .ifBlank { "Servicio sin nombre" }
        }
        .map { (nombre, registros) ->
            ServicioReporte(
                nombre = nombre,
                cantidad = registros.size
            )
        }
        .sortedWith(
            compareByDescending<ServicioReporte> {
                it.cantidad
            }.thenBy {
                it.nombre
            }
        )
        .take(5)

    val filasPorPagina = 20

    val paginasDetalle =
        (citasOrdenadas.size + filasPorPagina - 1) /
                filasPorPagina

    val totalPaginas = 1 + paginasDetalle

    val documento = PdfDocument()

    val verde = android.graphics.Color.rgb(46, 91, 75)
    val textoPrincipal = android.graphics.Color.rgb(34, 34, 34)
    val textoSecundario = android.graphics.Color.rgb(105, 105, 105)
    val fondoCard = android.graphics.Color.rgb(245, 248, 246)
    val borde = android.graphics.Color.rgb(225, 230, 226)
    val blanco = android.graphics.Color.WHITE

    // --------------------------------------------------
    // FUNCIONES AUXILIARES DE DIBUJO
    // --------------------------------------------------

    fun dibujarTexto(
        canvas: android.graphics.Canvas,
        contenido: String,
        x: Float,
        y: Float,
        tamano: Float,
        color: Int = textoPrincipal,
        negrita: Boolean = false,
        alineacion: Paint.Align = Paint.Align.LEFT
    ) {

        val pintura = Paint(
            Paint.ANTI_ALIAS_FLAG
        ).apply {

            this.color = color
            textSize = tamano

            typeface = if (negrita) {
                android.graphics.Typeface.create(
                    android.graphics.Typeface.DEFAULT,
                    android.graphics.Typeface.BOLD
                )
            } else {
                android.graphics.Typeface.DEFAULT
            }

            textAlign = alineacion
        }

        canvas.drawText(
            contenido,
            x,
            y,
            pintura
        )
    }

    fun dibujarLinea(
        canvas: android.graphics.Canvas,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float
    ) {

        val pintura = Paint(
            Paint.ANTI_ALIAS_FLAG
        ).apply {

            color = borde
            strokeWidth = 1f
        }

        canvas.drawLine(
            x1,
            y1,
            x2,
            y2,
            pintura
        )
    }

    fun dibujarEncabezado(
        canvas: android.graphics.Canvas,
        titulo: String
    ) {

        dibujarTexto(
            canvas,
            "VetSync",
            margen,
            52f,
            24f,
            verde,
            true
        )

        dibujarTexto(
            canvas,
            titulo,
            margen,
            78f,
            12f,
            textoPrincipal,
            true
        )

        dibujarLinea(
            canvas,
            margen,
            94f,
            anchoPagina - margen,
            94f
        )
    }

    fun dibujarPie(
        canvas: android.graphics.Canvas,
        pagina: Int
    ) {

        dibujarLinea(
            canvas,
            margen,
            798f,
            anchoPagina - margen,
            798f
        )

        dibujarTexto(
            canvas,
            "VetSync | Reporte administrativo",
            margen,
            818f,
            9f,
            textoSecundario
        )

        dibujarTexto(
            canvas,
            "Página $pagina de $totalPaginas",
            anchoPagina - margen,
            818f,
            9f,
            textoSecundario,
            alineacion = Paint.Align.RIGHT
        )
    }

    fun dibujarTarjeta(
        canvas: android.graphics.Canvas,
        x: Float,
        y: Float,
        titulo: String,
        valor: String
    ) {

        val pintura = Paint(
            Paint.ANTI_ALIAS_FLAG
        ).apply {

            color = fondoCard
            style = Paint.Style.FILL
        }

        canvas.drawRoundRect(
            x,
            y,
            x + 250f,
            y + 62f,
            10f,
            10f,
            pintura
        )

        dibujarTexto(
            canvas,
            titulo,
            x + 12f,
            y + 23f,
            10f,
            textoSecundario
        )

        dibujarTexto(
            canvas,
            valor,
            x + 12f,
            y + 48f,
            18f,
            verde,
            true
        )
    }

    fun ajustarTexto(
        texto: String,
        anchoMaximo: Float,
        tamano: Float
    ): String {

        val pintura = Paint(
            Paint.ANTI_ALIAS_FLAG
        ).apply {
            textSize = tamano
        }

        if (pintura.measureText(texto) <= anchoMaximo) {
            return texto
        }

        var resultado = texto

        while (
            resultado.isNotEmpty() &&
            pintura.measureText("$resultado...") > anchoMaximo
        ) {

            resultado = resultado.dropLast(1)
        }

        return "$resultado..."
    }

    fun textoEstado(cita: Cita): String {

        return when (cita.estado) {

            EstadoCita.PENDIENTE -> "Pendiente"

            EstadoCita.CONFIRMADA -> "Confirmada"

            EstadoCita.COMPLETADA -> "Completada"

            EstadoCita.CANCELADA -> "Cancelada"
        }
    }

    // --------------------------------------------------
    // PRIMERA PÁGINA: RESUMEN GENERAL
    // --------------------------------------------------

    run {

        val informacion = PdfDocument.PageInfo.Builder(
            anchoPagina,
            altoPagina,
            1
        ).create()

        val pagina = documento.startPage(informacion)

        val canvas = pagina.canvas

        canvas.drawColor(blanco)

        dibujarEncabezado(
            canvas,
            "REPORTE ADMINISTRATIVO"
        )

        dibujarTexto(
            canvas,
            "Período: ${
                formatearFecha(fechaInicio)
            } - ${
                formatearFecha(fechaFin)
            }",
            margen,
            120f,
            11f,
            textoPrincipal,
            true
        )

        dibujarTexto(
            canvas,
            "Generado: ${
                LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                )
            }",
            margen,
            140f,
            9f,
            textoSecundario
        )

        dibujarTexto(
            canvas,
            "Resumen general",
            margen,
            165f,
            15f,
            textoPrincipal,
            true
        )

        val indicadores = listOf(
            "Total de citas" to citas.size.toString(),
            "Completadas" to completadas.toString(),
            "Pendientes" to pendientes.toString(),
            "Confirmadas" to confirmadas.toString(),
            "Canceladas" to canceladas.toString(),
            "Ingresos estimados" to formatearUSD(ingresos)
        )

        indicadores.forEachIndexed { indice, indicador ->

            val columna = indice % 2
            val fila = indice / 2

            val x = margen + columna * 265f
            val y = 178f + fila * 76f

            dibujarTarjeta(
                canvas,
                x,
                y,
                indicador.first,
                indicador.second
            )
        }

        dibujarTexto(
            canvas,
            "Servicios más solicitados",
            margen,
            430f,
            15f,
            textoPrincipal,
            true
        )

        if (servicios.isEmpty()) {

            dibujarTexto(
                canvas,
                "No hay servicios registrados en este período.",
                margen,
                460f,
                11f,
                textoSecundario
            )

        } else {

            servicios.forEachIndexed { indice, servicio ->

                val y = 465f + indice * 31f

                dibujarTexto(
                    canvas,
                    "${indice + 1}. ${
                        ajustarTexto(
                            servicio.nombre,
                            400f,
                            10f
                        )
                    }",
                    margen,
                    y,
                    10f,
                    textoPrincipal
                )

                dibujarTexto(
                    canvas,
                    "${servicio.cantidad} citas",
                    anchoPagina - margen,
                    y,
                    10f,
                    verde,
                    true,
                    Paint.Align.RIGHT
                )
            }
        }

        dibujarTexto(
            canvas,
            "Ingresos estimados",
            margen,
            665f,
            11f,
            textoPrincipal,
            true
        )

        dibujarTexto(
            canvas,
            "Corresponden al costo registrado de las citas completadas.",
            margen,
            684f,
            9f,
            textoSecundario
        )

        dibujarTexto(
            canvas,
            "No representan cobros verificados ni ingresos efectivamente percibidos.",
            margen,
            700f,
            9f,
            textoSecundario
        )

        dibujarTexto(
            canvas,
            "Total de registros incluidos: ${citas.size}",
            margen,
            735f,
            10f,
            textoPrincipal,
            true
        )

        dibujarPie(
            canvas,
            1
        )

        documento.finishPage(pagina)
    }

    // --------------------------------------------------
    // PÁGINAS SIGUIENTES: DETALLE DE CITAS
    // --------------------------------------------------

    for (numeroPagina in 0 until paginasDetalle) {

        val numeroReal = numeroPagina + 2

        val informacion = PdfDocument.PageInfo.Builder(
            anchoPagina,
            altoPagina,
            numeroReal
        ).create()

        val pagina = documento.startPage(informacion)

        val canvas = pagina.canvas

        canvas.drawColor(blanco)

        dibujarEncabezado(
            canvas,
            "DETALLE DE CITAS"
        )

        dibujarTexto(
            canvas,
            "Período: ${
                formatearFecha(fechaInicio)
            } - ${
                formatearFecha(fechaFin)
            }",
            margen,
            118f,
            10f,
            textoSecundario
        )

        val inicio = numeroPagina * filasPorPagina

        val fin = minOf(
            inicio + filasPorPagina,
            citasOrdenadas.size
        )

        val encabezados = arrayOf(
            "Fecha",
            "Hora",
            "Mascota",
            "Servicio",
            "Estado",
            "Costo"
        )

        val anchos = floatArrayOf(
            68f,
            40f,
            88f,
            145f,
            85f,
            89f
        )

        val posiciones = floatArrayOf(
            40f,
            108f,
            148f,
            236f,
            381f,
            466f
        )

        // Encabezado de la tabla.

        val pinturaEncabezado = Paint(
            Paint.ANTI_ALIAS_FLAG
        ).apply {

            color = verde
            style = Paint.Style.FILL
        }

        canvas.drawRoundRect(
            margen,
            135f,
            anchoPagina - margen,
            168f,
            5f,
            5f,
            pinturaEncabezado
        )

        encabezados.forEachIndexed { indice, titulo ->

            dibujarTexto(
                canvas,
                titulo,
                posiciones[indice] + 4f,
                155f,
                8f,
                blanco,
                true
            )
        }

        var y = 168f

        val alturaFila = 29f

        for (indice in inicio until fin) {

            val cita = citasOrdenadas[indice]

            val fondo = Paint(
                Paint.ANTI_ALIAS_FLAG
            ).apply {

                color = if ((indice - inicio) % 2 == 0) {
                    android.graphics.Color.rgb(247, 249, 247)
                } else {
                    blanco
                }
            }

            canvas.drawRect(
                margen,
                y,
                anchoPagina - margen,
                y + alturaFila,
                fondo
            )

            val fechaTexto = runCatching {
                LocalDate.parse(cita.fecha).format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                )
            }.getOrDefault(cita.fecha)

            val valores = arrayOf(
                fechaTexto,
                cita.hora,
                cita.nombreMascota,
                cita.nombreServicio,
                textoEstado(cita),
                formatearUSD(cita.costo)
            )

            valores.forEachIndexed { columna, valor ->

                val texto = ajustarTexto(
                    valor.ifBlank { "-" },
                    anchos[columna] - 8f,
                    8f
                )

                dibujarTexto(
                    canvas,
                    texto,
                    posiciones[columna] + 4f,
                    y + 19f,
                    8f,
                    textoPrincipal
                )
            }

            y += alturaFila
        }

        dibujarTexto(
            canvas,
            "Registros: ${citasOrdenadas.size}",
            margen,
            775f,
            9f,
            textoSecundario
        )

        dibujarPie(
            canvas,
            numeroReal
        )

        documento.finishPage(pagina)
    }

    // --------------------------------------------------
    // GENERAR ARCHIVO EN MEMORIA
    // --------------------------------------------------

    return try {

        ByteArrayOutputStream().use { salida ->

            documento.writeTo(salida)

            salida.toByteArray()
        }

    } finally {

        documento.close()
    }
}

// --------------------------------------------------
// TARJETA DE INDICADOR
// --------------------------------------------------

@Composable
private fun TarjetaIndicador(
    modifier: Modifier = Modifier,
    titulo: String,
    valor: String,
    icono: ImageVector,
    color: Color
) {

    Card(
        modifier = modifier.heightIn(min = 112.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            1.dp,
            Color(0xFFE8E8E8)
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp)
        ) {

            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(9.dp))

            Text(
                text = valor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222),
                maxLines = 1
            )

            Text(
                text = titulo,
                fontSize = 11.sp,
                color = GrisReporte,
                maxLines = 2
            )
        }
    }
}

// --------------------------------------------------
// GRÁFICO DE ACTIVIDAD DE CITAS
// --------------------------------------------------

@Composable
private fun GraficoActividad(
    puntos: List<PuntoActividad>
) {

    TarjetaReporte {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "Actividad de citas",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Cantidad de citas registradas en el período",
                fontSize = 12.sp,
                color = GrisReporte
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (puntos.isEmpty()) {

                MensajeSinDatos()

            } else {

                val maximo = puntos.maxOfOrNull {
                    it.cantidad
                }?.coerceAtLeast(1) ?: 1

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Bottom
                ) {

                    puntos.forEach { punto ->

                        Column(
                            modifier = Modifier.width(42.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = punto.cantidad.toString(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GrisReporte
                            )

                            Spacer(modifier = Modifier.height(5.dp))

                            Box(
                                modifier = Modifier
                                    .height(105.dp)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.BottomCenter
                            ) {

                                val altura = if (punto.cantidad == 0) {
                                    2.dp
                                } else {
                                    (95f * punto.cantidad / maximo).dp
                                }

                                Box(
                                    modifier = Modifier
                                        .width(23.dp)
                                        .height(altura)
                                        .clip(
                                            RoundedCornerShape(
                                                topStart = 6.dp,
                                                topEnd = 6.dp
                                            )
                                        )
                                        .background(VerdeReporte)
                                )
                            }

                            Spacer(modifier = Modifier.height(7.dp))

                            Text(
                                text = punto.etiqueta,
                                fontSize = 9.sp,
                                color = GrisReporte,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

// --------------------------------------------------
// SERVICIOS MÁS SOLICITADOS
// --------------------------------------------------

@Composable
private fun GraficoServicios(
    servicios: List<ServicioReporte>
) {

    TarjetaReporte {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "Servicios más solicitados",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222)
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Distribución según las citas registradas",
                fontSize = 12.sp,
                color = GrisReporte
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (servicios.isEmpty()) {

                MensajeSinDatos()

            } else {

                val maximo = servicios.maxOf {
                    it.cantidad
                }.coerceAtLeast(1)

                servicios.forEachIndexed { index, servicio ->

                    Column {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "${index + 1}. ${servicio.nombre}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF333333),
                                modifier = Modifier.weight(1f)
                            )

                            Text(
                                text = servicio.cantidad.toString(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = VerdeReporte
                            )
                        }

                        Spacer(modifier = Modifier.height(7.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(9.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFEBEFEC))
                        ) {

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(
                                        servicio.cantidad.toFloat() / maximo
                                    )
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(VerdeReporte)
                            )
                        }
                    }

                    if (index != servicios.lastIndex) {
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }
            }
        }
    }
}

// --------------------------------------------------
// TARJETA REUTILIZABLE
// --------------------------------------------------

@Composable
private fun TarjetaReporte(
    contenido: @Composable () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(
            1.dp,
            Color(0xFFE5E5E5)
        )
    ) {
        contenido()
    }
}

@Composable
private fun MensajeSinDatos() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 25.dp),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = "No hay información disponible para este período.",
            fontSize = 13.sp,
            color = GrisReporte
        )
    }
}

// --------------------------------------------------
// CÁLCULO DE RANGOS
// --------------------------------------------------

private fun obtenerRangoReporte(
    periodo: PeriodoReporte,
    fechaInicioPersonalizada: LocalDate,
    fechaFinPersonalizada: LocalDate
): Pair<LocalDate, LocalDate> {

    val hoy = LocalDate.now()

    return when (periodo) {

        PeriodoReporte.HOY -> {
            hoy to hoy
        }

        PeriodoReporte.ULTIMOS_7_DIAS -> {
            hoy.minusDays(6) to hoy
        }

        PeriodoReporte.MES_ACTUAL -> {
            hoy.withDayOfMonth(1) to hoy
        }

        PeriodoReporte.PERSONALIZADO -> {

            if (fechaInicioPersonalizada.isAfter(fechaFinPersonalizada)) {
                fechaFinPersonalizada to fechaInicioPersonalizada
            } else {
                fechaInicioPersonalizada to fechaFinPersonalizada
            }
        }
    }
}

// --------------------------------------------------
// GENERAR PUNTOS PARA EL GRÁFICO
// --------------------------------------------------

private fun generarActividad(
    citas: List<Cita>,
    fechaInicio: LocalDate,
    fechaFin: LocalDate
): List<PuntoActividad> {

    val cantidadDias =
        ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1

    val puntos = mutableListOf<PuntoActividad>()

    // Períodos cortos: una barra por día.
    if (cantidadDias <= 14) {

        var fecha = fechaInicio

        while (!fecha.isAfter(fechaFin)) {

            val cantidad = contarCitasEnRango(
                citas,
                fecha,
                fecha
            )

            puntos.add(
                PuntoActividad(
                    etiqueta = fecha.format(
                        DateTimeFormatter.ofPattern("dd/MM")
                    ),
                    cantidad = cantidad
                )
            )

            fecha = fecha.plusDays(1)
        }

        // Períodos medianos: una barra por semana.
    } else if (cantidadDias <= 90) {

        var inicio = fechaInicio

        while (!inicio.isAfter(fechaFin)) {

            val fin = minOf(
                inicio.plusDays(6),
                fechaFin
            )

            val cantidad = contarCitasEnRango(
                citas,
                inicio,
                fin
            )

            puntos.add(
                PuntoActividad(
                    etiqueta = "${inicio.dayOfMonth}/${inicio.monthValue}",
                    cantidad = cantidad
                )
            )

            inicio = fin.plusDays(1)
        }

        // Períodos largos: una barra por mes.
    } else {

        var mes = fechaInicio.withDayOfMonth(1)

        while (!mes.isAfter(fechaFin)) {

            val inicio = maxOf(
                mes,
                fechaInicio
            )

            val fin = minOf(
                mes.plusMonths(1).minusDays(1),
                fechaFin
            )

            val cantidad = contarCitasEnRango(
                citas,
                inicio,
                fin
            )

            puntos.add(
                PuntoActividad(
                    etiqueta = mes.format(
                        DateTimeFormatter.ofPattern(
                            "MMM yy",
                            Locale("es", "SV")
                        )
                    ),
                    cantidad = cantidad
                )
            )

            mes = mes.plusMonths(1)
        }
    }

    return puntos
}

private fun contarCitasEnRango(
    citas: List<Cita>,
    fechaInicio: LocalDate,
    fechaFin: LocalDate
): Int {

    return citas.count { cita ->

        val fecha = runCatching {
            LocalDate.parse(cita.fecha)
        }.getOrNull()

        fecha != null &&
                !fecha.isBefore(fechaInicio) &&
                !fecha.isAfter(fechaFin)
    }
}

// --------------------------------------------------
// FORMATO DE FECHAS Y MONEDA
// --------------------------------------------------

private fun formatearFecha(fecha: LocalDate): String {

    return fecha.format(
        DateTimeFormatter.ofPattern("dd/MM/yyyy")
    )
}

private fun formatearUSD(valor: Double): String {

    val formato = NumberFormat.getCurrencyInstance(Locale.US)

    formato.currency = Currency.getInstance("USD")

    return formato.format(valor)
}
