
package com.example.vetsync.vista.screens

import android.widget.Toast
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

private val VerdeClinica = Color(0xFF2E5B4B)
private val FondoClinica = Color(0xFFFDFCF9)

@Composable
fun AdminInformacionClinicaScreen(
    onBackClick: () -> Unit,
    controlador: InformacionClinicaControlador = remember {
        InformacionClinicaControlador()
    }
) {

    val context = LocalContext.current

    // --------------------------------------------------
    // ESTADOS
    // --------------------------------------------------

    var nombre by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    var cargando by remember { mutableStateOf(true) }
    var guardando by remember { mutableStateOf(false) }

    var errorCarga by remember { mutableStateOf<String?>(null) }
    var errorFormulario by remember { mutableStateOf<String?>(null) }

    var reintentos by remember { mutableIntStateOf(0) }

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
            .background(FondoClinica)
    ) {

        // Encabezado.

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
                    tint = VerdeClinica
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Información de la clínica",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )

                Text(
                    text = "Configuración institucional",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        HorizontalDivider(
            color = Color(0xFFEAEAEA)
        )

        // Indicador de carga.

        if (cargando) {

            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = VerdeClinica
            )
        }

        // Contenido.

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

            // Tarjeta informativa.

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE2EFE7)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {

                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Outlined.Business,
                        contentDescription = null,
                        tint = VerdeClinica,
                        modifier = Modifier.size(34.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {

                        Text(
                            text = "Datos institucionales",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = VerdeClinica
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Administra la información de contacto y presentación de VetSync.",
                            fontSize = 12.sp,
                            color = Color(0xFF43584D),
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // Error de carga.

            if (errorCarga != null) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF0EE)
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = "No se pudo cargar la información",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB3261E)
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = errorCarga.orEmpty(),
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )

                        TextButton(
                            onClick = {
                                reintentos++
                            }
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            // Formulario: solo se habilita cuando la carga
            // inicial ha finalizado correctamente.

            if (errorCarga == null && !cargando) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Color(0xFFE5E5E5)
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
                            color = Color(0xFF222222)
                        )

                        // Nombre.

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

                        // Dirección.

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

                        // Teléfono.

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

                        // Correo.

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

                        // Descripción.

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
                            color = Color.Gray,
                            modifier = Modifier.align(
                                Alignment.End
                            )
                        )

                        // Errores de validación.

                        if (errorFormulario != null) {

                            Text(
                                text = errorFormulario.orEmpty(),
                                color = Color(0xFFB3261E),
                                fontSize = 12.sp
                            )
                        }

                        // Botón guardar.

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
                                containerColor = VerdeClinica
                            )
                        ) {

                            if (guardando) {

                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
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
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0xFFE5E5E5)
                )
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        text = "Horario de atención",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF222222)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    HorarioClinicaRow(
                        dia = "Lunes a viernes",
                        horario = "09:00 - 17:00"
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = Color(0xFFEEEEEE)
                    )

                    HorarioClinicaRow(
                        dia = "Sábados",
                        horario = "09:00 - 16:00"
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = Color(0xFFEEEEEE)
                    )

                    HorarioClinicaRow(
                        dia = "Domingos",
                        horario = "Cerrado"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Estos horarios corresponden a las reglas actuales de disponibilidad de citas.",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// --------------------------------------------------
// COLORES DE LOS CAMPOS
// --------------------------------------------------

@Composable
private fun coloresCampoClinica() =
    OutlinedTextFieldDefaults.colors(
        focusedBorderColor = VerdeClinica,
        focusedLabelColor = VerdeClinica,
        cursorColor = VerdeClinica
    )

// --------------------------------------------------
// FILA DE HORARIO
// --------------------------------------------------

@Composable
private fun HorarioClinicaRow(
    dia: String,
    horario: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = dia,
            fontSize = 13.sp,
            color = Color(0xFF3A3A3A)
        )

        Text(
            text = horario,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = VerdeClinica
        )
    }
}
