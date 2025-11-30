/**
 * SeccionHijosDashboard - Componente de sección de hijos en el dashboard
 * Muestro lista de hijos con cards interactivas. Cada card tiene icono de engranaje que abre
 * menú con opciones: Editar y Eliminar. Gestiono estados de carga y errores.
 */
package com.antjrobles.kidsplanner.modulos.hijo

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
import com.antjrobles.kidsplanner.componentes.PrimaryButton
import com.antjrobles.kidsplanner.tema.KidsPlannerColors

@Composable
fun SeccionHijosDashboard(
    hijos: List<Hijo>,
    cargando: Boolean,
    error: String?,
    onCrearHijo: () -> Unit,
    onEditarHijo: (Hijo) -> Unit = {},
    onEliminarHijo: (Hijo) -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Hijos",
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
        // Muestro lista de hijos o mensaje si está vacía
        else {
            if (hijos.isEmpty()) {
                Text("No hay hijos", color = KidsPlannerColors.TextSecondary)
            } else {
                hijos.forEach { hijo ->
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
                                        onEditarHijo(hijo)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Eliminar") },
                                    onClick = {
                                        menuExpandido = false
                                        onEliminarHijo(hijo)
                                    }
                                )
                            }

                            Text(
                                text = hijo.nombre,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = KidsPlannerColors.TextPrimary,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            PrimaryButton(text = "Añadir hijo", onClick = onCrearHijo)
        }
    }
}
