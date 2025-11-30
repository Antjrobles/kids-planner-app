/**
 * DialogoCrearMaterial - Diálogo modal para crear materiales
 * Muestro formulario simple con nombre (obligatorio) y cantidad opcional.
 * Reutilizable para diferentes actividades.
 */
package com.antjrobles.kidsplanner.modulos.material

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import com.antjrobles.kidsplanner.componentes.PrimaryButton
import com.antjrobles.kidsplanner.componentes.SecondaryButton
import com.antjrobles.kidsplanner.tema.KidsPlannerColors

@Composable
fun DialogoCrearMaterial(
    nombreActividad: String,  // Para mostrar "Añadir material para [Actividad]"
    nombreActual: String,
    alCambiarNombre: (String) -> Unit,
    cantidadActual: String,
    alCambiarCantidad: (String) -> Unit,
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
                    text = "📦",
                    fontSize = 48.sp
                )
                Text(
                    text = "Añadir material",
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

                // Campo nombre del material (obligatorio)
                OutlinedTextField(
                    value = nombreActual,
                    onValueChange = alCambiarNombre,
                    label = { Text("Nombre del material *") },
                    placeholder = { Text("Ej: Botas de fútbol, Partitura...") },
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

                // Campo cantidad (opcional, por defecto 1)
                OutlinedTextField(
                    value = cantidadActual,
                    onValueChange = alCambiarCantidad,
                    label = { Text("Cantidad") },
                    placeholder = { Text("1") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
