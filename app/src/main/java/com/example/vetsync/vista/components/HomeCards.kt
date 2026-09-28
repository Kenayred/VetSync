package com.example.vetsync.vista.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Paleta de colores compartida en el módulo de Home
val FondoCrema = Color(0xFFFAF8F5)
val VerdeVetSync = Color(0xFF43614A)
val VerdeClaroIcono = Color(0xFFD6E6D9)
val VerdeBotonAcceso = Color(0xFF88A68E)
val BeigePendiente = Color(0xFFEADCD5)
val TextoPendiente = Color(0xFF6E4E42)
val FondoAccesos = Color(0xFFF5EFEA)
val FondoAlertaCita = Color(0xFFFCF2EE)
val RojoAlerta = Color(0xFFC92A2A)
val GrisTextoSecundario = Color(0xFF5C6460)

@Composable
fun ProximaCitaCard(
    onVerDetallesClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E8E5))
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 30.dp, y = (-30).dp)
                    .clip(CircleShape)
                    .background(VerdeClaroIcono.copy(alpha = 0.5f))
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(VerdeClaroIcono),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = VerdeVetSync,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Próxima cita",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E221F)
                        )
                    }

                    Surface(
                        color = BeigePendiente,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "Pendiente",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextoPendiente,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEFECE6))
                        .border(1.5.dp, VerdeVetSync.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🐶", fontSize = 40.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Revisión General - Max",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E221F)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = null,
                        tint = GrisTextoSecundario,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Mañana, 10:00 AM",
                        fontSize = 12.sp,
                        color = GrisTextoSecundario
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = GrisTextoSecundario,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Clinica Central (Sala 2)",
                        fontSize = 12.sp,
                        color = GrisTextoSecundario
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onVerDetallesClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeVetSync)
                ) {
                    Text(
                        text = "Ver detalles",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun MascotaItemCard(
    nombre: String,
    detalle: String,
    emojiPlaceholder: String,
    seleccionada: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(84.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(
            width = 1.dp,
            color = if (seleccionada) VerdeVetSync.copy(alpha = 0.6f) else Color(0xFFE5E8E5)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEFECE6))
                    .border(
                        width = if (seleccionada) 1.5.dp else 0.dp,
                        color = if (seleccionada) VerdeVetSync else Color.Transparent,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emojiPlaceholder, fontSize = 26.sp)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = nombre,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E221F)
                )
                Text(
                    text = detalle,
                    fontSize = 12.sp,
                    color = GrisTextoSecundario,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
fun AccesosRapidosSection(
    onAgendarCitaClick: () -> Unit,
    onMisMascotasClick: () -> Unit,
    onHistorialClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FondoAccesos),
        border = BorderStroke(1.dp, Color(0xFFE5DFD9))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Accesos Rápidos",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E221F),
                modifier = Modifier.padding(bottom = 2.dp)
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable { onAgendarCitaClick() },
                shape = RoundedCornerShape(10.dp),
                color = VerdeBotonAcceso
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = null,
                        tint = Color(0xFF1B3522),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Agendar cita",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1B3522)
                    )
                }
            }

            AccesoRapidoBlancoItem(
                titulo = "Mis mascotas",
                icono = Icons.Default.Favorite,
                onClick = onMisMascotasClick
            )

            AccesoRapidoBlancoItem(
                titulo = "Mi historial",
                icono = Icons.Default.Refresh,
                onClick = onHistorialClick
            )
        }
    }
}

@Composable
fun AccesoRapidoBlancoItem(
    titulo: String,
    icono: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE5DFD9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = Color(0xFF1E1E1E),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = titulo,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E221F)
            )
        }
    }
}

@Composable
fun CitaPorConfirmarCard(
    onConfirmarClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FondoAlertaCita),
        border = BorderStroke(1.dp, Color(0xFFF3DFD8))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = RojoAlerta,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Cita por confirmar",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2B2220)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tienes una cita pendiente de confirmación para el día Viernes, 15 de Oct.",
                    fontSize = 12.sp,
                    color = GrisTextoSecundario,
                    lineHeight = 17.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Confirmar ahora",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VerdeVetSync,
                    modifier = Modifier.clickable { onConfirmarClick() }
                )
            }
        }
    }
}