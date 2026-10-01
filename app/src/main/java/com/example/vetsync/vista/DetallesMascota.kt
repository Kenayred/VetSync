package com.example.vetsync.vista

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vetsync.modelo.SesionUsuario
import com.example.vetsync.vista.components.*
import com.example.vetsync.vista.theme.*
import com.example.vetsync.modelo.Mascota
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.runtime.remember
import com.example.vetsync.modelo.ImagenUtils
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.vetsync.controlador.MascotaControlador

@Composable
fun DetalleMascotaScreen(
    mascota: Mascota,
    controlador: MascotaControlador = remember { MascotaControlador() },
    onBackClick: () -> Unit = {},
    onMascotaActualizada: (Mascota) -> Unit = {},
    onAgendarCitaClick: () -> Unit = {},
    onVerHistorialClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val usuario = SesionUsuario.usuarioActual

    // Estado local para que la foto cambie al instante en pantalla
    var fotoBase64Actual by remember(mascota.id) { mutableStateOf(mascota.fotoUrl) }
    val bitmapMascota = remember(fotoBase64Actual) { ImagenUtils.base64ABitmap(fotoBase64Actual) }

    // Lanzador para abrir la galería y actualizar en Firebase
    val cambiarFotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val nuevaBase64 = ImagenUtils.uriABase64(context, uri)
            if (nuevaBase64.isNotBlank()) {
                controlador.actualizarFotoMascota(
                    mascota = mascota,
                    nuevaFotoBase64 = nuevaBase64,
                    onSuccess = { mascotaActualizada ->
                        fotoBase64Actual = nuevaBase64
                        onMascotaActualizada(mascotaActualizada)
                        Toast.makeText(context, "Foto actualizada con éxito", Toast.LENGTH_SHORT).show()
                    },
                    onError = { error ->
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

//    val emojiMascota = when (mascota.especie.lowercase()) {
//        "perro", "canino" -> "🐶"
//        "gato", "felino" -> "🐱"
//        "ave" -> "🐦"
//        "conejo" -> "🐰"
//        else -> "🐾"
//    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoCrema)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        // Botón superior para regresar al listado de Mis mascotas
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onBackClick() }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Regresar",
                tint = VerdeVetSync,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Volver a Mis mascotas",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = VerdeVetSync
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 1. Imagen principal de la mascota (Tocar la foto o el botón editar abre la galería)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFFEADFD3))
                .clickable { cambiarFotoLauncher.launch("image/*") },
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
               // Text(text = emojiMascota, fontSize = 96.sp)
            }

            Surface(
                color = Color.White.copy(alpha = 0.92f),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
                    .clickable { cambiarFotoLauncher.launch("image/*") }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Cambiar foto",
                        tint = VerdeVetSync,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Cambiar foto",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = VerdeVetSync
                    )
                }
            }

            Surface(
                color = Color.White.copy(alpha = 0.92f),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(VerdeVetSync)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Activo",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = VerdeVetSync
                    )
                }
            }
        }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = mascota.nombre.ifBlank { "Max" },
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E221F)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🐾", fontSize = 13.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = mascota.raza.ifBlank { "Golden Retriever" },
                    fontSize = 14.sp,
                    color = GrisTextoSecundario
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onAgendarCitaClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = VerdeVetSync)
            ) {
                Icon(
                    imageVector = Icons.Outlined.DateRange,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Agendar cita",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onVerHistorialClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, Color(0xFFE0DBD4)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF2E3330)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(17.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ver historial",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            val textoAnios = if (mascota.edad.toString() == "1") "1 año" else "${mascota.edad} años"

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AtributoMascotaCard(
                    titulo = "Peso",
                    valor = "${mascota.peso} kg",
                    icono = Icons.Outlined.Info,
                    fondoIcono = VerdeClaroIcono,
                    tintIcono = VerdeVetSync,
                    modifier = Modifier.weight(1f)
                )
                AtributoMascotaCard(
                    titulo = "Edad",
                    valor = textoAnios,
                    icono = Icons.Outlined.DateRange,
                    fondoIcono = NaranjaSuaveIcono,
                    tintIcono = NaranjaIcono,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AtributoMascotaCard(
                    titulo = "Sexo",
                    valor = mascota.sexo.ifBlank { "Macho" },
                    icono = Icons.Outlined.FavoriteBorder,
                    fondoIcono = Color(0xFFEAEDE9),
                    tintIcono = VerdeVetSync,
                    modifier = Modifier.weight(1f)
                )
                AtributoMascotaCard(
                    titulo = "Especie",
                    valor = mascota.especie.ifBlank { "Canino" },
                    icono = Icons.Outlined.Star,
                    fondoIcono = VerdeClaroIcono,
                    tintIcono = VerdeVetSync,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            InformacionGeneralSection(
                propietario = usuario?.nombre ?: "Laura Méndez",
                telefono = usuario?.telefono?.ifBlank { "+503 6000 1234" } ?: "+503 6000 1234",
                colorMarcas = mascota.colorMarcas.ifBlank { "Sin especificar" },
                alergiasObservaciones = mascota.observaciones.ifBlank { "Ninguna registrada" }
            )

            Spacer(modifier = Modifier.height(20.dp))

            HistorialMedicoSection(onVerTodoClick = onVerHistorialClick)

            Spacer(modifier = Modifier.height(20.dp))

            VacunasSection()

            Spacer(modifier = Modifier.height(20.dp))

            ProximasCitasMascotaSection()

            Spacer(modifier = Modifier.height(20.dp))

            PlanPreventivoCard()

            Spacer(modifier = Modifier.height(28.dp))
        }
    }


@Composable
fun DetalleMascotaTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(FondoCrema)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(34.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Regresar",
                tint = VerdeVetSync
            )
        }

        Text(
            text = "VetSync",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = VerdeVetSync
        )

        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(VerdeClaroIcono)
                .border(1.dp, VerdeVetSync.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Perfil",
                tint = VerdeVetSync,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun AtributoMascotaCard(
    titulo: String,
    valor: String,
    icono: ImageVector,
    fondoIcono: Color,
    tintIcono: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFEEECE8))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(fondoIcono),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = titulo,
                    tint = tintIcono,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = titulo,
                fontSize = 11.sp,
                color = GrisTextoSecundario
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = valor,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E221F)
            )
        }
    }
}

