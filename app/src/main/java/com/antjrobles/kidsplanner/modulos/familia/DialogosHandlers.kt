/**
 * DialogosHandlers - Composables wrapper para diálogos del dashboard
 * Encapsulo la lógica de manejo de estados y validaciones de cada diálogo.
 * Esto mantiene PantallaDashboard.kt limpia y fácil de leer.
 * Uso try/catch simple como enseña el temario de PMDM.
 */
package com.antjrobles.kidsplanner.modulos.familia

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.antjrobles.kidsplanner.modulos.actividad.Actividad
import com.antjrobles.kidsplanner.modulos.actividad.ActividadViewModel
import com.antjrobles.kidsplanner.modulos.actividad.DialogoCrearActividad
import com.antjrobles.kidsplanner.modulos.actividad.DialogoEliminarActividad
import com.antjrobles.kidsplanner.modulos.hijo.Hijo
import com.antjrobles.kidsplanner.modulos.hijo.HijoViewModel
import com.antjrobles.kidsplanner.modulos.hijo.DialogoCrearHijo
import com.antjrobles.kidsplanner.modulos.hijo.DialogoEliminarHijo
import com.antjrobles.kidsplanner.modulos.material.DialogoCrearMaterial
import com.antjrobles.kidsplanner.modulos.material.MaterialViewModel
import com.antjrobles.kidsplanner.modulos.pago.Pago
import com.antjrobles.kidsplanner.modulos.pago.PagoViewModel
import com.antjrobles.kidsplanner.modulos.pago.DialogoCrearPago
import com.antjrobles.kidsplanner.modulos.pago.DialogoMarcarComoPagado
import kotlinx.coroutines.launch

/**
 * DialogoHijoHandler - Manejo completo del diálogo de crear/editar hijo
 * Gestiono internamente todos los estados del formulario y la lógica de guardado.
 */
@Composable
fun DialogoHijoHandler(
    dashboardViewModel: DashboardViewModel,
    hijoViewModel: HijoViewModel,
    token: String,
    hijoAEditar: Hijo?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Estados del formulario
    var nombre by remember { mutableStateOf(hijoAEditar?.nombre ?: "") }
    var fechaNacimiento by remember { mutableStateOf(hijoAEditar?.fechaNacimiento ?: "") }
    var cursoEscolar by remember { mutableStateOf(hijoAEditar?.cursoEscolar ?: "") }
    var alergias by remember { mutableStateOf(hijoAEditar?.alergias ?: "") }
    var error by remember { mutableStateOf<String?>(null) }
    var guardando by remember { mutableStateOf(false) }

    DialogoCrearHijo(
        nombreActual = nombre,
        alCambiarNombre = { nombre = it; error = null },
        fechaNacimientoActual = fechaNacimiento,
        alCambiarFechaNacimiento = { fechaNacimiento = it },
        cursoEscolarActual = cursoEscolar,
        alCambiarCursoEscolar = { cursoEscolar = it },
        alergiasActuales = alergias,
        alCambiarAlergias = { alergias = it },
        mensajeError = error,
        estaGuardando = guardando,
        alGuardar = {
            if (guardando) return@DialogoCrearHijo

            val nombreTrim = nombre.trim()
            if (nombreTrim.isEmpty()) {
                error = "Introduce un nombre válido"
                return@DialogoCrearHijo
            }

            guardando = true
            scope.launch {
                val exito = if (hijoAEditar == null) {
                    val hijo = hijoViewModel.crearHijo(
                        token = token,
                        familiaId = dashboardViewModel.familiaSeleccionadaId,
                        nombre = nombreTrim,
                        fechaNacimiento = fechaNacimiento.trim().takeIf { it.isNotEmpty() },
                        cursoEscolar = cursoEscolar.trim().takeIf { it.isNotEmpty() },
                        alergias = alergias.trim().takeIf { it.isNotEmpty() }
                    )
                    hijo != null
                } else {
                    hijoViewModel.actualizarHijo(
                        token = token,
                        hijoId = hijoAEditar.id,
                        nombre = nombreTrim,
                        fechaNacimiento = fechaNacimiento.trim().takeIf { it.isNotEmpty() },
                        cursoEscolar = cursoEscolar.trim().takeIf { it.isNotEmpty() },
                        alergias = alergias.trim().takeIf { it.isNotEmpty() }
                    )
                }

                guardando = false
                if (exito) {
                    Toast.makeText(context, "Hijo guardado", Toast.LENGTH_SHORT).show()
                    onDismiss()
                } else {
                    error = "Error al guardar hijo"
                }
            }
        },
        alCancelar = onDismiss
    )
}

