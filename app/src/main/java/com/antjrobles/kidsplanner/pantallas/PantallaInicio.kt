/**
 * PantallaInicio - Pantalla de bienvenida (Landing Page)
 * Primera pantalla que ve el usuario al abrir la app. Muestro el logo, descripción de la app
 * y botones para navegar a Login o Registro.
 */
package com.antjrobles.kidsplanner.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antjrobles.kidsplanner.tema.KidsPlannerColors
import com.antjrobles.kidsplanner.componentes.PrimaryButton
import com.antjrobles.kidsplanner.componentes.SecondaryButton

/**
 * Pantalla de inicio
 * Pantalla de bienvenida con opciones de Login y Registro
 */
@Composable
fun PantallaInicio(
    alHacerClicLogin: () -> Unit = {},
    alHacerClicRegistro: () -> Unit = {}
) {
    // Usar colores centralizados del tema
    Surface(modifier = Modifier.fillMaxSize(), color = KidsPlannerColors.Background) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo o ícono de bienvenida (placeholder)
                Text(
                    text = "🎯",
                    fontSize = 64.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Título principal
                Text(
                    text = "Kids Planner",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = KidsPlannerColors.TextPrimary,
                    textAlign = TextAlign.Center
                )

                // Espaciador
                Spacer(modifier = Modifier.height(12.dp))

                // Slogan/descripción breve
                Text(
                    text = "Organiza tareas, completa metas, celebra logros",
                    fontSize = 14.sp,
                    color = KidsPlannerColors.Primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                // Espaciador medio
                Spacer(modifier = Modifier.height(16.dp))

                // Descripción de la app
                Text(
                    text = "Una app pensada para que los niños gestionen sus tareas diarias de forma divertida y responsable.",
                    fontSize = 12.sp,
                    color = KidsPlannerColors.TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                // Espaciador grande
                Spacer(modifier = Modifier.height(48.dp))

                // Botón: Iniciar Sesión
                PrimaryButton(
                    text = "Iniciar Sesión",
                    onClick = alHacerClicLogin
                )

                // Espaciador pequeño
                Spacer(modifier = Modifier.height(12.dp))

                // Botón: Registrarse
                SecondaryButton(
                    text = "Registrarse",
                    onClick = alHacerClicRegistro
                )

                // Espaciador pequeño
                Spacer(modifier = Modifier.height(16.dp))



                // Espaciador pequeño
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
