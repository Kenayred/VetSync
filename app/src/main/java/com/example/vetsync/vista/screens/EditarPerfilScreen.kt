package com.example.vetsync.vista.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vetsync.controlador.UsuarioControlador
import com.example.vetsync.modelo.ImagenUtils
import com.example.vetsync.modelo.Usuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPerfilScreen(
    usuario: Usuario,
    controlador: UsuarioControlador,
    onBackClick: () -> Unit,
    onPerfilActualizado: () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme

    // Estados inicializados con los datos actuales del usuario.
    var nombre by remember { mutableStateOf(usuario.nombre) }
    var username by remember { mutableStateOf(usuario.username) }
    var telefono by remember { mutableStateOf(usuario.telefono) }
    var correo by remember { mutableStateOf(usuario.correo) }
    var ubicacion by remember { mutableStateOf(usuario.ubicacion) }

    var isLoading by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { uriSeleccionada ->
            val nuevaFotoBase64 = ImagenUtils.uriABase64(
                context,
                uriSeleccionada
            )

            if (nuevaFotoBase64 != null) {
                isLoading = true

                controlador.actualizarFotoDePerfil(
                    nuevaFotoBase64 = nuevaFotoBase64,
                    onSuccess = {
                        isLoading = false

                        Toast.makeText(
                            context,
                            "Foto actualizada",
                            Toast.LENGTH_SHORT
                        ).show()

                        onPerfilActualizado()
                    },
                    onError = { mensaje ->
                        isLoading = false

                        Toast.makeText(
                            context,
                            mensaje,
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
            .statusBarsPadding()
            .background(colorScheme.background)
    ) {
        // Encabezado.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Regresar",
                    tint = colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Editar Perfil",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onBackground
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            val bitmapPerfil = remember(usuario.fotoUrl) {
                ImagenUtils.base64ABitmap(usuario.fotoUrl)
            }

            // Fotografía de perfil y botón para cambiarla.
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(colorScheme.surfaceVariant),
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
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Avatar",
                            modifier = Modifier.size(50.dp),
                            tint = colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = {
                        imagePickerLauncher.launch("image/*")
                    },
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(colorScheme.primary)
                        .border(
                            2.dp,
                            colorScheme.background,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Cambiar foto",
                        tint = colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Campo: Nombre completo.
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = {
                    Text("Nombre completo")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorScheme.primary,
                    focusedLabelColor = colorScheme.primary,
                    cursorColor = colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo: Username.
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = {
                    Text("Username")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorScheme.primary,
                    focusedLabelColor = colorScheme.primary,
                    cursorColor = colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo: Correo electrónico.
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = {
                    Text("Correo electrónico")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorScheme.primary,
                    focusedLabelColor = colorScheme.primary,
                    cursorColor = colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo: Teléfono.
            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = {
                    Text("Teléfono")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorScheme.primary,
                    focusedLabelColor = colorScheme.primary,
                    cursorColor = colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo: Ubicación.
            OutlinedTextField(
                value = ubicacion,
                onValueChange = {
                    ubicacion = it
                },
                label = {
                    Text("Ubicación")
                },
                placeholder = {
                    Text("Ej. San Salvador, El Salvador")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                supportingText = {
                    Text("${ubicacion.length}/150 caracteres")
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorScheme.primary,
                    focusedLabelColor = colorScheme.primary,
                    cursorColor = colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Guardar cambios.
            Button(
                onClick = {
                    val correoCambio =
                        correo.trim() != usuario.correo.trim()

                    isLoading = true

                    controlador.actualizarPerfil(
                        nuevoNombre = nombre,
                        nuevoUsername = username,
                        nuevoTelefono = telefono,
                        nuevoCorreo = correo,
                        nuevoUbicacion = ubicacion,

                        onSuccess = {
                            isLoading = false

                            val mensaje = if (correoCambio) {
                                "Perfil actualizado. Revisa tu nuevo correo para confirmar el cambio."
                            } else {
                                "Perfil actualizado correctamente."
                            }

                            Toast.makeText(
                                context,
                                mensaje,
                                Toast.LENGTH_LONG
                            ).show()

                            onPerfilActualizado()
                        },

                        onError = { mensajeError ->
                            isLoading = false

                            Toast.makeText(
                                context,
                                mensajeError,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Guardar Cambios",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}