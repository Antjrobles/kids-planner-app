/**
 * DialogoCrearHijo - Diálogo modal para crear/editar hijos
 * Muestro formulario con 4 campos (nombre obligatorio + fecha, curso y alergias opcionales).
 * Reutilizable en modo crear y modo editar según los datos que reciba.
 */
package com.antjrobles.kidsplanner.modulos.hijo

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

/**
 * Muestro un diálogo para crear un hijo dentro de una familia.
 */
@Composable
fun DialogoCrearHijo(
    nombreActual: String,
    alCambiarNombre: (String) -> Unit,
    fechaNacimientoActual: String,
    alCambiarFechaNacimiento: (String) -> Unit,
    cursoEscolarActual: String,
    alCambiarCursoEscolar: (String) -> Unit,
    alergiasActuales: String,
    alCambiarAlergias: (String) -> Unit,
    mensajeError: String?,
    estaGuardando: Boolean,
    alGuardar: () -> Unit,
    alCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = alCancelar,
        confirmButton = {
            PrimaryButton(
                text = "Guardar hijo",
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
                    text = "\uD83D\uDC67",
                    fontSize = 48.sp
                )
                Text(
                    text = "Añadir hijo",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = KidsPlannerColors.TextPrimary
                )
                Text(
                    text = "Introduce el nombre del hijo para gestionar sus actividades y recordatorios.",
                    fontSize = 14.sp,
                    color = KidsPlannerColors.TextSecondary,
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

                OutlinedTextField(
                    value = nombreActual,
                    onValueChange = alCambiarNombre,
                    label = { Text("Nombre del hijo *") },
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

                OutlinedTextField(
                    value = fechaNacimientoActual,
                    onValueChange = alCambiarFechaNacimiento,
                    label = { Text("Fecha de nacimiento (AAAA-MM-DD)") },
                    placeholder = { Text("2015-03-20") },
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

                OutlinedTextField(
                    value = cursoEscolarActual,
                    onValueChange = alCambiarCursoEscolar,
                    label = { Text("Curso escolar") },
                    placeholder = { Text("3º Primaria") },
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

                OutlinedTextField(
                    value = alergiasActuales,
                    onValueChange = alCambiarAlergias,
                    label = { Text("Alergias") },
                    placeholder = { Text("Polen, frutos secos...") },
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
