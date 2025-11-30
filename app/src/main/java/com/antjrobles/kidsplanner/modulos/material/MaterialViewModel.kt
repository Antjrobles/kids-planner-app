/**
 * MaterialViewModel - Gestor de lógica de Materiales
 * Gestiono todos los datos y operaciones relacionadas con materiales: cargar, crear, actualizar estado y eliminar.
 * Uso variables separadas (materiales, cargando, error) para simplificar el código según PMDM.
 */
package com.antjrobles.kidsplanner.modulos.material

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antjrobles.kidsplanner.modulos.actividad.Actividad
import kotlinx.coroutines.launch

/**
 * MaterialViewModel - ViewModel especializado para gestión de materiales
 */
class MaterialViewModel : ViewModel() {

    // === REPOSITORIO ===

    private val materialRepository = MaterialRepository()

    // === ESTADOS DE DATOS ===

    // Lista de materiales cargados
    var materiales by mutableStateOf<List<Material>>(emptyList())
        private set

    // Indica si estoy cargando datos
    var cargando by mutableStateOf(false)
        private set

    // Mensaje de error si algo falla
    var error by mutableStateOf<String?>(null)
        private set

    // Contador para forzar recargas cuando creo/edito/elimino materiales
    var contadorRecargaMateriales by mutableIntStateOf(0)
        private set

    // === FUNCIONES DE CARGA DE DATOS ===

    /**
     * cargarMateriales - Cargo todos los materiales de todas las actividades
     */
    fun cargarMateriales(token: String, actividades: List<Actividad>) {
        viewModelScope.launch {
            if (actividades.isEmpty()) {
                materiales = emptyList()
                cargando = false
                error = null
                return@launch
            }

            cargando = true
            error = null

            try {
                // Cargo materiales de cada actividad y los junto en una sola lista
                val todosLosMateriales = mutableListOf<Material>()
                actividades.forEach { actividad ->
                    val resultado = materialRepository.consultarMateriales(token, actividad.id)
                    todosLosMateriales.addAll(resultado)
                }

                materiales = todosLosMateriales
                error = null
            } catch (e: Exception) {
                error = "Error al cargar materiales"
                materiales = emptyList()
            } finally {
                cargando = false
            }
        }
    }

    // === FUNCIONES DE OPERACIONES CON MATERIALES ===

    /**
     * crearMaterial - Creo un nuevo material para una actividad
     * Devuelvo el material creado si tiene éxito, null si hay error.
     */
    suspend fun crearMaterial(
        token: String,
        actividadId: Int,
        nombre: String,
        cantidad: Int
    ): Material? {
        val materialCreado = materialRepository.crearMaterial(
            tokenAcceso = token,
            actividadId = actividadId,
            nombre = nombre,
            cantidad = cantidad
        )

        // Si se creó correctamente, incremento contador para recargar materiales
        if (materialCreado != null) {
            contadorRecargaMateriales++
        }

        return materialCreado
    }

    /**
     * actualizarEstadoMaterial - Cambio el estado de preparado de un material
     * Devuelvo true si se actualizó exitosamente, false si hay error.
     */
    suspend fun actualizarEstadoMaterial(
        token: String,
        materialId: Int,
        preparado: Boolean
    ): Boolean {
        val exito = materialRepository.actualizarEstado(
            tokenAcceso = token,
            materialId = materialId,
            preparado = preparado
        )

        // Si se actualizó correctamente, incremento contador para recargar materiales
        if (exito) {
            contadorRecargaMateriales++
        }

        return exito
    }

    /**
     * eliminarMaterial - Elimino un material de la base de datos
     * Devuelvo true si se eliminó exitosamente, false si hay error.
     */
    suspend fun eliminarMaterial(token: String, materialId: Int): Boolean {
        val exito = materialRepository.eliminarMaterial(token, materialId)

        // Si se eliminó correctamente, incremento contador para recargar materiales
        if (exito) {
            contadorRecargaMateriales++
        }

        return exito
    }
}
