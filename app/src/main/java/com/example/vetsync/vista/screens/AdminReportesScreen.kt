package com.example.vetsync.vista.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CheckCircleOutline
//import androidx.compose.material.icons.outlined.Stethoscope
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vetsync.vista.theme.* // Asegúrate de importar tus colores

@Composable
fun AdminReportesScreen(
    modifier: Modifier = Modifier
) {
    val backgroundColor = Color(0xFFFDFCF9)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // 1. Tarjeta: Ingresos Totales
        IngresosTotalesCard()

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Tarjeta: Servicio más solicitado
        ServicioMasSolicitadoCard()

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Tarjeta: Citas Completadas
        CitasCompletadasCard()

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Tarjeta: Gráfica de Ingresos (Meses)
        IngresosPorMesCard()

        Spacer(modifier = Modifier.height(16.dp))

        // 5. Tarjeta: Gráfica de Demanda (Dona)
        DemandaServiciosCard()

        Spacer(modifier = Modifier.height(80.dp)) // Espacio para el BottomBar
    }
}

@Composable
fun IngresosTotalesCard() {
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
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE2EFE7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountBalanceWallet,
                        contentDescription = null,
                        tint = Color(0xFF4A7D5E),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Etiqueta de porcentaje
                Row(
                    modifier = Modifier
                        .background(Color(0xFFE2EFE7), RoundedCornerShape(16.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.TrendingUp, contentDescription = null, tint = Color(0xFF4A7D5E), modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "+12.5%", fontSize = 12.sp, color = Color(0xFF4A7D5E), fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Ingresos Totales", fontSize = 13.sp, color = Color.DarkGray)
            Text(text = "$45,280", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
            Text(text = "vs. mes anterior", fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ServicioMasSolicitadoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5EFEB)),
                contentAlignment = Alignment.Center
            ) {
                // Puedes usar un ícono personalizado aquí. Usamos un placeholder de Stethoscope si está disponible o uno genérico.
                Icon(
                    imageVector = Icons.Outlined.AccountBalanceWallet, // Reemplazar con Stethoscope si tienes librería extendida
                    contentDescription = null,
                    tint = Color(0xFF1A1A1A),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Servicio más solicitado", fontSize = 13.sp, color = Color.DarkGray)
            Text(text = "Consulta Gen.", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Círculos superpuestos
                Canvas(modifier = Modifier.size(width = 56.dp, height = 24.dp)) {
                    drawCircle(color = Color(0xFF8BA793), radius = 12.dp.toPx(), center = Offset(12.dp.toPx(), 12.dp.toPx()))
                    drawCircle(color = Color(0xFFCDE2D4), radius = 12.dp.toPx(), center = Offset(28.dp.toPx(), 12.dp.toPx()))
                    drawCircle(color = Color(0xFFD68B71), radius = 12.dp.toPx(), center = Offset(44.dp.toPx(), 12.dp.toPx()))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "45% del total", fontSize = 12.sp, color = Color.DarkGray)
            }
        }
    }
}

@Composable
fun CitasCompletadasCard() {
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
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE2EFE7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircleOutline,
                        contentDescription = null,
                        tint = Color(0xFF1A1A1A),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(text = "Este mes", fontSize = 12.sp, color = Color.DarkGray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Citas Completadas", fontSize = 13.sp, color = Color.DarkGray)
            Text(text = "842", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))

            Spacer(modifier = Modifier.height(8.dp))

            // Barra de progreso personalizada
            LinearProgressIndicator(
                progress = 0.78f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Color(0xFF4A7D5E),
                trackColor = Color(0xFFF0F0F0)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "78% capacidad",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
fun IngresosPorMesCard() {
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
                Text(text = "Ingresos generados por\nmes", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                Icon(Icons.Default.MoreVert, contentDescription = "Opciones", tint = Color.DarkGray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Área de la gráfica (Líneas punteadas)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val lineCount = 4
                    val spacing = size.height / (lineCount - 1)
                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

                    for (i in 0 until lineCount) {
                        val y = spacing * i
                        drawLine(
                            color = Color(0xFFE5E5E5),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = pathEffect
                        )
                    }
                }

                // Aquí irían las barras del gráfico (se omiten para reflejar el diseño estático base)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Eje X (Meses)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val meses = listOf("Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic")
                meses.forEach { mes ->
                    Text(
                        text = mes,
                        fontSize = 10.sp,
                        color = if (mes == "Ago") Color.Black else Color.Gray,
                        fontWeight = if (mes == "Ago") FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun DemandaServiciosCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Demanda de servicios", fontSize = 18.sp, fontWeight = FontWeight.Medium)

            Spacer(modifier = Modifier.height(24.dp))

            // Gráfica de Dona
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(160.dp)) {
                    val strokeWidth = 32.dp.toPx()
                    // Replicamos la dona visualizada en el diseño base (un solo arco verde oscuro como placeholder)
                    drawArc(
                        color = Color(0xFF43694E),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                    )
                }

                // Texto Central
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "842", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(text = "Total", fontSize = 12.sp, color = Color.DarkGray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Leyendas (Grid 2x2 simulado)
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    LeyendaItem(color = Color(0xFF43694E), texto = "Consulta (45%)", modifier = Modifier.weight(1f))
                    LeyendaItem(color = Color(0xFFCDE2D4), texto = "Vacunación (30%)", modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    LeyendaItem(color = Color(0xFFF7D1C9), texto = "Peluquería (15%)", modifier = Modifier.weight(1f))
                    LeyendaItem(color = Color(0xFFD68B71), texto = "Emergencia (10%)", modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun LeyendaItem(color: Color, texto: String, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = texto, fontSize = 11.sp, color = Color.DarkGray)
    }
}