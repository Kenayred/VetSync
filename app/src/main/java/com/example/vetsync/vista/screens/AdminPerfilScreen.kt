package com.example.vetsync.vista.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vetsync.modelo.ImagenUtils
import com.example.vetsync.modelo.RolUsuario
import com.example.vetsync.modelo.Usuario

@Composable
fun AdminPerfilScreen(
    usuario: Usuario?,
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit = {}
) {

    var mostrarDialogoCerrarSesion by remember {
        mutableStateOf(false)
    }

    val esAdministrador = usuario?.rol == RolUsuario.ADMINISTRADOR

    val fotoAdministrador = remember(usuario?.fotoUrl) {
        usuario?.fotoUrl?.let { foto ->
            runCatching {
                ImagenUtils.base64ABitmap(foto)
            }.getOrNull()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFDFCF9))
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            )
    ) {

        Text(
            text = "Perfil de Administrador",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // INFORMACIÓN DEL ADMINISTRADOR

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Color(0xFFE5E5E5)
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // FOTO DE PERFIL

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE2EFE7))
                        .border(
                            2.dp,
                            Color(0xFFCDE2D4),
                            CircleShape
                        ),

                    contentAlignment = Alignment.Center
                ) {

                    if (fotoAdministrador != null) {

                        Image(
                            bitmap = fotoAdministrador,
                            contentDescription = "Foto del administrador",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                    } else {

                        Text(
                            text = usuario?.nombre
                                ?.firstOrNull()
                                ?.uppercase()
                                ?: "A",

                            fontSize = 38.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E5B4B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = usuario?.nombre
                        ?.takeIf { it.isNotBlank() }
                        ?: "Administrador",

                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )

                Spacer(modifier = Modifier.height(5.dp))

                Surface(
                    color = Color(0xFFE2EFE7),
                    shape = RoundedCornerShape(20.dp)
                ) {

                    Text(
                        text = "Administrador",
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 6.dp
                        ),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2E5B4B)
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                HorizontalDivider(
                    color = Color(0xFFEEEEEE)
                )

                Spacer(modifier = Modifier.height(18.dp))

                if (esAdministrador && usuario != null) {

                    DatoPerfilAdmin(
                        titulo = "Nombre completo",
                        valor = usuario.nombre
                    )

                    DatoPerfilAdmin(
                        titulo = "Nombre de usuario",
                        valor = usuario.username
                    )

                    DatoPerfilAdmin(
                        titulo = "Correo electrónico",
                        valor = usuario.correo
                    )

                    DatoPerfilAdmin(
                        titulo = "Teléfono",
                        valor = usuario.telefono
                    )

                    DatoPerfilAdmin(
                        titulo = "Rol de usuario",
                        valor = usuario.rol.name
                    )

                } else {

                    Text(
                        text = "No se encontró una sesión de administrador activa.",
                        fontSize = 14.sp,
                        color = Color(0xFFB3261E)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SEGURIDAD Y CIERRE DE SESIÓN

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Color(0xFFE5E5E5)
            )
        ) {

            Column(
                modifier = Modifier.padding(18.dp)
            ) {

                Text(
                    text = "Seguridad",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Gestiona el acceso a tu cuenta de administrador.",
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        mostrarDialogoCerrarSesion = true
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),

                    shape = RoundedCornerShape(10.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFCE8E6)
                    )
                ) {

                    Text(
                        text = "Cerrar sesión",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB3261E)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }

    // CONFIRMACIÓN DE CIERRE DE SESIÓN

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
                    text = "¿Estás seguro de que deseas cerrar tu sesión de administrador?"
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
                        color = Color(0xFFB3261E),
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
                        color = Color.Gray
                    )
                }
            },

            containerColor = Color.White
        )
    }
}

@Composable
private fun DatoPerfilAdmin(
    titulo: String,
    valor: String
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {

        Text(
            text = titulo,
            fontSize = 12.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = valor.ifBlank {
                "No registrado"
            },
            fontSize = 14.sp,
            color = Color(0xFF252525),
            fontWeight = FontWeight.Medium
        )
    }
}