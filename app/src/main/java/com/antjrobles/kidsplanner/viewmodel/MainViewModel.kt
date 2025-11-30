/**
 * MainViewModel - ViewModel principal (Arquitectura MVVM)
 * Gestiono todo el estado de la aplicación: navegación entre pantallas, datos de sesión
 * (token, userId, correo) y lógica de creación de familias. Sobrevivo a rotaciones de pantalla.
 */
package com.antjrobles.kidsplanner.viewmodel

// getValue: función de Compose para leer valores de variables reactivas (las que usan "by")
import androidx.compose.runtime.getValue

// mutableStateOf: función de Compose para crear variables que cuando cambian redibujan la interfaz automáticamente
import androidx.compose.runtime.mutableStateOf

// setValue: función de Compose para escribir valores en variables reactivas (las que usan "by")
import androidx.compose.runtime.setValue

// ViewModel: clase base de Android para crear ViewModels (clases que sobreviven a rotaciones de pantalla)
import androidx.lifecycle.ViewModel

// viewModelScope: ámbito especial para ejecutar tareas en segundo plano dentro de un ViewModel
import androidx.lifecycle.viewModelScope

import com.antjrobles.kidsplanner.modulos.familia.FamiliaCreada
import com.antjrobles.kidsplanner.modulos.familia.FamiliaRepository
import com.antjrobles.kidsplanner.modulos.familia.RegistrarUsuario
import com.antjrobles.kidsplanner.modulos.familia.ResultadoRegistrarUsuario

import android.util.Log

// delay: función de Kotlin para pausar la ejecución durante un tiempo (como setTimeout en JavaScript)
import kotlinx.coroutines.delay

// launch: función de Kotlin para ejecutar código en segundo plano sin bloquear la interfaz
import kotlinx.coroutines.launch

/**
 * MainViewModel - Gestor principal de la aplicación
 * Hereda de ViewModel (clase de Android) para sobrevivir a rotaciones de pantalla.
 * Guardo aquí todos los datos que necesita la app: qué pantalla mostrar, datos del usuario, etc.
 */
class MainViewModel : ViewModel() {

    // === ESTADOS DE LA APLICACIÓN ===

    // estaCargando: controla si el splash screen está visible o no
    // Uso "by mutableStateOf" para que cuando cambie este valor, la interfaz se redibuje automáticamente
    // "private set" significa que solo yo puedo cambiar este valor desde dentro de MainViewModel
    var estaCargando by mutableStateOf(true)
        private set

    // pantallaActual: guarda qué pantalla mostrar ("inicio", "login", "dashboard" o "crearFamilia")
    // Cuando cambio este valor, MainActivity lee el cambio y muestra otra pantalla
    var pantallaActual by mutableStateOf("inicio")
        private set

    // tokenSesion: guardo el token de autenticación que devuelve Supabase cuando haces login
    // Lo necesito para hacer peticiones a la base de datos en nombre del usuario
    var tokenSesion by mutableStateOf("")
        private set

    // userIdSesion: guardo el ID único del usuario en la base de datos
    var userIdSesion by mutableStateOf("")
        private set

    // correoSesion: guardo el email del usuario que hizo login
    var correoSesion by mutableStateOf("")
        private set

    // nombreFamiliaNueva: guardo el texto que el usuario escribe en el formulario de crear familia
    // Cuando el usuario escribe "Los Pérez", este valor va actualizándose letra a letra
    var nombreFamiliaNueva by mutableStateOf("")
        private set

    // estaGuardandoFamilia: controla si estoy guardando una familia en la base de datos
    // Mientras está en true, muestro un indicador de carga en la pantalla
    var estaGuardandoFamilia by mutableStateOf(false)
        private set

    // contadorRecargaFamilias: cada vez que creo o elimino una familia, sumo 1 a este contador
    // Cuando cambia, PantallaDashboard detecta el cambio y recarga la lista de familias
    // Es un truco simple para forzar recargas sin usar sistemas más complejos
    var contadorRecargaFamilias by mutableStateOf(0)
        private set

    // Repositorios para operaciones de datos
    private val registrarUsuario = RegistrarUsuario()
    private val familiaRepository = FamiliaRepository()

    // === INICIALIZACIÓN ===

    // init: bloque que se ejecuta automáticamente cuando se crea el MainViewModel
    // Es como el constructor de la clase
    init {
        inicializarSplashScreen()
    }

    /**
     * inicializarSplashScreen - Mantiene el splash visible durante 3 segundos
     *
     * Uso viewModelScope.launch para ejecutar esto en segundo plano sin bloquear la interfaz.
     * delay(3000) pausa durante 3000 milisegundos (3 segundos).
     * Después cambio estaCargando a false, lo que hace que MainActivity oculte el splash.
     */
    private fun inicializarSplashScreen() {
        viewModelScope.launch {
            delay(3000)
            estaCargando = false
        }
    }

    // === FUNCIONES DE NAVEGACIÓN ===
    // Estas funciones solo cambian el valor de pantallaActual.
    // MainActivity detecta el cambio y muestra otra pantalla automáticamente.

    /**
     * volverAInicio - Cambia pantallaActual a "inicio"
     * MainActivity detecta el cambio y muestra PantallaInicio
     */
    fun volverAInicio() {
        pantallaActual = "inicio"
    }

