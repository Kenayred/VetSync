package com.example.vetsync.vista.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.vetsync.modelo.ImagenUtils

// --------------------------------------------------
// TARJETA DE PRÓXIMA CITA
// --------------------------------------------------

@Composable
fun ProximaCitaCard(
    onVerDetallesClick: () -> Unit = {}
) {

    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            colorScheme.outlineVariant
        )
    ) {

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 30.dp, y = (-30).dp)
                    .clip(CircleShape)
                    .background(
                        colorScheme.primaryContainer.copy(alpha = 0.5f)
                    )
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

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "Próxima cita",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = colorScheme.onSurface
                        )
                    }

                    Surface(
                        color = colorScheme.tertiaryContainer,
                        contentColor = colorScheme.onTertiaryContainer,
                        shape = RoundedCornerShape(50)
                    ) {

                        Text(
                            text = "Pendiente",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 4.dp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(CircleShape)
                        .background(colorScheme.surfaceVariant)
                        .border(
                            1.5.dp,
                            colorScheme.primary.copy(alpha = 0.6f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "🐶",
                        fontSize = 40.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Revisión General - Max",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = null,
                        tint = colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "Mañana, 10:00 AM",
                        fontSize = 12.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "Clinica Central (Sala 2)",
                        fontSize = 12.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onVerDetallesClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary,
                        contentColor = colorScheme.onPrimary
                    )
                ) {

                    Text(
                        text = "Ver detalles",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// --------------------------------------------------
// TARJETA DE MASCOTA DEL DASHBOARD
// --------------------------------------------------

@Composable
fun MascotaItemCard(
    nombre: String,
    detalle: String,
    fotoUrl: String = "",
    seleccionada: Boolean,
    modifier: Modifier = Modifier
) {

    val colorScheme = MaterialTheme.colorScheme

    val bitmapMascota = remember(fotoUrl) {
        ImagenUtils.base64ABitmap(fotoUrl)
    }

    Card(
        modifier = modifier.height(84.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (seleccionada) {
                colorScheme.primary.copy(alpha = 0.7f)
            } else {
                colorScheme.outlineVariant
            }
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
                    .background(colorScheme.surfaceVariant)
                    .border(
                        width = if (seleccionada) 1.5.dp else 0.dp,
                        color = if (seleccionada) {
                            colorScheme.primary
                        } else {
                            Color.Transparent
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

                if (bitmapMascota != null) {

                    Image(
                        bitmap = bitmapMascota,
                        contentDescription = nombre,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = nombre,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )

                Text(
                    text = detalle,
                    fontSize = 12.sp,
                    color = colorScheme.onSurfaceVariant,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

// --------------------------------------------------
// SECCIÓN DE ACCESOS RÁPIDOS
// --------------------------------------------------

@Composable
fun AccesosRapidosSection(
    onAgendarCitaClick: () -> Unit,
    onMisMascotasClick: () -> Unit,
    onHistorialClick: () -> Unit
) {

    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.secondaryContainer
        ),
        border = BorderStroke(
            1.dp,
            colorScheme.outlineVariant
        )
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
                color = colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(bottom = 2.dp)
            )

            // Agendar cita.

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable {
                        onAgendarCitaClick()
                    },
                shape = RoundedCornerShape(10.dp),
                color = colorScheme.primaryContainer,
                contentColor = colorScheme.onPrimaryContainer
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
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Agendar cita",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Mis mascotas.

            AccesoRapidoBlancoItem(
                titulo = "Mis mascotas",
                icono = Icons.Default.Favorite,
                onClick = onMisMascotasClick
            )

            // Historial clínico.

            AccesoRapidoBlancoItem(
                titulo = "Mi historial",
                icono = Icons.Default.Refresh,
                onClick = onHistorialClick
            )
        }
    }
}

// --------------------------------------------------
// ELEMENTO INDIVIDUAL DE ACCESO RÁPIDO
// --------------------------------------------------

@Composable
fun AccesoRapidoBlancoItem(
    titulo: String,
    icono: ImageVector,
    onClick: () -> Unit
) {

    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(10.dp),
        color = colorScheme.surface,
        contentColor = colorScheme.onSurface,
        border = BorderStroke(
            1.dp,
            colorScheme.outlineVariant
        )
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
                tint = colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = titulo,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = colorScheme.onSurface
            )
        }
    }
}

// --------------------------------------------------
// TARJETA DE CITA POR CONFIRMAR
// --------------------------------------------------

@Composable
fun CitaPorConfirmarCard(
    onConfirmarClick: () -> Unit = {}
) {

    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.errorContainer
        ),
        border = BorderStroke(
            1.dp,
            colorScheme.error.copy(alpha = 0.25f)
        )
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
                tint = colorScheme.onErrorContainer,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {

                Text(
                    text = "Cita por confirmar",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onErrorContainer
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Tienes una cita pendiente de confirmación para el día Viernes, 15 de Oct.",
                    fontSize = 12.sp,
                    color = colorScheme.onErrorContainer,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Confirmar ahora",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.primary,
                    modifier = Modifier.clickable {
                        onConfirmarClick()
                    }
                )
            }
        }
    }
}