/**
 * SeccionMaterialesDashboard - Componente de sección de materiales en el dashboard
 * Muestro materiales agrupados por actividad con checklist interactivo.
 * Permito marcar/desmarcar materiales como preparados y añadir nuevos materiales a cada actividad.
 */
package com.antjrobles.kidsplanner.modulos.material

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antjrobles.kidsplanner.modulos.actividad.Actividad
import com.antjrobles.kidsplanner.tema.KidsPlannerColors

@Composable
fun SeccionMaterialesDashboard(
    // Recibo las actividades para mostrar sus nombres
    actividades: List<Actividad>,
    // Recibo los materiales ya cargados
    materiales: List<Material>,
    // Callback cuando se marca/desmarca un material como preparado
    onCambiarEstado: (Material, Boolean) -> Unit,
    // Callback cuando se quiere añadir un material a una actividad
    onAñadirMaterial: (Actividad) -> Unit,
    // Callback cuando se quiere eliminar un material
    onEliminarMaterial: (Material) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Materiales necesarios",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = KidsPlannerColors.TextPrimary
        )

        // Agrupo materiales por actividad
        // Para cada actividad, muestro su nombre y los materiales asociados
        actividades.forEach { actividad ->
            // Filtro los materiales que pertenecen a esta actividad
            val materialesActividad = materiales.filter { it.actividadId == actividad.id }

            // Solo muestro la card si tiene materiales o para permitir añadir el primero
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = KidsPlannerColors.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Cabecera: nombre de la actividad + botón añadir
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = actividad.nombre,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = KidsPlannerColors.Primary
                        )

                        // Botón para añadir material a esta actividad
                        IconButton(
                            onClick = { onAñadirMaterial(actividad) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Añadir material",
                                tint = KidsPlannerColors.Primary
                            )
                        }
                    }

                    // Lista de materiales de esta actividad
                    if (materialesActividad.isEmpty()) {
                        Text(
                            text = "Sin materiales",
                            fontSize = 14.sp,
                            color = KidsPlannerColors.TextSecondary
                        )
                    } else {
                        materialesActividad.forEach { material ->
                            // Fila con checkbox + nombre + icono eliminar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (material.preparado) KidsPlannerColors.Primary.copy(alpha = 0.1f)
                                        else KidsPlannerColors.White
                                    )
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Checkbox para marcar como preparado
                                    Checkbox(
                                        checked = material.preparado,
                                        onCheckedChange = { nuevoEstado ->
                                            onCambiarEstado(material, nuevoEstado)
                                        }
                                    )

                                    // Nombre del material con tachado si está preparado
                                    Column {
                                        Text(
                                            text = material.nombre,
                                            fontSize = 14.sp,
                                            fontWeight = if (material.preparado) FontWeight.Normal else FontWeight.Medium,
                                            textDecoration = if (material.preparado) TextDecoration.LineThrough else TextDecoration.None,
                                            color = if (material.preparado) KidsPlannerColors.TextSecondary
                                            else KidsPlannerColors.TextPrimary
                                        )

                                        // Muestro cantidad si es mayor que 1
                                        if (material.cantidad != null && material.cantidad > 1) {
                                            Text(
                                                text = "Cantidad: ${material.cantidad}",
                                                fontSize = 12.sp,
                                                color = KidsPlannerColors.TextSecondary
                                            )
                                        }
                                    }
                                }

                                // Icono para eliminar el material
                                IconButton(
                                    onClick = { onEliminarMaterial(material) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar material",
                                        tint = KidsPlannerColors.Error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Mensaje si no hay actividades creadas
        if (actividades.isEmpty()) {
            Text(
                text = "Crea actividades primero para añadir materiales",
                fontSize = 14.sp,
                color = KidsPlannerColors.TextSecondary
            )
        }
    }
}
