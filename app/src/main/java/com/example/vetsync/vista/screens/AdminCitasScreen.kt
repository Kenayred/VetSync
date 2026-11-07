package com.example.vetsync.vista.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ViewAgenda
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AdminCitasScreen(
    modifier: Modifier = Modifier
) {
    val backgroundColor = Color(0xFFFDFCF9)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // 1. Cabecera y Botón Nueva Cita
        Text(
            text = "Agenda de Citas",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
        Text(
            text = "Jueves, 24 de Octubre de 2023",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { /* TODO: Navegar a crear cita */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A7D5E)),
            shape = RoundedCornerShape(24.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Nueva", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Nueva Cita", fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Tarjeta de Filtros y Búsqueda
        CardFiltrosBusqueda()

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Tarjeta del Calendario
        CardCalendarioMensual()

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Tarjeta de Resumen del Día
        CardResumenDia()

        Spacer(modifier = Modifier.height(24.dp))

        // 5. Lista de Citas (Línea de tiempo)
        CitaItem(
            hora = "09:00",
            ampm = "AM",
            nombrePaciente = "Max",
            estado = "PENDIENTE",
            estadoBgColor = Color(0xFFFDF0D5),
            estadoTextColor = Color(0xFFB57B22),
            nombreDueno = "Carlos Ruiz",
            etiquetas = listOf(
                Pair(Icons.Outlined.LocalHospital, "Vacunación Anual") to Color(0xFFE2EFE7),
                Pair(Icons.Outlined.Description, "$45.00") to Color(0xFFF0F0F0)
            ),
            botones = {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A7D5E)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Iniciar", fontSize = 13.sp)
                    }
                    OutlinedButton(
                        onClick = { },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.DarkGray)
                        Spacer(Modifier.width(4.dp))
                        Text("Editar", fontSize = 13.sp, color = Color.DarkGray)
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        CitaItem(
            hora = "10:30",
            ampm = "AM",
            horaColor = Color.Gray, // Hora más tenue por ser completada
            nombrePaciente = "Luna",
            estado = "COMPLETADA",
            estadoBgColor = Color(0xFF91C5A9), // Verde completado
            estadoTextColor = Color.White,
            nombreDueno = "María Gómez",
            etiquetas = listOf(
                Pair(Icons.Outlined.LocalHospital, "Consulta General") to Color(0xFFF0F0F0)
            ),
            botones = {
                OutlinedButton(
                    onClick = { },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                ) {
                    Icon(Icons.Outlined.Description, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.DarkGray)
                    Spacer(Modifier.width(4.dp))
                    Text("Ver Resumen", fontSize = 13.sp, color = Color.DarkGray)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        CitaItem(
            hora = "11:15",
            ampm = "AM",
            nombrePaciente = "Rocky",
            estado = "PENDIENTE",
            estadoBgColor = Color(0xFFFDF0D5),
            estadoTextColor = Color(0xFFB57B22),
            nombreDueno = "Ana Silva",
            esAvatarLetra = true,
            letraAvatar = "R",
            etiquetas = listOf(
                Pair(Icons.Outlined.ContentCut, "Peluquería") to Color(0xFFF5E0D8) // Color salmón claro
            ),
            botones = {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A7D5E)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Iniciar", fontSize = 13.sp)
                    }
                    OutlinedButton(
                        onClick = { },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.DarkGray)
                        Spacer(Modifier.width(4.dp))
                        Text("Editar", fontSize = 13.sp, color = Color.DarkGray)
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(80.dp)) // Padding para BottomBar
    }
}

@Composable
fun CardFiltrosBusqueda() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Chips de filtro
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChipCustom("Todas", true, Modifier.weight(1f))
                FilterChipCustom("Pendientes", false, Modifier.weight(1f))
                FilterChipCustom("Completadas", false, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Buscador y botones de vista
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Buscar paciente...", fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE5E5E5),
                        focusedBorderColor = Color(0xFF4A7D5E)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Toggle de vista (Lista / Calendario)
                Row(
                    modifier = Modifier
                        .background(Color(0xFFF5EFEB), RoundedCornerShape(8.dp))
                        .padding(4.dp)
                ) {
                    Box(modifier = Modifier.background(Color.White, RoundedCornerShape(6.dp)).padding(6.dp)) {
                        Icon(Icons.Outlined.ViewAgenda, contentDescription = "Lista", tint = Color.DarkGray, modifier = Modifier.size(20.dp))
                    }
                    Box(modifier = Modifier.padding(6.dp)) {
                        Icon(Icons.Outlined.CalendarMonth, contentDescription = "Calendario", tint = Color.Gray, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChipCustom(text: String, isSelected: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                color = if (isSelected) Color(0xFFF5EFEB) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = if (isSelected) Color(0xFFD7C9BE) else Color(0xFFE5E5E5),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = if (isSelected) Color.DarkGray else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
fun CardCalendarioMensual() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Octubre 2026", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Row {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Anterior", tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ChevronRight, contentDescription = "Siguiente", tint = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Días de la semana
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                listOf("D", "L", "M", "M", "J", "V", "S").forEach { day ->
                    Text(text = day, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Cuadrícula de fechas (Simulada para la vista estática)
            val fechas = listOf(
                listOf("1" to true, "2" to true, "3" to false, "4" to false, "5" to false, "6" to false, "7" to false),
                listOf("8" to false, "9" to false, "10" to false, "11" to false, "12" to false, "13" to false, "14" to false),
                listOf("15" to false, "16" to false, "17" to false, "18" to false, "19" to false, "20" to false, "21" to false),
                listOf("22" to false, "23" to false, "24" to false, "25" to false, "26" to false, "27" to false, "28" to false),
                listOf("29" to false, "30" to false, "31" to false, "" to false, "" to false, "" to false, "" to false)
            )

            fechas.forEach { semana ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceAround) {
                    semana.forEach { (dia, isTenue) ->
                        val isSelected = dia == "24" // Día actual seleccionado
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color(0xFF4A7D5E) else Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = dia,
                                    fontSize = 14.sp,
                                    color = if (isSelected) Color.White else if (isTenue) Color.LightGray else Color.Black
                                )
                                if (isSelected) {
                                    Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color.White))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CardResumenDia() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Resumen del Día", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))

            FilaResumen(color = Color(0xFFCDE2D4), etiqueta = "Total Citas", valor = "12", isBold = true)
            FilaResumen(color = Color(0xFFFDE89C), etiqueta = "Pendientes", valor = "8")
            FilaResumen(color = Color(0xFF91C5A9), etiqueta = "Completadas", valor = "3")
            FilaResumen(color = Color(0xFFF7D1C9), etiqueta = "Canceladas", valor = "1")
        }
    }
}

@Composable
fun FilaResumen(color: Color, etiqueta: String, valor: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = etiqueta, fontSize = 14.sp, color = Color.DarkGray)
        }
        Text(
            text = valor,
            fontSize = 14.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun CitaItem(
    hora: String,
    ampm: String,
    horaColor: Color = Color.Black,
    nombrePaciente: String,
    estado: String,
    estadoBgColor: Color,
    estadoTextColor: Color,
    nombreDueno: String,
    esAvatarLetra: Boolean = false,
    letraAvatar: String = "",
    etiquetas: List<Pair<Pair<ImageVector, String>, Color>>,
    botones: @Composable () -> Unit
) {
    Column {
        // Cabecera de Hora
        Box(modifier = Modifier.fillMaxWidth().background(Color(0xFFF5EFEB), RoundedCornerShape(8.dp)).padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = hora, fontSize = 20.sp, fontWeight = FontWeight.Medium, color = horaColor)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = ampm, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 3.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tarjeta de Detalle
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    // Avatar
                    if (esAvatarLetra) {
                        Box(
                            modifier = Modifier.size(50.dp).clip(CircleShape).background(Color(0xFFCDE2D4)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = letraAvatar, fontSize = 20.sp, color = Color(0xFF4A7D5E))
                        }
                    } else {
                        Box(modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.LightGray)) // Reemplazar con foto
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Info principal
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = nombrePaciente, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(modifier = Modifier.background(estadoBgColor, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                Text(text = estado, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = estadoTextColor)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Person, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = nombreDueno, fontSize = 13.sp, color = Color.Gray)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Etiquetas (Vacuna, Precio, etc.)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            etiquetas.forEach { (contenido, bgColor) ->
                                val (icono, texto) = contenido
                                Row(
                                    modifier = Modifier.background(bgColor, RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(icono, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.DarkGray)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = texto, fontSize = 11.sp, color = Color.DarkGray)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botones de acción
                botones()
            }
        }
    }
}