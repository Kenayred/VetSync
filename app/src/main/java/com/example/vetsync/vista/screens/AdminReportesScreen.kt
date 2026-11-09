package com.example.vetsync.vista.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AdminReportesScreen(
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFDFCF9))
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            ),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Card(
            modifier = Modifier.fillMaxWidth(),

            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),

            shape = RoundedCornerShape(18.dp),

            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Color(0xFFE5E5E5)
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Ícono ilustrativo.

                Text(
                    text = "📊",
                    fontSize = 48.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Reportes y estadísticas",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    color = Color(0xFFE2EFE7),
                    shape = RoundedCornerShape(20.dp)
                ) {

                    Text(
                        text = "Disponible en el Avance 3",
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 8.dp
                        ),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2E5B4B)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Esta sección estará disponible en el Avance 3 del proyecto.",

                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center,
                    lineHeight = 21.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "En la entrega final se incorporarán los reportes y estadísticas para consultar la información administrativa de VetSync.",

                    fontSize = 13.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
        }
    }
}