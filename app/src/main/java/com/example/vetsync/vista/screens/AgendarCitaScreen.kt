package com.example.vetsync.vista.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vetsync.modelo.Mascota
import com.example.vetsync.vista.theme.*

@Composable
fun AgendarCitaScreen(
    mascota: Mascota? = null,
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoCrema)
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
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E221F)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
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
                    contentDescription = "Agenda veterinaria",
                    tint = VerdeVetSync,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (mascota != null) {
                        "Cita para ${mascota.nombre}"
                    } else {
                        "Agenda una visita para tu mascota"
                    },
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E221F)
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (mascota != null) {
                    Text(
                        text = "Mascota seleccionada: ${mascota.nombre}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = VerdeVetSync
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = if (mascota != null) {
                        "Aquí podrás seleccionar el servicio, la fecha y el horario para esta mascota."
                    } else {
                        "Aquí podrás seleccionar una mascota, elegir el servicio veterinario y consultar los horarios disponibles."
                    },
                    fontSize = 14.sp,
                    color = GrisTextoSecundario,
                    lineHeight = 21.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "La reserva de citas se habilitará al implementar el módulo correspondiente.",
                    fontSize = 12.sp,
                    color = GrisTextoSecundario,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, VerdeVetSync)
                ) {
                    Text(
                        text = "Volver",
                        color = VerdeVetSync
                    )
                }
            }
        }
    }
}