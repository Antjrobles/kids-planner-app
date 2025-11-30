/**
 * PantallaDashboard - Pantalla principal post-login
 * Muestro el dashboard completo con familias, hijos, actividades, materiales y pagos.
 * TODA la lógica está en DashboardViewModel y DialogosHandlers.
 * Uso variables separadas (datos, cargando, error) para simplificar el código según PMDM.
 */
package com.antjrobles.kidsplanner.modulos.familia

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.antjrobles.kidsplanner.modulos.actividad.Actividad
import com.antjrobles.kidsplanner.modulos.actividad.ActividadViewModel
import com.antjrobles.kidsplanner.modulos.actividad.SeccionActividadesDashboard
import com.antjrobles.kidsplanner.modulos.hijo.Hijo
import com.antjrobles.kidsplanner.modulos.hijo.HijoViewModel
import com.antjrobles.kidsplanner.modulos.hijo.SeccionHijosDashboard
import com.antjrobles.kidsplanner.modulos.material.Material
import com.antjrobles.kidsplanner.modulos.material.MaterialViewModel
import com.antjrobles.kidsplanner.modulos.material.SeccionMaterialesDashboard
import com.antjrobles.kidsplanner.modulos.pago.Pago
import com.antjrobles.kidsplanner.modulos.pago.PagoViewModel
import com.antjrobles.kidsplanner.modulos.pago.SeccionPagosDashboard
import com.antjrobles.kidsplanner.componentes.PrimaryButton
import com.antjrobles.kidsplanner.componentes.SecondaryButton
import com.antjrobles.kidsplanner.tema.KidsPlannerColors
import kotlinx.coroutines.launch

/**
 * PantallaDashboard - Pantalla principal de la aplicación
 * Muestro el dashboard después de que el usuario inicia sesión.
 * Delego toda la lógica a DashboardViewModel para mantener este archivo simple y claro.
 */
