package com.example.vetsync.vista

import android.net.Uri
import android.widget.Toast

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.vetsync.controlador.MascotaControlador
import com.example.vetsync.modelo.ImagenUtils
import com.example.vetsync.modelo.Mascota
import com.example.vetsync.modelo.SesionUsuario
import com.example.vetsync.vista.theme.*

@Composable
fun DetalleMascotaScreen(
    mascota: Mascota,
    controlador: MascotaControlador = remember {
        MascotaControlador()
    },
    onBackClick: () -> Unit = {},
    onMascotaActualizada: (Mascota) -> Unit = {},
    onAgendarCitaClick: () -> Unit = {},
    onVerHistorialClick: () -> Unit = {},
    onMascotaEliminada: () -> Unit = {},
    onNavigateToEdit: (Mascota) -> Unit = {}
) {

    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val usuario = SesionUsuario.usuarioActual
    val colorScheme = MaterialTheme.colorScheme

    var mostrarDialogoEliminar by remember {
        mutableStateOf(false)
    }

    // Cambio de fotografía local.

    var fotoBase64Actual by remember(mascota.id) {
        mutableStateOf(mascota.fotoUrl)
    }

    val bitmapMascota = remember(fotoBase64Actual) {
        ImagenUtils.base64ABitmap(fotoBase64Actual)
    }

    val cambiarFotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->

        if (uri != null) {

            val nuevaBase64 = ImagenUtils.uriABase64(
                context,
                uri
            )

            if (nuevaBase64.isNotBlank()) {

                controlador.actualizarFotoMascota(
                    mascota = mascota,
                    nuevaFotoBase64 = nuevaBase64,

                    onSuccess = { mascotaActualizada ->

                        fotoBase64Actual = nuevaBase64
                        onMascotaActualizada(mascotaActualizada)

                        Toast.makeText(
                            context,
                            "Foto actualizada con éxito",
                            Toast.LENGTH_SHORT
                        ).show()
                    },

                    onError = { error ->

                        Toast.makeText(
                            context,
                            error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {

        // --------------------------------------------------
        // REGRESAR AL LISTADO
        // --------------------------------------------------

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onBackClick()
                }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Regresar",
                tint = colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Volver a Mis mascotas",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --------------------------------------------------
        // FOTOGRAFÍA DE LA MASCOTA
        // --------------------------------------------------

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(colorScheme.surfaceVariant)
                .clickable {
                    cambiarFotoLauncher.launch("image/*")
                },
            contentAlignment = Alignment.Center
        ) {

            if (bitmapMascota != null) {

                Image(
                    bitmap = bitmapMascota,
                    contentDescription = mascota.nombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Cambiar fotografía.

            Surface(
                color = colorScheme.surface.copy(alpha = 0.94f),
                contentColor = colorScheme.onSurface,
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
                    .clickable {
                        cambiarFotoLauncher.launch("image/*")
                    }
            ) {

                Row(
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 6.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Cambiar foto",
                        tint = colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "Cambiar foto",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.onSurface
                    )
                }
            }

            // Estado de la mascota.

            Surface(
                color = colorScheme.surface.copy(alpha = 0.94f),
                contentColor = colorScheme.onSurface,
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
            ) {

                Row(
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 4.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(colorScheme.primary)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "Activo",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --------------------------------------------------
        // DATOS PRINCIPALES
        // --------------------------------------------------

        Text(
            text = mascota.nombre.ifBlank {
                "Nombre no registrado"
            },
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "🐾",
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = mascota.raza.ifBlank {
                    "Raza no registrada"
                },
                fontSize = 14.sp,
                color = colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // --------------------------------------------------
        // AGENDAR CITA
        // --------------------------------------------------

        Button(
            onClick = onAgendarCitaClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary
            )
        ) {

            Icon(
                imageVector = Icons.Outlined.DateRange,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Agendar cita",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --------------------------------------------------
        // HISTORIAL CLÍNICO
        // --------------------------------------------------

        OutlinedButton(
            onClick = onVerHistorialClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            shape = RoundedCornerShape(50),
            border = BorderStroke(
                1.dp,
                colorScheme.outline
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = colorScheme.primary
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

        // --------------------------------------------------
        // ATRIBUTOS DE LA MASCOTA
        // --------------------------------------------------

        val textoAnios = if (mascota.edad == "1") {
            "1 año"
        } else {
            "${mascota.edad} años"
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            AtributoMascotaCard(
                titulo = "Peso",
                valor = "${mascota.peso} kg",
                icono = Icons.Outlined.Info,
                fondoIcono = colorScheme.primaryContainer,
                tintIcono = colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f)
            )

            AtributoMascotaCard(
                titulo = "Edad",
                valor = textoAnios,
                icono = Icons.Outlined.DateRange,
                fondoIcono = colorScheme.tertiaryContainer,
                tintIcono = colorScheme.onTertiaryContainer,
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
                valor = mascota.sexo.ifBlank {
                    "No especificado"
                },
                icono = Icons.Outlined.FavoriteBorder,
                fondoIcono = colorScheme.surfaceVariant,
                tintIcono = colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )

            AtributoMascotaCard(
                titulo = "Especie",
                valor = mascota.especie.ifBlank {
                    "No especificada"
                },
                icono = Icons.Outlined.Star,
                fondoIcono = colorScheme.primaryContainer,
                tintIcono = colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --------------------------------------------------
        // INFORMACIÓN GENERAL
        // --------------------------------------------------

        InformacionGeneralSection(
            propietario = usuario
                ?.nombre
                ?.takeIf { it.isNotBlank() }
                ?: "No disponible",

            telefono = usuario
                ?.telefono
                ?.takeIf { it.isNotBlank() }
                ?: "No registrado",

            colorMarcas = mascota.colorMarcas.ifBlank {
                "Sin especificar"
            },

            alergiasObservaciones = mascota.observaciones.ifBlank {
                "Ninguna registrada"
            },

            fechaNacimiento = mascota.fechaNacimiento.ifBlank {
                "No disponible"
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // --------------------------------------------------
        // EDITAR INFORMACIÓN
        // --------------------------------------------------

        Button(
            onClick = {
                onNavigateToEdit(mascota)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {

            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar mascota",
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                "Editar Información",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        HistorialMedicoSection(
            onVerTodoClick = onVerHistorialClick
        )

        Spacer(modifier = Modifier.height(20.dp))

        VacunasSection()

        Spacer(modifier = Modifier.height(20.dp))

        ProximasCitasMascotaSection()

        Spacer(modifier = Modifier.height(28.dp))

        // --------------------------------------------------
        // ELIMINAR MASCOTA
        // --------------------------------------------------

        OutlinedButton(
            onClick = {
                mostrarDialogoEliminar = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            shape = RoundedCornerShape(50),
            border = BorderStroke(
                1.dp,
                colorScheme.error
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = colorScheme.errorContainer,
                contentColor = colorScheme.onErrorContainer
            )
        ) {

            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                modifier = Modifier.size(17.dp)
            )

            Text(
                text = "Eliminar mascota",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // --------------------------------------------------
        // DIÁLOGO DE CONFIRMACIÓN DE ELIMINACIÓN
        // --------------------------------------------------

        if (mostrarDialogoEliminar) {

            AlertDialog(
                onDismissRequest = {
                    mostrarDialogoEliminar = false
                },

                icon = {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = "Advertencia",
                        tint = colorScheme.error
                    )
                },

                title = {
                    Text(
                        text = "¿Eliminar a ${mascota.nombre}?",
                        color = colorScheme.onSurface
                    )
                },

                text = {
                    Text(
                        text = "Esta acción eliminará permanentemente el registro de esta mascota. No podrás deshacerla.",
                        color = colorScheme.onSurfaceVariant
                    )
                },

                confirmButton = {

                    TextButton(
                        onClick = {

                            mostrarDialogoEliminar = false

                            controlador.eliminarMascota(
                                mascota.id,

                                onSuccess = {
                                    onMascotaEliminada()
                                },

                                onError = { mensajeError ->

                                    Toast.makeText(
                                        context,
                                        mensajeError,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                    ) {

                        Text(
                            "Sí, eliminar",
                            color = colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },

                dismissButton = {

                    TextButton(
                        onClick = {
                            mostrarDialogoEliminar = false
                        }
                    ) {

                        Text(
                            "Cancelar",
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                },

                containerColor = colorScheme.surface
            )
        }
    }
}

// --------------------------------------------------
// BARRA SUPERIOR DEL DETALLE
// --------------------------------------------------

@Composable
fun DetalleMascotaTopBar(
    onBackClick: () -> Unit
) {

    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorScheme.background)
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp
            ),
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
                tint = colorScheme.primary
            )
        }

        Text(
            text = "VetSync",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.primary
        )

        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(colorScheme.primaryContainer)
                .border(
                    1.dp,
                    colorScheme.outline,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Perfil",
                tint = colorScheme.onPrimaryContainer,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// --------------------------------------------------
// TARJETA DE ATRIBUTO
// --------------------------------------------------

@Composable
fun AtributoMascotaCard(
    titulo: String,
    valor: String,
    icono: ImageVector,
    fondoIcono: Color,
    tintIcono: Color,
    modifier: Modifier = Modifier
) {

    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            colorScheme.outlineVariant
        )
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
                color = colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = valor,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
        }
    }
}

// --------------------------------------------------
// INFORMACIÓN GENERAL
// --------------------------------------------------

@Composable
fun InformacionGeneralSection(
    propietario: String,
    telefono: String,
    colorMarcas: String,
    alergiasObservaciones: String,
    fechaNacimiento: String
) {

    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            colorScheme.outlineVariant
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Información general",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
            }

            // DatoGeneralItem(label = "Propietario", valor = propietario)

            DatoGeneralItem(
                label = "Teléfono",
                valor = telefono
            )

            DatoGeneralItem(
                label = "Color / Marcas",
                valor = colorMarcas
            )

            DatoGeneralItem(
                label = "Alergias / Observaciones",
                valor = alergiasObservaciones
            )

            DatoGeneralItem(
                label = "Fecha Nacimiento",
                valor = fechaNacimiento
            )
        }
    }
}

// --------------------------------------------------
// DATO GENERAL
// --------------------------------------------------

@Composable
fun DatoGeneralItem(
    label: String,
    valor: String
) {

    Column {

        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = valor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// --------------------------------------------------
// ESTADO VACÍO DE UN MÓDULO
// --------------------------------------------------

@Composable
private fun EstadoModuloVacio(
    icono: ImageVector,
    mensaje: String
) {

    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = colorScheme.onSurfaceVariant,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = mensaje,
            fontSize = 13.sp,
            color = colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 19.sp
        )
    }
}

// --------------------------------------------------
// HISTORIAL MÉDICO
// --------------------------------------------------

@Composable
fun HistorialMedicoSection(
    onVerTodoClick: () -> Unit
) {

    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            colorScheme.outlineVariant
        )
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

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Historial médico",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                }

                Text(
                    text = "Ver todo",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.primary,
                    modifier = Modifier.clickable {
                        onVerTodoClick()
                    }
                )
            }

            EstadoModuloVacio(
                icono = Icons.Outlined.DateRange,
                mensaje = "Consulta los diagnósticos, recetas médicas y observaciones registradas para esta mascota."
            )
        }
    }
}

// --------------------------------------------------
// ELEMENTO DEL HISTORIAL MÉDICO
// --------------------------------------------------

@Composable
fun ItemHistorialMedico(
    dia: String,
    mes: String,
    titulo: String,
    descripcion: String,
    doctor: String
) {

    val colorScheme = MaterialTheme.colorScheme

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
                color = colorScheme.primary
            )

            Text(
                text = mes,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = titulo,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = descripcion,
                fontSize = 12.sp,
                color = colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                color = colorScheme.surfaceVariant,
                shape = RoundedCornerShape(6.dp)
            ) {

                Text(
                    text = doctor,
                    fontSize = 10.sp,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 3.dp
                    )
                )
            }
        }
    }
}

// --------------------------------------------------
// VACUNAS
// --------------------------------------------------

@Composable
fun VacunasSection() {

    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            colorScheme.outlineVariant
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Vacunas",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
            }

            EstadoModuloVacio(
                icono = Icons.Outlined.CheckCircle,
                mensaje = "Los registros de vacunación estarán disponibles cuando se implemente este módulo."
            )
        }
    }
}

// --------------------------------------------------
// TARJETA INDIVIDUAL DE VACUNA
// --------------------------------------------------

@Composable
fun ItemVacunaCard(
    nombre: String,
    fecha: String,
    estado: String,
    esAlerta: Boolean
) {

    val colorScheme = MaterialTheme.colorScheme

    val fondoEstado = if (esAlerta) {
        colorScheme.tertiaryContainer
    } else {
        colorScheme.primaryContainer
    }

    val textoEstado = if (esAlerta) {
        colorScheme.onTertiaryContainer
    } else {
        colorScheme.onPrimaryContainer
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = colorScheme.surfaceVariant,
        border = BorderStroke(
            1.dp,
            colorScheme.outlineVariant
        )
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
                    imageVector = if (esAlerta) {
                        Icons.Outlined.Warning
                    } else {
                        Icons.Outlined.CheckCircle
                    },
                    contentDescription = null,
                    tint = if (esAlerta) {
                        colorScheme.tertiary
                    } else {
                        colorScheme.primary
                    },
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {

                    Text(
                        text = nombre,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )

                    Text(
                        text = fecha,
                        fontSize = 11.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                color = fondoEstado,
                contentColor = textoEstado,
                shape = RoundedCornerShape(8.dp)
            ) {

                Text(
                    text = estado,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    lineHeight = 12.sp,
                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    )
                )
            }
        }
    }
}

// --------------------------------------------------
// PRÓXIMAS CITAS DE LA MASCOTA
// --------------------------------------------------

@Composable
fun ProximasCitasMascotaSection() {

    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.secondaryContainer
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Outlined.DateRange,
                    contentDescription = null,
                    tint = colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Próximas citas",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSecondaryContainer
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            EstadoModuloVacio(
                icono = Icons.Outlined.DateRange,
                mensaje = "Las próximas citas se mostrarán aquí cuando conectemos la agenda con Firestore."
            )
        }
    }
}

// --------------------------------------------------
// PLAN PREVENTIVO
// --------------------------------------------------

@Composable
fun PlanPreventivoCard() {

    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.tertiaryContainer
        ),
        border = BorderStroke(
            1.dp,
            colorScheme.outlineVariant
        )
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
                    .background(colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = colorScheme.tertiary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Plan Preventivo Anual",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onTertiaryContainer
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Suscripción activa. Cubre visitas rutinarias y vacunas básicas.",
                    fontSize = 11.sp,
                    color = colorScheme.onTertiaryContainer,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Ver detalles del plan",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.primary
                )
            }
        }
    }
}