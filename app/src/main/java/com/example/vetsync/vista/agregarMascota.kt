package com.example.vetsync.vista

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vetsync.vista.components.FondoCrema
import com.example.vetsync.vista.components.GrisTextoSecundario
import com.example.vetsync.vista.components.VerdeClaroIcono
import com.example.vetsync.vista.components.VerdeVetSync
import com.example.vetsync.vista.theme.*
import com.example.vetsync.controlador.MascotaControlador
import com.example.vetsync.modelo.Mascota
import com.example.vetsync.vista.components.MascotaItemCard
import androidx.compose.ui.platform.LocalContext
import com.example.vetsync.modelo.SesionUsuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarMascotaScreen(
    controlador: MascotaControlador = remember { MascotaControlador() },
    onBackClick: () -> Unit = {},
    onCancelarClick: () -> Unit = {},
    onSubirFotoClick: () -> Unit = {},
    onMascotaGuardada: () -> Unit = {},
    onGuardarClick: (
        nombre: String,
        especie: String,
        raza: String,
        edad: Int,
        sexo: String,
        fechaNacimiento: String,
        peso: String,
        colorMarcas: String,
        observaciones: String
    ) -> Unit = { _, _, _, _, _, _, _, _, _ -> }
) {
    var nombre by remember { mutableStateOf("") }
    var especie by remember { mutableStateOf("") }
    var raza by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var sexo by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var colorMarcas by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }

    val opcionesEspecie = listOf("Perro", "Gato", "Ave", "Conejo", "Otro")
    val opcionesSexo = listOf("Macho", "Hembra")

    val scrollState = rememberScrollState()
    val context = LocalContext.current;

    Scaffold(
        containerColor = FondoCrema,
        topBar = {
            AgregarMascotaTopBar(onBackClick = onBackClick)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Agregar Mascota",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E221F)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Ingresa los detalles del nuevo paciente para mantener su registro al día.",
                fontSize = 14.sp,
                color = GrisTextoSecundario,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Container Formulario
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    //  Recuadro punteado para subir foto
                    SubirFotoMascotaBox(onClick = onSubirFotoClick)

                    Spacer(modifier = Modifier.height(20.dp))

                    //  Campo: Nombre
                    CampoFormularioMascota(
                        label = "Nombre",
                        value = nombre,
                        onValueChange = { nombre = it },
                        placeholder = "Ej. Firulais"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    //  Dropdown: Especie
                    DropdownFormularioMascota(
                        label = "Especie",
                        selectedValue = especie,
                        placeholder = "Selecciona especie",
                        opciones = opcionesEspecie,
                        onOptionSelected = { especie = it }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    //  Campo: Raza
                    CampoFormularioMascota(
                        label = "Raza",
                        value = raza,
                        onValueChange = { raza = it },
                        placeholder = "Ej. Golden Retriever"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    CampoFormularioMascota(
                        label = "Edad (Años)",
                        value = edad,
                        onValueChange = {edad = it},
                        placeholder = "2"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    //  Dropdown: Sexo
                    DropdownFormularioMascota(
                        label = "Sexo",
                        selectedValue = sexo,
                        placeholder = "Selecciona sexo",
                        opciones = opcionesSexo,
                        onOptionSelected = { sexo = it }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    //  Campo: Fecha de Nacimiento (Aprox.)
                    CampoFormularioMascota(
                        label = "Fecha de Nacimiento (Aprox.)",
                        value = fechaNacimiento,
                        onValueChange = { fechaNacimiento = it },
                        placeholder = "mm/dd/yyyy"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    //  Campo: Peso (kg)
                    CampoFormularioMascota(
                        label = "Peso (kg)",
                        value = peso,
                        onValueChange = { peso = it },
                        placeholder = "Ej. 15.5",
                        keyboardType = KeyboardType.Decimal
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    //  Campo: Color / Marcas
                    CampoFormularioMascota(
                        label = "Color / Marcas",
                        value = colorMarcas,
                        onValueChange = { colorMarcas = it },
                        placeholder = "Ej. Blanco con manchas negras"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    //  Campo: Observaciones o Condiciones Especiales (Multilínea)
                    CampoFormularioMascota(
                        label = "Observaciones o Condiciones Especiales",
                        value = observaciones,
                        onValueChange = { observaciones = it },
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

                    // Botón: Cancelar
                    OutlinedButton(
                        onClick = onCancelarClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, BordeInputMascota),
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

                    // Botón: Guardar mascota
                    Button(
                        onClick = {
                            controlador.registrarMascota(
                                idUsuario = SesionUsuario.idUsuario,
                                nombre = nombre,
                                especie = especie,
                                raza = raza,
                                sexo = sexo,
                                fechaNacimiento = fechaNacimiento,
                                pesoTexto = peso,
                                colorMarcas = colorMarcas,
                                observaciones = observaciones,
                                onSuccess = {
                                    android.widget.Toast.makeText(context, "¡Mascota registrada con éxito!", android.widget.Toast.LENGTH_SHORT).show()
                                    onMascotaGuardada() // Regresa al listado de mascotas o al Home
                                },
                                onError = { mensajeError ->
                                    android.widget.Toast.makeText(context, mensajeError, android.widget.Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = VerdeVetSync)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Guardar mascota",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AgregarMascotaTopBar(onBackClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(FondoCrema)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Regresar",
                    tint = Color(0xFF3B3B3B),
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "VetSync",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeVetSync
            )
        }
        HorizontalDivider(color = Color(0xFFE5E0D8), thickness = 1.dp)
    }
}

@Composable
fun SubirFotoMascotaBox(onClick: () -> Unit) {
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
    val borderColor = Color(0xFFC8D1C9)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(FondoInputMascota)
            .drawBehind {
                drawRoundRect(
                    color = borderColor,
                    style = Stroke(width = 3f, pathEffect = dashEffect),
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
            }
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(VerdeClaroIcono),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Subir foto",
                    tint = VerdeVetSync,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Subir foto de la mascota",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = VerdeVetSync
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "JPG, PNG hasta 5MB",
                fontSize = 11.sp,
                color = GrisTextoSecundario
            )
        }
    }
}

@Composable
fun CampoFormularioMascota(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minHeight: Int = 52
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextoLabelMascota
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    fontSize = 13.sp,
                    color = PlaceholderMascota
                )
            },
            singleLine = singleLine,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minHeight.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = FondoInputMascota,
                unfocusedContainerColor = FondoInputMascota,
                focusedBorderColor = VerdeVetSync,
                unfocusedBorderColor = BordeInputMascota,
                cursorColor = VerdeVetSync
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownFormularioMascota(
    label: String,
    selectedValue: String,
    placeholder: String,
    opciones: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextoLabelMascota
        )
        Spacer(modifier = Modifier.height(6.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedValue,
                onValueChange = {},
                readOnly = true,
                placeholder = {
                    Text(
                        text = placeholder,
                        fontSize = 13.sp,
                        color = Color(0xFF2B2B2B)
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color(0xFF4A4A4A)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = FondoInputMascota,
                    unfocusedContainerColor = FondoInputMascota,
                    focusedBorderColor = VerdeVetSync,
                    unfocusedBorderColor = BordeInputMascota
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(text = opcion, fontSize = 14.sp) },
                        onClick = {
                            onOptionSelected(opcion)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