/**
 * DialogoEliminarHijoHandler - Manejo del diálogo de eliminar hijo
 */
@Composable
fun DialogoEliminarHijoHandler(
    hijoViewModel: HijoViewModel,
    token: String,
    hijo: Hijo,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var eliminando by remember { mutableStateOf(false) }

    DialogoEliminarHijo(
        hijo = hijo,
        estaEliminando = eliminando,
        alConfirmar = {
            if (eliminando) return@DialogoEliminarHijo
            eliminando = true
            scope.launch {
                val exito = hijoViewModel.eliminarHijo(token, hijo.id)
                eliminando = false
                if (exito) {
                    Toast.makeText(context, "Hijo eliminado", Toast.LENGTH_SHORT).show()
                    onDismiss()
                } else {
                    Toast.makeText(context, "Error al eliminar hijo", Toast.LENGTH_LONG).show()
                    onDismiss()
                }
            }
        },
        alCancelar = onDismiss
    )
}

/**
 * DialogoActividadHandler - Manejo completo del diálogo de crear/editar actividad
 */
@Composable
fun DialogoActividadHandler(
    dashboardViewModel: DashboardViewModel,
    hijoViewModel: HijoViewModel,
    actividadViewModel: ActividadViewModel,
    token: String,
    actividadAEditar: Actividad?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Estados del formulario
    var hijoSeleccionado by remember { mutableStateOf(actividadAEditar?.hijoId) }
    var nombre by remember { mutableStateOf(actividadAEditar?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(actividadAEditar?.descripcion ?: "") }
    var lugar by remember { mutableStateOf(actividadAEditar?.lugar ?: "") }
    var diaSemana by remember { mutableStateOf(actividadAEditar?.diaSemana ?: "") }
    var horaInicio by remember { mutableStateOf(actividadAEditar?.horaInicio ?: "") }
    var horaFin by remember { mutableStateOf(actividadAEditar?.horaFin ?: "") }
    var nombreProfesor by remember { mutableStateOf(actividadAEditar?.nombreProfesor ?: "") }
    var error by remember { mutableStateOf<String?>(null) }
    var guardando by remember { mutableStateOf(false) }

    DialogoCrearActividad(
        hijosDisponibles = hijoViewModel.hijos,
        hijoSeleccionadoId = hijoSeleccionado,
        alSeleccionarHijo = { hijoSeleccionado = it },
        nombreActual = nombre,
        alCambiarNombre = { nombre = it; error = null },
        descripcionActual = descripcion,
        alCambiarDescripcion = { descripcion = it },
        lugarActual = lugar,
        alCambiarLugar = { lugar = it },
        diaSemanaActual = diaSemana,
        alCambiarDiaSemana = { diaSemana = it },
        horaInicioActual = horaInicio,
        alCambiarHoraInicio = { horaInicio = it },
        horaFinActual = horaFin,
        alCambiarHoraFin = { horaFin = it },
        nombreProfesorActual = nombreProfesor,
        alCambiarNombreProfesor = { nombreProfesor = it },
        mensajeError = error,
        estaGuardando = guardando,
        alGuardar = {
            if (guardando) return@DialogoCrearActividad

            val nombreTrim = nombre.trim()
            if (nombreTrim.isEmpty()) {
                error = "Introduce un nombre válido"
                return@DialogoCrearActividad
            }

            val hijo = hijoSeleccionado
            if (hijo == null) {
                error = "Selecciona un hijo"
                return@DialogoCrearActividad
            }

            guardando = true
            scope.launch {
                val exito = if (actividadAEditar == null) {
                    val actividad = actividadViewModel.crearActividad(
                        token = token,
                        familiaId = dashboardViewModel.familiaSeleccionadaId,
                        hijoId = hijo,
                        nombre = nombreTrim,
                        descripcion = descripcion.trim().takeIf { it.isNotEmpty() },
                        lugar = lugar.trim().takeIf { it.isNotEmpty() },
                        diaSemana = diaSemana.trim().takeIf { it.isNotEmpty() },
                        horaInicio = horaInicio.trim().takeIf { it.isNotEmpty() },
                        horaFin = horaFin.trim().takeIf { it.isNotEmpty() },
                        nombreProfesor = nombreProfesor.trim().takeIf { it.isNotEmpty() }
                    )
                    actividad != null
                } else {
                    actividadViewModel.actualizarActividad(
                        token = token,
                        actividadId = actividadAEditar.id,
                        hijoId = hijo,
                        nombre = nombreTrim,
                        descripcion = descripcion.trim().takeIf { it.isNotEmpty() },
                        lugar = lugar.trim().takeIf { it.isNotEmpty() },
                        diaSemana = diaSemana.trim().takeIf { it.isNotEmpty() },
                        horaInicio = horaInicio.trim().takeIf { it.isNotEmpty() },
                        horaFin = horaFin.trim().takeIf { it.isNotEmpty() },
                        nombreProfesor = nombreProfesor.trim().takeIf { it.isNotEmpty() }
                    )
                }

                guardando = false
                if (exito) {
                    Toast.makeText(context, "Actividad guardada", Toast.LENGTH_SHORT).show()
                    onDismiss()
                } else {
                    error = "Error al guardar actividad"
                }
            }
        },
        alCancelar = onDismiss
    )
}

/**
 * DialogoEliminarActividadHandler - Manejo del diálogo de eliminar actividad
 */
@Composable
fun DialogoEliminarActividadHandler(
    actividadViewModel: ActividadViewModel,
    token: String,
    actividad: Actividad,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var eliminando by remember { mutableStateOf(false) }

    DialogoEliminarActividad(
        actividad = actividad,
        estaEliminando = eliminando,
        alConfirmar = {
            if (eliminando) return@DialogoEliminarActividad
            eliminando = true
            scope.launch {
                val exito = actividadViewModel.eliminarActividad(token, actividad.id)
                eliminando = false
                if (exito) {
                    Toast.makeText(context, "Actividad eliminada", Toast.LENGTH_SHORT).show()
                    onDismiss()
                } else {
                    Toast.makeText(context, "Error al eliminar actividad", Toast.LENGTH_LONG).show()
                    onDismiss()
                }
            }
        },
        alCancelar = onDismiss
    )
}

/**
 * DialogoMaterialHandler - Manejo del diálogo de crear material
 */
@Composable
fun DialogoMaterialHandler(
    materialViewModel: MaterialViewModel,
    token: String,
    actividad: Actividad,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("1") }
    var error by remember { mutableStateOf<String?>(null) }
    var guardando by remember { mutableStateOf(false) }

    DialogoCrearMaterial(
        nombreActividad = actividad.nombre,
        nombreActual = nombre,
        alCambiarNombre = { nombre = it; error = null },
        cantidadActual = cantidad,
        alCambiarCantidad = { cantidad = it },
        mensajeError = error,
        estaGuardando = guardando,
        alGuardar = {
            if (guardando) return@DialogoCrearMaterial

            val nombreTrim = nombre.trim()
            if (nombreTrim.isEmpty()) {
                error = "Introduce un nombre válido"
                return@DialogoCrearMaterial
            }

            val cantidadNum = cantidad.trim().toIntOrNull() ?: 1

            guardando = true
            scope.launch {
                val material = materialViewModel.crearMaterial(
                    token = token,
                    actividadId = actividad.id,
                    nombre = nombreTrim,
                    cantidad = cantidadNum
                )

                guardando = false
                if (material != null) {
                    Toast.makeText(context, "Material añadido", Toast.LENGTH_SHORT).show()
                    onDismiss()
                } else {
                    error = "Error al añadir material"
                }
            }
        },
        alCancelar = onDismiss
    )
}

