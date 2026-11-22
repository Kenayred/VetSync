package com.example.vetsync

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Build
import android.widget.Toast
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.vetsync.controlador.UsuarioControlador
import com.example.vetsync.controlador.CitaControlador
import com.example.vetsync.modelo.RolUsuario
import com.example.vetsync.modelo.SesionUsuario
import com.example.vetsync.modelo.PreferenciasTemaRepository
import com.example.vetsync.vista.AdminHomeScreen
import com.example.vetsync.vista.HomeScreen
import com.example.vetsync.vista.LoginScreen
import com.example.vetsync.vista.RegisterScreen
import com.example.vetsync.vista.screens.LoadingScreen
import com.example.vetsync.vista.theme.VetSyncAppTheme
import com.example.vetsync.notificaciones.NotificacionSistema
import com.example.vetsync.notificaciones.ProgramadorRecordatorios
import com.example.vetsync.util.GestorIdioma

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(
            GestorIdioma.aplicarConfiguracion(newBase)
        )
    }

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

            val context = LocalContext.current

            // Repositorio de preferencias del tema.
            val preferenciasTemaRepository = remember(context) {
                PreferenciasTemaRepository(context)
            }

            // Estado global del modo oscuro.
            var modoOscuro by remember {
                mutableStateOf(false)
            }

            // Aplicar el tema seleccionado a toda la aplicación.
            VetSyncAppTheme(
                darkTheme = modoOscuro
            ) {

                // Adaptar los iconos de las barras del sistema.
                SideEffect {

                    val controller = WindowCompat.getInsetsController(
                        window,
                        window.decorView
                    )

                    controller.isAppearanceLightStatusBars = !modoOscuro
                    controller.isAppearanceLightNavigationBars = !modoOscuro
                }

                var permisoSolicitado by remember {
                    mutableStateOf(false)
                }

                // Solicitar autorización para mostrar notificaciones.
                val solicitarPermisoNotificaciones =
                    rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission()
                    ) { autorizado ->

                        if (!autorizado) {
                            Log.d(
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

                // Solicitar permiso cuando el cliente acceda a su inicio.
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

                // --------------------------------------------------
                // RECUPERAR LA SESIÓN AL INICIAR LA APLICACIÓN
                // --------------------------------------------------

                LaunchedEffect(Unit) {

                    usuarioControlador.restaurarSesion(

                        onSuccess = { usuario ->

                            // Recuperar el tema guardado para esta cuenta.
                            modoOscuro = preferenciasTemaRepository
                                .obtenerModoOscuro(SesionUsuario.idUsuario)

                            pantallaActual =
                                if (usuario.rol == RolUsuario.ADMINISTRADOR) {
                                    "adminHome"
                                } else {
                                    "home"
                                }

                            isLoading = false
                        },

                        onNoSession = {

                            modoOscuro = false
                            pantallaActual = "login"
                            isLoading = false
                        },

                        onError = { mensaje ->

                            Toast.makeText(
                                this@MainActivity,
                                mensaje,
                                Toast.LENGTH_LONG
                            ).show()

                            modoOscuro = false
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

                // --------------------------------------------------
                // NAVEGACIÓN PRINCIPAL
                // --------------------------------------------------

                if (isLoading) {

                    LoadingScreen()

                } else {

                    when (pantallaActual) {

                        // ------------------------------------------
                        // LOGIN
                        // ------------------------------------------

                        "login" -> {

                            LoginScreen(

                                onNavigateToRegister = {
                                    pantallaActual = "register"
                                },

                                onLoginSuccess = {

                                    // Recuperar la preferencia del cliente.
                                    modoOscuro = preferenciasTemaRepository
                                        .obtenerModoOscuro(SesionUsuario.idUsuario)

                                    pantallaActual = "home"
                                },

                                onNavigateToAdmin = {

                                    // Recuperar la preferencia del administrador.
                                    modoOscuro = preferenciasTemaRepository
                                        .obtenerModoOscuro(SesionUsuario.idUsuario)

                                    pantallaActual = "adminHome"
                                }
                            )
                        }

                        // ------------------------------------------
                        // REGISTRO
                        // ------------------------------------------

                        "register" -> {

                            RegisterScreen(
                                onNavigateToLogin = {
                                    pantallaActual = "login"
                                }
                            )
                        }

                        // ------------------------------------------
                        // INICIO DEL CLIENTE
                        // ------------------------------------------

                        "home" -> {

                            HomeScreen(

                                onNavigateToLogin = {

                                    ProgramadorRecordatorios
                                        .cancelarTodosLosRecordatorios(
                                            context.applicationContext
                                        )

                                    usuarioControlador.cerrarSesion()

                                    // Restablecer el tema al cerrar sesión.
                                    modoOscuro = false

                                    pantallaActual = "login"
                                },

                                modoOscuro = modoOscuro,

                                onModoOscuroChange = { nuevoModo ->

                                    modoOscuro = nuevoModo

                                    preferenciasTemaRepository.guardarModoOscuro(
                                        usuarioId = SesionUsuario.idUsuario,
                                        modoOscuro = nuevoModo
                                    )
                                }
                            )
                        }

                        // ------------------------------------------
                        // INICIO DEL ADMINISTRADOR
                        // ------------------------------------------

                        "adminHome" -> {

                            AdminHomeScreen(

                                onNavigateToLogin = {

                                    ProgramadorRecordatorios
                                        .cancelarTodosLosRecordatorios(
                                            context.applicationContext
                                        )

                                    usuarioControlador.cerrarSesion()

                                    // Restablecer el tema al cerrar sesión.
                                    modoOscuro = false

                                    pantallaActual = "login"
                                },

                                modoOscuro = modoOscuro,

                                onModoOscuroChange = { nuevoModo ->

                                    modoOscuro = nuevoModo

                                    preferenciasTemaRepository.guardarModoOscuro(
                                        usuarioId = SesionUsuario.idUsuario,
                                        modoOscuro = nuevoModo
                                    )
                                }
                            )
                        }

                        // ------------------------------------------
                        // RUTA ALTERNATIVA
                        // ------------------------------------------

                        else -> {

                            LoginScreen(

                                onNavigateToRegister = {
                                    pantallaActual = "register"
                                },

                                onLoginSuccess = {

                                    modoOscuro = preferenciasTemaRepository
                                        .obtenerModoOscuro(SesionUsuario.idUsuario)

                                    pantallaActual = "home"
                                },

                                onNavigateToAdmin = {

                                    modoOscuro = preferenciasTemaRepository
                                        .obtenerModoOscuro(SesionUsuario.idUsuario)

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