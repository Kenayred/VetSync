package com.example.vetsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import com.example.vetsync.vista.LoginScreen
import com.example.vetsync.vista.screens.LoadingScreen
import com.example.vetsync.vista.RegisterScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            MaterialTheme {

                var pantallaActual by remember { mutableStateOf("login") }
                var isLoading by remember {mutableStateOf(true)}

                LaunchedEffect(isLoading) {
                    if(isLoading){
                        delay(2000)
                        isLoading = false
                    }
                }

                if(isLoading){
                    LoadingScreen()
                } else {
                    when (pantallaActual){
                        "login" -> {
                            LoginScreen(
                                onNavigateToRegister = {
                                    pantallaActual = "register"
                                    isLoading = true
                                }
                            )
                        }
                        "register" -> {
                            RegisterScreen ( onNavigateToLogin = {
                                pantallaActual="login"
                                isLoading = true
                            })
                        }
                    }
                }
            }
        }
    }
}