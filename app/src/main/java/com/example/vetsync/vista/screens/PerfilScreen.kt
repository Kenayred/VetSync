package com.example.vetsync.vista.screens

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
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vetsync.R
import com.example.vetsync.modelo.Usuario
import com.example.vetsync.vista.theme.*
import com.example.vetsync.modelo.ImagenUtils

@Composable
fun PerfilScreen(
    usuario: Usuario,
    onLogoutClick: () -> Unit,
    onEditProfileClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoCrema)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            PerfilPrincipalCard(usuario = usuario, onEditProfileClick = onEditProfileClick)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                EstadisticaCard(
                    modifier = Modifier.weight(1f),
                    icono = {
                        Image(
                            painter = painterResource(id = R.drawable.logo_vetsync),
                            contentDescription = "Logo secundario de VetSync",
                            modifier = Modifier.size(28.dp) // Usamos 28.dp para que quede del mismo tamaño que los otros
                        )
                    },
                    valor = "2",
                    etiqueta = "Mascotas registradas"
                )
                EstadisticaCard(
                    modifier = Modifier.weight(1f),
                    icono = {
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = null,
                            tint = VerdeVetSync,
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    valor = "1",
                    etiqueta = "Próxima cita"
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            EstadisticaCard(
                modifier = Modifier.fillMaxWidth(),
                icono ={
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = null,
                        tint = VerdeVetSync,
                        modifier = Modifier.size(28.dp)
                    )
                },
                valor = "5",
                etiqueta = "Citas completadas"
            )
            Spacer(modifier = Modifier.height(16.dp))

            InformacionPersonalCard(usuario)
            Spacer(modifier = Modifier.height(16.dp))

            PreferenciasCard()
            Spacer(modifier = Modifier.height(16.dp))

            SeguridadCard(onLogoutClick)
            Spacer(modifier = Modifier.height(30.dp)) // Espacio para el BottomNavigation
        }
    }
}

//@Composable
//fun TopBarPerfil() {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 20.dp, vertical = 14.dp),
//        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Image(
//                painter = painterResource(id = android.R.drawable.ic_menu_gallery), // Reemplaza con tu logo/foto
//                contentDescription = "Avatar TopBar",
//                modifier = Modifier
//                    .size(36.dp)
//                    .clip(CircleShape)
//                    .border(1.dp, VerdeVetSync.copy(alpha = 0.4f), CircleShape),
//                contentScale = ContentScale.Crop
//            )
//            Spacer(modifier = Modifier.width(10.dp))
//            Text(
//                text = "VetSync",
//                fontSize = 22.sp,
//                fontWeight = FontWeight.Bold,
//                color = VerdeVetSync
//            )
//        }
//        Icon(
//            imageVector = Icons.Outlined.Settings,
//            contentDescription = "Configuración",
//            tint = VerdeVetSync,
//            modifier = Modifier.size(26.dp)
//        )
//    }
//}

@Composable
fun PerfilPrincipalCard(usuario: Usuario, onEditProfileClick: () -> Unit) {
    CardBlanca {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Decodificamos la imagen recordando el estado para evitar lag
            val bitmapPerfil = remember(usuario.fotoUrl) {
                ImagenUtils.base64ABitmap(usuario.fotoUrl)
            }

            // 2. Contenedor de la foto con el borde verde característico
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEBEBEB)) // Fondo sutil por si no hay foto
                    .border(2.dp, VerdeVetSync, CircleShape),
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
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = usuario.nombre, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(text = usuario.correo, fontSize = 14.sp, color = GrisTextoSecundario)
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .background(VerdeClaroIcono, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(text = "Cliente", fontSize = 12.sp, color = VerdeVetSync, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onEditProfileClick,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, VerdeVetSync),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = VerdeVetSync)
            ) {
                Text("Editar perfil")
            }
        }
    }
}
@Composable
fun EstadisticaCard(modifier: Modifier = Modifier,
                    icono: @Composable () -> Unit,
                    valor: String, etiqueta: String) {
    CardBlanca(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icono()
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = valor, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = VerdeVetSync)
            Text(text = etiqueta, fontSize = 12.sp, color = GrisTextoSecundario)
        }
    }
}

@Composable
fun InformacionPersonalCard(usuario: Usuario) {
    CardBlanca {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "Información Personal", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            InfoRowItem(icono = Icons.Outlined.Person, label = "Nombre completo", valor = usuario.nombre)
            Spacer(modifier = Modifier.height(12.dp))
            InfoRowItem(icono = Icons.Outlined.Phone, label = "Teléfono", valor = usuario.telefono)
            Spacer(modifier = Modifier.height(12.dp))
            InfoRowItem(icono = Icons.Outlined.Email, label = "Email", valor = usuario.correo)
            Spacer(modifier = Modifier.height(12.dp))
            InfoRowItem(icono = Icons.Outlined.LocationOn, label = "Ubicación", valor = "San Salvador")
        }
    }
}

@Composable
fun PreferenciasCard() {
    var modoOscuro by remember { mutableStateOf(false) }
    var notificaciones by remember { mutableStateOf(true) }

    CardBlanca {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "Preferencias", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            SwitchRowItem(icono = Icons.Outlined.Info, label = "Modo oscuro", checked = modoOscuro, onCheckedChange = { modoOscuro = it })
            Spacer(modifier = Modifier.height(12.dp))
            SwitchRowItem(icono = Icons.Outlined.Notifications, label = "Notificaciones", checked = notificaciones, onCheckedChange = { notificaciones = it })
            Spacer(modifier = Modifier.height(12.dp))

            // Fila de Idioma
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Outlined.Build, contentDescription = null, tint = GrisTextoSecundario, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Idioma", fontSize = 14.sp, modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .background(GrisBordeCard, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "Español", fontSize = 12.sp, color = Color.DarkGray)
                }
            }
        }
    }
}

@Composable
fun SeguridadCard(onLogoutClick: () -> Unit) {
    CardBlanca {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "Seguridad", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            ActionRowItem(icono = Icons.Outlined.Lock, label = "Cambiar contraseña")
            Spacer(modifier = Modifier.height(16.dp))
            ActionRowItem(icono = Icons.Outlined.Person, label = "Configuración de cuenta")
            Spacer(modifier = Modifier.height(20.dp))
            Divider(color = GrisBordeCard, thickness = 1.dp)
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLogoutClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Outlined.ExitToApp, contentDescription = null, tint = RojoAlerta, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Cerrar sesión", fontSize = 14.sp, color = RojoAlerta, fontWeight = FontWeight.Medium)
            }
        }
    }
}


@Composable
fun CardBlanca(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, GrisBordeCard)
    ) {
        content()
    }
}

@Composable
fun InfoRowItem(icono: ImageVector, label: String, valor: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Icon(imageVector = icono, contentDescription = null, tint = GrisTextoSecundario, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, fontSize = 11.sp, color = GrisTextoSecundario)
            Text(text = valor, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color(0xFF2B2B2B))
        }
    }
}

@Composable
fun SwitchRowItem(icono: ImageVector, label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Icon(imageVector = icono, contentDescription = null, tint = GrisTextoSecundario, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = VerdeVetSync
            )
        )
    }
}

@Composable
fun ActionRowItem(icono: ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Icon(imageVector = icono, contentDescription = null, tint = GrisTextoSecundario, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, contentDescription = null, tint = GrisTextoSecundario)
    }
}