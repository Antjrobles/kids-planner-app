/**
 * SeccionActividadesDashboard - Componente de sección de actividades en el dashboard
 * Muestro lista de actividades con cards que incluyen título, nombre del hijo, día de la semana y horarios.
 * Gestiono estados de carga, vacío y errores con mensajes apropiados.
 */
package com.antjrobles.kidsplanner.modulos.actividad

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antjrobles.kidsplanner.modulos.hijo.Hijo
import com.antjrobles.kidsplanner.componentes.PrimaryButton
import com.antjrobles.kidsplanner.tema.KidsPlannerColors

@Composable
fun SeccionActividadesDashboard(
    actividades: List<Actividad>,
    cargando: Boolean,
    error: String?,
    // Recibo la lista de hijos para mostrar el nombre del hijo en cada actividad
    hijosDisponibles: List<Hijo>,
    onCrearActividad: () -> Unit,
    onEditarActividad: (Actividad) -> Unit = {},
    onEliminarActividad: (Actividad) -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Actividades",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = KidsPlannerColors.TextPrimary
        )

        // Muestro estado de carga
        if (cargando) {
            Text("Cargando...", color = KidsPlannerColors.TextSecondary)
        }
        // Muestro error si lo hay
        else if (error != null) {
            Text("Error al cargar: $error", color = KidsPlannerColors.Error)
        }
        // Muestro lista de actividades
        else {
            if (actividades.isEmpty()) {
                Text("No hay actividades", color = KidsPlannerColors.TextSecondary)
            } else {
                actividades.forEach { actividad ->
                    var menuExpandido by remember { mutableStateOf(false) }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = KidsPlannerColors.White)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { menuExpandido = true },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Opciones",
                                    tint = KidsPlannerColors.Primary
                                )
                            }

                            DropdownMenu(
                                expanded = menuExpandido,
                                onDismissRequest = { menuExpandido = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Editar") },
                                    onClick = {
                                        menuExpandido = false
                                        onEditarActividad(actividad)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Eliminar") },
                                    onClick = {
                                        menuExpandido = false
                                        onEliminarActividad(actividad)
                                    }
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Muestro el nombre de la actividad
                                Text(
                                    text = actividad.nombre,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = KidsPlannerColors.TextPrimary,
                                    textAlign = TextAlign.Center
                                )

                                // Busco el nombre del hijo asociado a esta actividad
                                val nombreHijo = actividad.hijoId?.let { hijoId ->
                                    hijosDisponibles.find { it.id == hijoId }?.nombre
                                } ?: "Sin asignar"

                                // Muestro el nombre del hijo que realiza esta actividad
                                Text(
                                    text = nombreHijo,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = KidsPlannerColors.Primary,
                                    textAlign = TextAlign.Center
                                )

                                // Muestro el día y la hora
                                Text(
                                    text = "${actividad.diaSemana?.ifBlank { "Sin dia" } ?: "Sin dia"} · ${actividad.horaInicio?.ifBlank { "--:--" } ?: "--:--"}",
                                    fontSize = 14.sp,
                                    color = KidsPlannerColors.TextSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
            PrimaryButton(text = "Anadir actividad", onClick = onCrearActividad)
        }
    }
}
