package com.example.vetsync

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.vetsync.controlador.UsuarioControlador
import com.example.vetsync.modelo.RolUsuario
import com.example.vetsync.vista.AdminHomeScreen
import com.example.vetsync.vista.HomeScreen
import com.example.vetsync.vista.LoginScreen
import com.example.vetsync.vista.RegisterScreen
import com.example.vetsync.vista.screens.LoadingScreen
import com.example.vetsync.vista.theme.VetSyncAppTheme
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import com.example.vetsync.notificaciones.NotificacionSistema
import android.util.Log
import com.example.vetsync.controlador.CitaControlador
import com.example.vetsync.modelo.SesionUsuario
import com.example.vetsync.notificaciones.ProgramadorRecordatorios

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = android.graphics.Color.TRANSPARENT,
                darkScrim = android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = android.graphics.Color.TRANSPARENT,
                darkScrim = android.graphics.Color.TRANSPARENT
            )
        )

        setContent {
            VetSyncAppTheme {

                val context = LocalContext.current

                var permisoSolicitado by remember {
                    mutableStateOf(false)
                }

                // Solicitar autorización para mostrar notificaciones.
                val solicitarPermisoNotificaciones =
                    rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission()
                    ) { autorizado ->

                        // La aplicación seguirá funcionando aunque el usuario
                        // decida no autorizar las notificaciones.
                        if (!autorizado) {
                            android.util.Log.d(
                                "VetSync",
                                "El usuario no autorizó las notificaciones."
                            )
                        }
                    }

                // Crear el canal de notificaciones.
                LaunchedEffect(Unit) {
                    NotificacionSistema.crearCanal(context)
                }

                val usuarioControlador = remember {
                    UsuarioControlador()
                }

                var pantallaActual by remember {
                    mutableStateOf("loading")
                }

                var isLoading by remember {
                    mutableStateOf(true)
                }

                // Solicitar el permiso cuando el cliente acceda a su inicio.
                LaunchedEffect(isLoading, pantallaActual) {

                    if (
                        !isLoading &&
                        pantallaActual == "home" &&
                        !permisoSolicitado
                    ) {

                        permisoSolicitado = true

                        if (
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {

                            solicitarPermisoNotificaciones.launch(
                                Manifest.permission.POST_NOTIFICATIONS
                            )
                        }
                    }
                }

                // Recuperar la sesión al iniciar la aplicación.
                LaunchedEffect(Unit) {

                    usuarioControlador.restaurarSesion(

                        onSuccess = { usuario ->

                            pantallaActual =
                                if (usuario.rol == RolUsuario.ADMINISTRADOR) {
                                    "adminHome"
                                } else {
                                    "home"
                                }

                            isLoading = false
                        },

                        onNoSession = {

                            pantallaActual = "login"
                            isLoading = false
                        },

                        onError = { mensaje ->

                            Toast.makeText(
                                this@MainActivity,
                                mensaje,
                                Toast.LENGTH_LONG
                            ).show()

                            pantallaActual = "login"
                            isLoading = false
                        }
                    )
                }

                // --------------------------------------------------
                // SINCRONIZAR RECORDATORIOS AL INGRESAR COMO CLIENTE
                // --------------------------------------------------

                LaunchedEffect(
                    isLoading,
                    pantallaActual
                ) {

                    if (
                        !isLoading &&
                        pantallaActual == "home" &&
                        SesionUsuario.idUsuario.isNotBlank()
                    ) {

                        CitaControlador().obtenerCitasUsuario(

                            onSuccess = { lista ->

                                ProgramadorRecordatorios.sincronizarRecordatorios(
                                    context = context.applicationContext,
                                    citas = lista
                                )

                                Log.d(
                                    "VetSync",
                                    "Recordatorios sincronizados: ${lista.size} citas."
                                )
                            },

                            onError = { error ->

                                Log.w(
                                    "VetSync",
                                    "No se pudieron sincronizar los recordatorios: $error"
                                )
                            }
                        )
                    }
                }

                if (isLoading) {

                    LoadingScreen()

                } else {

                    when (pantallaActual) {

                        "login" -> {

                            LoginScreen(
                                onNavigateToRegister = {
                                    pantallaActual = "register"
                                },

                                onLoginSuccess = {
                                    pantallaActual = "home"
                                },

                                onNavigateToAdmin = {
                                    pantallaActual = "adminHome"
                                }
                            )
                        }

                        "register" -> {

                            RegisterScreen(
                                onNavigateToLogin = {
                                    pantallaActual = "login"
                                }
                            )
                        }

                        "home" -> {

                            HomeScreen(
                                onNavigateToLogin = {
                                    ProgramadorRecordatorios.cancelarTodosLosRecordatorios(
                                        context.applicationContext
                                    )

                                    usuarioControlador.cerrarSesion()
                                    pantallaActual = "login"
                                }
                            )
                        }

                        "adminHome" -> {

                            AdminHomeScreen(
                                onNavigateToLogin = {
                                    ProgramadorRecordatorios.cancelarTodosLosRecordatorios(
                                        context.applicationContext
                                    )

                                    usuarioControlador.cerrarSesion()
                                    pantallaActual = "login"
                                }
                            )
                        }

                        else -> {
                            LoginScreen(
                                onNavigateToRegister = {
                                    pantallaActual = "register"
                                },
                                onLoginSuccess = {
                                    pantallaActual = "home"
                                },
                                onNavigateToAdmin = {
                                    pantallaActual = "adminHome"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}