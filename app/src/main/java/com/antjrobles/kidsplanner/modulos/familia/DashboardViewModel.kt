/**
 * DashboardViewModel - Gestor de lógica del Dashboard
 * Gestiono la familia seleccionada y orquestación general del dashboard.
 * Las operaciones específicas de hijos, actividades, materiales y pagos están en sus ViewModels especializados.
 * Uso variables separadas (familias, cargando, error) para simplificar el código según PMDM.
 */
package com.antjrobles.kidsplanner.modulos.familia

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * DashboardViewModel - Gestor de estado y lógica de familias en el dashboard
 * Centralizo aquí la lógica de familias y coordinación general.
 */
class DashboardViewModel : ViewModel() {

    // === REPOSITORIO ===

    private val familiaRepository = FamiliaRepository()

    // === ESTADOS DE DATOS ===

    // Lista de familias del usuario
    var familias by mutableStateOf<List<Familia>>(emptyList())
        private set

    // Indica si estoy cargando datos
    var cargando by mutableStateOf(false)
        private set

    // Mensaje de error si algo falla
    var error by mutableStateOf<String?>(null)
        private set

    // Familia seleccionada actualmente (normalmente la primera del usuario)
    var familiaSeleccionadaId by mutableIntStateOf(0)
        private set

    // === FUNCIONES DE CARGA DE DATOS ===

    /**
     * cargarFamilias - Cargo las familias del usuario desde Supabase
     * Llamada automática cuando se carga el dashboard por primera vez.
     */
    fun cargarFamilias(token: String, userId: String) {
        viewModelScope.launch {
            cargando = true
            error = null
            familiaSeleccionadaId = 0

            try {
                // Consulto familias usando el repository
                val resultado = familiaRepository.consultarFamilias(token, userId)
                familias = resultado
                error = null

                // Si hay familias, selecciono automáticamente la primera
                if (resultado.isNotEmpty()) {
                    familiaSeleccionadaId = resultado.first().id
                }
            } catch (e: Exception) {
                error = "Error al cargar familias"
                familias = emptyList()
            } finally {
                cargando = false
            }
        }
    }

    // === FUNCIONES AUXILIARES ===

    /**
     * obtenerFamiliaActual - Devuelvo la familia actualmente seleccionada
     * Útil para mostrar el nombre de la familia en la UI.
     */
    fun obtenerFamiliaActual(): Familia? {
        return familias.firstOrNull { it.id == familiaSeleccionadaId }
    }
}
