package com.example.vetsync.vista

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.vetsync.vista.components.*
import com.example.vetsync.vista.screens.AdminDashboardScreen
import com.example.vetsync.vista.theme.*
import com.example.vetsync.modelo.SesionUsuario
import com.example.vetsync.vista.screens.AdminNotificacionesScreen
import com.example.vetsync.vista.screens.AdminCitasScreen
import com.example.vetsync.vista.screens.AdminPacientesScreen
import com.example.vetsync.vista.screens.AdminPerfilScreen
import com.example.vetsync.vista.screens.AdminReportesScreen
import com.example.vetsync.controlador.UsuarioControlador
import com.example.vetsync.vista.screens.EditarPerfilScreen
import com.example.vetsync.vista.screens.AdminInformacionClinicaScreen
import androidx.compose.ui.platform.LocalContext
import com.example.vetsync.modelo.PreferenciasAdminRepository

sealed class SubPantallaAdminHome {
    data object Notificaciones : SubPantallaAdminHome()
    data object EditarPerfil : SubPantallaAdminHome()
    data object InformacionClinica : SubPantallaAdminHome()
}
@Composable
fun AdminHomeScreen(
    nombreUsuario: String = "Laura",
    onNavigateToLogin: () -> Unit = {},
    modoOscuro: Boolean = false,
    onModoOscuroChange: (Boolean) -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    var subPantallaActual by remember { mutableStateOf<SubPantallaAdminHome?>(null) }

    val usuarioId = SesionUsuario.idUsuario

    val context = LocalContext.current

    val preferenciasAdminRepository = remember(context) {
        PreferenciasAdminRepository(context)
    }

    var mostrarIndicadorNotificaciones by remember(usuarioId) {
        mutableStateOf(
            preferenciasAdminRepository
                .obtenerMostrarIndicadorNotificaciones(usuarioId)
        )
    }

    val usuarioControlador = remember {
        UsuarioControlador()
    }

    val notificacionRepository = remember {
        com.example.vetsync.modelo.NotificacionRepository()
    }

    var notificacionesNoLeidas by remember(usuarioId) {
        mutableIntStateOf(0)
    }

    DisposableEffect(usuarioId) {

        val listener = notificacionRepository.observarNotificacionesAdmin(
            usuarioId = usuarioId,

            onSuccess = { lista ->
                notificacionesNoLeidas = lista.count {
                    !it.leida
                }
            },

            onFailure = {
                notificacionesNoLeidas = 0
            }
        )

        onDispose {
            listener?.remove()
        }
    }

    BackHandler(
        enabled = subPantallaActual != null || selectedTab != 0
    ) {
        if (subPantallaActual != null) {
            subPantallaActual = null
        } else {
            selectedTab = 0
        }
    }

    when (val pantalla = subPantallaActual) {

        is SubPantallaAdminHome.Notificaciones -> {
            Scaffold(
                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
                topBar = {
                    AdminHomeTopBar(
                        mostrarFotoPerfil = false,
                        onNotificationsClick = {
                            // Ya estás en notificaciones; opcionalmente puedes volver atrás:
                            subPantallaActual = null
                        },
                        notificacionesNoLeidas = if (
                            mostrarIndicadorNotificaciones
                        ) {
                            notificacionesNoLeidas
                        } else {
                            0
                        }
                    )
                },
                bottomBar = {
                    AdminHomeBottomNavigationBar(
                        selectedTab = selectedTab,
                        onTabSelected = { tab ->
                            subPantallaActual = null
                            selectedTab = tab
                        }
                    )
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    AdminNotificacionesScreen()
                }
            }
        }

        is SubPantallaAdminHome.EditarPerfil -> {

            Scaffold(
                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background,

                topBar = {
                    AdminHomeTopBar(
                        mostrarFotoPerfil = false,
                        onNotificationsClick = {
                            subPantallaActual =
                                SubPantallaAdminHome.Notificaciones
                        },
                        notificacionesNoLeidas = if (
                            mostrarIndicadorNotificaciones
                        ) {
                            notificacionesNoLeidas
                        } else {
                            0
                        }
                    )
                },

                bottomBar = {
                    AdminHomeBottomNavigationBar(
                        selectedTab = selectedTab,
                        onTabSelected = { tab ->
                            subPantallaActual = null
                            selectedTab = tab
                        }
                    )
                }

            ) { paddingValues ->

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {

                    val usuarioActual = SesionUsuario.usuarioActual

                    if (usuarioActual != null) {

                        EditarPerfilScreen(
                            usuario = usuarioActual,
                            controlador = usuarioControlador,

                            onBackClick = {
                                subPantallaActual = null
                                selectedTab = 4
                            },

                            onPerfilActualizado = {
                                subPantallaActual = null
                                selectedTab = 4
                            }
                        )

                    } else {

                        androidx.compose.material3.Text(
                            text = "No se encontró una sesión administrativa activa."
                        )
                    }
                }
            }
        }

        is SubPantallaAdminHome.InformacionClinica -> {

            Scaffold(
                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background,

                topBar = {
                    AdminHomeTopBar(
                        mostrarFotoPerfil = false,
                        onNotificationsClick = {
                            subPantallaActual =
                                SubPantallaAdminHome.Notificaciones
                        },
                        notificacionesNoLeidas = if (
                            mostrarIndicadorNotificaciones
                        ) {
                            notificacionesNoLeidas
                        } else {
                            0
                        }
                    )
                },

                bottomBar = {
                    AdminHomeBottomNavigationBar(
                        selectedTab = selectedTab,
                        onTabSelected = { tab ->

                            subPantallaActual = null
                            selectedTab = tab
                        }
                    )
                }

            ) { paddingValues ->

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {

                    AdminInformacionClinicaScreen(

                        onBackClick = {
                            subPantallaActual = null
                            selectedTab = 4
                        }
                    )
                }
            }
        }

        null -> {
            Scaffold(
                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
                topBar = {
                    AdminHomeTopBar(
                        mostrarFotoPerfil = (selectedTab != 4),
                        onNotificationsClick = {
                            subPantallaActual = SubPantallaAdminHome.Notificaciones
                        },
                        notificacionesNoLeidas = if (
                            mostrarIndicadorNotificaciones
                        ) {
                            notificacionesNoLeidas
                        } else {
                            0
                        }
                    )
                },
                bottomBar = {
                    AdminHomeBottomNavigationBar(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {

                    when (selectedTab) {
                        0 -> AdminDashboardScreen(
                            onNavigateToCitas = {
                                selectedTab = 1
                            },
                            onNavigateToPacientes = {
                                selectedTab = 2
                            }
                        )
                        1 -> {
                            AdminCitasScreen()
                        }
                        2 -> {
                            AdminPacientesScreen()
                        }
                        3 -> {
                            AdminReportesScreen()
                        }
                        4 -> {
                            AdminPerfilScreen(
                                usuario = SesionUsuario.usuarioActual,
                                controlador = usuarioControlador,

                                onEditProfileClick = {
                                    subPantallaActual =
                                        SubPantallaAdminHome.EditarPerfil
                                },

                                onInformacionClinicaClick = {
                                    subPantallaActual =
                                        SubPantallaAdminHome.InformacionClinica
                                },

                                onLogoutClick = {
                                    usuarioControlador.cerrarSesion()
                                    selectedTab = 0
                                    onNavigateToLogin()
                                },

                                mostrarIndicadorNotificaciones = mostrarIndicadorNotificaciones,
                                onMostrarIndicadorNotificacionesChange = { mostrar ->

                                    mostrarIndicadorNotificaciones = mostrar

                                    preferenciasAdminRepository
                                        .guardarMostrarIndicadorNotificaciones(
                                            usuarioId = usuarioId,
                                            mostrar = mostrar
                                        )
                                },
                                modoOscuro = modoOscuro,
                                onModoOscuroChange = onModoOscuroChange
                            )
                        }
                    }
                }
            }
        }
    }
}