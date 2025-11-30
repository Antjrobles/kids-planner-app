/**
 * DialogoEliminarHijo - Diálogo modal de confirmación para eliminar hijo
 * Muestro alerta de confirmación con el nombre del hijo y botones Sí/Cancelar.
 * Componente simple y reutilizable para evitar eliminaciones accidentales.
 */
package com.antjrobles.kidsplanner.modulos.hijo

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.antjrobles.kidsplanner.modulos.hijo.Hijo
import com.antjrobles.kidsplanner.tema.KidsPlannerColors

@Composable
fun DialogoEliminarHijo(
    hijo: Hijo,
    estaEliminando: Boolean,
    alConfirmar: () -> Unit,
    alCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            if (!estaEliminando) alCancelar()
        },
        title = {
            Text(
                text = "¿Eliminar hijo?",
                fontWeight = FontWeight.Bold,
                color = KidsPlannerColors.TextPrimary
            )
        },
        text = {
            Text(
                text = "¿Estás seguro de eliminar a ${hijo.nombre}? Esta acción no se puede deshacer.",
                color = KidsPlannerColors.TextSecondary
            )
        },
        confirmButton = {
            TextButton(
                onClick = alConfirmar,
                enabled = !estaEliminando
            ) {
                Text(
                    text = if (estaEliminando) "Eliminando..." else "Sí, eliminar",
                    color = KidsPlannerColors.Error
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = alCancelar,
                enabled = !estaEliminando
            ) {
                Text(
                    text = "Cancelar",
                    color = KidsPlannerColors.TextSecondary
                )
            }
        },
        containerColor = KidsPlannerColors.White
    )
}
