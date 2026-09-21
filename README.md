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

# 🧩 Funcionalidades actuales

La implementación actual de la **Etapa 2** se encuentra desarrollada en Kotlin modo consola.

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