    /**
     * navegarALogin - Cambia pantallaActual a "login"
     * MainActivity detecta el cambio y muestra PantallaLogin
     */
    fun navegarALogin() {
        pantallaActual = "login"
    }

    /**
     * navegarARegistro - Cambia pantallaActual a "registro"
     * MainActivity detecta el cambio y muestra PantallaRegistro
     */
    fun navegarARegistro() {
        pantallaActual = "registro"
    }

    /**
     * navegarADashboard - Cambia pantallaActual a "dashboard"
     * MainActivity detecta el cambio y muestra PantallaDashboard
     */
    fun navegarADashboard() {
        pantallaActual = "dashboard"
    }

    /**
     * navegarACrearFamilia - Cambia pantallaActual a "crearFamilia"
     * MainActivity detecta el cambio y muestra PantallaCrearFamilia
     */
    fun navegarACrearFamilia() {
        pantallaActual = "crearFamilia"
    }

    // === FUNCIONES DE SESIÓN ===

    /**
     * guardarSesion - Guarda los datos del usuario cuando hace login exitoso
     * Recibo token, userId y correo desde PantallaLogin y los guardo aquí.
     * Los necesito guardados para hacer peticiones autenticadas a Supabase.
     */
    fun guardarSesion(token: String, userId: String, correo: String) {
        tokenSesion = token
        userIdSesion = userId
        correoSesion = correo
    }

    /**
     * cerrarSesion - Borra todos los datos del usuario y vuelve a la pantalla de inicio
     * Vacío todas las variables de sesión y cambio pantallaActual a "inicio"
     */
    fun cerrarSesion() {
        tokenSesion = ""
        userIdSesion = ""
        correoSesion = ""
        volverAInicio()
    }

    // === FUNCIONES DE GESTIÓN DE FAMILIAS ===

    /**
     * actualizarNombreFamilia - Actualiza nombreFamiliaNueva mientras el usuario escribe
     * PantallaCrearFamilia llama a esta función cada vez que el usuario escribe una letra.
     * Simplemente guardo el nuevo valor en nombreFamiliaNueva.
     */
    fun actualizarNombreFamilia(nombre: String) {
        nombreFamiliaNueva = nombre
    }

    /**
     * crearFamilia - Crea una nueva familia en Supabase
     *
     * 1. Valido que el nombre no esté vacío (trim elimina espacios, isBlank verifica si está vacío)
     * 2. Cambio estaGuardandoFamilia a true para mostrar indicador de carga en la pantalla
     * 3. Uso viewModelScope.launch para hacer la petición HTTP en segundo plano
     * 4. Primero registro al usuario (si no existe ya) y luego creo la familia
     * 5. Recibo el resultado (Exito o Error)
     * 6. Si es éxito: vacío el nombre, sumo 1 al contador y navego al dashboard
     * 7. Si es error: no hago nada (debería mostrar mensaje pero lo eliminé por simplicidad)
     */
    fun crearFamilia() {
        // Valido que el nombre no esté vacío
        // trim() elimina espacios al principio y al final
        // isBlank() devuelve true si está vacío o solo tiene espacios
        val nombre = nombreFamiliaNueva.trim()
        if (nombre.isBlank()) {
            // Si está vacío, salgo de la función sin crear nada
            return
        }

        // Cambio estaGuardandoFamilia a true para mostrar el indicador de carga en PantallaCrearFamilia
        estaGuardandoFamilia = true

        // viewModelScope.launch ejecuta el código en segundo plano sin bloquear la interfaz
        viewModelScope.launch {
            Log.d("MainViewModel", "Iniciando creación de familia: $nombre")
            Log.d("MainViewModel", "Token: ${tokenSesion.take(20)}... UserId: $userIdSesion")

            // Primero me aseguro de que el usuario existe en public.users
            val registro = registrarUsuario.asegurar(tokenSesion, userIdSesion, correoSesion)
            Log.d("MainViewModel", "Resultado registro: $registro")

            val familiaCreada = if (registro is ResultadoRegistrarUsuario.Error) {
                // Si falló el registro del usuario, no creo la familia
                Log.e("MainViewModel", "Error en registro usuario: ${registro.mensaje}")
                null
            } else {
                // Si el usuario existe, creo la familia
                Log.d("MainViewModel", "Usuario registrado, creando familia...")
                familiaRepository.crearFamilia(tokenSesion, nombre, userIdSesion)
            }

            // Oculto el indicador de carga
            estaGuardandoFamilia = false

            // Miro si se creó la familia correctamente
            if (familiaCreada != null) {
                // Si creé la familia correctamente:
                Log.d("MainViewModel", "Familia creada exitosamente!")
                nombreFamiliaNueva = ""  // Vacío el campo de texto
                contadorRecargaFamilias++  // Sumo 1 para que PantallaDashboard recargue la lista
                navegarADashboard()  // Vuelvo al dashboard
            } else {
                // Si hubo un error, lo muestro en consola para poder debuggear
                Log.e("MainViewModel", "ERROR al crear familia")
                println("ERROR al crear familia")
            }
        }
    }

    /**
     * cancelarCrearFamilia - Cancela la creación de familia y vuelve al dashboard
     * Vacío el nombre, cambio estaGuardando a false y navego al dashboard
     */
    fun cancelarCrearFamilia() {
        nombreFamiliaNueva = ""
        estaGuardandoFamilia = false
        navegarADashboard()
    }
}
