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
import com.example.vetsync.vista.screens.MisMascotasScreen
import com.example.vetsync.vista.screens.PerfilScreen
import com.example.vetsync.vista.theme.*
import com.example.vetsync.modelo.SesionUsuario
import com.example.vetsync.modelo.Usuario
import com.example.vetsync.vista.screens.CitasScreen
import com.example.vetsync.vista.screens.NotificacionesScreen
import com.example.vetsync.modelo.Mascota
import com.example.vetsync.vista.screens.AdminCitasScreen
import com.example.vetsync.vista.screens.AdminPacientesScreen
import com.example.vetsync.vista.screens.AdminPerfilScreen
import com.example.vetsync.vista.screens.AdminReportesScreen


sealed class SubPantallaAdminHome {

}
@Composable
fun AdminHomeScreen(
    nombreUsuario: String = "Laura",
    onNavigateToLogin: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    var subPantallaActual by remember { mutableStateOf<SubPantallaAdminHome?>(null) }

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

        null -> {
            Scaffold(
                containerColor = FondoCrema,
                topBar = {
                    AdminHomeTopBar(
                        mostrarFotoPerfil = (selectedTab != 4),
                        onNotificationsClick = {
                            selectedTab = 3
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
                        1 ->{
                            AdminCitasScreen()
                        }
                        2 -> {
                            AdminPacientesScreen()
                        }
                        3 -> {
                            AdminReportesScreen()
                        }
                        4 ->  {
                            AdminPerfilScreen()
                        }
                    }
                }
            }
        }
    }
}