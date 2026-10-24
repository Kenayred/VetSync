package com.example.vetsync.vista.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vetsync.vista.theme.*
import com.example.vetsync.modelo.Mascota
import com.example.vetsync.vista.CampoFormularioMascota
import com.example.vetsync.vista.DropdownFormularioMascota
import com.example.vetsync.vista.SelectorFechaMascota
import com.example.vetsync.controlador.MascotaControlador

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarMascotaScreen(
    // En tu implementación real, podrías recibir el objeto Mascota completo
    mascota: Mascota,
    controlador: MascotaControlador,
    onBackClick: () -> Unit,
    onCancelarClick: () -> Unit,
    onMascotaEditada: () -> Unit,
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var nombre by remember { mutableStateOf(mascota.nombre) }
    var tipo by remember { mutableStateOf(mascota.especie) }
    var raza by remember { mutableStateOf(mascota.raza) }
    var fechaNacimiento by remember { mutableStateOf(mascota.fechaNacimiento) }
    var colorMarcas by remember { mutableStateOf(mascota.colorMarcas) }

    var nombreError by remember { mutableStateOf(false) }
    var tipoError by remember { mutableStateOf(false) }

    val tiposMascota = listOf("Perro", "Gato", "Ave", "Reptil", "Otro")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Mascota", fontWeight = FontWeight.Bold, color = VerdeVetSync) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = VerdeVetSync)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Información de la mascota",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2B2B2B)
            )
            Spacer(modifier = Modifier.height(24.dp))

            CampoFormularioMascota(
                label = "Nombre de la mascota *",
                value = nombre,
                onValueChange = {
                    nombre = it
                    nombreError = false // Limpia el error al escribir
                },
                isError = nombreError,
                placeholder = "Ej. Max"
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Dropdown Tipo (Obligatorio)
            DropdownFormularioMascota(
                label = "Tipo de mascota *",
                selectedValue = tipo,
                placeholder = "Seleccione el tipo",
                opciones = tiposMascota,
                isError = tipoError,
                onOptionSelected = {
                    tipo = it
                    tipoError = false // Limpia el error al seleccionar
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Campo Raza (Opcional - no lleva validación isError)
            CampoFormularioMascota(
                label = "Raza (Opcional)",
                value = raza,
                onValueChange = { raza = it },
                placeholder = "Ej. Golden Retriever"
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Selector de Fecha (Opcional)
            SelectorFechaMascota(
                label = "Fecha de nacimiento (Opcional)",
                value = fechaNacimiento,
                onValueChange = { fechaNacimiento = it },
                placeholder = "dd/mm/aaaa"
            )
            Spacer(modifier = Modifier.height(16.dp))

            CampoFormularioMascota(
                label = "Color / Marcas",
                value = colorMarcas,
                onValueChange = { colorMarcas = it },
                placeholder = "Ej. 25.5",
                keyboardType = KeyboardType.Decimal
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    nombreError = false
                    tipoError = false
                    var hayErrorVisual = false

                    if (nombre.isBlank()) {
                        nombreError = true
                        hayErrorVisual = true
                    }
                    if (tipo.isBlank() || tipo == "Seleccione el tipo") {
                        tipoError = true
                        hayErrorVisual = true
                    }

                    if (!hayErrorVisual) {

                        val mascotaActualizada = mascota.copy(
                            nombre = nombre,
                            especie = tipo,
                            raza = raza,
                            fechaNacimiento = fechaNacimiento,
                            colorMarcas = colorMarcas
                        )

                        controlador.editarMascota(
                            mascota = mascotaActualizada,
                            onSuccess = {
                                Toast.makeText(context, "Mascota actualizada con éxito", Toast.LENGTH_SHORT).show()
                                onMascotaEditada()
                            },
                            onError = { mensajeError ->
                                Toast.makeText(context, mensajeError, Toast.LENGTH_LONG).show()
                            }
                        )
                    } else {
                        Toast.makeText(context, "Revisa los campos en rojo", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdeVetSync),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar Cambios", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}