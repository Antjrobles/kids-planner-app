/**
 * HijoViewModel - Gestor de lógica de Hijos
 * Gestiono todos los datos y operaciones relacionadas con hijos: cargar, crear, actualizar y eliminar.
 * Uso variables separadas (hijos, cargando, error) para simplificar el código según PMDM.
 */
package com.antjrobles.kidsplanner.modulos.hijo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * HijoViewModel - ViewModel especializado para gestión de hijos
 */
class HijoViewModel : ViewModel() {

    // === REPOSITORIO ===

    private val hijoRepository = HijoRepository()

    // === ESTADOS DE DATOS ===

    // Lista de hijos cargados
    var hijos by mutableStateOf<List<Hijo>>(emptyList())
        private set

    // Indica si estoy cargando datos
    var cargando by mutableStateOf(false)
        private set

    // Mensaje de error si algo falla
    var error by mutableStateOf<String?>(null)
        private set

    // Contador para forzar recargas cuando creo/edito/elimino hijos
    var contadorRecargaHijos by mutableIntStateOf(0)
        private set

    // === FUNCIONES DE CARGA DE DATOS ===

    /**
     * cargarHijos - Cargo los hijos de una familia específica
     */
    fun cargarHijos(token: String, familiaId: Int) {
        if (familiaId == 0) return

        viewModelScope.launch {
            cargando = true
            error = null

            try {
                val resultado = hijoRepository.consultarHijos(token, familiaId)
                hijos = resultado
                error = null
            } catch (e: Exception) {
                error = "Error al cargar hijos"
                hijos = emptyList()
            } finally {
                cargando = false
            }
        }
    }

    // === FUNCIONES DE OPERACIONES CON HIJOS ===

    /**
     * crearHijo - Creo un nuevo hijo en la base de datos
     * Devuelvo true si fue exitoso, false si hubo error.
     */
    suspend fun crearHijo(
        token: String,
        familiaId: Int,
        nombre: String,
        fechaNacimiento: String?,
        cursoEscolar: String?,
        alergias: String?
    ): Boolean {
        val hijoCreado = hijoRepository.crearHijo(
            tokenAcceso = token,
            familiaId = familiaId,
            nombre = nombre,
            fechaNacimiento = fechaNacimiento,
            cursoEscolar = cursoEscolar,
            alergias = alergias
        )

        // Si se creó correctamente, incremento contador para recargar lista
        return if (hijoCreado != null) {
            contadorRecargaHijos++
            true
        } else {
            false
        }
    }

    /**
     * actualizarHijo - Actualizo un hijo existente
     * Devuelvo true si fue exitoso, false si hubo error.
     */
    suspend fun actualizarHijo(
        token: String,
        hijoId: Int,
        nombre: String,
        fechaNacimiento: String?,
        cursoEscolar: String?,
        alergias: String?
    ): Boolean {
        val exitoso = hijoRepository.actualizarHijo(
            tokenAcceso = token,
            hijoId = hijoId,
            nombre = nombre,
            fechaNacimiento = fechaNacimiento,
            cursoEscolar = cursoEscolar,
            alergias = alergias
        )

        // Si se actualizó correctamente, incremento contador para recargar lista
        if (exitoso) {
            contadorRecargaHijos++
        }

        return exitoso
    }

    /**
     * eliminarHijo - Elimino un hijo de la base de datos
     * Devuelvo true si fue exitoso, false si hubo error.
     */
    suspend fun eliminarHijo(token: String, hijoId: Int): Boolean {
        val exitoso = hijoRepository.eliminarHijo(token, hijoId)

        // Si se eliminó correctamente, incremento contador para recargar lista
        if (exitoso) {
            contadorRecargaHijos++
        }

        return exitoso
    }

    // === FUNCIONES AUXILIARES ===

    /**
     * obtenerListaHijos - Devuelvo la lista de hijos cargada
     */
    fun obtenerListaHijos(): List<Hijo> {
        return hijos
    }
}
