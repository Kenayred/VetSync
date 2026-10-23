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
fun HistorialClinicoScreen(
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
                text = "Historial clínico",
                fontSize = 23.sp,
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
                    contentDescription = "Historial médico",
                    tint = VerdeVetSync,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (mascota != null) {
                        "Historial de ${mascota.nombre}"
                    } else {
                        "Historial de tus mascotas"
                    },
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E221F)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "En esta sección podrás consultar las consultas veterinarias, los diagnósticos, los tratamientos y las indicaciones registradas.",
                    fontSize = 14.sp,
                    color = GrisTextoSecundario,
                    lineHeight = 21.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Los registros clínicos reales se conectarán cuando se implemente el módulo de historial.",
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