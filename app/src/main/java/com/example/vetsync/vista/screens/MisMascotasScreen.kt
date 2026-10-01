package com.example.vetsync.vista.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vetsync.controlador.MascotaControlador
import com.example.vetsync.modelo.Mascota
import com.example.vetsync.modelo.SesionUsuario
import com.example.vetsync.vista.components.LoadingDots
import com.example.vetsync.vista.theme.*
import com.example.vetsync.vista.DetalleMascotaScreen
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import com.example.vetsync.modelo.ImagenUtils
@Composable
fun MisMascotasScreen(
    controladorMascota: MascotaControlador = remember { MascotaControlador() },
    onAgregarMascotaClick: () -> Unit = {},
    onAgendarCitaClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var listaMascotas by remember { mutableStateOf<List<Mascota>>(emptyList()) }
    var cargandoMascotas by remember { mutableStateOf(true) }

    var mascotaSeleccionada by remember { mutableStateOf<Mascota?>(null) }

    //REGRESAR A LA PAGINA ANTERIOR
    BackHandler(enabled = mascotaSeleccionada != null) {
        mascotaSeleccionada = null
    }

    LaunchedEffect(SesionUsuario.idUsuario) {
        cargandoMascotas = true
        controladorMascota.obtenerMascotasUsuario(
            idUsuario = SesionUsuario.idUsuario,
            onSuccess = { mascotas ->
                listaMascotas = mascotas
                cargandoMascotas = false
            },
            onError = { error ->
                cargandoMascotas = false
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (mascotaSeleccionada != null) {
        DetalleMascotaScreen(
            mascota = mascotaSeleccionada!!,
            onBackClick = { mascotaSeleccionada = null },
            onMascotaActualizada = { mascotaConNuevaFoto ->
                mascotaSeleccionada = mascotaConNuevaFoto
            },
            onAgendarCitaClick = onAgendarCitaClick
        )
    } else {
        // Si es null, mostramos el listado de "Mis mascotas"
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Mis mascotas",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E221F)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Gestiona el perfil y salud de tus compañeros.",
                    fontSize = 14.sp,
                    color = GrisTextoSecundario
                )

                Spacer(modifier = Modifier.height(22.dp))

                if (cargandoMascotas) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingDots()
                    }
                } else if (listaMascotas.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aún no tienes mascotas registradas.",
                            fontSize = 14.sp,
                            color = GrisTextoSecundario
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                        listaMascotas.forEachIndexed { index, mascota ->
                            MascotaPerfilCompletoCard(
                                mascota = mascota,
                                destacada = index == 0,
                                onVerPerfilClick = {
                                    // Al tocar el botón "Ver perfil" o la tarjeta, abre DetalleMascotaScreen
                                    mascotaSeleccionada = mascota
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(88.dp))
            }

            // Botón flotante (+)
            FloatingActionButton(
                onClick = onAgregarMascotaClick,
                containerColor = VerdeVetSync,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 16.dp)
                    .size(58.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar mascota",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun MascotaPerfilCompletoCard(
    mascota: Mascota,
    destacada: Boolean = false,
    onVerPerfilClick: () -> Unit = {}
) {


    val textoEdad = if (mascota.edad.toString() == "1") "1 año" else "${mascota.edad} años"
    val textoPeso = if (mascota.peso % 1.0 == 0.0) {
        "${mascota.peso.toInt()} kg"
    } else {
        "${mascota.peso} kg"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onVerPerfilClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFEEECE8)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            val bitmapMascota = remember(mascota.fotoUrl) { ImagenUtils.base64ABitmap(mascota.fotoUrl) }
//            if (destacada) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(4.dp)
//                        .background(Color(0xFFD0DBD2))
//                )
//            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF2ECE4)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (bitmapMascota != null) {
                            Image(
                                bitmap = bitmapMascota,
                                contentDescription = mascota.nombre,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
//                            Text(text = emoji, fontSize = 34.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = mascota.nombre,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E221F)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            //Text(text = "", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${mascota.especie} • ${mascota.raza}",
                                fontSize = 14.sp,
                                color = GrisTextoSecundario
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                HorizontalDivider(
                    color = Color(0xFFF4F2EE),
                    thickness = 1.dp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "EDAD",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = GrisTextoSecundario,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = textoEdad,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E221F)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PESO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = GrisTextoSecundario,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = textoPeso,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E221F)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ESTADO",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = GrisTextoSecundario,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF1F7F2)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = VerdeVetSync,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Todo al día",
                            fontSize = 13.sp,
                            color = Color(0xFF4A5D4E)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Botón actualizado: Ver perfil
                OutlinedButton(
                    onClick = onVerPerfilClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFE5E2DC)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = VerdeVetSync
                    )
                ) {
                    Text(
                        text = "Ver perfil",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = VerdeVetSync
                    )
                }
            }
        }
    }
}