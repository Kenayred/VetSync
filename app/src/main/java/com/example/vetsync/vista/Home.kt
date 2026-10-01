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

sealed class SubPantallaHome {
    object AgregarMascota : SubPantallaHome()
}
@Composable
fun HomeScreen(
    nombreUsuario: String = "Laura",
    onNavigateToLogin: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    var subPantallaActual by remember { mutableStateOf<SubPantallaHome?>(null) }

    when (val pantalla = subPantallaActual) {
        is SubPantallaHome.AgregarMascota -> {
            AgregarMascotaScreen(
                onBackClick = { subPantallaActual = null },
                onCancelarClick = { subPantallaActual = null },
                onMascotaGuardada = { subPantallaActual =null }
            )
        }
        null -> {
            Scaffold(
                containerColor = FondoCrema,
                topBar = { HomeTopBar() },
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
                            onAgregarMascotaClick = { subPantallaActual = SubPantallaHome.AgregarMascota },
                            onMisMascotasClick = { selectedTab = 1 },
                        )
                        1 ->
                            MisMascotasScreen(
                            onAgregarMascotaClick = { subPantallaActual = SubPantallaHome.AgregarMascota }
                        )

                        2 -> { /* CitasScreen() */ }
                        3 -> { /* NotificacionesScreen() */ }
                        4 ->  {
                            // Validamos que el usuario no sea nulo, si lo es mandamos uno vacío temporal
                            val usuarioActivo = SesionUsuario.usuarioActual ?: com.example.vetsync.modelo.Usuario()

                            PerfilScreen(
                                usuario = usuarioActivo,
                                onLogoutClick = {
                                    // Limpias la sesión actual
                                    SesionUsuario.cerrarSesion()
                                    //SesionUsuario.idUsuario = ""
                                    // Navegas a la pantalla de inicio de sesión
                                    onNavigateToLogin()
                                },
                                onEditProfileClick = {
                                    // Aquí puedes crear un SubPantallaHome.EditarPerfil en el futuro
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}