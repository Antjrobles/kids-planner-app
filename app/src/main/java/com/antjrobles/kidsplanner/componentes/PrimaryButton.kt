/**
 * PrimaryButton - Componente de botón principal reutilizable
 * Botón azul estilizado con el color primario de la app. Incluye animación de escala al presionar
 * para feedback visual. Usado para acciones principales (Login, Guardar, Crear, etc).
 */
package com.antjrobles.kidsplanner.componentes

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antjrobles.kidsplanner.tema.KidsPlannerColors

/**
 * Componente de botón primario reutilizable
 * @param text Texto que se muestra en el botón
 * @param onClick Acción que se ejecuta al hacer clic
 * @param modifier Modificadores opcionales
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    expandWidth: Boolean = true,
    horizontalPadding: Dp = 32.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = interactionSource.collectIsPressedAsState()

    var buttonModifier = modifier.height(48.dp)
    if (expandWidth) {
        buttonModifier = buttonModifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
    }

    Button(
        onClick = onClick,
        modifier = buttonModifier
            .graphicsLayer {
                // Efecto de escala cuando se presiona
                scaleX = if (isPressed.value) 0.95f else 1f
                scaleY = if (isPressed.value) 0.95f else 1f
            },
        colors = ButtonDefaults.buttonColors(
            containerColor = KidsPlannerColors.Primary,
            contentColor = KidsPlannerColors.White
        ),
        interactionSource = interactionSource
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

