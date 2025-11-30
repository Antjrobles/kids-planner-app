/**
 * DialogoCrearPago - Diálogo modal para crear pagos
 * Muestro formulario con monto (obligatorio), fecha de vencimiento (obligatoria) y notas opcionales.
 * Reutilizable para diferentes actividades.
 */
package com.antjrobles.kidsplanner.modulos.pago

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antjrobles.kidsplanner.componentes.PrimaryButton
import com.antjrobles.kidsplanner.componentes.SecondaryButton
import com.antjrobles.kidsplanner.tema.KidsPlannerColors

@Composable
fun DialogoCrearPago(
    nombreActividad: String,  // Para mostrar "Añadir pago para [Actividad]"
    montoActual: String,
    alCambiarMonto: (String) -> Unit,
    fechaVencimientoActual: String,
    alCambiarFechaVencimiento: (String) -> Unit,
    notasActual: String,
    alCambiarNotas: (String) -> Unit,
    mensajeError: String?,
    estaGuardando: Boolean,
    alGuardar: () -> Unit,
    alCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = alCancelar,
        confirmButton = {
            PrimaryButton(
                text = "Añadir",
                onClick = alGuardar,
                modifier = Modifier.fillMaxWidth()
            )
        },
        dismissButton = {
            SecondaryButton(
                text = "Cancelar",
                onClick = alCancelar,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "💳",
                    fontSize = 48.sp
                )
                Text(
                    text = "Añadir pago",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = KidsPlannerColors.TextPrimary
                )
                Text(
                    text = "Para: $nombreActividad",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = KidsPlannerColors.Primary,
                    textAlign = TextAlign.Center
                )

                if (!mensajeError.isNullOrBlank()) {
                    Text(
                        text = mensajeError,
                        color = Color(0xFFD32F2F),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Campo monto (obligatorio)
                OutlinedTextField(
                    value = if (montoActual.isEmpty()) "" else "$montoActual €",
                    onValueChange = { newValue ->
                        // Quito el símbolo € y espacios para guardar solo el número
                        val valorLimpio = newValue.replace("€", "").trim()
                        alCambiarMonto(valorLimpio)
                    },
                    label = { Text("Cantidad en Euros *") },
                    placeholder = { Text("Ej: 45.50") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = KidsPlannerColors.Primary,
                        unfocusedBorderColor = KidsPlannerColors.TextSecondary,
                        focusedLabelColor = KidsPlannerColors.Primary,
                        unfocusedLabelColor = KidsPlannerColors.TextSecondary
                    )
                )

                // Campo fecha de vencimiento (obligatorio, formato DD-MM-YYYY)
                OutlinedTextField(
                    value = fechaVencimientoActual,
                    onValueChange = alCambiarFechaVencimiento,
                    label = { Text("Fecha vencimiento *") },
                    placeholder = { Text("DD-MM-AAAA (Ej: 15-12-2025)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = KidsPlannerColors.Primary,
                        unfocusedBorderColor = KidsPlannerColors.TextSecondary,
                        focusedLabelColor = KidsPlannerColors.Primary,
                        unfocusedLabelColor = KidsPlannerColors.TextSecondary
                    )
                )

                // Campo notas (opcional)
                OutlinedTextField(
                    value = notasActual,
                    onValueChange = alCambiarNotas,
                    label = { Text("Notas") },
                    placeholder = { Text("Mensualidad noviembre...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = KidsPlannerColors.Primary,
                        unfocusedBorderColor = KidsPlannerColors.TextSecondary,
                        focusedLabelColor = KidsPlannerColors.Primary,
                        unfocusedLabelColor = KidsPlannerColors.TextSecondary
                    )
                )

                if (estaGuardando) {
                    CircularProgressIndicator(color = KidsPlannerColors.Primary)
                }
            }
        }
    )
}
