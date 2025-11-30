/**
 * DialogoMarcarComoPagado - Diálogo modal para marcar pago como pagado
 * Muestro formulario simple con fecha de pago y método de pago.
 * Usado cuando el usuario marca un pago pendiente como pagado.
 */
package com.antjrobles.kidsplanner.modulos.pago

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antjrobles.kidsplanner.componentes.PrimaryButton
import com.antjrobles.kidsplanner.componentes.SecondaryButton
import com.antjrobles.kidsplanner.tema.KidsPlannerColors

@Composable
fun DialogoMarcarComoPagado(
    montoOriginal: String,  // Para mostrar "Marcar pago de [monto]€ como pagado"
    fechaPagoActual: String,
    alCambiarFechaPago: (String) -> Unit,
    metodoPagoActual: String,
    alCambiarMetodoPago: (String) -> Unit,
    mensajeError: String?,
    estaGuardando: Boolean,
    alGuardar: () -> Unit,
    alCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = alCancelar,
        confirmButton = {
            PrimaryButton(
                text = "Confirmar pago",
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
                    text = "✅",
                    fontSize = 48.sp
                )
                Text(
                    text = "Marcar como pagado",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = KidsPlannerColors.TextPrimary
                )
                Text(
                    text = "Pago de $montoOriginal€",
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

                // Campo fecha de pago (obligatorio, formato DD-MM-YYYY)
                OutlinedTextField(
                    value = fechaPagoActual,
                    onValueChange = alCambiarFechaPago,
                    label = { Text("Fecha de pago *") },
                    placeholder = { Text("DD-MM-AAAA (Ej: 17-11-2025)") },
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

                // Campo método de pago (obligatorio)
                OutlinedTextField(
                    value = metodoPagoActual,
                    onValueChange = alCambiarMetodoPago,
                    label = { Text("Método de pago *") },
                    placeholder = { Text("Efectivo, Tarjeta, Transferencia...") },
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

                if (estaGuardando) {
                    CircularProgressIndicator(color = KidsPlannerColors.Primary)
                }
            }
        }
    )
}
