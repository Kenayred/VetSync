package com.example.vetsync.vista.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.vetsync.vista.theme.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.ContentScale
import com.example.vetsync.modelo.ImagenUtils
import com.example.vetsync.modelo.SesionUsuario
import androidx.compose.material3.IconButton
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vetsync.controlador.UsuarioControlador
import com.example.vetsync.vista.theme.RojoAlerta
import com.example.vetsync.vista.theme.VerdeVetSync

@Composable
fun HomeTopBar(
    mostrarFotoPerfil: Boolean = true,
    notificacionesNoLeidas: Int = 0,
    onNotificationsClick: () -> Unit = {}
) {

    val usuario = SesionUsuario.usuarioActual
    val fotoUrl = usuario?.fotoUrl ?: ""

    val bitmapPerfil = remember(fotoUrl) {
        if (fotoUrl.isNotEmpty()) ImagenUtils.base64ABitmap(fotoUrl) else null
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if(mostrarFotoPerfil){
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(VerdeClaroIcono)
                    .border(1.dp, VerdeVetSync.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {

                    if (bitmapPerfil != null) {
                        Image(
                            bitmap = bitmapPerfil,
                            contentDescription = "Foto de perfil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Perfil",
                            tint = VerdeVetSync,
                            modifier = Modifier.size(22.dp)
                        )
                    }

            }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "VetSync",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeVetSync
            )
        }

        IconButton(
            onClick = onNotificationsClick
        ) {
            Box(
                contentAlignment = Alignment.TopEnd
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Abrir notificaciones",
                    tint = Color(0xFF2B2B2B),
                    modifier = Modifier.size(26.dp)
                )

                if (notificacionesNoLeidas > 0) {
                    Surface(
                        modifier = Modifier.offset(
                            x = 5.dp,
                            y = (-5).dp
                        ),
                        color = RojoAlerta,
                        shape = CircleShape
                    ) {
                        Text(
                            text = if (notificacionesNoLeidas > 9) {
                                "9+"
                            } else {
                                notificacionesNoLeidas.toString()
                            },
                            modifier = Modifier.padding(
                                horizontal = 4.dp,
                                vertical = 2.dp
                            ),
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminHomeTopBar(
    mostrarFotoPerfil: Boolean = true,
    onNotificationsClick: () -> Unit = {},
    notificacionesNoLeidas: Int = 0
) {

    val usuario = SesionUsuario.usuarioActual
    val fotoUrl = usuario?.fotoUrl ?: ""

    val bitmapPerfil = remember(fotoUrl) {
        if (fotoUrl.isNotEmpty()) ImagenUtils.base64ABitmap(fotoUrl) else null
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if(mostrarFotoPerfil){
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(VerdeClaroIcono)
                        .border(1.dp, VerdeVetSync.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {

                    if (bitmapPerfil != null) {
                        Image(
                            bitmap = bitmapPerfil,
                            contentDescription = "Foto de perfil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Perfil",
                            tint = VerdeVetSync,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "VetSync",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeVetSync
            )
        }

        IconButton(
            onClick = onNotificationsClick
        ) {
            Box(
                contentAlignment = Alignment.TopEnd
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Abrir notificaciones",
                    tint = Color(0xFF2B2B2B),
                    modifier = Modifier.size(26.dp)
                )

                if (notificacionesNoLeidas > 0) {
                    Surface(
                        modifier = Modifier.offset(
                            x = 5.dp,
                            y = (-5).dp
                        ),
                        color = RojoAlerta,
                        shape = CircleShape
                    ) {
                        Text(
                            text = if (notificacionesNoLeidas > 9) {
                                "9+"
                            } else {
                                notificacionesNoLeidas.toString()
                            },
                            modifier = Modifier.padding(
                                horizontal = 4.dp,
                                vertical = 2.dp
                            ),
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeBottomNavigationBar(
    selectedTab: Int,
    notificacionesNoLeidas: Int = 0,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        color = Color(0xFFF4EFEA),
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                label = "Inicio",
                icon = Icons.Default.Home,
                selected = selectedTab == 0,
                onClick = { onTabSelected(0) }
            )
            BottomNavItem(
                label = "Mis\nmascotas",
                icon = Icons.Outlined.FavoriteBorder,
                selected = selectedTab == 1,
                onClick = { onTabSelected(1) }
            )
            BottomNavItem(
                label = "Citas",
                icon = Icons.Outlined.DateRange,
                selected = selectedTab == 2,
                hasBadge = true,
                onClick = { onTabSelected(2) }
            )
            BottomNavItem(
                label = "Notificaciones",
                icon = Icons.Outlined.Notifications,
                selected = selectedTab == 3,
                hasBadge = notificacionesNoLeidas > 0,
                badgeCount = notificacionesNoLeidas,
                onClick = { onTabSelected(3) }
            )
            BottomNavItem(
                label = "Perfil",
                icon = Icons.Outlined.Person,
                selected = selectedTab == 4,
                onClick = { onTabSelected(4) }
            )
        }
    }
}

@Composable
fun AdminHomeBottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        color = Color(0xFFF4EFEA),
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                label = "Inicio",
                icon = Icons.Default.Dashboard,
                selected = selectedTab == 0,
                onClick = { onTabSelected(0) }
            )
            BottomNavItem(
                label = "Citas",
                icon = Icons.Outlined.CalendarToday,
                selected = selectedTab == 1,
                onClick = { onTabSelected(1) }
            )
            BottomNavItem(
                label = "Pacientes",
                icon = Icons.Outlined.Pets,
                selected = selectedTab == 2,
                hasBadge = true,
                onClick = { onTabSelected(2) }
            )
            BottomNavItem(
                label = "Reportes",
                icon = Icons.Outlined.Analytics,
                selected = selectedTab == 3,
                onClick = { onTabSelected(3) }
            )
            BottomNavItem(
                label = "Perfil",
                icon = Icons.Outlined.Person,
                selected = selectedTab == 4,
                onClick = { onTabSelected(4) }
            )
        }
    }
}

@Composable
fun BottomNavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    hasBadge: Boolean = false,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) VerdeClaroIcono else Color.Transparent
    val contentColor = if (selected) VerdeVetSync else Color(0xFF3B3B3B)

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(22.dp)
            )
            if (hasBadge) {

                if (badgeCount > 0) {

                    Surface(
                        modifier = Modifier.offset(
                            x = 6.dp,
                            y = (-5).dp
                        ),
                        color = RojoAlerta,
                        shape = CircleShape
                    ) {
                        Text(
                            text = if (badgeCount > 9) {
                                "9+"
                            } else {
                                badgeCount.toString()
                            },
                            modifier = Modifier.padding(
                                horizontal = 4.dp,
                                vertical = 1.dp
                            ),
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                } else {

                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .offset(x = 2.dp, y = (-1).dp)
                            .clip(CircleShape)
                            .background(RojoAlerta)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = contentColor,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            lineHeight = 12.sp
        )
    }
}

@Composable
fun DialogoCambiarContrasena(
    controlador: UsuarioControlador,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var contrasenaActual by remember { mutableStateOf("") }
    var nuevaContrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }

    var verActual by remember { mutableStateOf(false) }
    var verNueva by remember { mutableStateOf(false) }
    var verConfirmar by remember { mutableStateOf(false) }

    var cargando by remember { mutableStateOf(false) }
    var errorMensaje by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = { if (!cargando) onDismiss() },
        containerColor = Color.White,
        title = {
            Text(
                text = "Cambiar contraseña",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column {
                Text(
                    text = "Por seguridad, ingresa tu contraseña actual antes de definir una nueva.",
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Contraseña actual
                OutlinedTextField(
                    value = contrasenaActual,
                    onValueChange = {
                        contrasenaActual = it
                        errorMensaje = null
                    },
                    enabled = !cargando,
                    label = { Text("Contraseña actual") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { verActual = !verActual }) {
                            Icon(
                                imageVector = if (verActual) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (verActual) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VerdeVetSync,
                        focusedLabelColor = VerdeVetSync,
                        cursorColor = VerdeVetSync
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Nueva contraseña
                OutlinedTextField(
                    value = nuevaContrasena,
                    onValueChange = {
                        nuevaContrasena = it
                        errorMensaje = null
                    },
                    enabled = !cargando,
                    label = { Text("Nueva contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { verNueva = !verNueva }) {
                            Icon(
                                imageVector = if (verNueva) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (verNueva) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VerdeVetSync,
                        focusedLabelColor = VerdeVetSync,
                        cursorColor = VerdeVetSync
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Confirmar nueva contraseña
                OutlinedTextField(
                    value = confirmarContrasena,
                    onValueChange = {
                        confirmarContrasena = it
                        errorMensaje = null
                    },
                    enabled = !cargando,
                    label = { Text("Confirmar nueva contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { verConfirmar = !verConfirmar }) {
                            Icon(
                                imageVector = if (verConfirmar) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (verConfirmar) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VerdeVetSync,
                        focusedLabelColor = VerdeVetSync,
                        cursorColor = VerdeVetSync
                    )
                )

                if (errorMensaje != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMensaje.orEmpty(),
                        color = RojoAlerta,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    cargando = true
                    errorMensaje = null

                    controlador.cambiarContrasena(
                        contrasenaActual = contrasenaActual,
                        nuevaContrasena = nuevaContrasena,
                        confirmarContrasena = confirmarContrasena,
                        onSuccess = {
                            cargando = false
                            Toast.makeText(
                                context,
                                "Contraseña actualizada correctamente.",
                                Toast.LENGTH_SHORT
                            ).show()
                            onDismiss()
                        },
                        onError = { mensaje ->
                            cargando = false
                            errorMensaje = mensaje
                        }
                    )
                },
                enabled = !cargando,
                colors = ButtonDefaults.buttonColors(containerColor = VerdeVetSync)
            ) {
                if (cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Actualizar")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !cargando
            ) {
                Text("Cancelar", color = Color.Gray)
            }
        }
    )
}