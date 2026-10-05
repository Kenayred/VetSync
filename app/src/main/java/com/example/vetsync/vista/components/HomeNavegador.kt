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
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.ContentScale
import com.example.vetsync.modelo.ImagenUtils
import com.example.vetsync.modelo.SesionUsuario
@Composable
fun HomeTopBar(mostrarFotoPerfil: Boolean = true) {

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

        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notificaciones",
                tint = Color(0xFF2B2B2B),
                modifier = Modifier.size(26.dp)
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(RojoAlerta)
            )
        }
    }
}

@Composable
fun HomeBottomNavigationBar(
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
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .offset(x = 2.dp, y = (-1).dp)
                        .clip(CircleShape)
                        .background(RojoAlerta)
                )
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