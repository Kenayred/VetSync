package com.example.vetsync.vista.screens

import android.util.Log

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.vetsync.controlador.MascotaControlador
import com.example.vetsync.controlador.CitaControlador
import com.example.vetsync.controlador.NotaClinicaControlador
import com.example.vetsync.controlador.UsuarioControlador

import com.example.vetsync.modelo.ImagenUtils
import com.example.vetsync.modelo.Mascota
import com.example.vetsync.modelo.MascotaConDueno
import com.example.vetsync.modelo.Cita
import com.example.vetsync.modelo.NotaClinica
import com.example.vetsync.modelo.Usuario

import com.example.vetsync.vista.AgregarMascotaScreen

import kotlinx.coroutines.launch

import java.util.Locale

@Composable
fun AdminPacientesScreen(
    controladorMascota: MascotaControlador = remember {
        MascotaControlador()
    },
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    val controladorCitas = remember {
        CitaControlador()
    }

    val controladorNotas = remember {
        NotaClinicaControlador()
    }

    val controladorUsuarios = remember {
        UsuarioControlador()
    }

    // Control de navegación para registrar pacientes.
    var mostrandoSelectorClientes by remember {
        mutableStateOf(false)
    }

    var clienteSeleccionadoParaRegistro by remember {
        mutableStateOf<Usuario?>(null)
    }

    // Clientes disponibles.
    var clientesDisponibles by remember {
        mutableStateOf<List<Usuario>>(emptyList())
    }

    var textoBusquedaClientes by remember {
        mutableStateOf("")
    }

    var cargandoClientes by remember {
        mutableStateOf(false)
    }

    var errorClientes by remember {
        mutableStateOf<String?>(null)
    }

    var recargarClientes by remember {
        mutableStateOf(0)
    }

    var pacienteSeleccionado by remember {
        mutableStateOf<MascotaConDueno?>(null)
    }

    var pacienteEnEdicion by remember {
        mutableStateOf<MascotaConDueno?>(null)
    }

    // Datos de la ficha.
    var citasPaciente by remember {
        mutableStateOf<List<Cita>>(emptyList())
    }

    var historialPaciente by remember {
        mutableStateOf<List<NotaClinica>>(emptyList())
    }

    // Estados de carga independientes.
    var cargandoCitas by remember {
        mutableStateOf(false)
    }

    var cargandoHistorial by remember {
        mutableStateOf(false)
    }

    // Errores independientes.
    var errorCitas by remember {
        mutableStateOf<String?>(null)
    }

    var errorHistorial by remember {
        mutableStateOf<String?>(null)
    }

    var recargarDetalle by remember {
        mutableStateOf(0)
    }

    var listaMascotas by remember {
        mutableStateOf<List<MascotaConDueno>>(emptyList())
    }

    var textoBusqueda by remember {
        mutableStateOf("")
    }

    var filtroSeleccionado by remember {
        mutableStateOf("Todos")
    }

    var cargando by remember {
        mutableStateOf(true)
    }

    var errorCarga by remember {
        mutableStateOf<String?>(null)
    }

    var recargar by remember {
        mutableStateOf(0)
    }

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    var posicionYBuscador by remember {
        mutableStateOf(0f)
    }

    // --------------------------------------------------
    // CARGAR TODAS LAS MASCOTAS
    // --------------------------------------------------

    fun cargarMascotas() {

        cargando = true
        errorCarga = null

        controladorMascota.obtenerMascotasAdmin(

            onSuccess = { mascotas ->

                listaMascotas = mascotas
                cargando = false

                Log.d(
                    "AdminPacientes",
                    "Pacientes recuperados: ${mascotas.size}"
                )
            },

            onError = { error ->

                errorCarga = error
                cargando = false

                Log.e(
                    "AdminPacientes",
                    "Error al consultar pacientes: $error"
                )
            }
        )
    }

    LaunchedEffect(recargar) {
        cargarMascotas()
    }

    // --------------------------------------------------
    // CARGAR CLIENTES PARA REGISTRAR UNA NUEVA MASCOTA
    // --------------------------------------------------

    LaunchedEffect(
        mostrandoSelectorClientes,
        recargarClientes
    ) {

        if (mostrandoSelectorClientes) {

            cargandoClientes = true
            errorClientes = null

            controladorUsuarios.obtenerClientesAdmin(

                onSuccess = { clientes ->

                    clientesDisponibles = clientes
                    cargandoClientes = false
                    errorClientes = null
                },

                onError = { error ->

                    errorClientes = error
                    cargandoClientes = false
                }
            )
        }
    }

    // =====================================================
    // CARGAR INFORMACIÓN DE LA FICHA DEL PACIENTE
    // =====================================================

    LaunchedEffect(
        pacienteSeleccionado?.mascota?.id,
        recargarDetalle
    ) {

        val paciente = pacienteSeleccionado

        if (paciente == null) {

            citasPaciente = emptyList()
            historialPaciente = emptyList()

            return@LaunchedEffect
        }

        val mascotaId = paciente.mascota.id

        cargandoCitas = true
        cargandoHistorial = true

        errorCitas = null
        errorHistorial = null

        // Consultar citas de la mascota.
        controladorCitas.obtenerCitasMascotaAdmin(

            mascotaId = mascotaId,

            onSuccess = { lista ->

                if (
                    pacienteSeleccionado?.mascota?.id == mascotaId
                ) {
                    citasPaciente = lista
                    cargandoCitas = false
                    errorCitas = null
                }
            },

            onFailure = { error ->

                if (
                    pacienteSeleccionado?.mascota?.id == mascotaId
                ) {
                    cargandoCitas = false

                    errorCitas = error.message
                        ?: "No se pudieron consultar las citas."
                }
            }
        )

        // Consultar historial clínico.
        controladorNotas.obtenerHistorialMascotaAdmin(

            mascotaId = mascotaId,

            onSuccess = { historial ->

                if (
                    pacienteSeleccionado?.mascota?.id == mascotaId
                ) {
                    historialPaciente = historial
                    cargandoHistorial = false
                    errorHistorial = null
                }
            },

            onError = { mensaje ->

                if (
                    pacienteSeleccionado?.mascota?.id == mascotaId
                ) {
                    cargandoHistorial = false
                    errorHistorial = mensaje
                }
            }
        )
    }

    // --------------------------------------------------
    // APLICAR BÚSQUEDA Y FILTROS
    // --------------------------------------------------

    val listaMostrada = listaMascotas.filter { paciente ->

        val nombreMascota = paciente.mascota.nombre
        val nombreDueno = paciente.nombreDueno
        val consulta = textoBusqueda.trim()

        val coincideTexto =
            nombreMascota.contains(
                consulta,
                ignoreCase = true
            ) ||
                    nombreDueno.contains(
                        consulta,
                        ignoreCase = true
                    )

        val coincideFiltro = coincideFiltroEspecie(
            especieTexto = paciente.mascota.especie,
            filtro = filtroSeleccionado
        )

        coincideTexto && coincideFiltro
    }

    // =====================================================
    // MOSTRAR EDITOR DE PACIENTE
    // =====================================================

    if (pacienteEnEdicion != null) {

        val paciente = pacienteEnEdicion!!

        EditarMascotaAdminScreen(
            mascota = paciente.mascota,
            controlador = controladorMascota,

            onBackClick = {
                pacienteEnEdicion = null
            },

            onMascotaEditada = { mascotaActualizada ->

                // Actualizar la mascota en el listado.
                listaMascotas = listaMascotas.map { elemento ->

                    if (elemento.mascota.id == mascotaActualizada.id) {
                        elemento.copy(mascota = mascotaActualizada)
                    } else {
                        elemento
                    }
                }

                // Actualizar también la ficha abierta.
                pacienteSeleccionado = pacienteSeleccionado?.let { seleccionado ->

                    if (seleccionado.mascota.id == mascotaActualizada.id) {
                        seleccionado.copy(mascota = mascotaActualizada)
                    } else {
                        seleccionado
                    }
                }

                pacienteEnEdicion = null
            }
        )

        return
    }

    // =====================================================
    // MOSTRAR FICHA INDIVIDUAL O LISTADO
    // =====================================================

    if (pacienteSeleccionado != null) {

        FichaPacienteAdmin(
            paciente = pacienteSeleccionado!!,
            citas = citasPaciente,
            historial = historialPaciente,
            cargandoCitas = cargandoCitas,
            cargandoHistorial = cargandoHistorial,
            errorCitas = errorCitas,
            errorHistorial = errorHistorial,

            onEditar = {
                pacienteEnEdicion = pacienteSeleccionado
            },

            onVolver = {
                pacienteSeleccionado = null
            },

            onReintentar = {
                recargarDetalle++
            }
        )

        return
    }

    // =====================================================
    // FORMULARIO PARA REGISTRAR UNA NUEVA MASCOTA
    // =====================================================

    clienteSeleccionadoParaRegistro?.let { cliente ->

        AgregarMascotaScreen(
            controlador = controladorMascota,
            propietarioId = cliente.id,

            onBackClick = {
                clienteSeleccionadoParaRegistro = null
            },

            onCancelarClick = {
                clienteSeleccionadoParaRegistro = null
            },

            onMascotaGuardada = {
                clienteSeleccionadoParaRegistro = null
                recargar++
            }
        )

        return
    }

    // =====================================================
    // SELECTOR DE CLIENTES
    // =====================================================

    if (mostrandoSelectorClientes) {

        SelectorClientePacienteAdmin(
            clientes = clientesDisponibles,
            textoBusqueda = textoBusquedaClientes,

            onBusquedaChange = {
                textoBusquedaClientes = it
            },

            cargando = cargandoClientes,
            error = errorClientes,

            onSeleccionarCliente = { cliente ->

                clienteSeleccionadoParaRegistro = cliente
                mostrandoSelectorClientes = false
            },

            onVolver = {
                mostrandoSelectorClientes = false
                textoBusquedaClientes = ""
            },

            onReintentar = {
                recargarClientes++
            }
        )

        return
    }

    // --------------------------------------------------
    // INTERFAZ PRINCIPAL
    // --------------------------------------------------

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .imePadding()
            .verticalScroll(scrollState)
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            )
    ) {

        // CABECERA.

        Text(
            text = "Pacientes",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "Gestión clínica de mascotas registradas.",
            fontSize = 14.sp,
            color = colorScheme.onSurfaceVariant
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        Button(
            onClick = {
                textoBusquedaClientes = ""
                mostrandoSelectorClientes = true
            },

            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),

            shape = RoundedCornerShape(12.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary
            )
        ) {

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text(
                text = "Nuevo Paciente",
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        // RESUMEN DE PACIENTES.

        Card(
            modifier = Modifier.fillMaxWidth(),

            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface
            ),

            shape = RoundedCornerShape(12.dp),

            border = BorderStroke(
                1.dp,
                colorScheme.outlineVariant
            )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),

                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = null,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(30.dp)
                )

                Spacer(
                    modifier = Modifier.width(14.dp)
                )

                Column {

                    Text(
                        text = if (cargando) {
                            "Consultando..."
                        } else {
                            listaMascotas.size.toString()
                        },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )

                    Text(
                        text = "Mascotas registradas",
                        fontSize = 12.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // BUSCADOR Y FILTROS.

        BuscadorFiltrosPacientesCard(
            textoBusqueda = textoBusqueda,

            onBusquedaChange = {
                textoBusqueda = it
            },

            filtroSeleccionado = filtroSeleccionado,

            onFiltroChange = {
                filtroSeleccionado = it
            },

            modifier = Modifier.onGloballyPositioned { coordenadas ->
                posicionYBuscador =
                    coordenadas.positionInParent().y
            },

            onSearchFocus = {
                coroutineScope.launch {
                    scrollState.animateScrollTo(
                        posicionYBuscador.toInt()
                    )
                }
            }
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // RESULTADOS.

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = "Listado de pacientes",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )

            Text(
                text = "${listaMostrada.size} resultados",
                fontSize = 12.sp,
                color = colorScheme.onSurfaceVariant
            )
        }

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        when {

            // CARGANDO.

            cargando -> {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(45.dp),

                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        CircularProgressIndicator(
                            color = colorScheme.primary
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Text(
                            text = "Cargando pacientes...",
                            fontSize = 13.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ERROR.

            errorCarga != null -> {

                Card(
                    modifier = Modifier.fillMaxWidth(),

                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.errorContainer
                    ),

                    shape = RoundedCornerShape(14.dp)
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),

                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "No se pudieron cargar los pacientes.",
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onErrorContainer
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = errorCarga.orEmpty(),
                            fontSize = 12.sp,
                            color = colorScheme.onErrorContainer
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Button(
                            onClick = {
                                recargar++
                            },

                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorScheme.primary,
                                contentColor = colorScheme.onPrimary
                            )
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            // SIN MASCOTAS REGISTRADAS.

            listaMascotas.isEmpty() -> {

                EstadoPacientesVacio(
                    mensaje = "Todavía no hay mascotas registradas."
                )
            }

            // SIN RESULTADOS DE BÚSQUEDA.

            listaMostrada.isEmpty() -> {

                EstadoPacientesVacio(
                    mensaje = "No se encontraron mascotas con esos criterios."
                )
            }

            // MOSTRAR PACIENTES.

            else -> {

                listaMostrada.forEach { paciente ->

                    PacienteItemCard(
                        nombre = paciente.mascota.nombre,

                        tipoRaza = listOf(
                            paciente.mascota.especie,
                            paciente.mascota.raza
                        )
                            .filter { it.isNotBlank() }
                            .joinToString(" • "),

                        estado = "Registrada",
                        estadoBgColor = colorScheme.primaryContainer,
                        estadoTextColor = colorScheme.onPrimaryContainer,
                        nombrePropietario = paciente.nombreDueno,
                        fotoUrl = paciente.mascota.fotoUrl,

                        onClick = {
                            pacienteSeleccionado = paciente
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(80.dp)
        )
    }
}

// =====================================================
// SELECTOR DE CLIENTES
// =====================================================

@Composable
private fun SelectorClientePacienteAdmin(
    clientes: List<Usuario>,
    textoBusqueda: String,
    onBusquedaChange: (String) -> Unit,
    cargando: Boolean,
    error: String?,
    onSeleccionarCliente: (Usuario) -> Unit,
    onVolver: () -> Unit,
    onReintentar: () -> Unit
) {

    val colorScheme = MaterialTheme.colorScheme

    val clientesFiltrados = clientes.filter { cliente ->

        val consulta = textoBusqueda.trim()

        consulta.isBlank() ||
                cliente.nombre.contains(
                    consulta,
                    ignoreCase = true
                ) ||
                cliente.correo.contains(
                    consulta,
                    ignoreCase = true
                ) ||
                cliente.telefono.contains(
                    consulta,
                    ignoreCase = true
                ) ||
                cliente.username.contains(
                    consulta,
                    ignoreCase = true
                )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            )
    ) {

        TextButton(
            onClick = onVolver,
            contentPadding = PaddingValues(
                horizontal = 0.dp,
                vertical = 4.dp
            )
        ) {

            Text(
                text = "← Volver a pacientes",
                color = colorScheme.primary,
                fontSize = 14.sp
            )
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Text(
            text = "Nuevo paciente",
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = "Selecciona al propietario al que deseas asociar la nueva mascota.",
            fontSize = 14.sp,
            color = colorScheme.onSurfaceVariant,
            lineHeight = 20.sp
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // BUSCADOR DE CLIENTES.

        OutlinedTextField(
            value = textoBusqueda,
            onValueChange = onBusquedaChange,
            modifier = Modifier.fillMaxWidth(),

            placeholder = {
                Text(
                    text = "Buscar por nombre, correo o teléfono...",
                    fontSize = 13.sp
                )
            },

            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            },

            singleLine = true,

            shape = RoundedCornerShape(12.dp),

            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorScheme.primary,
                unfocusedBorderColor = colorScheme.outlineVariant,
                focusedContainerColor = colorScheme.surface,
                unfocusedContainerColor = colorScheme.surface,
                cursorColor = colorScheme.primary
            )
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = "Clientes registrados (${clientesFiltrados.size})",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        when {

            cargando -> {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),

                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        CircularProgressIndicator(
                            color = colorScheme.primary
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Text(
                            text = "Consultando clientes...",
                            fontSize = 13.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            error != null -> {

                Card(
                    modifier = Modifier.fillMaxWidth(),

                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.errorContainer
                    ),

                    shape = RoundedCornerShape(14.dp)
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),

                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "No se pudieron consultar los clientes.",
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onErrorContainer
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = error,
                            fontSize = 12.sp,
                            color = colorScheme.onErrorContainer
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Button(
                            onClick = onReintentar,

                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorScheme.primary,
                                contentColor = colorScheme.onPrimary
                            )
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            clientes.isEmpty() -> {

                EstadoPacientesVacio(
                    mensaje = "Todavía no hay clientes registrados."
                )
            }

            clientesFiltrados.isEmpty() -> {

                EstadoPacientesVacio(
                    mensaje = "No se encontraron clientes con ese criterio de búsqueda."
                )
            }

            else -> {

                clientesFiltrados.forEach { cliente ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSeleccionarCliente(cliente)
                            },

                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.surface
                        ),

                        shape = RoundedCornerShape(14.dp),

                        border = BorderStroke(
                            1.dp,
                            colorScheme.outlineVariant
                        )
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),

                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(colorScheme.primaryContainer),

                                contentAlignment = Alignment.Center
                            ) {

                                Icon(
                                    imageVector = Icons.Outlined.Person,
                                    contentDescription = null,
                                    tint = colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(27.dp)
                                )
                            }

                            Spacer(
                                modifier = Modifier.width(12.dp)
                            )

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    text = cliente.nombre.ifBlank {
                                        "Cliente sin nombre"
                                    },

                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.onSurface
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text = cliente.correo.ifBlank {
                                        "Correo no disponible"
                                    },

                                    fontSize = 12.sp,
                                    color = colorScheme.onSurfaceVariant
                                )

                                if (cliente.telefono.isNotBlank()) {

                                    Spacer(
                                        modifier = Modifier.height(3.dp)
                                    )

                                    Text(
                                        text = "Teléfono: ${cliente.telefono}",
                                        fontSize = 12.sp,
                                        color = colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Seleccionar cliente",
                                tint = colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(30.dp)
        )
    }
}

// =====================================================
// FICHA INDIVIDUAL DEL PACIENTE
// =====================================================

@Composable
private fun FichaPacienteAdmin(
    paciente: MascotaConDueno,
    citas: List<Cita>,
    historial: List<NotaClinica>,
    cargandoCitas: Boolean,
    cargandoHistorial: Boolean,
    errorCitas: String?,
    errorHistorial: String?,
    onEditar: () -> Unit,
    onVolver: () -> Unit,
    onReintentar: () -> Unit
) {

    val colorScheme = MaterialTheme.colorScheme
    val mascota = paciente.mascota

    val bitmapMascota = remember(mascota.fotoUrl) {
        runCatching {
            ImagenUtils.base64ABitmap(mascota.fotoUrl)
        }.getOrNull()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = 20.dp,
                vertical = 14.dp
            )
    ) {

        // CABECERA.

        TextButton(
            onClick = onVolver,
            contentPadding = PaddingValues(
                horizontal = 0.dp,
                vertical = 4.dp
            )
        ) {

            Text(
                text = "← Volver a pacientes",
                color = colorScheme.primary,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Ficha del paciente",
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(18.dp))

        // INFORMACIÓN PRINCIPAL.

        Card(
            modifier = Modifier.fillMaxWidth(),

            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface
            ),

            shape = RoundedCornerShape(18.dp),

            border = BorderStroke(
                1.dp,
                colorScheme.outlineVariant
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (bitmapMascota != null) {

                    Image(
                        bitmap = bitmapMascota,
                        contentDescription = mascota.nombre,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                    )

                } else {

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(colorScheme.primaryContainer),

                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.Pets,
                            contentDescription = null,
                            tint = colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = mascota.nombre,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "${mascota.especie} • ${mascota.raza}",
                    fontSize = 14.sp,
                    color = colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = colorScheme.primaryContainer,
                    contentColor = colorScheme.onPrimaryContainer,
                    shape = RoundedCornerShape(20.dp)
                ) {

                    Text(
                        text = "Paciente registrado",
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 6.dp
                        ),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // DATOS GENERALES.

        TituloSeccionFicha("Información general")

        Card(
            modifier = Modifier.fillMaxWidth(),

            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface
            ),

            shape = RoundedCornerShape(14.dp),

            border = BorderStroke(
                1.dp,
                colorScheme.outlineVariant
            )
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                DatoFicha(
                    titulo = "Especie",
                    valor = mascota.especie
                )

                DatoFicha(
                    titulo = "Raza",
                    valor = mascota.raza
                )

                DatoFicha(
                    titulo = "Edad",
                    valor = "${mascota.edad} años"
                )

                DatoFicha(
                    titulo = "Sexo",
                    valor = mascota.sexo
                )

                DatoFicha(
                    titulo = "Fecha de nacimiento",
                    valor = mascota.fechaNacimiento
                )

                DatoFicha(
                    titulo = "Peso",
                    valor = if (mascota.peso > 0) {
                        "${mascota.peso} kg"
                    } else {
                        "No registrado"
                    }
                )

                DatoFicha(
                    titulo = "Color y marcas",
                    valor = mascota.colorMarcas.ifBlank {
                        "No registrado"
                    }
                )

                DatoFicha(
                    titulo = "Observaciones",
                    valor = mascota.observaciones.ifBlank {
                        "Sin observaciones"
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onEditar,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(
                1.dp,
                colorScheme.primary
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = colorScheme.primary
            )
        ) {
            Text(
                text = "Editar datos del paciente",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // PROPIETARIO.

        TituloSeccionFicha("Información del propietario")

        Card(
            modifier = Modifier.fillMaxWidth(),

            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface
            ),

            shape = RoundedCornerShape(14.dp),

            border = BorderStroke(
                1.dp,
                colorScheme.outlineVariant
            )
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {

                        Text(
                            text = paciente.nombreDueno,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )

                        Text(
                            text = "Propietario registrado",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                DatoFicha(
                    titulo = "ID del propietario",
                    valor = mascota.idDueno
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // HISTORIAL DE CITAS.

        TituloSeccionFicha(
            "Historial de citas (${citas.size})"
        )

        when {

            cargandoCitas -> {

                CargandoFicha()
            }

            errorCitas != null -> {

                ErrorFicha(
                    mensaje = errorCitas,
                    onReintentar = onReintentar
                )
            }

            citas.isEmpty() -> {

                EstadoFichaVacio(
                    mensaje = "Esta mascota todavía no tiene citas registradas."
                )
            }

            else -> {

                citas.forEach { cita ->

                    val coloresEstado = obtenerColoresEstadoCita(
                        cita.estado
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),

                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.surface
                        ),

                        shape = RoundedCornerShape(14.dp),

                        border = BorderStroke(
                            1.dp,
                            colorScheme.outlineVariant
                        )
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Text(
                                    text = cita.nombreServicio,
                                    modifier = Modifier.weight(1f),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = colorScheme.onSurface
                                )

                                Surface(
                                    color = coloresEstado.first,
                                    contentColor = coloresEstado.second,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = cita.estado.etiqueta,
                                        modifier = Modifier.padding(
                                            horizontal = 8.dp,
                                            vertical = 4.dp
                                        ),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            DatoFicha(
                                titulo = "Fecha",
                                valor = cita.fecha
                            )

                            DatoFicha(
                                titulo = "Hora",
                                valor = cita.hora
                            )

                            DatoFicha(
                                titulo = "Costo",
                                valor = String.format(
                                    Locale.US,
                                    "$%.2f",
                                    cita.costo
                                )
                            )

                            if (cita.motivo.isNotBlank()) {

                                DatoFicha(
                                    titulo = "Motivo",
                                    valor = cita.motivo
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // HISTORIAL CLÍNICO.

        TituloSeccionFicha(
            "Historial clínico (${historial.size})"
        )

        when {

            cargandoHistorial -> {

                CargandoFicha()
            }

            errorHistorial != null -> {

                ErrorFicha(
                    mensaje = errorHistorial,
                    onReintentar = onReintentar
                )
            }

            historial.isEmpty() -> {

                EstadoFichaVacio(
                    mensaje = "Esta mascota todavía no tiene registros clínicos."
                )
            }

            else -> {

                historial.forEach { nota ->

                    Card(
                        modifier = Modifier.fillMaxWidth(),

                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.surface
                        ),

                        shape = RoundedCornerShape(14.dp),

                        border = BorderStroke(
                            1.dp,
                            colorScheme.outlineVariant
                        )
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = "Registro clínico",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            DatoFicha(
                                titulo = "Fecha",
                                valor = nota.fecha
                            )

                            DatoFicha(
                                titulo = "Motivo",
                                valor = nota.motivo.ifBlank {
                                    "No especificado"
                                }
                            )

                            if (nota.peso != null) {

                                DatoFicha(
                                    titulo = "Peso",
                                    valor = "${nota.peso} kg"
                                )
                            }

                            if (nota.temperatura != null) {

                                DatoFicha(
                                    titulo = "Temperatura",
                                    valor = "${nota.temperatura} °C"
                                )
                            }

                            DatoFicha(
                                titulo = "Diagnóstico",
                                valor = nota.diagnostico
                            )

                            DatoFicha(
                                titulo = "Receta médica",
                                valor = nota.receta
                            )

                            if (!nota.observaciones.isNullOrBlank()) {

                                DatoFicha(
                                    titulo = "Observaciones",
                                    valor = nota.observaciones.orEmpty()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(35.dp))
    }
}

// =====================================================
// COLORES SEMÁNTICOS PARA ESTADOS DE CITAS
// =====================================================

@Composable
private fun obtenerColoresEstadoCita(
    estado: com.example.vetsync.modelo.EstadoCita
): Pair<Color, Color> {

    val colorScheme = MaterialTheme.colorScheme

    return when (estado) {

        com.example.vetsync.modelo.EstadoCita.PENDIENTE ->
            colorScheme.tertiaryContainer to colorScheme.onTertiaryContainer

        com.example.vetsync.modelo.EstadoCita.CONFIRMADA ->
            colorScheme.secondaryContainer to colorScheme.onSecondaryContainer

        com.example.vetsync.modelo.EstadoCita.COMPLETADA ->
            colorScheme.surfaceVariant to colorScheme.onSurfaceVariant

        com.example.vetsync.modelo.EstadoCita.CANCELADA ->
            colorScheme.errorContainer to colorScheme.onErrorContainer
    }
}

// =====================================================
// TÍTULO DE SECCIÓN
// =====================================================

@Composable
private fun TituloSeccionFicha(
    titulo: String
) {

    Text(
        text = titulo,
        fontSize = 19.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )

    Spacer(modifier = Modifier.height(12.dp))
}

// =====================================================
// DATO DE LA FICHA
// =====================================================

@Composable
private fun DatoFicha(
    titulo: String,
    valor: String
) {

    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 11.dp)
    ) {

        Text(
            text = titulo,
            fontSize = 12.sp,
            color = colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = valor.ifBlank {
                "No registrado"
            },
            fontSize = 14.sp,
            color = colorScheme.onSurface
        )
    }
}

// =====================================================
// CARGANDO FICHA
// =====================================================

@Composable
private fun CargandoFicha() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),

        contentAlignment = Alignment.Center
    ) {

        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary
        )
    }
}

// =====================================================
// ERROR EN LA FICHA
// =====================================================

@Composable
private fun ErrorFicha(
    mensaje: String,
    onReintentar: () -> Unit
) {

    val colorScheme = MaterialTheme.colorScheme

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
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = mensaje,
                color = colorScheme.onErrorContainer,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onReintentar
            ) {

                Text(
                    text = "Reintentar",
                    color = colorScheme.onErrorContainer
                )
            }
        }
    }
}

// =====================================================
// ESTADO VACÍO DE LA FICHA
// =====================================================

@Composable
private fun EstadoFichaVacio(
    mensaje: String
) {

    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),

        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),

        shape = RoundedCornerShape(14.dp),

        border = BorderStroke(
            1.dp,
            colorScheme.outlineVariant
        )
    ) {

        Text(
            text = mensaje,
            modifier = Modifier.padding(20.dp),
            fontSize = 13.sp,
            color = colorScheme.onSurfaceVariant
        )
    }
}

// =====================================================
// BUSCADOR Y FILTROS DE PACIENTES
// =====================================================

@Composable
fun BuscadorFiltrosPacientesCard(
    textoBusqueda: String,
    onBusquedaChange: (String) -> Unit,
    filtroSeleccionado: String,
    onFiltroChange: (String) -> Unit,
    onSearchFocus: () -> Unit = {},
    modifier: Modifier = Modifier
) {

    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = modifier.fillMaxWidth(),

        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),

        shape = RoundedCornerShape(12.dp),

        border = BorderStroke(
            1.dp,
            colorScheme.outlineVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // BUSCADOR.

            OutlinedTextField(
                value = textoBusqueda,

                onValueChange = { nuevaLetra ->
                    onBusquedaChange(nuevaLetra)
                },

                placeholder = {
                    Text(
                        "Buscar por nombre de mascota o\npropietario...",
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    )
                },

                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = colorScheme.onSurfaceVariant
                    )
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { foco ->
                        if (foco.isFocused) {
                            onSearchFocus()
                        }
                    },

                shape = RoundedCornerShape(8.dp),

                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = colorScheme.outlineVariant,
                    focusedBorderColor = colorScheme.primary,
                    unfocusedContainerColor = colorScheme.surface,
                    focusedContainerColor = colorScheme.surface,
                    cursorColor = colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // CHIPS DE FILTRO.

            val opcionesFiltro = listOf(
                "Todos",
                "Perro",
                "Gato",
                "Ave",
                "Conejo",
                "Otro"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                opcionesFiltro.forEach { opcion ->

                    FiltroMascotaChip(
                        text = opcion,
                        isSelected = filtroSeleccionado == opcion,
                        showIcon = opcion == "Todos",
                        modifier = Modifier.clickable {
                            onFiltroChange(opcion)
                        }
                    )
                }
            }
        }
    }
}

// =====================================================
// CHIP DE FILTRO DE MASCOTA
// =====================================================

@Composable
fun FiltroMascotaChip(
    text: String,
    isSelected: Boolean,
    showIcon: Boolean = false,
    modifier: Modifier = Modifier
) {

    val colorScheme = MaterialTheme.colorScheme

    val bgColor = if (isSelected) {
        colorScheme.primaryContainer
    } else {
        colorScheme.surface
    }

    val contentColor = if (isSelected) {
        colorScheme.onPrimaryContainer
    } else {
        colorScheme.onSurfaceVariant
    }

    val borderColor = if (isSelected) {
        colorScheme.primary
    } else {
        colorScheme.outlineVariant
    }

    Row(
        modifier = modifier
            .background(
                bgColor,
                RoundedCornerShape(20.dp)
            )
            .border(
                1.dp,
                borderColor,
                RoundedCornerShape(20.dp)
            )
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),

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
            fontWeight = if (isSelected) {
                FontWeight.Medium
            } else {
                FontWeight.Normal
            }
        )
    }
}

// =====================================================
// TARJETA INDIVIDUAL DEL PACIENTE
// =====================================================

@Composable
fun PacienteItemCard(
    nombre: String,
    tipoRaza: String,
    estado: String,
    estadoBgColor: Color,
    estadoTextColor: Color,
    nombrePropietario: String,
    fotoUrl: String,
    onClick: () -> Unit
) {

    val colorScheme = MaterialTheme.colorScheme

    val bitmapMascota = remember(fotoUrl) {
        ImagenUtils.base64ABitmap(fotoUrl)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),

        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),

        shape = RoundedCornerShape(12.dp),

        border = BorderStroke(
            1.dp,
            colorScheme.outlineVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // FOTO, INFORMACIÓN Y ESTADO.

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (bitmapMascota != null) {

                    Image(
                        bitmap = bitmapMascota,
                        contentDescription = nombre,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                    )

                } else {

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(colorScheme.primaryContainer),

                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.Pets,
                            contentDescription = null,
                            tint = colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(27.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {

                    Text(
                        text = nombre,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = tipoRaza,
                        fontSize = 13.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = estadoBgColor,
                    contentColor = estadoTextColor,
                    shape = RoundedCornerShape(6.dp)
                ) {

                    Text(
                        text = estado,
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        ),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                color = colorScheme.outlineVariant,
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // PROPIETARIO.

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Propietario",
                        tint = colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = nombrePropietario,
                        fontSize = 13.sp,
                        color = colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Ver detalles",
                    tint = colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// =====================================================
// IDENTIFICAR ESPECIE PARA LOS FILTROS
// =====================================================

private fun coincideFiltroEspecie(
    especieTexto: String,
    filtro: String
): Boolean {

    val especie = especieTexto.trim().lowercase()

    val esPerro = listOf(
        "perro",
        "perra",
        "perros",
        "canino",
        "canina"
    ).any {
        especie.contains(it)
    }

    val esGato = listOf(
        "gato",
        "gata",
        "gatos",
        "felino",
        "felina"
    ).any {
        especie.contains(it)
    }

    val esAve = listOf(
        "ave",
        "aves",
        "pájaro",
        "pajaro",
        "loro",
        "perico"
    ).any {
        especie.contains(it)
    }

    val esConejo = listOf(
        "conejo",
        "coneja",
        "conejos"
    ).any {
        especie.contains(it)
    }

    return when (filtro) {
        "Todos" -> true
        "Perro" -> esPerro
        "Gato" -> esGato
        "Ave" -> esAve
        "Conejo" -> esConejo
        "Otro" -> especie.isNotBlank() &&
                !esPerro &&
                !esGato &&
                !esAve &&
                !esConejo
        else -> true
    }
}

// =====================================================
// ESTADO VACÍO DE PACIENTES
// =====================================================

@Composable
private fun EstadoPacientesVacio(
    mensaje: String
) {

    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(16.dp),

        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),

        border = BorderStroke(
            1.dp,
            colorScheme.outlineVariant
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Default.Pets,
                contentDescription = null,
                tint = colorScheme.primary,
                modifier = Modifier.size(42.dp)
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = mensaje,
                fontSize = 14.sp,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}