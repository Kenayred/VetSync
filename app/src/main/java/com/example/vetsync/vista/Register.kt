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
    var passError by remember {mutableStateOf(false)}
    var confirmar by remember { mutableStateOf("") }
    var terminos by remember { mutableStateOf(false) }
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
                text = "El username es obligatorio",
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
                text = "El nombre es obligatorio",
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
                text = "El correo es obligatorio",
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
                text = "El contraseña es obligatoria",
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
            onValueChange = { confirmar = it },
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

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = terminos,
                onCheckedChange = { terminos = it },
                colors = CheckboxDefaults.colors(checkedColor = VerdePrincipal)
            )
            Text(
                text = "He leído y acepto los Términos de servicio y la Política de privacidad.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                userError = false
                correoError = false
                nombreError = false
                passError = false
                var errorVisual = false

                if(username.isBlank()){
                    userError = true
                    errorVisual = true
                }

                if(correo.isBlank() || !EMAIL_ADDRESS.matcher(correo.trim()).matches()){
                    correoError = true
                    errorVisual = true
                }
                if(nombreCompleto.isBlank()){
                    nombreError = true
                    errorVisual = true
                }
                if(password.isBlank() || password.length < 6){
                    passError = true
                    errorVisual = true
                }

                if(!errorVisual){
                    controlador.registrarUsuario(
                        username = username,
                        nombreCompleto = nombreCompleto,
                        correo = correo,
                        telefono = telefono,
                        contrasena = password,
                        confirmarContrasena = confirmar,
                        terminosAceptados = terminos,
                        fotoUrl = fotoBase64,
                        onSuccess = {
                            Toast.makeText(context, "¡Cuenta creada con éxito!", Toast.LENGTH_SHORT).show()
                            onNavigateToLogin()
                        },
                        onError = { mensajeError ->
                            Toast.makeText(context, mensajeError, Toast.LENGTH_SHORT).show()
                        }
                    )
                }

            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal)
        ) {
            Text("Crear cuenta →", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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