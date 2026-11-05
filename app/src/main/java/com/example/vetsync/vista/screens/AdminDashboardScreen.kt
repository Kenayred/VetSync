package com.example.vetsync.vista.screens

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
import com.example.vetsync.R // Asegúrate de ajustar este import a tu proyecto

@Composable
fun AdminDashboardScreen(
    modifier: Modifier = Modifier
) {
    // Color de fondo general extraído del diseño
    val backgroundColor = Color(0xFFFDFCF9)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Resumen del Día",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tarjeta: Citas Totales
        DashboardKpiCard(
            title = "Citas Totales",
            value = "24",
            icon = Icons.Default.DateRange,
            iconBgColor = Color(0xFFE2EFE7),
            iconTintColor = Color(0xFF4A7D5E),
            extraRightText = "Hoy",
            trendText = "~12%"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tarjeta: Ingresos Estimados
        DashboardKpiCard(
            title = "Ingresos Estimados",
            value = "$1,250",
            icon = Icons.Outlined.AccountBalanceWallet,
            iconBgColor = Color(0xFFEBCBB9),
            iconTintColor = Color(0xFF915133)
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
                numero = "12",
                estado = "Completadas",
                numeroColor = Color(0xFF2E5B4B),
                bgColor = Color(0xFFF5EFEB)
            )
            EstadoCitaMiniCard(
                modifier = Modifier.weight(1f),
                numero = "8",
                estado = "Pendientes",
                numeroColor = Color(0xFFB57B22),
                bgColor = Color(0xFFFDF8EA)
            )
            EstadoCitaMiniCard(
                modifier = Modifier.weight(1f),
                numero = "4",
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

        PacienteCard(
            nombre = "Max",
            razaEdad = "Golden Retriever • 2 años",
            estado = "Pendiente",
            estadoBgColor = Color(0xFFFDF0D5),
            estadoTextColor = Color(0xFFB57B22),
            hora = "10:30 AM",
            tipoConsulta = "Consulta General",
            descripcion = "Revisión anual y aplicación de vacunas múltiples. Presenta leve picor en oreja izquierda."
        )

        Spacer(modifier = Modifier.height(12.dp))

        PacienteCard(
            nombre = "Luna",
            razaEdad = "Siamés • 4 años",
            estado = "Confirmada",
            estadoBgColor = Color(0xFFD5E8D4), // Verde claro
            estadoTextColor = Color(0xFF2E5B4B),
            hora = "11:15 AM",
            tipoConsulta = "Seguimiento",
            descripcion = "Revisión de suturas post-operación de esterilización. Estado general favorable."
        )

        Spacer(modifier = Modifier.height(80.dp)) // Espacio para que el BottomBar no tape el último elemento
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