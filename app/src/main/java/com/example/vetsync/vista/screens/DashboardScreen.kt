package com.example.vetsync.vista.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vetsync.vista.*
import com.example.vetsync.vista.theme.*
import com.example.vetsync.vista.components.MascotaItemCard
import com.example.vetsync.vista.components.AccesosRapidosSection
import com.example.vetsync.vista.components.CitaPorConfirmarCard
import com.example.vetsync.modelo.SesionUsuario

@Composable
fun DashboardScreen(
    onAgregarMascotaClick: () -> Unit = {},
    onAgendarCitaClick: () -> Unit = {},
    onMisMascotasClick: () -> Unit = {},
    onHistorialClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val usuario = SesionUsuario.usuarioActual
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Hola, ${SesionUsuario.primerNombre}",
            fontSize = 34.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF1E221F)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Cuida de quienes siempre están contigo.",
            fontSize = 14.sp,
            color = GrisTextoSecundario
        )

        Spacer(modifier = Modifier.height(20.dp))

        //ProximaCitaCard()

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mis mascotas",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E221F)
            )
            Text(
                text = "Ver todas",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = VerdeVetSync,
                modifier = Modifier.clickable { onMisMascotasClick() }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MascotaItemCard(
                nombre = "Luna",
                detalle = "Gato • 3\naños",
                emojiPlaceholder = "🐱",
                seleccionada = false,
                modifier = Modifier.weight(1f)
            )
            MascotaItemCard(
                nombre = "Max",
                detalle = "Perro • 1 año",
                emojiPlaceholder = "🐶",
                seleccionada = true,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onAgregarMascotaClick() },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal)
        ) {
            Text("Agregar Mascota", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        AccesosRapidosSection(
            onAgendarCitaClick = onAgendarCitaClick,
            onMisMascotasClick = onMisMascotasClick,
            onHistorialClick = onHistorialClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        CitaPorConfirmarCard()

        Spacer(modifier = Modifier.height(20.dp))
    }
}