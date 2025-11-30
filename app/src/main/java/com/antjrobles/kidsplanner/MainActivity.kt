/**
 * MainActivity - Actividad principal de la aplicación
 * Punto de entrada de la app. Gestiono el splash screen inicial y renderizo la UI completa
 * según la pantalla actual (Inicio, Login, Dashboard, CrearFamilia) usando Jetpack Compose.
 */
package com.antjrobles.kidsplanner

// Bundle: paquete que guarda el estado de la Activity cuando se recrea (ej: rotar pantalla)
import android.os.Bundle


// ComponentActivity: clase base de Android para Activities que usan Jetpack Compose
import androidx.activity.ComponentActivity

// setContent: función que permite definir la interfaz de usuario usando Compose en lugar de XML
import androidx.activity.compose.setContent

// enableEdgeToEdge: activa el modo pantalla completa (edge-to-edge) sin barras del sistema opacas
import androidx.activity.enableEdgeToEdge

// viewModels: delega para crear y obtener el ViewModel asociado a esta Activity
import androidx.activity.viewModels

// installSplashScreen: API moderna de Android 12+ para mostrar splash screen mientras carga la app
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

// KidsPlannerTheme: tema personalizado con colores, tipografías y estilos Material 3 de la app
import com.antjrobles.kidsplanner.tema.KidsPlannerTheme

// PantallaInicio: pantalla de bienvenida (landing page) con logo y botón para ir al login
import com.antjrobles.kidsplanner.pantallas.PantallaInicio

// PantallaLogin: pantalla de autenticación con campos de correo y contraseña
import com.antjrobles.kidsplanner.pantallas.PantallaLogin

// PantallaRegistro: pantalla para crear nueva cuenta de usuario
import com.antjrobles.kidsplanner.pantallas.PantallaRegistro

// PantallaDashboard: pantalla principal que muestra familias e hijos del usuario autenticado
import com.antjrobles.kidsplanner.modulos.familia.PantallaDashboard

// PantallaCrearFamilia: pantalla con formulario para crear una nueva familia
import com.antjrobles.kidsplanner.modulos.familia.PantallaCrearFamilia

// MainViewModel: gestiona el estado global de la app (token, navegación, datos de sesión)
import com.antjrobles.kidsplanner.viewmodel.MainViewModel

/**
 * MainActivity - Pantalla principal de la aplicación
 * Aquí se  decide qué pantalla mostrar según el estado actual.
 * Y uso ComponentActivity que es la clase de Android que me permite usar Jetpack Compose para dibujar la interfaz.
 */
class MainActivity : ComponentActivity() {

    /** Gestor principal de la app: guarda datos (token, email), controla qué pantalla mostrar, y maneja el estado general
     Lo llamo gestorPrincipal en lugar de viewModel porque los nombres descriptivos en español me ayudan a entender el código
     */
    private val gestorPrincipal: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Instalo splash screen que se mantiene visible mientras estaCargando sea true
        installSplashScreen().apply {
            setKeepOnScreenCondition { gestorPrincipal.estaCargando }
        }

        // Llamada al metodo onCreate de la superclase ComponentActivity para inicializar la Activity
        super.onCreate(savedInstanceState)

        // Configuro la UI de la aplicación (pantalla completa sin bordes)
        enableEdgeToEdge()

        // Aquí defino toda la interfaz visual de la app usando Compose
        setContent {
            // Aplico el tema de colores y estilos de Kids Planner
            KidsPlannerTheme {
                // Miro qué pantalla mostrar según la variable pantallaActual del gestorPrincipal
                if (gestorPrincipal.pantallaActual == "inicio") {
                    // Muestro la pantalla de bienvenida con el logo
                    PantallaInicio(
                        alHacerClicLogin = { gestorPrincipal.navegarALogin() },
                        alHacerClicRegistro = { gestorPrincipal.navegarARegistro() }
                    )
                } else if (gestorPrincipal.pantallaActual == "login") {
                    // Muestro la pantalla de login con correo y contraseña
                    PantallaLogin(
                        alVolverAtras = { gestorPrincipal.volverAInicio() },
                        loginCorrecto = { token, userId, correo ->
                            gestorPrincipal.guardarSesion(token, userId, correo)
                            gestorPrincipal.navegarADashboard()
                        }
                    )
                } else if (gestorPrincipal.pantallaActual == "registro") {
                    // Muestro la pantalla de registro para crear nueva cuenta
                    PantallaRegistro(
                        alVolverAtras = { gestorPrincipal.volverAInicio() },
                        registroCorrecto = { token, userId, correo ->
                            gestorPrincipal.guardarSesion(token, userId, correo)
                            gestorPrincipal.navegarADashboard()
                        }
                    )
                } else if (gestorPrincipal.pantallaActual == "dashboard") {
                    // Muestro la pantalla principal con familias e hijos
                    PantallaDashboard(
                        token = gestorPrincipal.tokenSesion,
                        userId = gestorPrincipal.userIdSesion,
                        contadorRecarga = gestorPrincipal.contadorRecargaFamilias,
                        alCerrarSesion = { gestorPrincipal.cerrarSesion() },
                        alCrearFamilia = { gestorPrincipal.navegarACrearFamilia() }
                    )
                } else if (gestorPrincipal.pantallaActual == "crearFamilia") {
                    // Muestro el formulario para crear una nueva familia
                    PantallaCrearFamilia(
                        estaGuardando = gestorPrincipal.estaGuardandoFamilia,
                        nombreFamilia = gestorPrincipal.nombreFamiliaNueva,
                        alCambiarNombre = { nombre ->
                            gestorPrincipal.actualizarNombreFamilia(nombre)
                        },
                        alGuardar = { gestorPrincipal.crearFamilia() },
                        alCancelar = { gestorPrincipal.cancelarCrearFamilia() }
                    )
                }
            }
        }
    }
}
