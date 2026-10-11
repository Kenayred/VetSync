package com.example.vetsync.vista.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vetsync.vista.theme.*

@Composable
fun CitasScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoCrema)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Mis citas",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E221F)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Consulta y gestiona tus citas veterinarias.",
            fontSize = 14.sp,
            color = GrisTextoSecundario
        )

        Spacer(modifier = Modifier.height(24.dp))

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
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Outlined.DateRange,
                    contentDescription = "Agenda de citas",
                    tint = VerdeVetSync,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Tu agenda veterinaria",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E221F)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Aquí podrás consultar tus citas y sus estados. " +
                            "La reserva y gestión de citas se implementarán " +
                            "en el siguiente avance.",
                    fontSize = 14.sp,
                    color = GrisTextoSecundario,
                    lineHeight = 21.sp
                )
            }
        }
    }
}