@Composable
fun InformacionGeneralSection(
    propietario: String,
    telefono: String,
    colorMarcas: String,
    alergiasObservaciones: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFEEECE8))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = VerdeVetSync,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Información general",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E221F)
                )
            }

            DatoGeneralItem(label = "Propietario", valor = propietario)
            DatoGeneralItem(label = "Teléfono", valor = telefono)
            DatoGeneralItem(label = "Color / Marcas", valor = colorMarcas)
            DatoGeneralItem(label = "Alergias / Observaciones", valor = alergiasObservaciones)
        }
    }
}

@Composable
fun DatoGeneralItem(label: String, valor: String) {
    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            color = GrisTextoSecundario
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = valor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1E221F)
        )
    }
}

@Composable
fun HistorialMedicoSection(onVerTodoClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFEEECE8))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = null,
                        tint = VerdeVetSync,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Historial médico",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E221F)
                    )
                }
                Text(
                    text = "Ver todo",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VerdeVetSync,
                    modifier = Modifier.clickable { onVerTodoClick() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ItemHistorialMedico(
                dia = "12",
                mes = "OCT",
                titulo = "Revisión anual",
                descripcion = "Examen físico general sin alteraciones. Constantes vitales...",
                doctor = "Dr. Martínez"
            )

            Spacer(modifier = Modifier.height(16.dp))

            ItemHistorialMedico(
                dia = "05",
                mes = "JUN",
                titulo = "Consulta dermatológica",
                descripcion = "Prurito intenso en zona lumbar. Diagnóstico: Dermatitis alérgica...",
                doctor = "Dra. Gómez"
            )
        }
    }
}

@Composable
fun ItemHistorialMedico(
    dia: String,
    mes: String,
    titulo: String,
    descripcion: String,
    doctor: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            Text(
                text = dia,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeVetSync
            )
            Text(
                text = mes,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = GrisTextoSecundario
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = titulo,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E221F)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = descripcion,
                fontSize = 12.sp,
                color = GrisTextoSecundario,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                color = Color(0xFFF4F2EE),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = doctor,
                    fontSize = 10.sp,
                    color = Color(0xFF4A4E4B),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
fun VacunasSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFEEECE8))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = VerdeVetSync,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Vacunas",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E221F)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            ItemVacunaCard(
                nombre = "Rabia",
                fecha = "Aplicada: 12/10/2023",
                estado = "Al día",
                esAlerta = false
            )
            ItemVacunaCard(
                nombre = "Polivalente (DHPPi)",
                fecha = "Vence: 15/11/2023",
                estado = "Próxima a\nvencer",
                esAlerta = true
            )
            ItemVacunaCard(
                nombre = "Tos de las perreras",
                fecha = "Aplicada: 05/06/2023",
                estado = "Al día",
                esAlerta = false
            )
        }
    }
}

@Composable
fun ItemVacunaCard(
    nombre: String,
    fecha: String,
    estado: String,
    esAlerta: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFAF9F7),
        border = BorderStroke(1.dp, Color(0xFFEEECE8))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (esAlerta) Icons.Outlined.Warning else Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = if (esAlerta) NaranjaIcono else VerdeVetSync,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = nombre,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E221F)
                    )
                    Text(
                        text = fecha,
                        fontSize = 11.sp,
                        color = GrisTextoSecundario
                    )
                }
            }

            Surface(
                color = if (esAlerta) AmarilloAlertaFondo else VerdeAlDiaFondo,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = estado,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (esAlerta) AmarilloAlertaTexto else VerdeVetSync,
                    textAlign = TextAlign.Center,
                    lineHeight = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun ProximasCitasMascotaSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = FondoProximasCitas)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.DateRange,
                    contentDescription = null,
                    tint = VerdeVetSync,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Próximas citas",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E221F)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = "Mañana, 10:00 AM",
                                fontSize = 11.sp,
                                color = GrisTextoSecundario
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Vacunación Polivalente",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E221F)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "👤 Dr. Martínez",
                                fontSize = 11.sp,
                                color = GrisTextoSecundario
                            )
                        }

                        Surface(
                            color = Color(0xFFF2EFE9),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "NOV",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GrisTextoSecundario
                                )
                                Text(
                                    text = "14",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E221F)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { },
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFE0DBD4)),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "Reprogramar",
                                fontSize = 12.sp,
                                color = Color(0xFF2E3330)
                            )
                        }

                        Button(
                            onClick = { },
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = VerdeVetSync),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "Confirmar",
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlanPreventivoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FondoPlanPreventivo),
        border = BorderStroke(1.dp, Color(0xFFF3E3DC))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(NaranjaSuaveIcono),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = NaranjaIcono,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Plan Preventivo Anual",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E221F)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Suscripción activa. Cubre visitas rutinarias y vacunas básicas.",
                    fontSize = 11.sp,
                    color = GrisTextoSecundario,
                    lineHeight = 15.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Ver detalles del plan",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VerdeVetSync
                )
            }
        }
    }
}