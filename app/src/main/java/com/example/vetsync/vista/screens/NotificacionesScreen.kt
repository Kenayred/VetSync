package com.example.vetsync.vista.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
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
fun NotificacionesScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoCrema)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Notificaciones",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E221F)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Mantente al tanto del cuidado de tus mascotas.",
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
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notificaciones",
                    tint = VerdeVetSync,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Tus avisos aparecerán aquí",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E221F)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "En esta sección podrás consultar recordatorios " +
                            "de citas y actualizaciones relacionadas con " +
                            "tus mascotas. Las notificaciones se conectarán " +
                            "en una etapa posterior.",
                    fontSize = 14.sp,
                    color = GrisTextoSecundario,
                    lineHeight = 21.sp
                )
            }
        }
    }
}