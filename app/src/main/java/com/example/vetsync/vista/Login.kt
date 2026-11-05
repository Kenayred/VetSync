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
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.example.vetsync.R
import com.example.vetsync.modelo.RolUsuario
import android.util.Log

@Composable
fun LoginScreen(
    controlador: UsuarioControlador = remember { UsuarioControlador() },
    onNavigateToRegister: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    onNavigateToAdmin: () -> Unit ={}
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var keepSession by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMensaje by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    //INICIALIZACION DE LAUNCHER GOOGLE

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let { token ->

                    isLoading = true
                    errorMensaje = null

                    controlador.iniciarSesionConGoogle(
                        idToken = token,
                        onSuccess = { usuarioEncontrado ->
                            isLoading = false
                            Log.e("PRUEBA ROL", "El rol es ${usuarioEncontrado.rol}")
                            if(usuarioEncontrado.rol == RolUsuario.ADMINISTRADOR){
                                onNavigateToAdmin()
                            } else {
                                onLoginSuccess()
                            }

                            Toast.makeText(
                                context,
                                "¡Ha iniciado sesión con Google!",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onError = { mensajeError ->
                            isLoading = false
                            errorMensaje = mensajeError
                        }
                    )
                }
            } catch (e: ApiException) {
                isLoading = false
                // Si el usuario cierra la ventanita de Google antes de elegir cuenta,
                // caerá aquí. Puedes dejar el errorMensaje null o poner un aviso.
            }
        }
    }

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
            onValueChange = { username = it
                errorMensaje = null},
            enabled = !isLoading,
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
            onValueChange = { password = it
                errorMensaje = null },
            enabled = !isLoading,
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

        if (errorMensaje != null) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = errorMensaje.orEmpty(),
                color = RojoAlerta,
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

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
                errorMensaje = null
                isLoading = true

                controlador.iniciarSesion(
                    correo = username.trim(),
                    password = password,

                    onSuccess = { usuarioEncontrado ->
                        isLoading = false

                        SesionUsuario.iniciarSesion(usuarioEncontrado)

                        if(usuarioEncontrado.rol == RolUsuario.ADMINISTRADOR){
                            onNavigateToAdmin()
                        } else {
                            onLoginSuccess()
                        }
                        Toast.makeText(
                            context,
                            "¡Ha iniciado sesión!",
                            Toast.LENGTH_SHORT
                        ).show()
                    },

                    onError = { mensajeError ->
                        isLoading = false
                        errorMensaje = mensajeError
                    }
                )
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
                    text = "Iniciar sesión",
                    fontSize = 16.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(modifier = Modifier.weight(1f), color = Color.Gray, thickness = 1.dp)
            Text(
                text = "O iniciar sesión con",
                modifier = Modifier.padding(horizontal = 8.dp),
                color = Color.Gray
            )
            Divider(modifier = Modifier.weight(1f), color = Color.Gray, thickness = 1.dp)
        }

        Button(
            onClick = { launcher.launch(googleSignInClient.signInIntent) },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
        ){
            Icon(
                painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_google),
                contentDescription = "Logo de Google",
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified
            )
            Text(
                text = "\tGoogle",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
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