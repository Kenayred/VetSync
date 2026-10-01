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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vetsync.vista.*
import com.example.vetsync.vista.theme.*
import com.example.vetsync.vista.components.MascotaItemCard
import com.example.vetsync.modelo.Mascota
import com.example.vetsync.vista.components.AccesosRapidosSection
import com.example.vetsync.vista.components.CitaPorConfirmarCard
import com.example.vetsync.modelo.SesionUsuario
import com.example.vetsync.controlador.MascotaControlador
import com.example.vetsync.vista.components.LoadingDots
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items


@Composable
fun DashboardScreen(
    controladorMascota: MascotaControlador = remember { MascotaControlador() },
    onAgregarMascotaClick: () -> Unit = {},
    onMascotaClick: (Mascota) -> Unit = {},
    onAgendarCitaClick: () -> Unit = {},
    onMisMascotasClick: () -> Unit = {},
    onHistorialClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val usuario = SesionUsuario.usuarioActual
    var listaMascotas by remember { mutableStateOf<List<Mascota>>(emptyList()) }
    var cargandoMascotas by remember { mutableStateOf(true) }
    var mascotaSeleccionadaId by remember { mutableIntStateOf(-1) }

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

        LaunchedEffect(SesionUsuario.idUsuario) {
            cargandoMascotas = true
            controladorMascota.obtenerMascotasUsuario(
                idUsuario = SesionUsuario.idUsuario,
                onSuccess = { mascotas ->
                    listaMascotas = mascotas
                    if (mascotas.isNotEmpty() && mascotaSeleccionadaId == -1) {
                        mascotaSeleccionadaId = mascotas.first().id
                    }
                    cargandoMascotas = false
                },
                onError = { error ->
                    cargandoMascotas = false
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }
            )
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

//            if (cargandoMascotas) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(84.dp),
//                    contentAlignment = Alignment.Center
//                ) {
//                    LoadingDots()
//                }
//            } else
                if (listaMascotas.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aún no tienes mascotas registradas.",
                        fontSize = 13.sp,
                        color = GrisTextoSecundario
                    )
                }
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(listaMascotas) { mascot ->
                        val textoAnios = if (mascot.edad.toString() == "1") "1 año" else "${mascot.edad} años"

                        MascotaItemCard(
                            nombre = mascot.nombre,
                            detalle = "${mascot.especie} • $textoAnios",
                            fotoUrl = mascot.fotoUrl,
                            seleccionada = mascot.id == mascotaSeleccionadaId,
                            modifier = Modifier
                                .width(165.dp)
                                .clickable {
                                    mascotaSeleccionadaId = mascot.id
                                    onMascotaClick(mascot)
                                }
                        )
                    }
                }
            }

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