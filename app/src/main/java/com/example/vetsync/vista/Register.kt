package com.example.vetsync.vista

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vetsync.vista.theme.*
import com.example.vetsync.controlador.UsuarioControlador
import androidx.compose.ui.platform.LocalContext
import com.example.vetsync.modelo.ImagenUtils
import android.util.Patterns.EMAIL_ADDRESS
@Composable
fun RegisterScreen(
    controlador: UsuarioControlador = remember { UsuarioControlador() },
    onNavigateToLogin: () -> Unit = {}
) {
    var nombreCompleto by remember { mutableStateOf("") }
    var nombreError by remember { mutableStateOf(false) }
    var username by remember {mutableStateOf("") }
    var userError by remember {mutableStateOf(false) }
    var correo by remember { mutableStateOf("") }
    var correoError by remember { mutableStateOf(false) }
    var telefono by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passError by remember { mutableStateOf(false) }
    var confirmar by remember { mutableStateOf("") }
    var confirmarError by remember { mutableStateOf(false) }
    var terminos by remember { mutableStateOf(false) }
    var terminosError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorRegistro by remember { mutableStateOf<String?>(null) }
    var fotoBase64 by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val bitmapPerfil = remember(fotoBase64) {
        if (fotoBase64.isNotEmpty()) ImagenUtils.base64ABitmap(fotoBase64) else null
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { uriSeleccionada ->
            val base64 = ImagenUtils.uriABase64(context, uriSeleccionada)
            if (base64 != null) {
                fotoBase64 = base64
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "VetSync",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = VerdePrincipal
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Crear cuenta",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Ingresa tus datos para registrarte como usuario.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEBEBEB)),
                    contentAlignment = Alignment.Center
                ) {
                    if (bitmapPerfil != null) {
                        Image(
                            bitmap = bitmapPerfil,
                            contentDescription = "Foto de perfil elegida",
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar por defecto",
                            modifier = Modifier.size(50.dp),
                            tint = Color.Gray
                        )
                    }
                }

                IconButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    enabled = !isLoading,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(VerdePrincipal)
                        .border(2.dp, Color.White, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Seleccionar foto",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        // Campo: Nombre de usuario
        Text(
            text = "Username",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                userError = false
            },
            enabled = !isLoading,
            isError = userError,
            placeholder = { Text("Ej. Martinez02") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VerdePrincipal,
                focusedLabelColor = VerdePrincipal,
                cursorColor = VerdePrincipal,
                focusedLeadingIconColor = VerdePrincipal,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                errorBorderColor = RojoAlerta
            )
        )

        if(userError){
            Text(
                text = "Usa entre 3 y 20 caracteres: letras, números, punto, guion o guion bajo.",
                color = RojoAlerta,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Campo: Nombre completo
        Text(
            text = "Nombre completo",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = nombreCompleto,
            onValueChange = {
                nombreCompleto = it
                nombreError = false
            },
            enabled = !isLoading,
            isError = nombreError,
            placeholder = { Text("Ej. Ana Martínez") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VerdePrincipal,
                focusedLabelColor = VerdePrincipal,
                cursorColor = VerdePrincipal,
                focusedLeadingIconColor = VerdePrincipal,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                errorBorderColor = RojoAlerta
            )
        )

        if(nombreError){
            Text(
                text = "El nombre completo debe tener al menos 3 caracteres.",
                color = RojoAlerta,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Campo: Correo electrónico
        Text(
            text = "Correo electrónico",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = correo,
            onValueChange = {
                correo = it
                correoError = false
            },
            enabled = !isLoading,
            isError = correoError,
            placeholder = { Text("ana@ejemplo.com") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VerdePrincipal,
                focusedLabelColor = VerdePrincipal,
                cursorColor = VerdePrincipal,
                focusedLeadingIconColor = VerdePrincipal,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                errorBorderColor = RojoAlerta
            )
        )

        if(correoError){
            Text(
                text = "Ingresa un correo electrónico válido.",
                color = RojoAlerta,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Campo: Teléfono (opcional)
        Text(
            text = "Teléfono (opcional)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            enabled = !isLoading,
            placeholder = { Text("+503 7499 0620") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VerdePrincipal,
                focusedLabelColor = VerdePrincipal,
                cursorColor = VerdePrincipal,
                focusedLeadingIconColor = VerdePrincipal,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo: Contraseña
        Text(
            text = "Contraseña",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passError = false
            },
            enabled = !isLoading,
            isError = passError,
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VerdePrincipal,
                focusedLabelColor = VerdePrincipal,
                cursorColor = VerdePrincipal,
                focusedLeadingIconColor = VerdePrincipal,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                errorBorderColor = RojoAlerta
            )
        )

        if(passError){
            Text(
                text = "La contraseña debe tener al menos 6 caracteres.",
                color = RojoAlerta,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Campo: Confirmar Contraseña
        Text(
            text = "Confirmar Contraseña",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = confirmar,
            onValueChange = {
                confirmar = it
                confirmarError = false
                errorRegistro = null
            },
            enabled = !isLoading,
            isError = confirmarError,
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VerdePrincipal,
                focusedLabelColor = VerdePrincipal,
                cursorColor = VerdePrincipal,
                focusedLeadingIconColor = VerdePrincipal,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
        )

        if (confirmarError) {
            Text(
                text = "Las contraseñas no coinciden o la confirmación está vacía.",
                color = RojoAlerta,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = terminos,
                enabled = !isLoading,
                onCheckedChange = {
                    terminos = it
                    terminosError = !it
                    errorRegistro = null
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = VerdePrincipal
                )
            )
            Text(
                text = "He leído y acepto los Términos de servicio y la Política de privacidad.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (terminosError) {
            Text(
                text = "Debes aceptar los términos y condiciones para registrarte.",
                color = RojoAlerta,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                // Limpiar errores anteriores.
                userError = false
                correoError = false
                nombreError = false
                passError = false
                confirmarError = false
                terminosError = false
                errorRegistro = null

                var errorVisual = false

                val usernameLimpio = username.trim()
                val nombreLimpio = nombreCompleto.trim()
                val correoLimpio = correo.trim()

                val formatoUsername =
                    Regex("^[\\p{L}\\p{N}._-]{3,20}$")

                // Validar el nombre de usuario.
                if (!formatoUsername.matches(usernameLimpio)) {
                    userError = true
                    errorVisual = true
                }

                // Validar el nombre completo.
                if (nombreLimpio.length < 3) {
                    nombreError = true
                    errorVisual = true
                }

                // Validar el correo.
                if (
                    correoLimpio.isBlank() ||
                    !EMAIL_ADDRESS.matcher(correoLimpio).matches()
                ) {
                    correoError = true
                    errorVisual = true
                }

                // Validar la contraseña.
                if (password.length < 6) {
                    passError = true
                    errorVisual = true
                }

                // Validar la confirmación.
                if (
                    confirmar.isBlank() ||
                    confirmar != password
                ) {
                    confirmarError = true
                    errorVisual = true
                }

                // Validar los términos.
                if (!terminos) {
                    terminosError = true
                    errorVisual = true
                }

                // Registrar solo si todas las validaciones pasan.
                if (!errorVisual) {
                    isLoading = true

                    controlador.registrarUsuario(
                        username = usernameLimpio,
                        nombreCompleto = nombreLimpio,
                        correo = correoLimpio,
                        telefono = telefono,
                        contrasena = password,
                        confirmarContrasena = confirmar,
                        terminosAceptados = terminos,
                        fotoUrl = fotoBase64,

                        onSuccess = {
                            isLoading = false

                            Toast.makeText(
                                context,
                                "¡Cuenta creada con éxito!",
                                Toast.LENGTH_SHORT
                            ).show()

                            onNavigateToLogin()
                        },

                        onError = { mensajeError ->
                            isLoading = false
                            errorRegistro = mensajeError
                        }
                    )
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdePrincipal
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Crear cuenta →",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (errorRegistro != null) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = errorRegistro.orEmpty(),
                color = RojoAlerta,
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Volver al login
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "¿Ya tienes una cuenta?", fontSize = 14.sp)
            TextButton(onClick = onNavigateToLogin) {
                Text(
                    text = "Inicia sesión",
                    fontWeight = FontWeight.Bold,
                    color = VerdePrincipal
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}