/**
 * DialogoPagoHandler - Manejo del diálogo de crear pago
 */
@Composable
fun DialogoPagoHandler(
    dashboardViewModel: DashboardViewModel,
    pagoViewModel: PagoViewModel,
    token: String,
    actividad: Actividad,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var monto by remember { mutableStateOf("") }
    var fechaVencimiento by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var guardando by remember { mutableStateOf(false) }

    DialogoCrearPago(
        nombreActividad = actividad.nombre,
        montoActual = monto,
        alCambiarMonto = { monto = it; error = null },
        fechaVencimientoActual = fechaVencimiento,
        alCambiarFechaVencimiento = { fechaVencimiento = it; error = null },
        notasActual = notas,
        alCambiarNotas = { notas = it },
        mensajeError = error,
        estaGuardando = guardando,
        alGuardar = {
            if (guardando) return@DialogoCrearPago

            val montoTexto = monto.trim()
            val fechaVenc = fechaVencimiento.trim()

            if (montoTexto.isEmpty()) {
                error = "Introduce un monto válido"
                return@DialogoCrearPago
            }

            if (fechaVenc.isEmpty()) {
                error = "Introduce una fecha de vencimiento"
                return@DialogoCrearPago
            }

            val montoNum = montoTexto.toDoubleOrNull()
            if (montoNum == null || montoNum <= 0) {
                error = "El monto debe ser un número mayor que 0"
                return@DialogoCrearPago
            }

            // Convierto fecha de DD-MM-YYYY a YYYY-MM-DD
            val fechaConvertida = try {
                val partes = fechaVenc.split("-")
                if (partes.size != 3) {
                    error = "Formato de fecha inválido. Usa DD-MM-AAAA"
                    return@DialogoCrearPago
                }
                "${partes[2]}-${partes[1]}-${partes[0]}"
            } catch (e: Exception) {
                error = "Formato de fecha inválido"
                return@DialogoCrearPago
            }

            guardando = true
            scope.launch {
                val exito = pagoViewModel.crearPago(
                    token = token,
                    familiaId = dashboardViewModel.familiaSeleccionadaId,
                    actividadId = actividad.id,
                    monto = montoNum,
                    fechaVencimiento = fechaConvertida,
                    notas = notas.trim().takeIf { it.isNotEmpty() }
                )

                guardando = false
                if (exito) {
                    Toast.makeText(context, "Pago añadido", Toast.LENGTH_SHORT).show()
                    onDismiss()
                } else {
                    error = "Error al añadir pago"
                }
            }
        },
        alCancelar = onDismiss
    )
}

