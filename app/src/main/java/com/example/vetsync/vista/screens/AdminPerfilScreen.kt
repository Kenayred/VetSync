package com.example.vetsync.vista.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vetsync.modelo.SesionUsuario // Opcional, si deseas conectar el botón de cerrar sesión

@Composable
fun AdminPerfilScreen(
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit = {}
) {
    val backgroundColor = Color(0xFFFDFCF9)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Título de la pantalla
        Text(
            text = "Perfil de Administrador",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 1. Tarjeta Principal del Perfil
        PerfilHeaderCard()

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Tarjeta: Información de la Clínica
        InfoClinicaCard()

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Tarjeta: Preferencias
        PreferenciasAdminCard()

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Tarjeta: Seguridad y Cerrar Sesión
        SeguridadAdminCard(onLogoutClick = onLogoutClick)

        Spacer(modifier = Modifier.height(80.dp)) // Espacio para el BottomBar
    }
}

@Composable
fun PerfilHeaderCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5))
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Fondo superior verde claro
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(Color(0xFFCDE2D4))
            )

            // Contenido superpuesto (Avatar y Textos)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 46.dp) // Empuja el contenido para que el avatar quede a la mitad del verde
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .border(4.dp, Color.White, CircleShape) // Borde blanco alrededor de la foto
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Nombre y Rol
                Text(
                    text = "Dr. Alejandro Vega",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Administrador / Veterinario Principal",
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Etiqueta de Correo
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE2EFE7), RoundedCornerShape(8.dp))
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = "Correo",
                        tint = Color(0xFF2E5B4B),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "a.vega@vetsync.cl",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2E5B4B)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botón Editar Perfil
                OutlinedButton(
                    onClick = { /* TODO: Navegar a editar perfil */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                        tint = Color.DarkGray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Editar Perfil",
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

@Composable
fun InfoClinicaCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Título de la sección
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Business,
                    contentDescription = null,
                    tint = Color(0xFF1A1A1A),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Información de la Clínica",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Campo: Nombre
            Text(text = "Nombre", fontSize = 12.sp, color = Color.Gray)
            Text(text = "Clínica Veterinaria Central", fontSize = 14.sp, color = Color.Black)

            Spacer(modifier = Modifier.height(16.dp))

            // Campo: Sede
            Text(text = "Sede", fontSize = 12.sp, color = Color.Gray)
            Text(text = "Apopa, San Salvador", fontSize = 14.sp, color = Color.Black)

            Spacer(modifier = Modifier.height(16.dp))

            // Campo: Licencia
            Text(text = "Licencia Activa", fontSize = 12.sp, color = Color.Gray)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF8BA793)))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Plan Enterprise (Vence: 12/2025)", fontSize = 14.sp, color = Color.Black)
            }
        }
    }
}

@Composable
fun PreferenciasAdminCard() {
    // Estados para los switches
    var modoOscuro by remember { mutableStateOf(false) }
    var notificaciones by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Título
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = null,
                    tint = Color(0xFF1A1A1A),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Preferencias",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Toggle 1: Modo Oscuro
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Modo Oscuro", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text(text = "Ajustar tema visual", fontSize = 12.sp, color = Color.Gray)
                }
                Switch(
                    checked = modoOscuro,
                    onCheckedChange = { modoOscuro = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF8BA793) // Verde principal tenue
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Toggle 2: Notificaciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Notificaciones", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text(text = "Alertas de citas nuevas", fontSize = 12.sp, color = Color.Gray)
                }
                Switch(
                    checked = notificaciones,
                    onCheckedChange = { notificaciones = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF8BA793)
                    )
                )
            }
        }
    }
}

@Composable
fun SeguridadAdminCard(onLogoutClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Título
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Security,
                    contentDescription = null,
                    tint = Color(0xFF1A1A1A),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Seguridad",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Opciones de seguridad
            SeguridadItemRow(texto = "Cambiar Contraseña")
            Spacer(modifier = Modifier.height(16.dp))
            SeguridadItemRow(texto = "Ver Sesiones Activas")

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Cerrar Sesión
            Button(
                onClick = onLogoutClick, // Conecta esto con la limpieza de tu SesionUsuario
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFCE8E6) // Fondo rojo claro
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Logout,
                    contentDescription = null,
                    tint = Color(0xFFB3261E), // Rojo de alerta
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cerrar Sesión",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB3261E)
                )
            }
        }
    }
}

@Composable
fun SeguridadItemRow(texto: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: Navegar a la opción */ },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = texto, fontSize = 14.sp, color = Color.DarkGray)
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Ir",
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}