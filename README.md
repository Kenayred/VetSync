# 🐾 VetSync – Sistema Inteligente de Gestión Veterinaria

<p align="center">
  <strong>Proyecto de Cátedra – ByteForge</strong><br>
  Grupo E
</p>

## 📌 Descripción

**VetSync** es un sistema de gestión veterinaria desarrollado para apoyar la organización de procesos de la **Clínica Veterinaria Super Mascota**, ubicada en Apopa, San Salvador.

El proyecto busca mejorar principalmente la gestión de pacientes, mascotas, servicios, citas y expedientes clínicos, incorporando reglas de negocio que permitan una administración más organizada de la información.

VetSync contempla dos tipos principales de usuarios:

- **Cliente:** administra sus mascotas y solicita y consulta sus citas.
- **Administrador/Veterinario:** gestiona citas, servicios, pacientes, notas clínicas y reportes.

El desarrollo se realiza de manera progresiva en diferentes etapas, iniciando con un núcleo funcional en **Kotlin modo consola** y posteriormente extendiéndose a una aplicación móvil Android.

---

# 🎯 Objetivos

## Objetivo general

Desarrollar VetSync como una solución digital para apoyar la gestión de pacientes, mascotas, citas y expedientes de una clínica veterinaria, aplicando reglas de negocio y una estructura organizada que facilite su mantenimiento y futura implementación móvil.

## Objetivos específicos

- Gestionar usuarios y sus roles.
- Registrar y administrar múltiples mascotas por cliente.
- Gestionar servicios veterinarios y sus costos.
- Registrar y administrar citas.
- Validar disponibilidad de horarios.
- Controlar los estados de las citas.
- Permitir la reprogramación y cancelación según las reglas de negocio.
- Registrar notas clínicas.
- Consultar historiales clínicos.
- Generar reportes y estadísticas.
- Aplicar validaciones y manejo de excepciones.
- Mantener un registro de errores mediante archivos de log.
- Aplicar conceptos de Programación Orientada a Objetos.

---

# Funcionalidades anteriores

La implementación anterior de la **Etapa 2** se encuentra desarrollada en Kotlin modo consola.

### 👤 Gestión de usuarios

- Registro de clientes.
- Inicio de sesión.
- Validación de usuarios.
- Control de roles.
- Cierre de sesión.
- Menús diferenciados para Cliente y Administrador/Veterinario.

### 🐶 Gestión de mascotas

- Registrar mascotas.
- Consultar mascotas.
- Actualizar información.
- Eliminar mascotas.
- Relacionar cada mascota con su propietario.
- Consultar mascotas del usuario autenticado.
- Manejo de información como:
  - Nombre
  - Especie
  - Edad
  - Raza
  - Sexo
  - Peso
  - Microchip
  - Alergias

### 🩺 Gestión de servicios

- Consultar servicios disponibles.
- Registrar servicios.
- Actualizar servicios.
- Desactivar servicios mediante eliminación lógica.
- Gestionar costos de los servicios.

### 📅 Gestión de citas

- Solicitar citas.
- Asociar una cita con:
  - Propietario
  - Mascota
  - Servicio
  - Fecha
  - Hora
- Validar fecha y hora.
- Validar horario de atención.
- Evitar traslapes.
- Consultar citas del propietario.
- Reprogramar citas pendientes.
- Cancelar citas mediante cambio de estado.
- Controlar transiciones entre estados.

### 📋 Estados de las citas

Las citas utilizan los siguientes estados:

```text
PENDIENTE
CONFIRMADA
COMPLETADA
CANCELADA
```

# Funcionalidades Actuales

La implementación de la **Etapa 3** marca la transición del sistema hacia una aplicación móvil nativa para Android, desarrollando la interfaz gráfica (UI) de forma declarativa utilizando **Jetpack Compose**. 

A continuación se detallan los módulos y pantallas desarrolladas para esta etapa:

### 📱 Navegación Principal e Interfaz
- Estructuración de la navegación mediante una barra inferior (*BottomBar*).
- Acceso directo a las vistas principales de la aplicación: Inicio, Mis Mascotas, Citas, Notificaciones y Perfil.

### 👤 Módulo de Usuario y Autenticación
- **Registro e Inicio de sesión:** Pantallas de *login* y registro para clientes.
- **Gestión del Perfil:** 
  - Pantalla para visualizar toda la información personal del usuario.
  - Pantalla de formulario para editar y actualizar los datos del usuario.
  - Opción de cierre de sesión segura (por reglas de negocio actuales, no se permite la eliminación de la cuenta).

### 🐶 Módulo de Mascotas (UI)
- **Listado interactivo:** Pantalla que muestra todas las mascotas registradas por el usuario autenticado.
- **Vista de detalles:** Pantalla específica para observar la información completa y detallada de una mascota seleccionada.
- **Edición de información:** Formulario integrado (accesible desde el detalle) para actualizar los datos de la mascota (nombre, raza, peso, fechas, etc.).

### 📅 Módulo de Agendamiento y Gestión de Citas
- **Flujo guiado para nueva cita:**
  1. Selección de la mascota a atender.
  2. Selección del servicio veterinario a realizar.
  3. Selección de fecha y hora, integrando un motor de validación que filtra y muestra **únicamente los horarios disponibles** para evitar traslapes.
  4. Pantalla de confirmación para revisar el resumen de la reserva antes de enviarla.
- **Administración de citas del cliente:**
  - Pantalla dedicada para listar el historial de citas asociadas al usuario.
  - Vista de detalles de la cita.
  - Funcionalidad para **reprogramar citas**.

### 🔔 Notificaciones
- Pantalla dedicada a la bandeja de notificaciones para mantener al cliente informado sobre actualizaciones de la clínica o cambios en el estado de sus servicios.

## 🎨 Diseño UX/UI y Mockups

El diseño de la interfaz de usuario de **VetSync** (incluyendo los flujos de Cliente, Administrador/Veterinario y Modo Oscuro) fue elaborado en **Figma**. 

Puedes explorar el prototipo interactivo y todas las pantallas completas en el siguiente enlace:

👉 **[Ver Mockups y Prototipo Interactivo en Figma](https://www.figma.com/design/0Jpg74BJwsswNO80F35fbi/VetSync?node-id=0-1&t=yz79oJSiThlVsF0x-1)**

### Pantallas incluidas en el diseño:
* **Acceso:** Splash Screen, Inicio de Sesión y Registro de Usuario.
* **Flujo del Cliente:** Inicio (Próxima cita y accesos rápidos), Mis Mascotas, Agendamiento Inteligente de Citas y Notificaciones.
* **Flujo del Administrador/Veterinario:** Gestión de citas, edición de expedientes clínicos y dashboard de estadísticas.
* **Accesibilidad:** Soporte completo para tema claro y modo oscuro.

## 📄 Licencia

<p>
  <a href="https://github.com/Kenayred/VetSync">VetSync</a> © 2026 by 
  <a href="https://github.com/Kenayred/VetSync">ByteForge</a> (Jeferson Enrique Crespín Galdámez & Kennet Adonay Vargas Hernández) is licensed under 
  <a href="https://creativecommons.org/licenses/by-nc/4.0/">CC BY-NC 4.0</a>
  <img src="https://mirrors.creativecommons.org/presskit/icons/cc.svg" alt="CC" width="18" height="18">
  <img src="https://mirrors.creativecommons.org/presskit/icons/by.svg" alt="BY" width="18" height="18">
  <img src="https://mirrors.creativecommons.org/presskit/icons/nc.svg" alt="NC" width="18" height="18">
</p>
