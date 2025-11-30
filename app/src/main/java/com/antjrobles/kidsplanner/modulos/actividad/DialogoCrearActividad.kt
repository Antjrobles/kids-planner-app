/**
 * DialogoCrearActividad - Diálogo modal para crear/editar actividades
 * Muestro formulario con 8 campos: selector de hijo (obligatorio) + nombre, descripción, lugar, día, horarios, profesor.
 * Reutilizable en modo crear y modo editar según los datos que reciba.
 */
package com.antjrobles.kidsplanner.modulos.actividad

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antjrobles.kidsplanner.modulos.hijo.Hijo
import com.antjrobles.kidsplanner.componentes.PrimaryButton
import com.antjrobles.kidsplanner.componentes.SecondaryButton
import com.antjrobles.kidsplanner.tema.KidsPlannerColors

@Composable
fun DialogoCrearActividad(
    // Recibo la lista de hijos disponibles para que el usuario elija cuál realiza esta actividad
    hijosDisponibles: List<Hijo>,
    // Recibo el ID del hijo actualmente seleccionado (puede ser null si aún no eligió)
    hijoSeleccionadoId: Int?,
    // Callback que se ejecuta cuando el usuario hace clic en un hijo diferente
    alSeleccionarHijo: (Int) -> Unit,
    // Los demás campos del formulario (igual que antes)
    nombreActual: String,
    alCambiarNombre: (String) -> Unit,
    descripcionActual: String,
    alCambiarDescripcion: (String) -> Unit,
    lugarActual: String,
    alCambiarLugar: (String) -> Unit,
    diaSemanaActual: String,
    alCambiarDiaSemana: (String) -> Unit,
    horaInicioActual: String,
    alCambiarHoraInicio: (String) -> Unit,
    horaFinActual: String,
    alCambiarHoraFin: (String) -> Unit,
    nombreProfesorActual: String,
    alCambiarNombreProfesor: (String) -> Unit,
    mensajeError: String?,
    estaGuardando: Boolean,
    alGuardar: () -> Unit,
    alCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = alCancelar,
        confirmButton = {
            PrimaryButton(
                text = "Guardar actividad",
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
                    .height(500.dp)
                    .padding(top = 8.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "⚽",
                    fontSize = 48.sp
                )
                Text(
                    text = "Añadir actividad",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = KidsPlannerColors.TextPrimary
                )
                Text(
                    text = "Introduce los datos de la actividad extraescolar.",
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

                // Selector de hijo: muestro todos los hijos disponibles como botones
                // El usuario hace clic en uno para seleccionarlo
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "¿Quién realiza esta actividad? *",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = KidsPlannerColors.TextPrimary
                    )

                    // Itero sobre cada hijo disponible y creo un botón clickeable para cada uno
                    hijosDisponibles.forEach { hijo ->
                        // Determino si este hijo está seleccionado comparando su ID con el ID seleccionado
                        val estaSeleccionado = hijo.id == hijoSeleccionadoId

                        // Creo una caja clickeable que representa al hijo
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                // Si está seleccionado, uso fondo azul. Si no, fondo blanco
                                .background(
                                    if (estaSeleccionado) KidsPlannerColors.Primary.copy(alpha = 0.1f)
                                    else Color.White
                                )
                                // Si está seleccionado, borde grueso azul. Si no, borde gris fino
                                .border(
                                    width = if (estaSeleccionado) 2.dp else 1.dp,
                                    color = if (estaSeleccionado) KidsPlannerColors.Primary
                                    else KidsPlannerColors.TextSecondary.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                // Cuando el usuario hace clic, ejecuto el callback con el ID del hijo
                                .clickable { alSeleccionarHijo(hijo.id) }
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Muestro el nombre del hijo
                                Text(
                                    text = hijo.nombre,
                                    fontSize = 16.sp,
                                    fontWeight = if (estaSeleccionado) FontWeight.Bold else FontWeight.Normal,
                                    color = if (estaSeleccionado) KidsPlannerColors.Primary
                                    else KidsPlannerColors.TextPrimary
                                )

                                // Si está seleccionado, muestro un check mark visual
                                if (estaSeleccionado) {
                                    Text(
                                        text = "✓",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = KidsPlannerColors.Primary
                                    )
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = nombreActual,
                    onValueChange = alCambiarNombre,
                    label = { Text("Nombre de la actividad *") },
                    placeholder = { Text("Fútbol, Piano, Natación...") },
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
                    value = descripcionActual,
                    onValueChange = alCambiarDescripcion,
                    label = { Text("Descripción") },
                    placeholder = { Text("Entrenamiento equipo infantil") },
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
                    value = lugarActual,
                    onValueChange = alCambiarLugar,
                    label = { Text("Lugar") },
                    placeholder = { Text("Polideportivo Municipal") },
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
                    value = diaSemanaActual,
                    onValueChange = alCambiarDiaSemana,
                    label = { Text("Día de la semana") },
                    placeholder = { Text("Lunes, Martes, Miércoles...") },
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
                    value = horaInicioActual,
                    onValueChange = alCambiarHoraInicio,
                    label = { Text("Hora inicio (HH:MM)") },
                    placeholder = { Text("17:00") },
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
                    value = horaFinActual,
                    onValueChange = alCambiarHoraFin,
                    label = { Text("Hora fin (HH:MM)") },
                    placeholder = { Text("18:30") },
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
                    value = nombreProfesorActual,
                    onValueChange = alCambiarNombreProfesor,
                    label = { Text("Nombre del profesor") },
                    placeholder = { Text("Juan García") },
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
