import java.util.Scanner

// 1. EL MODELO (Basado en la clase Producto/Persona de tus apuntes)
class Mascota(val nombre: String, val especie: String, var edad: Int) {
    fun mostrarDetalles() {
        println("Paciente: $nombre | Especie: $especie | Edad: $edad años")
    }
}

// 2. EL GESTOR (Basado en la clase Inventario de tus apuntes)
class GestorMascotas {
    private val pacientes: MutableList<Mascota> = mutableListOf()

    fun agregarPaciente(mascota: Mascota) {
        pacientes.add(mascota)
    }

    fun mostrarPacientes() {
        if (pacientes.isEmpty()) {
            println("No hay pacientes registrados en la clínica.")
        } else {
            println("----- Lista de Pacientes -----")
            for (paciente in pacientes) {
                paciente.mostrarDetalles()
            }
        }
    }
}

// 3. LA VISTA / MENÚ (Basado en la clase Menu de tus apuntes)
object Menu {
    private val scanner = Scanner(System.`in`)
    private val gestor = GestorMascotas()

    fun ejecutarMenu() {
        var opcion: Int
        do {
            println("\n--- 🐾 VetSync: Menú Principal ---")
            println("1. Registrar nueva mascota")
            println("2. Mostrar mascotas registradas")
            println("3. Salir")
            print("Ingrese una opción: ")

            opcion = scanner.nextInt()

            when (opcion) {
                1 -> agregarMascota()
                2 -> gestor.mostrarPacientes()
                3 -> println("Saliendo del sistema VetSync. ¡Hasta luego!")
                else -> println("Opción inválida. Intente de nuevo.")
            }
        } while (opcion != 3)
    }

    private fun agregarMascota() {
        println("--- Agregar Paciente ---")
        print("Nombre de la mascota: ")
        val nombre = scanner.next()
        print("Especie (Ej. Perro/Gato): ")
        val especie = scanner.next()
        print("Edad: ")
        val edad = scanner.nextInt()

        val nuevaMascota = Mascota(nombre, especie, edad)
        gestor.agregarPaciente(nuevaMascota)
        println("¡Paciente agregado con éxito!")
    }
}

// 4. PUNTO DE ENTRADA (Main)
fun main() {
    println("Iniciando sistema VetSync...")
    Menu.ejecutarMenu()
}