package com.example.vetsync.vista.screens

import android.widget.Toast

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Phone

import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.vetsync.controlador.InformacionClinicaControlador
import com.example.vetsync.modelo.InformacionClinica

@Composable
fun AdminInformacionClinicaScreen(
    onBackClick: () -> Unit,
    controlador: InformacionClinicaControlador = remember {
        InformacionClinicaControlador()
    }
) {

    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme

    // --------------------------------------------------
    // ESTADOS
    // --------------------------------------------------

    var nombre by remember {
        mutableStateOf("")
    }

    var direccion by remember {
        mutableStateOf("")
    }

    var telefono by remember {
        mutableStateOf("")
    }

    var correo by remember {
        mutableStateOf("")
    }

    var descripcion by remember {
        mutableStateOf("")
    }

    var cargando by remember {
        mutableStateOf(true)
    }

    var guardando by remember {
        mutableStateOf(false)
    }

    var errorCarga by remember {
        mutableStateOf<String?>(null)
    }

    var errorFormulario by remember {
        mutableStateOf<String?>(null)
    }

    var reintentos by remember {
        mutableIntStateOf(0)
    }

    // --------------------------------------------------
    // CARGAR INFORMACIÓN DE FIRESTORE
    // --------------------------------------------------

    LaunchedEffect(reintentos) {

        cargando = true
        errorCarga = null
        errorFormulario = null

        controlador.obtenerInformacionClinica(

            onSuccess = { informacion ->

                nombre = informacion.nombre
                direccion = informacion.direccion
                telefono = informacion.telefono
                correo = informacion.correo
                descripcion = informacion.descripcion

                cargando = false
            },

            onError = { mensaje ->

                errorCarga = mensaje
                cargando = false
            }
        )
    }

    // --------------------------------------------------
    // INTERFAZ
    // --------------------------------------------------

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {

        // --------------------------------------------------
        // ENCABEZADO
        // --------------------------------------------------

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                ),

            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBackClick,
                enabled = !guardando
            ) {

                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Regresar",
                    tint = colorScheme.primary
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Información de la clínica",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground
                )

                Text(
                    text = "Configuración institucional",
                    fontSize = 12.sp,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }

        HorizontalDivider(
            color = colorScheme.outlineVariant
        )

        // --------------------------------------------------
        // INDICADOR DE CARGA
        // --------------------------------------------------

        if (cargando) {

            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = colorScheme.primary
            )
        }

        // --------------------------------------------------
        // CONTENIDO
        // --------------------------------------------------

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 20.dp,
                    vertical = 18.dp
                ),

            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // --------------------------------------------------
            // TARJETA INFORMATIVA
            // --------------------------------------------------

            Card(
                modifier = Modifier.fillMaxWidth(),

                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.primaryContainer
                ),

                shape = RoundedCornerShape(14.dp),

                border = BorderStroke(
                    1.dp,
                    colorScheme.outlineVariant
                )
            ) {

                Row(
                    modifier = Modifier.padding(16.dp),

                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Outlined.Business,
                        contentDescription = null,
                        tint = colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(34.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {

                        Text(
                            text = "Datos institucionales",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = colorScheme.onPrimaryContainer
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Administra la información de contacto y presentación de VetSync.",
                            fontSize = 12.sp,
                            color = colorScheme.onPrimaryContainer,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // --------------------------------------------------
            // ERROR DE CARGA
            // --------------------------------------------------

            if (errorCarga != null) {

                Card(
                    modifier = Modifier.fillMaxWidth(),

                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.errorContainer
                    ),

                    shape = RoundedCornerShape(14.dp),

                    border = BorderStroke(
                        1.dp,
                        colorScheme.error.copy(alpha = 0.30f)
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = "No se pudo cargar la información",
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onErrorContainer
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = errorCarga.orEmpty(),
                            fontSize = 13.sp,
                            color = colorScheme.onErrorContainer
                        )

                        TextButton(
                            onClick = {
                                reintentos++
                            }
                        ) {

                            Text(
                                "Reintentar",
                                color = colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

            // --------------------------------------------------
            // FORMULARIO
            // --------------------------------------------------

            if (errorCarga == null && !cargando) {

                Card(
                    modifier = Modifier.fillMaxWidth(),

                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surface
                    ),

                    shape = RoundedCornerShape(16.dp),

                    border = BorderStroke(
                        1.dp,
                        colorScheme.outlineVariant
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),

                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {

                        Text(
                            text = "Información general",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )

                        // NOMBRE.

                        OutlinedTextField(
                            value = nombre,

                            onValueChange = {
                                nombre = it
                                errorFormulario = null
                            },

                            modifier = Modifier.fillMaxWidth(),

                            enabled = !guardando,

                            singleLine = true,

                            label = {
                                Text("Nombre de la clínica")
                            },

                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Business,
                                    contentDescription = null
                                )
                            },

                            colors = coloresCampoClinica()
                        )

                        // DIRECCIÓN.

                        OutlinedTextField(
                            value = direccion,

                            onValueChange = {
                                direccion = it
                                errorFormulario = null
                            },

                            modifier = Modifier.fillMaxWidth(),

                            enabled = !guardando,

                            singleLine = true,

                            label = {
                                Text("Dirección")
                            },

                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.LocationOn,
                                    contentDescription = null
                                )
                            },

                            colors = coloresCampoClinica()
                        )

                        // TELÉFONO.

                        OutlinedTextField(
                            value = telefono,

                            onValueChange = {
                                telefono = it
                                errorFormulario = null
                            },

                            modifier = Modifier.fillMaxWidth(),

                            enabled = !guardando,

                            singleLine = true,

                            label = {
                                Text("Teléfono")
                            },

                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Phone,
                                    contentDescription = null
                                )
                            },

                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone
                            ),

                            colors = coloresCampoClinica()
                        )

                        // CORREO.

                        OutlinedTextField(
                            value = correo,

                            onValueChange = {
                                correo = it
                                errorFormulario = null
                            },

                            modifier = Modifier.fillMaxWidth(),

                            enabled = !guardando,

                            singleLine = true,

                            label = {
                                Text("Correo institucional")
                            },

                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Email,
                                    contentDescription = null
                                )
                            },

                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email
                            ),

                            colors = coloresCampoClinica()
                        )

                        // DESCRIPCIÓN.

                        OutlinedTextField(
                            value = descripcion,

                            onValueChange = {
                                descripcion = it
                                errorFormulario = null
                            },

                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 110.dp),

                            enabled = !guardando,

                            label = {
                                Text("Descripción de la clínica")
                            },

                            placeholder = {
                                Text(
                                    "Escribe una breve descripción de los servicios y atención veterinaria..."
                                )
                            },

                            minLines = 3,

                            colors = coloresCampoClinica()
                        )

                        Text(
                            text = "${descripcion.length}/500 caracteres",
                            fontSize = 11.sp,
                            color = colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(
                                Alignment.End
                            )
                        )

                        // ERRORES DE VALIDACIÓN.

                        if (errorFormulario != null) {

                            Text(
                                text = errorFormulario.orEmpty(),
                                color = colorScheme.error,
                                fontSize = 12.sp
                            )
                        }

                        // GUARDAR INFORMACIÓN.

                        Button(
                            onClick = {

                                errorFormulario = null
                                guardando = true

                                val informacion = InformacionClinica(
                                    nombre = nombre,
                                    direccion = direccion,
                                    telefono = telefono,
                                    correo = correo,
                                    descripcion = descripcion
                                )

                                controlador.guardarInformacionClinica(
                                    informacion = informacion,

                                    onSuccess = {

                                        guardando = false

                                        Toast.makeText(
                                            context,
                                            "Información de la clínica actualizada correctamente.",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        onBackClick()
                                    },

                                    onError = { mensaje ->

                                        guardando = false
                                        errorFormulario = mensaje
                                    }
                                )
                            },

                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),

                            enabled = !guardando,

                            shape = RoundedCornerShape(12.dp),

                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorScheme.primary,
                                contentColor = colorScheme.onPrimary
                            )
                        ) {

                            if (guardando) {

                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text("Guardando...")

                            } else {

                                Text(
                                    text = "Guardar información",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // --------------------------------------------------
            // HORARIO DE ATENCIÓN
            // --------------------------------------------------

            Card(
                modifier = Modifier.fillMaxWidth(),

                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surface
                ),

                shape = RoundedCornerShape(16.dp),

                border = BorderStroke(
                    1.dp,
                    colorScheme.outlineVariant
                )
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        text = "Horario de atención",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    HorarioClinicaRow(
                        dia = "Lunes a viernes",
                        horario = "09:00 - 17:00"
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = colorScheme.outlineVariant
                    )

                    HorarioClinicaRow(
                        dia = "Sábados",
                        horario = "09:00 - 16:00"
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = colorScheme.outlineVariant
                    )

                    HorarioClinicaRow(
                        dia = "Domingos",
                        horario = "Cerrado"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Estos horarios corresponden a las reglas actuales de disponibilidad de citas.",
                        fontSize = 11.sp,
                        color = colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// ======================================================
// COLORES DE LOS CAMPOS
// ======================================================

@Composable
private fun coloresCampoClinica() =
    OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        cursorColor = MaterialTheme.colorScheme.primary
    )

// ======================================================
// FILA DE HORARIO
// ======================================================

@Composable
private fun HorarioClinicaRow(
    dia: String,
    horario: String
) {

    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxWidth(),

        horizontalArrangement = Arrangement.SpaceBetween,

        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = dia,
            fontSize = 13.sp,
            color = colorScheme.onSurfaceVariant
        )

        Text(
            text = horario,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.primary
        )
    }
}