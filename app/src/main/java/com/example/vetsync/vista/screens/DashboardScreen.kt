package com.example.vetsync.vista.screens

import android.widget.Toast

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.vetsync.vista.components.MascotaItemCard
import com.example.vetsync.vista.components.AccesosRapidosSection
import com.example.vetsync.vista.components.LoadingDots
import com.example.vetsync.modelo.Mascota
import com.example.vetsync.modelo.SesionUsuario
import com.example.vetsync.controlador.MascotaControlador

@Composable
fun DashboardScreen(
    controladorMascota: MascotaControlador = remember {
        MascotaControlador()
    },
    onAgregarMascotaClick: () -> Unit = {},
    onMascotaClick: (Mascota) -> Unit = {},
    onAgendarCitaClick: () -> Unit = {},
    onMisMascotasClick: () -> Unit = {},
    onHistorialClick: () -> Unit = {}
) {

    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme

    var listaMascotas by remember {
        mutableStateOf<List<Mascota>>(emptyList())
    }

    var cargandoMascotas by remember {
        mutableStateOf(true)
    }

    var errorCargarMascotas by remember {
        mutableStateOf(false)
    }

    var mascotaSeleccionadaId by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {

        Text(
            text = "Hola, ${SesionUsuario.primerNombre}",
            fontSize = 34.sp,
            fontWeight = FontWeight.Normal,
            color = colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Cuida de quienes siempre están contigo.",
            fontSize = 14.sp,
            color = colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        Spacer(modifier = Modifier.height(24.dp))

        // --------------------------------------------------
        // MIS MASCOTAS
        // --------------------------------------------------

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Mis mascotas",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = colorScheme.onSurface
            )

            Text(
                text = "Ver todas",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = colorScheme.primary,
                modifier = Modifier.clickable {
                    onMisMascotasClick()
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --------------------------------------------------
        // CARGAR MASCOTAS
        // --------------------------------------------------

        LaunchedEffect(SesionUsuario.idUsuario) {

            cargandoMascotas = true
            errorCargarMascotas = false

            controladorMascota.obtenerMascotasUsuario(
                idUsuario = SesionUsuario.idUsuario,

                onSuccess = { mascotas ->

                    listaMascotas = mascotas

                    mascotaSeleccionadaId =
                        mascotas.firstOrNull()?.id.orEmpty()

                    cargandoMascotas = false
                },

                onError = { error ->

                    errorCargarMascotas = true
                    cargandoMascotas = false

                    Toast.makeText(
                        context,
                        error,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }

        Column(
            modifier = Modifier.padding(
                horizontal = 20.dp,
                vertical = 8.dp
            )
        ) {

            Spacer(modifier = Modifier.height(12.dp))

            when {

                cargandoMascotas -> {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(84.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        LoadingDots()
                    }
                }

                errorCargarMascotas -> {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(84.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "No se pudieron cargar tus mascotas. Intenta nuevamente.",
                            fontSize = 13.sp,
                            color = colorScheme.error
                        )
                    }
                }

                listaMascotas.isEmpty() -> {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(84.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "Aún no tienes mascotas registradas.",
                            fontSize = 13.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        items(listaMascotas) { mascota ->

                            val textoAnios = if (mascota.edad == "1") {
                                "1 año"
                            } else {
                                "${mascota.edad} años"
                            }

                            MascotaItemCard(
                                nombre = mascota.nombre,
                                detalle = "${mascota.especie} • $textoAnios",
                                fotoUrl = mascota.fotoUrl,
                                seleccionada = mascota.id == mascotaSeleccionadaId,
                                modifier = Modifier
                                    .width(165.dp)
                                    .clickable {

                                        mascotaSeleccionadaId = mascota.id
                                        onMascotaClick(mascota)
                                    }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --------------------------------------------------
        // AGREGAR MASCOTA
        // --------------------------------------------------

        Button(
            onClick = {
                onAgregarMascotaClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary
            )
        ) {

            Text(
                text = "Agregar Mascota",
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --------------------------------------------------
        // ACCESOS RÁPIDOS
        // --------------------------------------------------

        AccesosRapidosSection(
            onAgendarCitaClick = onAgendarCitaClick,
            onMisMascotasClick = onMisMascotasClick,
            onHistorialClick = onHistorialClick
        )

        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.height(20.dp))
    }
}