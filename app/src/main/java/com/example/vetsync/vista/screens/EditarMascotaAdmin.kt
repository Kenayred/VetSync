
package com.example.vetsync.vista.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vetsync.controlador.MascotaControlador
import com.example.vetsync.modelo.ImagenUtils
import com.example.vetsync.modelo.Mascota
import com.example.vetsync.vista.AgregarMascotaTopBar
import com.example.vetsync.vista.CampoFormularioMascota
import com.example.vetsync.vista.DropdownFormularioMascota
import com.example.vetsync.vista.SelectorFechaMascota
import com.example.vetsync.vista.SubirFotoMascotaBox
import com.example.vetsync.vista.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarMascotaAdminScreen(
    mascota: Mascota,
    controlador: MascotaControlador,
    onBackClick: () -> Unit,
    onMascotaEditada: (Mascota) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Datos actuales de la mascota.
    var nombre by remember(mascota.id) {
        mutableStateOf(mascota.nombre)
    }

    var especie by remember(mascota.id) {
        mutableStateOf(mascota.especie)
    }

    var raza by remember(mascota.id) {
        mutableStateOf(mascota.raza)
    }

    var edad by remember(mascota.id) {
        mutableStateOf(mascota.edad)
    }

    var sexo by remember(mascota.id) {
        mutableStateOf(mascota.sexo)
    }

    var fechaNacimiento by remember(mascota.id) {
        mutableStateOf(mascota.fechaNacimiento)
    }

    var peso by remember(mascota.id) {
        mutableStateOf(
            if (mascota.peso > 0.0) mascota.peso.toString() else ""
        )
    }

    var colorMarcas by remember(mascota.id) {
        mutableStateOf(mascota.colorMarcas)
    }

    var observaciones by remember(mascota.id) {
        mutableStateOf(mascota.observaciones)
    }

    var fotoBase64 by remember(mascota.id) {
        mutableStateOf(mascota.fotoUrl)
    }

    // Estados de validación visual.
    var nombreError by remember { mutableStateOf(false) }
    var especieError by remember { mutableStateOf(false) }
    var razaError by remember { mutableStateOf(false) }
    var edadError by remember { mutableStateOf(false) }
    var sexoError by remember { mutableStateOf(false) }
    var fechaError by remember { mutableStateOf(false) }
    var pesoError by remember { mutableStateOf(false) }

    var guardando by remember { mutableStateOf(false) }

    val opcionesSexo = listOf("Macho", "Hembra")

    // Selector de fotografías.
    val selectorImagenLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->

        if (uri != null) {
            runCatching {
                ImagenUtils.uriABase64(context, uri)
            }.onSuccess { imagen ->

                if (imagen.isNotBlank()) {
                    fotoBase64 = imagen
                } else {
                    Toast.makeText(
                        context,
                        "No se pudo procesar la fotografía.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }.onFailure {
                Toast.makeText(
                    context,
                    "Ocurrió un error al seleccionar la fotografía.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    Scaffold(
        containerColor = FondoCrema,
        topBar = {
            AgregarMascotaTopBar(
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(
                    horizontal = 20.dp,
                    vertical = 12.dp
                )
        ) {

            Text(
                text = "Editar paciente",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E221F)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Actualiza la información general de esta mascota.",
                fontSize = 14.sp,
                color = GrisTextoSecundario,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.5.dp
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {

                    // FOTOGRAFÍA

                    Text(
                        text = "Fotografía del paciente",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextoLabelMascota
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    SubirFotoMascotaBox(
                        fotoBase64 = fotoBase64,
                        onClick = {
                            selectorImagenLauncher.launch("image/*")
                        }
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    // NOMBRE

                    CampoFormularioMascota(
                        label = "Nombre",
                        value = nombre,
                        onValueChange = {
                            nombre = it
                            nombreError = false
                        },
                        isError = nombreError,
                        placeholder = "Ej. Max"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // ESPECIE

                    CampoFormularioMascota(
                        label = "Especie",
                        value = especie,
                        onValueChange = {
                            especie = it
                            especieError = false
                        },
                        isError = especieError,
                        placeholder = "Ej. Perro, Gato, Ave..."
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // RAZA

                    CampoFormularioMascota(
                        label = "Raza",
                        value = raza,
                        onValueChange = {
                            raza = it
                            razaError = false
                        },
                        isError = razaError,
                        placeholder = "Ej. Labrador"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // EDAD

                    CampoFormularioMascota(
                        label = "Edad (años)",
                        value = edad,
                        onValueChange = {
                            edad = it
                            edadError = false
                        },
                        isError = edadError,
                        placeholder = "Ej. 3",
                        keyboardType = KeyboardType.Number
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // SEXO

                    DropdownFormularioMascota(
                        label = "Sexo",
                        selectedValue = sexo,
                        isError = sexoError,
                        placeholder = "Selecciona sexo",
                        opciones = opcionesSexo,
                        onOptionSelected = {
                            sexo = it
                            sexoError = false
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // FECHA DE NACIMIENTO

                    SelectorFechaMascota(
                        label = "Fecha de nacimiento",
                        value = fechaNacimiento,
                        isError = fechaError,
                        onValueChange = {
                            fechaNacimiento = it
                            fechaError = false
                        },
                        placeholder = "Selecciona una fecha"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // PESO

                    CampoFormularioMascota(
                        label = "Peso (kg)",
                        value = peso,
                        onValueChange = {
                            peso = it
                            pesoError = false
                        },
                        isError = pesoError,
                        placeholder = "Ej. 15.5",
                        keyboardType = KeyboardType.Decimal
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // COLOR Y MARCAS

                    CampoFormularioMascota(
                        label = "Color / Marcas",
                        value = colorMarcas,
                        onValueChange = {
                            colorMarcas = it
                        },
                        placeholder = "Ej. Blanco con manchas negras"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // OBSERVACIONES

                    CampoFormularioMascota(
                        label = "Observaciones o condiciones especiales",
                        value = observaciones,
                        onValueChange = {
                            observaciones = it
                        },
                        placeholder = "Alergias, comportamientos, etc.",
                        singleLine = false,
                        minHeight = 96
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    HorizontalDivider(
                        color = Color(0xFFEEEEEE),
                        thickness = 1.dp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // CANCELAR

                    OutlinedButton(
                        onClick = onBackClick,
                        enabled = !guardando,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF3A4D3E)
                        )
                    ) {
                        Text(
                            text = "Cancelar",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // GUARDAR CAMBIOS

                    Button(
                        onClick = {

                            nombreError = nombre.isBlank()
                            especieError = especie.isBlank()
                            razaError = raza.isBlank()
                            edadError = edad.trim()
                                .toIntOrNull()
                                ?.let { it < 0 || false }
                                ?: true
                            sexoError = sexo.isBlank()
                            fechaError = fechaNacimiento.isBlank()

                            val pesoConvertido = peso
                                .trim()
                                .replace(",", ".")
                                .toDoubleOrNull()

                            pesoError = pesoConvertido == null ||
                                    !pesoConvertido.isFinite() ||
                                    pesoConvertido <= 0.0

                            val hayErrores =
                                nombreError ||
                                        especieError ||
                                        razaError ||
                                        edadError ||
                                        sexoError ||
                                        fechaError ||
                                        pesoError

                            if (hayErrores) {

                                Toast.makeText(
                                    context,
                                    "Revisa los campos obligatorios.",
                                    Toast.LENGTH_SHORT
                                ).show()

                                return@Button
                            }

                            val mascotaActualizada = mascota.copy(
                                nombre = nombre.trim(),
                                especie = especie.trim(),
                                raza = raza.trim(),
                                edad = edad.trim(),
                                sexo = sexo.trim(),
                                fechaNacimiento = fechaNacimiento.trim(),
                                peso = pesoConvertido!!,
                                colorMarcas = colorMarcas.trim(),
                                observaciones = observaciones.trim(),
                                fotoUrl = fotoBase64
                            )

                            guardando = true

                            controlador.editarMascotaAdmin(
                                mascota = mascotaActualizada,

                                onSuccess = { actualizada ->

                                    guardando = false

                                    Toast.makeText(
                                        context,
                                        "Paciente actualizado correctamente.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    onMascotaEditada(actualizada)
                                },

                                onError = { mensaje ->

                                    guardando = false

                                    Toast.makeText(
                                        context,
                                        mensaje,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            )
                        },

                        enabled = !guardando,

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),

                        shape = RoundedCornerShape(50),

                        colors = ButtonDefaults.buttonColors(
                            containerColor = VerdeVetSync
                        )
                    ) {

                        if (guardando) {

                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )

                        } else {

                            Text(
                                text = "Guardar cambios",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