/**
 * DialogoMarcarPagadoHandler - Manejo del diálogo de marcar como pagado
 */
@Composable
fun DialogoMarcarPagadoHandler(
    pagoViewModel: PagoViewModel,
    token: String,
    pago: Pago,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var fechaPago by remember { mutableStateOf("") }
    var metodoPago by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var guardando by remember { mutableStateOf(false) }

    DialogoMarcarComoPagado(
        montoOriginal = pago.monto.toString(),
        fechaPagoActual = fechaPago,
        alCambiarFechaPago = { fechaPago = it; error = null },
        metodoPagoActual = metodoPago,
        alCambiarMetodoPago = { metodoPago = it; error = null },
        mensajeError = error,
        estaGuardando = guardando,
        alGuardar = {
            if (guardando) return@DialogoMarcarComoPagado

            val fecha = fechaPago.trim()
            val metodo = metodoPago.trim()

            if (fecha.isEmpty()) {
                error = "Introduce una fecha de pago"
                return@DialogoMarcarComoPagado
            }

            if (metodo.isEmpty()) {
                error = "Introduce un método de pago"
                return@DialogoMarcarComoPagado
            }

            // Convierto fecha de DD-MM-YYYY a YYYY-MM-DD
            val fechaConvertida = try {
                val partes = fecha.split("-")
                if (partes.size != 3) {
                    error = "Formato de fecha inválido. Usa DD-MM-AAAA"
                    return@DialogoMarcarComoPagado
                }
                "${partes[2]}-${partes[1]}-${partes[0]}"
            } catch (e: Exception) {
                error = "Formato de fecha inválido"
                return@DialogoMarcarComoPagado
            }

            guardando = true
            scope.launch {
                val exito = pagoViewModel.marcarComoPagado(
                    token = token,
                    pagoId = pago.id,
                    fechaPago = fechaConvertida,
                    metodoPago = metodo
                )

                guardando = false
                if (exito) {
                    Toast.makeText(context, "Pago marcado como pagado", Toast.LENGTH_SHORT).show()
                    onDismiss()
                } else {
                    error = "Error al marcar como pagado"
                }
            }
        },
        alCancelar = onDismiss
    )
}
