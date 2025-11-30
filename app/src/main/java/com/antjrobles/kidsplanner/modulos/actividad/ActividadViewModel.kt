/**
 * ActividadViewModel - Gestor de lógica de Actividades
 * Gestiono todos los datos y operaciones relacionadas con actividades: cargar, crear, actualizar y eliminar.
 * Uso variables separadas (actividades, cargando, error) para simplificar el código según PMDM.
 */
package com.antjrobles.kidsplanner.modulos.actividad

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * ActividadViewModel - ViewModel especializado para gestión de actividades
 */
class ActividadViewModel : ViewModel() {

    // === REPOSITORIO ===

    private val actividadRepository = ActividadRepository()

    // === ESTADOS DE DATOS ===

    // Lista de actividades cargadas
    var actividades by mutableStateOf<List<Actividad>>(emptyList())
        private set

    // Indica si estoy cargando datos
    var cargando by mutableStateOf(false)
        private set

    // Mensaje de error si algo falla
    var error by mutableStateOf<String?>(null)
        private set

    // Contador para forzar recargas cuando creo/edito/elimino actividades
    var contadorRecargaActividades by mutableIntStateOf(0)
        private set

    // === FUNCIONES DE CARGA DE DATOS ===

    /**
     * cargarActividades - Cargo las actividades de una familia específica
     */
    fun cargarActividades(token: String, familiaId: Int) {
        if (familiaId == 0) return

        viewModelScope.launch {
            cargando = true
            error = null

            try {
                val resultado = actividadRepository.consultarActividades(token, familiaId)
                actividades = resultado
                error = null
            } catch (e: Exception) {
                error = "Error al cargar actividades"
                actividades = emptyList()
            } finally {
                cargando = false
            }
        }
    }

    // === FUNCIONES DE OPERACIONES CON ACTIVIDADES ===

    /**
     * crearActividad - Creo una nueva actividad en la base de datos
     * Devuelvo la actividad creada si tiene éxito, null si hay error.
     */
    suspend fun crearActividad(
        token: String,
        familiaId: Int,
        hijoId: Int,
        nombre: String,
        descripcion: String?,
        lugar: String?,
        diaSemana: String?,
        horaInicio: String?,
        horaFin: String?,
        nombreProfesor: String?
    ): Actividad? {
        val actividadCreada = actividadRepository.crearActividad(
            tokenAcceso = token,
            familiaId = familiaId,
            hijoId = hijoId,
            nombre = nombre,
            descripcion = descripcion,
            lugar = lugar,
            diaSemana = diaSemana,
            horaInicio = horaInicio,
            horaFin = horaFin,
            nombreProfesor = nombreProfesor
        )

        // Si se creó correctamente, incremento contador para recargar actividades
        if (actividadCreada != null) {
            contadorRecargaActividades++
        }

        return actividadCreada
    }

    /**
     * actualizarActividad - Actualizo una actividad existente
     * Devuelvo true si se actualizó exitosamente, false si hay error.
     */
    suspend fun actualizarActividad(
        token: String,
        actividadId: Int,
        hijoId: Int,
        nombre: String,
        descripcion: String?,
        lugar: String?,
        diaSemana: String?,
        horaInicio: String?,
        horaFin: String?,
        nombreProfesor: String?
    ): Boolean {
        val exito = actividadRepository.actualizarActividad(
            tokenAcceso = token,
            actividadId = actividadId,
            hijoId = hijoId,
            nombre = nombre,
            descripcion = descripcion,
            lugar = lugar,
            diaSemana = diaSemana,
            horaInicio = horaInicio,
            horaFin = horaFin,
            nombreProfesor = nombreProfesor
        )

        // Si se actualizó correctamente, incremento contador para recargar actividades
        if (exito) {
            contadorRecargaActividades++
        }

        return exito
    }

    /**
     * eliminarActividad - Elimino una actividad de la base de datos
     * Devuelvo true si se eliminó exitosamente, false si hay error.
     */
    suspend fun eliminarActividad(token: String, actividadId: Int): Boolean {
        val exito = actividadRepository.eliminarActividad(token, actividadId)

        // Si se eliminó correctamente, incremento contador para recargar actividades
        if (exito) {
            contadorRecargaActividades++
        }

        return exito
    }
}
