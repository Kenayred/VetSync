import com.example.vetsync.controlador.UsuarioController
import com.example.vetsync.vista.LoginView
import com.example.vetsync.vista.RegistroView
import com.example.vetsync.vista.Menu
import com.example.vetsync.utils.ConsolaUtil
import java.util.Scanner

fun main() {
    val scanner = Scanner(System.`in`)
    val usuarioController = UsuarioController()
    val consola = ConsolaUtil;
    var opcionInicio: Int

    println("🐾 Bienvenido al sistema VetSync 🐾")

    // Ciclo externo: Menú de Acceso
    do {
        consola.limpiarPantalla();

        println("\n--- 🐾 Menú de Acceso ---")
        println("1. Iniciar sesión")
        println("2. Registrarse (Crear cuenta)")
        println("3. Salir del programa")
        print("Ingrese una opción: ")

        opcionInicio = scanner.nextInt()

        when (opcionInicio) {
            1 -> {
                val usuarioActual = LoginView.mostrarPantalla(scanner, usuarioController)

                if (usuarioActual != null) {
                    Menu.ejecutarMenu(scanner, usuarioActual)
                }
            }
            2 -> {

                RegistroView.mostrarPantalla(scanner,usuarioController);

            }
            3 -> println("Apagando el sistema VetSync. ¡Hasta pronto!")
            else -> println("Opción inválida. Intente de nuevo.")
        }
    } while (opcionInicio != 3)

}