/**
 * PagoViewModel - Gestor de lógica de Pagos
 * Gestiono todos los datos y operaciones relacionadas con pagos: cargar, crear, marcar como pagado y eliminar.
 * Uso variables separadas (pagos, cargando, error) para simplificar el código según PMDM.
 */
package com.antjrobles.kidsplanner.modulos.pago

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antjrobles.kidsplanner.modulos.actividad.Actividad
import kotlinx.coroutines.launch

/**
 * PagoViewModel - ViewModel especializado para gestión de pagos
 */
class PagoViewModel : ViewModel() {

    // === REPOSITORIO ===

    private val pagoRepository = PagoRepository()

    // === ESTADOS DE DATOS ===

    // Lista de pagos cargados
    var pagos by mutableStateOf<List<Pago>>(emptyList())
        private set

    // Indica si estoy cargando datos
    var cargando by mutableStateOf(false)
        private set

    // Mensaje de error si algo falla
    var error by mutableStateOf<String?>(null)
        private set

    // Contador para forzar recargas cuando creo/edito/elimino pagos
    var contadorRecargaPagos by mutableIntStateOf(0)
        private set

    // === FUNCIONES DE CARGA DE DATOS ===

    /**
     * cargarPagos - Cargo todos los pagos de todas las actividades
     */
    fun cargarPagos(token: String, actividades: List<Actividad>) {
        viewModelScope.launch {
            if (actividades.isEmpty()) {
                pagos = emptyList()
                cargando = false
                error = null
                return@launch
            }

            cargando = true
            error = null

            try {
                // Cargo pagos de cada actividad y los junto en una sola lista
                val todosLosPagos = mutableListOf<Pago>()
                actividades.forEach { actividad ->
                    val resultado = pagoRepository.consultarPagos(token, actividad.id)
                    todosLosPagos.addAll(resultado)
                }

                pagos = todosLosPagos
                error = null
            } catch (e: Exception) {
                error = "Error al cargar pagos"
                pagos = emptyList()
            } finally {
                cargando = false
            }
        }
    }

    // === FUNCIONES DE OPERACIONES CON PAGOS ===

    /**
     * crearPago - Creo un nuevo pago para una actividad
     * Devuelvo true si se crea exitosamente, false si hay error.
     */
    suspend fun crearPago(
        token: String,
        familiaId: Int,
        actividadId: Int,
        monto: Double,
        fechaVencimiento: String,
        notas: String?
    ): Boolean {
        val exito = pagoRepository.crearPago(
            tokenAcceso = token,
            familiaId = familiaId,
            actividadId = actividadId,
            monto = monto,
            fechaVencimiento = fechaVencimiento,
            notas = notas
        )

        // Si se creó correctamente, incremento contador para recargar pagos
        if (exito) {
            contadorRecargaPagos++
        }

        return exito
    }

    /**
     * marcarComoPagado - Marco un pago como pagado con fecha y método
     * Devuelvo true si se actualiza exitosamente, false si hay error.
     */
    suspend fun marcarComoPagado(
        token: String,
        pagoId: Int,
        fechaPago: String,
        metodoPago: String
    ): Boolean {
        val exito = pagoRepository.marcarComoPagado(
            tokenAcceso = token,
            pagoId = pagoId,
            fechaPago = fechaPago,
            metodoPago = metodoPago
        )

        // Si se actualizó correctamente, incremento contador para recargar pagos
        if (exito) {
            contadorRecargaPagos++
        }

        return exito
    }

    /**
     * eliminarPago - Elimino un pago de la base de datos
     * Devuelvo true si se elimina exitosamente, false si hay error.
     */
    suspend fun eliminarPago(token: String, pagoId: Int): Boolean {
        val exito = pagoRepository.eliminarPago(token, pagoId)

        // Si se eliminó correctamente, incremento contador para recargar pagos
        if (exito) {
            contadorRecargaPagos++
        }

        return exito
    }
}
