package com.example.vetsync.vista.screens

import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vetsync.controlador.MascotaControlador
import com.example.vetsync.modelo.ImagenUtils
import com.example.vetsync.modelo.Mascota
import com.example.vetsync.modelo.MascotaConDueno
import com.example.vetsync.vista.theme.*
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
@Composable
fun AdminPacientesScreen(
    controladorMascota: MascotaControlador = remember {MascotaControlador()},
    modifier: Modifier = Modifier
) {
    val backgroundColor = Color(0xFFFDFCF9)

    var listaMascotas by remember {mutableStateOf<List<MascotaConDueno>>(emptyList())}
    var textoBusqueda by remember {mutableStateOf("")}
    var filtroSeleccionado by remember {mutableStateOf("Todos")}

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val posicionYBuscador by remember {mutableStateOf(0f)}

    LaunchedEffect(Unit) {
        Log.e("PRUEBA_MASCOTAS", "Iniciando peticion")
        controladorMascota.obtenerMascotasAdmin(
            onSuccess = {mascotas ->
                Log.e("EXITO", "${mascotas.size}")
                listaMascotas = mascotas.toList()},
            onError = {error -> Log.e("ERROR", "${error}")}
        )

    }

    val listaMostrada = listaMascotas.filter { mascota ->
        val nombreMascota = mascota.mascota.nombre ?: ""
        val nombreDueno = mascota.nombreDueno ?: ""

        val coincideTexto = nombreMascota.contains(textoBusqueda, ignoreCase = true) ||
                nombreDueno.contains(textoBusqueda, ignoreCase = true)

        val coincideFiltro = if(filtroSeleccionado == "Todos"){
            true
        } else {
            val especieFiltro = filtroSeleccionado.dropLast(1)
            val especieMascota = mascota.mascota.especie ?: ""
            especieMascota.equals(especieFiltro, ignoreCase = true)
        }

        coincideTexto && coincideFiltro
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoCrema)
            .imePadding()
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
        BuscadorFiltrosPacientesCard(
            textoBusqueda = textoBusqueda,
            onBusquedaChange = { textoBusqueda = it },
            filtroSeleccionado = filtroSeleccionado,
            onFiltroChange = { filtroSeleccionado = it },
            onSearchFocus = {
                coroutineScope.launch {
                    scrollState.animateScrollTo(posicionYBuscador.toInt())
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column {
            if (listaMostrada.isEmpty()) {
                Text(
                    text = "No se encontraron mascotas.",
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 16.dp)
                )
            } else {
                listaMostrada.forEach { mascota ->
                    PacienteItemCard(
                        nombre = mascota.mascota.nombre,
                        tipoRaza = "${mascota.mascota.especie} · ${mascota.mascota.raza}",
                        estado = "",
                        estadoBgColor = Color(0xFFE2EFE7),
                        estadoTextColor = Color(0xFF2E5B4B),
                        nombrePropietario = mascota.nombreDueno,
                        fotoUrl = mascota.mascota.fotoUrl
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun BuscadorFiltrosPacientesCard(
    textoBusqueda: String,
    onBusquedaChange: (String) -> Unit,
    filtroSeleccionado: String,
    onFiltroChange: (String) -> Unit,
    onSearchFocus: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Buscador
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { nuevaLetra -> onBusquedaChange(nuevaLetra) },
                placeholder = {
                    Text("Buscar por nombre de mascota o\npropietario...", fontSize = 14.sp, lineHeight = 18.sp)
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.DarkGray) },
                modifier = Modifier.fillMaxWidth().onFocusChanged{
                    foco ->
                    if(foco.isFocused){
                        onSearchFocus()
                    }
                },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE5E5E5),
                    focusedBorderColor = Color(0xFF4A7D5E)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Chips de Filtro
            val opcionesFiltro = listOf("Todos", "Caninos", "Felinos", "Exóticos")

            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                opcionesFiltro.forEach { opcion ->
                    FiltroMascotaChip(
                        text = opcion,
                        isSelected = (filtroSeleccionado == opcion),
                        showIcon = (opcion == "Todos"),
                        modifier = Modifier.clickable { onFiltroChange(opcion) }
                    )
                }
            }
        }
    }
}

@Composable
fun FiltroMascotaChip(text: String, isSelected: Boolean, showIcon: Boolean = false, modifier: Modifier = Modifier) {
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
    nombrePropietario: String,
    fotoUrl: String
) {

    val bitmapMascota = remember(fotoUrl) { ImagenUtils.base64ABitmap(fotoUrl) }
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

                if (bitmapMascota != null) {
                    Image(
                        bitmap = bitmapMascota,
                        contentDescription = nombre,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(56.dp).clip(CircleShape)
                    )
                } else {

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )
                }
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