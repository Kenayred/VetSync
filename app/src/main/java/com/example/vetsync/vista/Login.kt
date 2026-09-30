package com.example.vetsync.vista

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vetsync.vista.theme.*
import com.example.vetsync.controlador.UsuarioControlador
import com.example.vetsync.modelo.SesionUsuario

@Composable
fun LoginScreen(
    controlador: UsuarioControlador = remember { UsuarioControlador() },
    onNavigateToRegister: () -> Unit = {},
    onLoginSuccess: () -> Unit = {}
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var keepSession by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        //Header
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "VetSync", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Text(text = "Bienvenido a VetSync", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Text(text = "Gestión veterinaria inteligente y cercana.", color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(32.dp))

        // Formulario
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario") },
            placeholder = { Text("kenay") },
            leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = "Email Icon") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VerdePrincipal,
                focusedLabelColor = VerdePrincipal,
                cursorColor = VerdePrincipal,
                focusedLeadingIconColor = VerdePrincipal
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Lock Icon") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VerdePrincipal,
                focusedLabelColor = VerdePrincipal,
                cursorColor = VerdePrincipal,
                focusedLeadingIconColor = VerdePrincipal
            )
        )

        // Opciones adicionales
        TextButton(
            onClick = {  },
            modifier = Modifier.align(Alignment.End),
        ) {
            Text(text="¿Olvidaste tu contraseña?", color=VerdePrincipal)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = keepSession,
                onCheckedChange = { keepSession = it }
            )
            Text("Mantener sesión iniciada")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón principal
        Button(
            onClick = {
                controlador.iniciarSesion(
                    username = username,
                    password = password,
                    onSuccess = {
                        usuarioEncontrado -> SesionUsuario.iniciarSesion(usuarioEncontrado)
                        onLoginSuccess()
                        Toast.makeText(context, "¡Ha iniciado sesion!", Toast.LENGTH_SHORT).show()
                    },
                    onError = { mensajeError ->
                        Toast.makeText(context, mensajeError, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal)
        ) {
            Text("Iniciar sesión", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // Enlace a registro
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("¿No tienes una cuenta?")
            TextButton(onClick =  onNavigateToRegister ) {
                Text(text = "Crear cuenta", fontWeight = FontWeight.Bold, color = VerdePrincipal)
            }
        }
    }
}