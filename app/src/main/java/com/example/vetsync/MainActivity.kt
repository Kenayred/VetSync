package com.example.vetsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import com.example.vetsync.vista.HomeScreen
import com.example.vetsync.vista.LoginScreen
import com.example.vetsync.vista.RegisterScreen
import com.example.vetsync.vista.screens.LoadingScreen
import androidx.activity.SystemBarStyle
import android.graphics.Color
import androidx.activity.enableEdgeToEdge

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
            MaterialTheme {

                var pantallaActual by remember { mutableStateOf("login") }
                var isLoading by remember { mutableStateOf(true) }
                // Guardamos el nombre del usuario que inicia sesión
                var nombreUsuario by remember { mutableStateOf("Laura") }

                LaunchedEffect(isLoading) {
                    if (isLoading) {
                        delay(2000)
                        isLoading = false
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
                                    isLoading = true
                                },
                                onLoginSuccess = {
                                    pantallaActual = "home"
                                    isLoading = true
                                }
                            )
                        }
                        "register" -> {
                            RegisterScreen(
                                onNavigateToLogin = {
                                    pantallaActual = "login"
                                    isLoading = true
                                }
                            )
                        }
                        "home" -> {
                            HomeScreen(
                                onNavigateToLogin = {
                                    pantallaActual = "login"
                                    isLoading = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}