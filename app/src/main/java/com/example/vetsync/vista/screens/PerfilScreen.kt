package com.example.vetsync.vista.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource

import com.example.vetsync.R
import com.example.vetsync.modelo.Usuario
import com.example.vetsync.modelo.ImagenUtils
import com.example.vetsync.controlador.MascotaControlador
import com.example.vetsync.controlador.CitaControlador
import com.example.vetsync.controlador.UsuarioControlador
import com.example.vetsync.modelo.Cita
import com.example.vetsync.modelo.EstadoCita
import com.example.vetsync.vista.components.DialogoCambiarContrasena
import com.example.vetsync.vista.components.SelectorIdioma

import java.time.LocalDate
import java.time.LocalTime

@Composable
fun PerfilScreen(
    usuario: Usuario,
    onLogoutClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    controlador: UsuarioControlador = remember {
        UsuarioControlador()
    },
    mostrarIndicadorNotificaciones: Boolean = true,
    onMostrarIndicadorNotificacionesChange: (Boolean) -> Unit = {},
    modoOscuro: Boolean = false,
    onModoOscuroChange: (Boolean) -> Unit = {}
) {

    var mostrarDialogoCerrarSesion by remember {
        mutableStateOf(false)
    }

    var mostrarDialogoCambiarContrasena by remember {
        mutableStateOf(false)
    }

    val controladorMascota = remember {
        MascotaControlador()
    }

    val controladorCitas = remember {
        CitaControlador()
    }

    var cantidadCitasProximas by remember(usuario.id) {
        mutableStateOf<Int?>(null)
    }

    var cantidadCitasCompletadas by remember(usuario.id) {
        mutableStateOf<Int?>(null)
    }

    var cargandoCitas by remember(usuario.id) {
        mutableStateOf(true)
    }

    var errorCargaCitas by remember(usuario.id) {
        mutableStateOf(false)
    }

    var cantidadMascotas by remember(usuario.id) {
        mutableStateOf<Int?>(null)
    }

    var cargandoMascotas by remember(usuario.id) {
        mutableStateOf(true)
    }

    var errorCargaMascotas by remember(usuario.id) {
        mutableStateOf(false)
    }

    LaunchedEffect(usuario.id) {

        if (usuario.id.isBlank()) {

            cantidadMascotas = null
            cargandoMascotas = false
            errorCargaMascotas = true

            cantidadCitasProximas = null
            cantidadCitasCompletadas = null
            cargandoCitas = false
            errorCargaCitas = true

            return@LaunchedEffect
        }

        // Cargar las mascotas registradas.

        cargandoMascotas = true
        errorCargaMascotas = false

        controladorMascota.obtenerMascotasUsuario(
            idUsuario = usuario.id,
            onSuccess = { mascotas ->
                cantidadMascotas = mascotas.size
                cargandoMascotas = false
            },
            onError = {
                cantidadMascotas = null
                cargandoMascotas = false
                errorCargaMascotas = true
            }
        )

        // Cargar las citas del cliente.

        cargandoCitas = true
        errorCargaCitas = false

        controladorCitas.obtenerCitasUsuario(
            onSuccess = { citas ->

                val hoy = LocalDate.now()
                val horaActual = LocalTime.now()

                cantidadCitasProximas = citas.count { cita ->

                    esCitaProxima(
                        cita = cita,
                        hoy = hoy,
                        horaActual = horaActual
                    )
                }

                cantidadCitasCompletadas = citas.count {
                    it.estado == EstadoCita.COMPLETADA
                }

                cargandoCitas = false
            },
            onError = {
                cantidadCitasProximas = null
                cantidadCitasCompletadas = null
                cargandoCitas = false
                errorCargaCitas = true
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(10.dp))

            PerfilPrincipalCard(
                usuario = usuario,
                onEditProfileClick = onEditProfileClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Resumen de tu cuenta",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Primera fila: mascotas y próxima cita.

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                EstadisticaCard(
                    modifier = Modifier.weight(1f),
                    icono = {
                        Icon(
                            imageVector = Icons.Outlined.Pets,
                            contentDescription = "Mascotas registradas",
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    valor = when {
                        cargandoMascotas -> "…"
                        errorCargaMascotas -> "—"
                        else -> cantidadMascotas?.toString() ?: "—"
                    },
                    etiqueta = "Mascotas registradas"
                )

                EstadisticaCard(
                    modifier = Modifier.weight(1f),
                    icono = {
                        Icon(
                            imageVector = Icons.Outlined.DateRange,
                            contentDescription = "Próximas citas",
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    },
                    valor = when {
                        cargandoCitas -> "…"
                        errorCargaCitas -> "—"
                        else -> cantidadCitasProximas?.toString() ?: "—"
                    },
                    etiqueta = "Próxima cita"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Segunda fila: citas completadas.

            EstadisticaCard(
                modifier = Modifier.fillMaxWidth(),
                icono = {
                    Icon(
                        imageVector = Icons.Filled.History,
                        contentDescription = "Citas completadas",
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                valor = when {
                    cargandoCitas -> "…"
                    errorCargaCitas -> "—"
                    else -> cantidadCitasCompletadas?.toString() ?: "—"
                },
                etiqueta = "Citas completadas"
            )

            if (errorCargaMascotas) {

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "No se pudo cargar la cantidad de mascotas.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (errorCargaCitas) {

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "No se pudo cargar el resumen de citas.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            InformacionPersonalCard(usuario)

            Spacer(modifier = Modifier.height(16.dp))

            PreferenciasCard(
                mostrarIndicadorNotificaciones = mostrarIndicadorNotificaciones,
                onMostrarIndicadorNotificacionesChange =
                    onMostrarIndicadorNotificacionesChange,
                modoOscuro = modoOscuro,
                onModoOscuroChange = onModoOscuroChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            SeguridadCard(
                onCambiarContrasenaClick = {
                    mostrarDialogoCambiarContrasena = true
                },
                onCerrarSesionClick = {
                    mostrarDialogoCerrarSesion = true
                },
                onLogoutClick = onLogoutClick
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // --------------------------------------------------
    // DIÁLOGO CAMBIAR CONTRASEÑA
    // --------------------------------------------------

    if (mostrarDialogoCambiarContrasena) {

        DialogoCambiarContrasena(
            controlador = controlador,
            onDismiss = {
                mostrarDialogoCambiarContrasena = false
            }
        )
    }

    // --------------------------------------------------
    // CONFIRMACIÓN DE CIERRE DE SESIÓN
    // --------------------------------------------------

    if (mostrarDialogoCerrarSesion) {

        AlertDialog(
            onDismissRequest = {
                mostrarDialogoCerrarSesion = false
            },

            title = {
                Text(
                    text = "Cerrar sesión",
                    fontWeight = FontWeight.Bold
                )
            },

            text = {
                Text(
                    text = "¿Estás seguro de que deseas cerrar tu sesión?"
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        mostrarDialogoCerrarSesion = false
                        onLogoutClick()
                    }
                ) {

                    Text(
                        text = "Cerrar sesión",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        mostrarDialogoCerrarSesion = false
                    }
                ) {

                    Text(
                        text = "Cancelar",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },

            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

// --------------------------------------------------
// VALIDAR SI UNA CITA ES PRÓXIMA
// --------------------------------------------------

private fun esCitaProxima(
    cita: Cita,
    hoy: LocalDate,
    horaActual: LocalTime
): Boolean {

    if (
        cita.estado != EstadoCita.PENDIENTE &&
        cita.estado != EstadoCita.CONFIRMADA
    ) {
        return false
    }

    return runCatching {

        val fechaCita = LocalDate.parse(cita.fecha)
        val horaCita = LocalTime.parse(cita.hora)

        fechaCita.isAfter(hoy) ||
                (
                        fechaCita.isEqual(hoy) &&
                                horaCita.isAfter(horaActual)
                        )

    }.getOrDefault(false)
}

// --------------------------------------------------
// TARJETA PRINCIPAL DEL PERFIL
// --------------------------------------------------

@Composable
fun PerfilPrincipalCard(
    usuario: Usuario,
    onEditProfileClick: () -> Unit
) {

    CardBlanca {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Decodificar la fotografía.

            val bitmapPerfil = remember(usuario.fotoUrl) {
                ImagenUtils.base64ABitmap(usuario.fotoUrl)
            }

            // Fotografía o avatar.

            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.primary,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

                if (bitmapPerfil != null) {

                    Image(
                        bitmap = bitmapPerfil,
                        contentDescription = "Foto de perfil de ${usuario.nombre}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                } else {

                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Avatar por defecto",
                        modifier = Modifier.size(50.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = usuario.nombre,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = usuario.correo,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(
                        horizontal = 12.dp,
                        vertical = 4.dp
                    )
            ) {

                Text(
                    text = "Cliente",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onEditProfileClick,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.primary
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {

                Text("Editar perfil")
            }
        }
    }
}

// --------------------------------------------------
// TARJETA DE ESTADÍSTICAS
// --------------------------------------------------

@Composable
fun EstadisticaCard(
    modifier: Modifier = Modifier,
    icono: @Composable () -> Unit,
    valor: String,
    etiqueta: String
) {

    CardBlanca(modifier = modifier) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            icono()

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = valor,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = etiqueta,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// --------------------------------------------------
// INFORMACIÓN PERSONAL
// --------------------------------------------------

@Composable
fun InformacionPersonalCard(usuario: Usuario) {

    CardBlanca {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                text = "Información Personal",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            InfoRowItem(
                icono = Icons.Outlined.Person,
                label = "Nombre completo",
                valor = usuario.nombre
            )

            Spacer(modifier = Modifier.height(12.dp))

            InfoRowItem(
                icono = Icons.Outlined.Phone,
                label = "Teléfono",
                valor = usuario.telefono
            )

            Spacer(modifier = Modifier.height(12.dp))

            InfoRowItem(
                icono = Icons.Outlined.Email,
                label = "Email",
                valor = usuario.correo
            )

            Spacer(modifier = Modifier.height(12.dp))

            InfoRowItem(
                icono = Icons.Outlined.LocationOn,
                label = "Ubicación",
                valor = usuario.ubicacion.ifBlank {
                    "No registrada"
                }
            )
        }
    }
}

// --------------------------------------------------
// PREFERENCIAS DEL CLIENTE
// --------------------------------------------------

@Composable
fun PreferenciasCard(
    mostrarIndicadorNotificaciones: Boolean,
    onMostrarIndicadorNotificacionesChange: (Boolean) -> Unit,
    modoOscuro: Boolean,
    onModoOscuroChange: (Boolean) -> Unit
) {

    CardBlanca {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                text = stringResource(R.string.client_preferences),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Modo oscuro.

            SwitchRowItem(
                icono = Icons.Outlined.DarkMode,
                label = stringResource(R.string.client_dark_mode),
                checked = modoOscuro,
                onCheckedChange = onModoOscuroChange
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Contador de notificaciones.

            SwitchRowItem(
                icono = Icons.Outlined.Notifications,
                label = stringResource(R.string.client_notification_counter),
                checked = mostrarIndicadorNotificaciones,
                onCheckedChange = onMostrarIndicadorNotificacionesChange
            )

            Text(
                text = stringResource(
                    R.string.client_notification_counter_description
                ),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(
                    start = 32.dp,
                    top = 2.dp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            SelectorIdioma()
        }
    }
}

// --------------------------------------------------
// SEGURIDAD
// --------------------------------------------------

@Composable
fun SeguridadCard(
    onCambiarContrasenaClick: () -> Unit,
    onCerrarSesionClick: () -> Unit,
    onLogoutClick: () -> Unit
) {

    CardBlanca {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                text = "Seguridad",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onCambiarContrasenaClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.primary
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {

                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Cambiar contraseña",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ActionRowItem(
                icono = Icons.Outlined.Person,
                label = "Configuración de cuenta"
            )

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onCerrarSesionClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.error
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                )
            ) {

                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Cerrar sesión",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// --------------------------------------------------
// TARJETA REUTILIZABLE
// --------------------------------------------------

@Composable
fun CardBlanca(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {

        content()
    }
}

// --------------------------------------------------
// FILA DE INFORMACIÓN
// --------------------------------------------------

@Composable
fun InfoRowItem(
    icono: ImageVector,
    label: String,
    valor: String
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {

        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {

            Text(
                text = label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = valor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// --------------------------------------------------
// FILA DE INTERRUPTOR
// --------------------------------------------------

@Composable
fun SwitchRowItem(
    icono: ImageVector,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {

        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = label,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

// --------------------------------------------------
// FILA DE ACCIÓN
// --------------------------------------------------

@Composable
fun ActionRowItem(
    icono: ImageVector,
    label: String
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {

        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = label,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface
        )

        Icon(
            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}