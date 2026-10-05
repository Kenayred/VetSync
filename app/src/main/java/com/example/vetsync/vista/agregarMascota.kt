package com.example.vetsync.vista

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vetsync.controlador.MascotaControlador
import com.example.vetsync.modelo.ImagenUtils
import com.example.vetsync.modelo.SesionUsuario
import com.example.vetsync.vista.theme.*
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.TextButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarMascotaScreen(
    controlador: MascotaControlador = remember { MascotaControlador() },
    onBackClick: () -> Unit = {},
    onCancelarClick: () -> Unit = {},
    onMascotaGuardada: () -> Unit = {}
) {
    var nombre by remember { mutableStateOf("") }
    var nombreError by remember { mutableStateOf(false) }
    var especie by remember { mutableStateOf("") }
    var especieError by remember { mutableStateOf(false) }
    var especiePersonalizada by remember { mutableStateOf("") }
    var raza by remember { mutableStateOf("") }
    var razaError by remember { mutableStateOf(false) }
    var edad by remember { mutableStateOf("") }
    var edadError by remember { mutableStateOf(false) }
    var sexo by remember { mutableStateOf("") }
    var sexoError by remember { mutableStateOf(false) }
    var fechaNacimiento by remember { mutableStateOf("") }
    var fechaNacError by remember { mutableStateOf(false) }
    var peso by remember { mutableStateOf("") }
    var pesoError by remember { mutableStateOf(false) }
    var colorMarcas by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }

    // Estado para guardar la imagen convertida en Base64
    var fotoBase64 by remember { mutableStateOf("") }

    val opcionesEspecie = listOf("Perro", "Gato", "Ave", "Conejo", "Otro")
    val opcionesSexo = listOf("Macho", "Hembra")

    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val selectorImagenLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            fotoBase64 = ImagenUtils.uriABase64(context, uri)
        }
    }

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
                    SubirFotoMascotaBox(
                        fotoBase64 = fotoBase64,
                        onClick = { selectorImagenLauncher.launch("image/*") }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    CampoFormularioMascota(
                        label = "Nombre",
                        value = nombre,
                        onValueChange = { nombre = it },
                        isError = nombreError,
                        placeholder = "Ej. Firulais"
                    )
                    if(nombreError){
                        Text(
                            text = "El nombre es obligatorio",
                            color = RojoAlerta,
                            fontSize = 12.sp,
                            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    DropdownFormularioMascota(
                        label = "Especie",
                        selectedValue = especie,
                        isError = especieError,
                        placeholder = "Selecciona especie",
                        opciones = opcionesEspecie,
                        onOptionSelected = { especie = it }
                    )

                    if(especieError){
                        Text(
                            text = "La especie es obligatoria",
                            color = RojoAlerta,
                            fontSize = 12.sp,
                            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    if(especie == "Otro"){
                        Spacer(modifier = Modifier.height(14.dp))
                        CampoFormularioMascota(
                            label = "Especificar Especie",
                            value = especiePersonalizada,
                            onValueChange = { especiePersonalizada = it },
                            placeholder = "Hamster, Serpiente, etc"
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    CampoFormularioMascota(
                        label = "Raza",
                        value = raza,
                        onValueChange = { raza = it },
                        isError = razaError,
                        placeholder = "Ej. Golden Retriever"
                    )

                    if(razaError){
                        Text(
                            text = "La raza es obligatoria",
                            color = RojoAlerta,
                            fontSize = 12.sp,
                            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    CampoFormularioMascota(
                        label = "Edad (Años)",
                        value = edad,
                        onValueChange = { edad = it },
                        isError = edadError,
                        placeholder = "2",
                        keyboardType = KeyboardType.Number
                    )

                    if(edadError){
                        Text(
                            text = "La edad es obligatoria",
                            color = RojoAlerta,
                            fontSize = 12.sp,
                            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    DropdownFormularioMascota(
                        label = "Sexo",
                        selectedValue = sexo,
                        placeholder = "Selecciona sexo",
                        isError = sexoError,
                        opciones = opcionesSexo,
                        onOptionSelected = { sexo = it }
                    )

                    if(sexoError){
                        Text(
                            text = "El sexo es obligatorio",
                            color = RojoAlerta,
                            fontSize = 12.sp,
                            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    SelectorFechaMascota(
                        label = "Fecha de Nacimiento",
                        value = fechaNacimiento,
                        isError = fechaNacError,
                        onValueChange = { fechaNacimiento = it },
                        placeholder = "Selecciona una fecha"
                    )

                    if(fechaNacError){
                        Text(
                            text = "La fecha de Nacimiento es obligatoria",
                            color = RojoAlerta,
                            fontSize = 12.sp,
                            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    CampoFormularioMascota(
                        label = "Peso (kg)",
                        value = peso,
                        onValueChange = { peso = it },
                        isError = pesoError,
                        placeholder = "Ej. 15.5",
                        keyboardType = KeyboardType.Decimal
                    )

                    if(pesoError){
                        Text(
                            text = "El peso es obligatorio",
                            color = RojoAlerta,
                            fontSize = 12.sp,
                            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    CampoFormularioMascota(
                        label = "Color / Marcas",
                        value = colorMarcas,
                        onValueChange = { colorMarcas = it },
                        placeholder = "Ej. Blanco con manchas negras"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

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

                    Button(
                        onClick = {
                            var errorVisual = false
                            nombreError = false
                            especieError =false
                            razaError = false
                            edadError = false
                            sexoError = false
                            fechaNacError = false
                            pesoError = false

                            if(nombre.isBlank()){
                                nombreError = true
                                errorVisual = true
                            }
                            if(especie.isBlank()){
                                especieError = true
                                errorVisual = true
                            }
                            if(raza.isBlank()){
                                razaError = true
                                errorVisual = true
                            }
                            if(edad.isBlank()){
                                edadError = true
                                errorVisual = true
                            }
                            if(sexo.isBlank()){
                                sexoError = true
                                errorVisual = true
                            }
                            if(fechaNacimiento.isBlank()){
                                fechaNacError = true
                                errorVisual = true
                            }
                            if(peso.isBlank()){
                                pesoError = true
                                errorVisual = true
                            }

                            if(!errorVisual){
                                val especieFinal = if(especie == "Otro") especiePersonalizada else especie;

                                controlador.registrarMascota(
                                    idUsuario = SesionUsuario.idUsuario,
                                    nombre = nombre,
                                    especie = especieFinal,
                                    raza = raza,
                                    edadTexto = edad,
                                    sexo = sexo,
                                    fechaNacimiento = fechaNacimiento,
                                    pesoTexto = peso,
                                    colorMarcas = colorMarcas,
                                    observaciones = observaciones,
                                    fotoUrl = fotoBase64, // <-- Imagen en base64
                                    onSuccess = {
                                        android.widget.Toast.makeText(
                                            context,
                                            "¡Mascota registrada con éxito!",
                                            android.widget.Toast.LENGTH_SHORT
                                        ).show()
                                        onMascotaGuardada()
                                    },
                                    onError = { mensajeError ->
                                        android.widget.Toast.makeText(
                                            context,
                                            mensajeError,
                                            android.widget.Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                )
                            }

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
                .statusBarsPadding()
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
fun SubirFotoMascotaBox(
    fotoBase64: String = "",
    onClick: () -> Unit
) {
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
    val borderColor = Color(0xFFC8D1C9)
    val imageBitmap = remember(fotoBase64) { ImagenUtils.base64ABitmap(fotoBase64) }

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
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap,
                contentDescription = "Foto seleccionada",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
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
}


@Composable
fun CampoFormularioMascota(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
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
            isError = isError,
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
                cursorColor = VerdeVetSync,
                errorBorderColor = RojoAlerta
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownFormularioMascota(
    label: String,
    selectedValue: String,
    isError: Boolean = false,
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
                isError = isError,
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
                    unfocusedBorderColor = BordeInputMascota,
                    errorBorderColor = RojoAlerta
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorFechaMascota(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    placeholder: String
) {
    var mostrarCalendario by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarCalendario = false
                        datePickerState.selectedDateMillis?.let { millis ->
                            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            sdf.timeZone = TimeZone.getTimeZone("UTC")
                            onValueChange(sdf.format(Date(millis)))
                        }
                    }
                ) {
                    Text("Aceptar", color = VerdeVetSync, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) {
                    Text("Cancelar", color = GrisTextoSecundario)
                }
            },
            colors = DatePickerDefaults.colors(containerColor = Color.White)
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    titleContentColor = VerdeVetSync,
                    headlineContentColor = VerdeVetSync,
                    selectedDayContainerColor = VerdeVetSync,
                    selectedDayContentColor = Color.White,
                    todayDateBorderColor = VerdeVetSync,
                    todayContentColor = VerdeVetSync
                )
            )
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextoLabelMascota
        )
        Spacer(modifier = Modifier.height(6.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                isError = isError,
                placeholder = {
                    Text(text = placeholder, fontSize = 13.sp, color = PlaceholderMascota)
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = "Seleccionar fecha",
                        tint = Color(0xFF4A4A4A)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = FondoInputMascota,
                    unfocusedContainerColor = FondoInputMascota,
                    focusedBorderColor = VerdeVetSync,
                    unfocusedBorderColor = BordeInputMascota,
                    errorBorderColor = RojoAlerta
                )
            )
            // Esta capa invisible intercepta el clic y abre el calendario
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { mostrarCalendario = true }
            )
        }
    }
}
