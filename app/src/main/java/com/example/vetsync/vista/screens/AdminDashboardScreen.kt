package com.example.vetsync.vista.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vetsync.R
import com.example.vetsync.vista.theme.*
import com.example.vetsync.controlador.CitaControlador
import com.example.vetsync.modelo.CitaConMascota
import com.example.vetsync.modelo.ResumenCitas

@Composable
fun AdminDashboardScreen(
    controladorCita: CitaControlador = remember { CitaControlador() },
    modifier: Modifier = Modifier
) {

    var resumenCitas by remember { mutableStateOf(ResumenCitas()) }
    var proximosPacientes by remember { mutableStateOf<List<CitaConMascota>>(emptyList())}
    LaunchedEffect(Unit) {
        controladorCita.obtenerResumenCitasDiaAdmin(
            onSuccess = { resumen ->
                resumenCitas = resumen
            },
            onFailure = { error ->
                Log.e("Firebase", "Error al traer citas: ${error.message}")
            }
        )

        controladorCita.obtenerProximosPacientes(
            onSuccess = { lista -> proximosPacientes = lista},
            onFailure = {error ->
                Log.e("ERROR: Proximos Pacientes", "${error.message}")
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoCrema)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Resumen del Día",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextoOscuro
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tarjeta: Citas Totales
        DashboardKpiCard(
            title = "Citas Totales",
            value = resumenCitas.total.toString(),
            icon = Icons.Default.DateRange,
            iconBgColor = FondoClaro,
            iconTintColor = VerdeFondoIcono,
            extraRightText = "Hoy",
            trendText = "~12%"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tarjeta: Ingresos Estimados
        DashboardKpiCard(
            title = "Ingresos Estimados",
            value = "$1,250",
            icon = Icons.Outlined.AccountBalanceWallet,
            iconBgColor = NaranjaSuaveIcono,
            iconTintColor = NaranjaIcono
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Sección: Estado de Citas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Estado de Citas",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Ver todas>",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EstadoCitaMiniCard(
                modifier = Modifier.weight(1f),
                numero = resumenCitas.completadas.toString(),
                estado = "Completadas",
                numeroColor = Color(0xFF2E5B4B),
                bgColor = Color(0xFFF5EFEB)
            )
            EstadoCitaMiniCard(
                modifier = Modifier.weight(1f),
                numero = resumenCitas.pendientes.toString(),
                estado = "Pendientes",
                numeroColor = Color(0xFFB57B22),
                bgColor = Color(0xFFFDF8EA)
            )
            EstadoCitaMiniCard(
                modifier = Modifier.weight(1f),
                numero = resumenCitas.confirmadas.toString(),
                estado = "Confirmadas",
                numeroColor = Color(0xFF333333),
                bgColor = Color(0xFFF5EFEB)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Sección: Próximos Pacientes
        Text(
            text = "Próximos Pacientes",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if(proximosPacientes.isEmpty()){
            Text("No hay citas confirmadas para hoy", color = TextoOscuro)
        } else {
            proximosPacientes.forEach { mascota ->

                PacienteCard(
                    nombre = mascota.cita.nombreMascota,
                    razaEdad = "${mascota.raza} • ${mascota.edad}",
                    estado = mascota.cita.estado.etiqueta,
                    estadoBgColor = Color(0xFFFDF0D5),
                    estadoTextColor = Color(0xFFB57B22),
                    hora = mascota.cita.hora,
                    tipoConsulta = mascota.cita.nombreServicio,
                    descripcion = mascota.cita.motivo
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

//        PacienteCard(
//            nombre = "Luna",
//            razaEdad = "Siamés • 4 años",
//            estado = "Confirmada",
//            estadoBgColor = Color(0xFFD5E8D4), // Verde claro
//            estadoTextColor = Color(0xFF2E5B4B),
//            hora = "11:15 AM",
//            tipoConsulta = "Seguimiento",
//            descripcion = "Revisión de suturas post-operación de esterilización. Estado general favorable."
//        )
//
//        Spacer(modifier = Modifier.height(80.dp)) // Espacio para que el BottomBar no tape el último elemento
    }
}

@Composable
fun DashboardKpiCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconBgColor: Color,
    iconTintColor: Color,
    extraRightText: String? = null,
    trendText: String? = null
) {
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
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                color = Color.DarkGray
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                if (trendText != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = trendText,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EstadoCitaMiniCard(
    modifier: Modifier = Modifier,
    numero: String,
    estado: String,
    numeroColor: Color,
    bgColor: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                color = Color.DarkGray,
                textAlign = TextAlign.Center
            )
        }
    }
}

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
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Fila superior: Avatar, Nombre/Raza, Etiqueta de Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sustituye el Icon por tu Image/AsyncImage cuando tengas las fotos
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = nombre,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = razaEdad,
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(estadoBgColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
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
            Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Fila de hora y tipo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = "Hora",
                        modifier = Modifier.size(14.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = hora,
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }
                Text(
                    text = tipoConsulta,
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Descripción
            Text(
                text = descripcion,
                fontSize = 13.sp,
                color = Color.DarkGray,
                lineHeight = 18.sp
            )
        }
    }
}