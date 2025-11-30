/**
 * PantallaCrearFamilia - Pantalla para crear nueva familia
 * Muestro formulario con campo de nombre de familia, validaciones y estados de carga.
 * Se usa cuando el usuario no tiene familias creadas aún.
 */
package com.antjrobles.kidsplanner.modulos.familia

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
 * Muestro la pantalla para crear una nueva familia.
 * Permito al usuario introducir el nombre y guardo la familia en la base de datos.
 */
@Composable
fun PantallaCrearFamilia(
    estaGuardando: Boolean,
    alCambiarNombre: (String) -> Unit,
    nombreFamilia: String,
    alGuardar: () -> Unit,
    alCancelar: () -> Unit
) {
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
                Text(
                    text = "👪",
                    fontSize = 48.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Crear familia",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = KidsPlannerColors.TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Introduce un nombre para identificar a tu familia en la app.",
                    fontSize = 14.sp,
                    color = KidsPlannerColors.TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = nombreFamilia,
                    onValueChange = alCambiarNombre,
                    label = { Text("Nombre de la familia") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
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

                Spacer(modifier = Modifier.height(32.dp))

                PrimaryButton(
                    text = "Guardar familia",
                    onClick = alGuardar,
                    modifier = Modifier.fillMaxWidth()
                )

                if (estaGuardando) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(color = KidsPlannerColors.Primary)
                }

                Spacer(modifier = Modifier.height(16.dp))

                SecondaryButton(
                    text = "Cancelar",
                    onClick = alCancelar,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
