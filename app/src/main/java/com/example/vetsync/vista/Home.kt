package com.example.vetsync.vista

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.vetsync.vista.components.*
import com.example.vetsync.vista.screens.DashboardScreen
import com.example.vetsync.vista.screens.MisMascotasScreen
import com.example.vetsync.vista.screens.PerfilScreen
import com.example.vetsync.vista.theme.*
import com.example.vetsync.modelo.SesionUsuario
import com.example.vetsync.modelo.Usuario
import com.example.vetsync.vista.screens.EditarPerfilScreen
import com.example.vetsync.controlador.UsuarioControlador
import com.example.vetsync.vista.screens.CitasScreen
import com.example.vetsync.vista.screens.NotificacionesScreen
import com.example.vetsync.modelo.Mascota
import com.example.vetsync.vista.screens.AgendarCitaScreen
import com.example.vetsync.vista.screens.HistorialClinicoScreen

sealed class SubPantallaHome {
    object AgregarMascota : SubPantallaHome()
    object EditarPerfil : SubPantallaHome()

    data class AgendarCita(
        val mascota: Mascota? = null
    ) : SubPantallaHome()

    data class HistorialClinico(
        val mascota: Mascota? = null
    ) : SubPantallaHome()
}
@Composable
fun HomeScreen(
    nombreUsuario: String = "Laura",
    onNavigateToLogin: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    var subPantallaActual by remember { mutableStateOf<SubPantallaHome?>(null) }

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
        is SubPantallaHome.AgregarMascota -> {
            AgregarMascotaScreen(
                onBackClick = { subPantallaActual = null },
                onCancelarClick = { subPantallaActual = null },
                onMascotaGuardada = { subPantallaActual =null }
            )
        }
        is SubPantallaHome.EditarPerfil -> {
            EditarPerfilScreen(
                usuario = SesionUsuario.usuarioActual!!,
                controlador = UsuarioControlador(),
                onBackClick = { subPantallaActual = null },
                onPerfilActualizado = {
                    subPantallaActual = null
                }
            )
        }
        is SubPantallaHome.AgendarCita -> {
            AgendarCitaScreen(
                mascota = pantalla.mascota,
                onBackClick = {
                    subPantallaActual = null
                },
                onCitaCreada = {
                    subPantallaActual = null
                    selectedTab = 2
                }
            )
        }

        is SubPantallaHome.HistorialClinico -> {
            HistorialClinicoScreen(
                mascota = pantalla.mascota,
                onBackClick = {
                    subPantallaActual = null
                }
            )
        }
        null -> {
            Scaffold(
                containerColor = FondoCrema,
                topBar = {
                    HomeTopBar(
                        mostrarFotoPerfil = (selectedTab != 4),
                        onNotificationsClick = {
                            selectedTab = 3
                        }
                    )
                },
                bottomBar = {
                    HomeBottomNavigationBar(
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
                        0 -> DashboardScreen(
                            onAgregarMascotaClick = {
                                subPantallaActual = SubPantallaHome.AgregarMascota
                            },
                            onMisMascotasClick = {
                                selectedTab = 1
                            },
                            onAgendarCitaClick = {
                                subPantallaActual = SubPantallaHome.AgendarCita()
                            },
                            onHistorialClick = {
                                subPantallaActual = SubPantallaHome.HistorialClinico()
                            }
                        )
                        1 -> MisMascotasScreen(
                            onAgregarMascotaClick = {
                                subPantallaActual = SubPantallaHome.AgregarMascota
                            },
                            onAgendarCitaClick = { mascota ->
                                subPantallaActual = SubPantallaHome.AgendarCita(mascota)
                            },
                            onHistorialClick = { mascota ->
                                subPantallaActual = SubPantallaHome.HistorialClinico(mascota)
                            }
                        )

                        2 -> {
                            CitasScreen(
                                onAgendarCitaClick = {
                                    subPantallaActual = SubPantallaHome.AgendarCita()
                                }
                            )
                        }

                        3 -> {
                            NotificacionesScreen()
                        }
                        4 ->  {
                            // Validamos que el usuario no sea nulo
                            val usuarioActivo = SesionUsuario.usuarioActual ?: Usuario()

                            PerfilScreen(
                                usuario = usuarioActivo,
                                onLogoutClick = {
                                    SesionUsuario.cerrarSesion()
                                    onNavigateToLogin()
                                },
                                onEditProfileClick = {
                                    subPantallaActual = SubPantallaHome.EditarPerfil
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}