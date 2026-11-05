package com.example.vetsync.vista.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vetsync.vista.theme.*

@Composable
fun AdminPacientesScreen(
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
        // 1. Cabecera
        Text(
            text = "Pacientes",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Gestión clínica de mascotas registradas.",
            fontSize = 14.sp,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Botón Nuevo Paciente
        Button(
            onClick = { /* TODO: Navegar a registrar mascota */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A7D5E)),
            shape = RoundedCornerShape(24.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Nuevo", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Nuevo Paciente", fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Tarjeta de Búsqueda y Filtros
        BuscadorFiltrosPacientesCard()

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Lista de Pacientes
        PacienteItemCard(
            nombre = "Luna",
            tipoRaza = "Canino · Golden Retriever",
            estado = "Consulta Hoy",
            estadoBgColor = Color(0xFFF7E3E1),
            estadoTextColor = Color(0xFF8B3A36),
            nombrePropietario = "María González"
        )

        Spacer(modifier = Modifier.height(16.dp))

        PacienteItemCard(
            nombre = "Salem",
            tipoRaza = "Felino · Mestizo",
            estado = "Estable",
            estadoBgColor = Color(0xFFE2EFE7),
            estadoTextColor = Color(0xFF2E5B4B),
            nombrePropietario = "Carlos Ruiz"
        )

        Spacer(modifier = Modifier.height(16.dp))

        PacienteItemCard(
            nombre = "Pelusa",
            tipoRaza = "Canino · Poodle",
            estado = "Seguimiento",
            estadoBgColor = Color(0xFFEBE7E0),
            estadoTextColor = Color(0xFF5C554D),
            nombrePropietario = "Ana López"
        )

        Spacer(modifier = Modifier.height(80.dp)) // Espacio para el BottomBar
    }
}

@Composable
fun BuscadorFiltrosPacientesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Buscador
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = {
                    Text(
                        "Buscar por nombre de mascota o\npropietario...",
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    )
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.DarkGray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE5E5E5),
                    focusedBorderColor = Color(0xFF4A7D5E)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Chips de Filtro (Scroll horizontal)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FiltroMascotaChip(text = "Todos", isSelected = true, showIcon = true)
                FiltroMascotaChip(text = "Caninos", isSelected = false)
                FiltroMascotaChip(text = "Felinos", isSelected = false)
                FiltroMascotaChip(text = "Exóticos", isSelected = false)
            }
        }
    }
}

@Composable
fun FiltroMascotaChip(text: String, isSelected: Boolean, showIcon: Boolean = false) {
    val bgColor = if (isSelected) Color(0xFF8BA793) else Color.White
    val contentColor = if (isSelected) Color(0xFF1E3D2A) else Color.DarkGray
    val borderColor = if (isSelected) Color.Transparent else Color(0xFFD4D4D4)

    Row(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(20.dp))
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showIcon) {
            Icon(
                imageVector = Icons.Default.Pets,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = text,
            fontSize = 13.sp,
            color = contentColor,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
fun PacienteItemCard(
    nombre: String,
    tipoRaza: String,
    estado: String,
    estadoBgColor: Color,
    estadoTextColor: Color,
    nombrePropietario: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: Navegar al detalle del paciente */ },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Fila Superior: Foto, Info y Etiqueta
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar temporal de la mascota
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Info principal
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = nombre,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tipoRaza,
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                }

                // Etiqueta de Estado
                Box(
                    modifier = Modifier
                        .background(estadoBgColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = estado,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = estadoTextColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Fila Inferior: Propietario
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Propietario",
                        tint = Color.DarkGray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = nombrePropietario,
                        fontSize = 13.sp,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Medium
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Ver detalles",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}