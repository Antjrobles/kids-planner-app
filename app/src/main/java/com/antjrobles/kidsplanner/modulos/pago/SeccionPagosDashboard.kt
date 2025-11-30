/**
 * SeccionPagosDashboard - Componente de sección de pagos en el dashboard
 * Muestro pagos agrupados por actividad con indicadores visuales de estado.
 * Permito marcar como pagado, añadir nuevos pagos y eliminar pagos existentes.
 */
package com.antjrobles.kidsplanner.modulos.pago

import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antjrobles.kidsplanner.modulos.actividad.Actividad
import com.antjrobles.kidsplanner.tema.KidsPlannerColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun SeccionPagosDashboard(
    // Recibo las actividades para mostrar sus nombres
    actividades: List<Actividad>,
    // Recibo los pagos ya cargados
    pagos: List<Pago>,
    // Callback cuando se quiere marcar un pago como pagado
    onMarcarComoPagado: (Pago) -> Unit,
    // Callback cuando se quiere añadir un pago a una actividad
    onAñadirPago: (Actividad) -> Unit,
    // Callback cuando se quiere eliminar un pago
    onEliminarPago: (Pago) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "💳 Pagos",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = KidsPlannerColors.TextPrimary
        )

        // Agrupo pagos por actividad
        // Para cada actividad, muestro su nombre y los pagos asociados
        actividades.forEach { actividad ->
            // Filtro los pagos que pertenecen a esta actividad
            val pagosActividad = pagos.filter { it.actividadId == actividad.id }

            // Solo muestro la card si tiene pagos o para permitir añadir el primero
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

                        // Botón para añadir pago a esta actividad
                        IconButton(
                            onClick = { onAñadirPago(actividad) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Añadir pago",
                                tint = KidsPlannerColors.Primary
                            )
                        }
                    }

                    // Lista de pagos de esta actividad
                    if (pagosActividad.isEmpty()) {
                        Text(
                            text = "Sin pagos registrados",
                            fontSize = 14.sp,
                            color = KidsPlannerColors.TextSecondary
                        )
                    } else {
                        pagosActividad.forEach { pago ->
                            // Determino el color de fondo según el estado
                            val colorFondo = when {
                                pago.estado == "paid" -> Color(0xFF4CAF50).copy(alpha = 0.15f)  // Verde claro
                                pago.estado == "overdue" -> Color(0xFFF44336).copy(alpha = 0.15f)  // Rojo claro
                                else -> Color(0xFFFFEB3B).copy(alpha = 0.15f)  // Amarillo claro (pending)
                            }

                            // Card de pago con color según estado
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(colorFondo)
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    // Monto
                                    Text(
                                        text = "${pago.monto}€",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        textDecoration = if (pago.estado == "paid") TextDecoration.LineThrough else TextDecoration.None,
                                        color = KidsPlannerColors.TextPrimary
                                    )

                                    // Fecha de vencimiento
                                    Text(
                                        text = "Vence: ${formatearFecha(pago.fechaVencimiento)}",
                                        fontSize = 13.sp,
                                        color = KidsPlannerColors.TextSecondary
                                    )

                                    // Si está pagado, muestro fecha de pago y método
                                    if (pago.estado == "paid" && pago.fechaPago != null) {
                                        Text(
                                            text = "Pagado: ${formatearFecha(pago.fechaPago)}",
                                            fontSize = 12.sp,
                                            color = Color(0xFF4CAF50),
                                            fontWeight = FontWeight.Medium
                                        )
                                        if (pago.metodoPago != null) {
                                            Text(
                                                text = "Método: ${pago.metodoPago}",
                                                fontSize = 12.sp,
                                                color = KidsPlannerColors.TextSecondary
                                            )
                                        }
                                    }

                                    // Notas si existen
                                    if (!pago.notas.isNullOrBlank()) {
                                        Text(
                                            text = pago.notas,
                                            fontSize = 12.sp,
                                            color = KidsPlannerColors.TextSecondary,
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                        )
                                    }
                                }

                                // Botones de acción
                                Column(
                                    horizontalAlignment = Alignment.End,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    // Botón "Marcar como Pagado" solo si NO está pagado
                                    if (pago.estado != "paid") {
                                        Button(
                                            onClick = { onMarcarComoPagado(pago) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = KidsPlannerColors.Primary
                                            ),
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        ) {
                                            Text(
                                                text = "✓ Pagar",
                                                fontSize = 12.sp,
                                                color = Color.White
                                            )
                                        }
                                    }

                                    // Icono para eliminar el pago
                                    IconButton(
                                        onClick = { onEliminarPago(pago) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Eliminar pago",
                                            tint = KidsPlannerColors.Error
                                        )
                                    }
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
                text = "Crea actividades primero para añadir pagos",
                fontSize = 14.sp,
                color = KidsPlannerColors.TextSecondary
            )
        }
    }
}

/**
 * Formateo una fecha de "YYYY-MM-DD" a "dd/MM/yyyy"
 */
private fun formatearFecha(fecha: String): String {
    return try {
        val localDate = LocalDate.parse(fecha, DateTimeFormatter.ISO_DATE)
        localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    } catch (e: Exception) {
        fecha  // Si hay error, devuelvo la fecha original
    }
}