@Composable
fun PantallaDashboard(
    token: String,
    userId: String,
    contadorRecarga: Int,
    alCerrarSesion: () -> Unit = {},
    alCrearFamilia: () -> Unit = {},
    alCrearHijo: () -> Unit = {}
) {
    // Obtengo el contexto para mostrar mensajes Toast y el scope para ejecutar operaciones asíncronas
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Obtengo los ViewModels especializados
    val dashboardViewModel: DashboardViewModel = viewModel()
    val hijoViewModel: HijoViewModel = viewModel()
    val actividadViewModel: ActividadViewModel = viewModel()
    val materialViewModel: MaterialViewModel = viewModel()
    val pagoViewModel: PagoViewModel = viewModel()

    // === ESTADOS SOLO PARA MOSTRAR/OCULTAR DIÁLOGOS ===

    // Estados para diálogos de hijo
    var mostrarDialogoHijo by remember { mutableStateOf(false) }
    var hijoAEditar by remember { mutableStateOf<Hijo?>(null) }
    var hijoAEliminar by remember { mutableStateOf<Hijo?>(null) }

    // Estados para diálogos de actividad
    var mostrarDialogoActividad by remember { mutableStateOf(false) }
    var actividadAEditar by remember { mutableStateOf<Actividad?>(null) }
    var actividadAEliminar by remember { mutableStateOf<Actividad?>(null) }

    // Estados para diálogos de material
    var mostrarDialogoMaterial by remember { mutableStateOf(false) }
    var actividadParaMaterial by remember { mutableStateOf<Actividad?>(null) }

    // Estados para diálogos de pago
    var mostrarDialogoPago by remember { mutableStateOf(false) }
    var actividadParaPago by remember { mutableStateOf<Actividad?>(null) }
    var mostrarDialogoMarcarPagado by remember { mutableStateOf(false) }
    var pagoAMarcar by remember { mutableStateOf<Pago?>(null) }

    // === EFECTOS DE CARGA DE DATOS ===

    // Cargo las familias cuando se monta el componente o cuando cambia contadorRecarga
    LaunchedEffect(contadorRecarga) {
        dashboardViewModel.cargarFamilias(token, userId)
    }

    // Cargo los hijos cuando cambia la familia seleccionada o el contador de recarga
    LaunchedEffect(dashboardViewModel.familiaSeleccionadaId, hijoViewModel.contadorRecargaHijos) {
        if (dashboardViewModel.familiaSeleccionadaId != 0) {
            hijoViewModel.cargarHijos(token, dashboardViewModel.familiaSeleccionadaId)
        }
    }

    // Cargo las actividades cuando cambia la familia seleccionada o el contador de recarga
    LaunchedEffect(dashboardViewModel.familiaSeleccionadaId, actividadViewModel.contadorRecargaActividades) {
        if (dashboardViewModel.familiaSeleccionadaId != 0) {
            actividadViewModel.cargarActividades(token, dashboardViewModel.familiaSeleccionadaId)
        }
    }

    // Cargo los materiales cuando cambian las actividades o el contador de recarga
    LaunchedEffect(actividadViewModel.actividades, materialViewModel.contadorRecargaMateriales) {
        materialViewModel.cargarMateriales(token, actividadViewModel.actividades)
    }

    // Cargo los pagos cuando cambian las actividades o el contador de recarga
    LaunchedEffect(actividadViewModel.actividades, pagoViewModel.contadorRecargaPagos) {
        pagoViewModel.cargarPagos(token, actividadViewModel.actividades)
    }

    // === INTERFAZ DE USUARIO ===

    Surface(modifier = Modifier.fillMaxSize(), color = KidsPlannerColors.Background) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Muestro diferentes pantallas según el estado de carga de familias
                if (dashboardViewModel.cargando) {
                    // Si aún está cargando, muestro indicador
                    PantallaCargando()
                } else if (dashboardViewModel.error != null) {
                    // Si hubo un error al cargar familias
                    PantallaError(
                        mensaje = dashboardViewModel.error ?: "Error desconocido",
                        alCerrarSesion = alCerrarSesion
                    )
                } else if (dashboardViewModel.familias.isEmpty()) {
                    // Si cargó correctamente pero no hay familias
                    PantallaSinFamilias(
                        alCrearFamilia = alCrearFamilia,
                        alCerrarSesion = alCerrarSesion
                    )
                } else {
                    // Si hay familias, muestro el dashboard completo
                    ContenidoDashboard(
                        nombreFamilia = dashboardViewModel.familias.first().nombre,
                        dashboardViewModel = dashboardViewModel,
                        hijoViewModel = hijoViewModel,
                        actividadViewModel = actividadViewModel,
                        materialViewModel = materialViewModel,
                        pagoViewModel = pagoViewModel,
                        token = token,
                        alCerrarSesion = alCerrarSesion,
                        alCrearHijo = {
                            hijoAEditar = null
                            mostrarDialogoHijo = true
                        },
                        alEditarHijo = { hijo ->
                            hijoAEditar = hijo
                            mostrarDialogoHijo = true
                        },
                        alEliminarHijo = { hijo ->
                            hijoAEliminar = hijo
                        },
                        alCrearActividad = {
                            if (hijoViewModel.hijos.isEmpty()) {
                                Toast.makeText(context, "Crea un hijo primero", Toast.LENGTH_LONG).show()
                                return@ContenidoDashboard
                            }
                            actividadAEditar = null
                            mostrarDialogoActividad = true
                        },
                        alEditarActividad = { actividad ->
                            actividadAEditar = actividad
                            mostrarDialogoActividad = true
                        },
                        alEliminarActividad = { actividad ->
                            actividadAEliminar = actividad
                        },
                        alAñadirMaterial = { actividad ->
                            actividadParaMaterial = actividad
                            mostrarDialogoMaterial = true
                        },
                        alCambiarEstadoMaterial = { material, nuevoEstado ->
                            scope.launch {
                                val exito = materialViewModel.actualizarEstadoMaterial(token, material.id, nuevoEstado)
                                if (!exito) {
                                    Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        alEliminarMaterial = { material ->
                            scope.launch {
                                val exito = materialViewModel.eliminarMaterial(token, material.id)
                                if (exito) {
                                    Toast.makeText(context, "Material eliminado", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        alAñadirPago = { actividad ->
                            actividadParaPago = actividad
                            mostrarDialogoPago = true
                        },
                        alMarcarComoPagado = { pago ->
                            pagoAMarcar = pago
                            mostrarDialogoMarcarPagado = true
                        },
                        alEliminarPago = { pago ->
                            scope.launch {
                                val exito = pagoViewModel.eliminarPago(token, pago.id)
                                if (exito) {
                                    Toast.makeText(context, "Pago eliminado", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                }
            }

            // === DIÁLOGOS ===

            // Diálogo de crear/editar hijo
            if (mostrarDialogoHijo) {
                DialogoHijoHandler(
                    dashboardViewModel = dashboardViewModel,
                    hijoViewModel = hijoViewModel,
                    token = token,
                    hijoAEditar = hijoAEditar,
                    onDismiss = {
                        mostrarDialogoHijo = false
                        hijoAEditar = null
                    }
                )
            }

            // Diálogo de eliminar hijo
            hijoAEliminar?.let { hijo ->
                DialogoEliminarHijoHandler(
                    hijoViewModel = hijoViewModel,
                    token = token,
                    hijo = hijo,
                    onDismiss = { hijoAEliminar = null }
                )
            }

            // Diálogo de crear/editar actividad
            if (mostrarDialogoActividad) {
                DialogoActividadHandler(
                    dashboardViewModel = dashboardViewModel,
                    hijoViewModel = hijoViewModel,
                    actividadViewModel = actividadViewModel,
                    token = token,
                    actividadAEditar = actividadAEditar,
                    onDismiss = {
                        mostrarDialogoActividad = false
                        actividadAEditar = null
                    }
                )
            }

            // Diálogo de eliminar actividad
            actividadAEliminar?.let { actividad ->
                DialogoEliminarActividadHandler(
                    actividadViewModel = actividadViewModel,
                    token = token,
                    actividad = actividad,
                    onDismiss = { actividadAEliminar = null }
                )
            }

            // Diálogo de crear material
            actividadParaMaterial?.let { actividad ->
                DialogoMaterialHandler(
                    materialViewModel = materialViewModel,
                    token = token,
                    actividad = actividad,
                    onDismiss = {
                        mostrarDialogoMaterial = false
                        actividadParaMaterial = null
                    }
                )
            }

            // Diálogo de crear pago
            actividadParaPago?.let { actividad ->
                DialogoPagoHandler(
                    dashboardViewModel = dashboardViewModel,
                    pagoViewModel = pagoViewModel,
                    token = token,
                    actividad = actividad,
                    onDismiss = {
                        mostrarDialogoPago = false
                        actividadParaPago = null
                    }
                )
            }

            // Diálogo para marcar como pagado
            pagoAMarcar?.let { pago ->
                DialogoMarcarPagadoHandler(
                    pagoViewModel = pagoViewModel,
                    token = token,
                    pago = pago,
                    onDismiss = {
                        mostrarDialogoMarcarPagado = false
                        pagoAMarcar = null
                    }
                )
            }
        }
    }
}

// === COMPOSABLES AUXILIARES PARA ORGANIZAR LA UI ===

/**
 * PantallaCargando - Muestro un indicador cuando estoy cargando los datos
 */
@Composable
private fun PantallaCargando() {
    Text(
        text = "⏳",
        fontSize = 64.sp,
        modifier = Modifier.padding(bottom = 24.dp)
    )
    Text(
        text = "Cargando...",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = KidsPlannerColors.TextPrimary
    )
}

/**
 * PantallaSinFamilias - Muestro mensaje cuando el usuario no tiene familias creadas
 */
@Composable
private fun PantallaSinFamilias(
    alCrearFamilia: () -> Unit,
    alCerrarSesion: () -> Unit
) {
    Text(
        text = "🏡",
        fontSize = 64.sp,
        modifier = Modifier.padding(bottom = 24.dp)
    )
    Text(
        text = "Para empezar, crea tu primera familia",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = KidsPlannerColors.TextPrimary,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "Podrás gestionar actividades, materiales y pagos desde un solo lugar.",
        fontSize = 16.sp,
        color = KidsPlannerColors.TextSecondary,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(48.dp))
    PrimaryButton(
        text = "Crear mi familia",
        onClick = alCrearFamilia
    )
    Spacer(modifier = Modifier.height(12.dp))
    SecondaryButton(
        text = "Cerrar sesión",
        onClick = alCerrarSesion
    )
}

/**
 * PantallaError - Muestro mensaje cuando hubo un error al cargar
 */
@Composable
private fun PantallaError(
    mensaje: String,
    alCerrarSesion: () -> Unit
) {
    Text(
        text = "⚠️",
        fontSize = 64.sp,
        modifier = Modifier.padding(bottom = 24.dp)
    )
    Text(
        text = "Error al cargar datos",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = KidsPlannerColors.TextPrimary,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = mensaje,
        fontSize = 14.sp,
        color = KidsPlannerColors.TextSecondary,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(48.dp))
    SecondaryButton(
        text = "Cerrar sesión",
        onClick = alCerrarSesion
    )
}

/**
 * ContenidoDashboard - Muestro el contenido completo del dashboard con todas las secciones
 */
@Composable
private fun ContenidoDashboard(
    nombreFamilia: String,
    dashboardViewModel: DashboardViewModel,
    hijoViewModel: HijoViewModel,
    actividadViewModel: ActividadViewModel,
    materialViewModel: MaterialViewModel,
    pagoViewModel: PagoViewModel,
    token: String,
    alCerrarSesion: () -> Unit,
    alCrearHijo: () -> Unit,
    alEditarHijo: (Hijo) -> Unit,
    alEliminarHijo: (Hijo) -> Unit,
    alCrearActividad: () -> Unit,
    alEditarActividad: (Actividad) -> Unit,
    alEliminarActividad: (Actividad) -> Unit,
    alAñadirMaterial: (Actividad) -> Unit,
    alCambiarEstadoMaterial: (Material, Boolean) -> Unit,
    alEliminarMaterial: (Material) -> Unit,
    alAñadirPago: (Actividad) -> Unit,
    alMarcarComoPagado: (Pago) -> Unit,
    alEliminarPago: (Pago) -> Unit
) {
    Text(
        text = "👪👨‍👩‍👦",
        fontSize = 64.sp,
        modifier = Modifier.padding(bottom = 16.dp)
    )
    Text(
        text = "Familia: $nombreFamilia",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = KidsPlannerColors.TextPrimary,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(32.dp))

    // Sección de hijos
    SeccionHijosDashboard(
        hijos = hijoViewModel.hijos,
        cargando = hijoViewModel.cargando,
        error = hijoViewModel.error,
        onCrearHijo = alCrearHijo,
        onEditarHijo = alEditarHijo,
        onEliminarHijo = alEliminarHijo
    )
    Spacer(modifier = Modifier.height(32.dp))

    // Sección de actividades
    SeccionActividadesDashboard(
        actividades = actividadViewModel.actividades,
        cargando = actividadViewModel.cargando,
        error = actividadViewModel.error,
        hijosDisponibles = hijoViewModel.hijos,
        onCrearActividad = alCrearActividad,
        onEditarActividad = alEditarActividad,
        onEliminarActividad = alEliminarActividad
    )
    Spacer(modifier = Modifier.height(32.dp))

    // Sección de materiales
    SeccionMaterialesDashboard(
        actividades = actividadViewModel.actividades,
        materiales = materialViewModel.materiales,
        onCambiarEstado = alCambiarEstadoMaterial,
        onAñadirMaterial = alAñadirMaterial,
        onEliminarMaterial = alEliminarMaterial
    )
    Spacer(modifier = Modifier.height(32.dp))

    // Sección de pagos
    SeccionPagosDashboard(
        actividades = actividadViewModel.actividades,
        pagos = pagoViewModel.pagos,
        onMarcarComoPagado = alMarcarComoPagado,
        onAñadirPago = alAñadirPago,
        onEliminarPago = alEliminarPago
    )
    Spacer(modifier = Modifier.height(32.dp))

    SecondaryButton(
        text = "Cerrar sesión",
        onClick = alCerrarSesion
    )
}
