package com.example.vetsync.vista

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.vetsync.vista.components.*
import com.example.vetsync.vista.AgregarMascotaScreen
import com.example.vetsync.vista.screens.DashboardScreen

sealed class SubPantallaHome {
    object AgregarMascota : SubPantallaHome()
}
@Composable
fun HomeScreen(
    nombreUsuario: String = "Laura"
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    var subPantallaActual by remember { mutableStateOf<SubPantallaHome?>(null) }

    BackHandler(enabled = subPantallaActual != null) {
        subPantallaActual = null
    }

    when (val pantalla = subPantallaActual) {
        is SubPantallaHome.AgregarMascota -> {
            AgregarMascotaScreen(
                onBackClick = { subPantallaActual = null },
                onCancelarClick = { subPantallaActual = null },
                onGuardarClick = { _, _, _, _, _, _, _, _, _ ->
                    subPantallaActual = null
                }
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
                        1 -> { /* MisMascotasScreen(onAgregarClick = { subPantallaActual = SubPantallaHome.AgregarMascota }) */ }
                        2 -> { /* CitasScreen() */ }
                        3 -> { /* NotificacionesScreen() */ }
                        4 -> { /* PerfilScreen() */ }
                    }
                }
            }
        }
    }
}